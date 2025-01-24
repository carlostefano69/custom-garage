package cg.customgarage.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import cg.customgarage.data.models.PaymentNotificationType
import cg.customgarage.data.repositories.PaymentNotificationRepository
import cg.customgarage.data.repositories.RenewalSettingsRepository
import cg.customgarage.data.repositories.SubscriptionRepository
import cg.customgarage.payment.gateway.PaymentGatewayService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.util.concurrent.TimeUnit

@HiltWorker
class SubscriptionRenewalWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val subscriptionRepository: SubscriptionRepository,
    private val renewalSettingsRepository: RenewalSettingsRepository,
    private val notificationRepository: PaymentNotificationRepository,
    private val paymentGatewayService: PaymentGatewayService
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val subscriptionId = inputData.getString(KEY_SUBSCRIPTION_ID)
            ?: return Result.failure()

        try {
            val subscription = subscriptionRepository.getActiveSubscription(subscriptionId).first()
                ?: return Result.failure()

            val renewalSettings = renewalSettingsRepository.getRenewalSettings(subscriptionId).first()
                ?: return Result.failure()

            if (!renewalSettings.isEnabled || !subscription.isActive) {
                return Result.success()
            }

            // Tenta il rinnovo
            val paymentResult = paymentGatewayService.processPayment(
                amount = getSubscriptionAmount(subscription.tier),
                currency = "EUR",
                paymentMethod = renewalSettings.paymentMethod,
                paymentDetails = getStoredPaymentDetails(subscription.userId)
            )

            when (paymentResult) {
                is PaymentGatewayResult.Success -> {
                    // Aggiorna la sottoscrizione
                    subscriptionRepository.renewSubscription(subscriptionId)
                    
                    // Notifica l'utente
                    notificationRepository.createNotification(
                        userId = subscription.userId,
                        type = PaymentNotificationType.PAYMENT_SUCCESSFUL,
                        message = "Abbonamento rinnovato con successo"
                    )

                    return Result.success()
                }
                is PaymentGatewayResult.Error -> {
                    // Registra il tentativo fallito
                    renewalSettingsRepository.recordFailedAttempt(renewalSettings.id)
                    
                    // Notifica l'utente
                    notificationRepository.createNotification(
                        userId = subscription.userId,
                        type = PaymentNotificationType.PAYMENT_FAILED,
                        message = "Rinnovo automatico fallito: ${paymentResult.message}"
                    )

                    // Pianifica un nuovo tentativo se necessario
                    if (renewalSettings.failedAttempts < MAX_RETRY_ATTEMPTS) {
                        scheduleRetry(subscriptionId)
                    }

                    return Result.retry()
                }
            }
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun scheduleRetry(subscriptionId: String) {
        val retryWork = OneTimeWorkRequestBuilder<SubscriptionRenewalWorker>()
            .setInputData(workDataOf(KEY_SUBSCRIPTION_ID to subscriptionId))
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                Duration.ofHours(RETRY_BACKOFF_HOURS).toMinutes(),
                TimeUnit.MINUTES
            )
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueue(retryWork)
    }

    companion object {
        private const val KEY_SUBSCRIPTION_ID = "subscription_id"
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val RETRY_BACKOFF_HOURS = 24L

        fun scheduleRenewal(
            context: Context,
            subscriptionId: String,
            delayHours: Long
        ) {
            val renewalWork = OneTimeWorkRequestBuilder<SubscriptionRenewalWorker>()
                .setInputData(workDataOf(KEY_SUBSCRIPTION_ID to subscriptionId))
                .setInitialDelay(delayHours, TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(context)
                .enqueue(renewalWork)
        }
    }
} 
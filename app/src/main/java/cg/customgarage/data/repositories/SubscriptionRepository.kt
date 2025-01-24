package cg.customgarage.data.repositories

import com.google.gson.Gson
import cg.customgarage.data.local.dao.SubscriptionDao
import cg.customgarage.data.local.dao.PaymentDao
import cg.customgarage.data.local.entities.PaymentEntity
import cg.customgarage.data.local.entities.SubscriptionEntity
import cg.customgarage.data.models.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepository @Inject constructor(
    private val subscriptionDao: SubscriptionDao,
    private val paymentDao: PaymentDao,
    private val gson: Gson
) {
    fun getActiveSubscription(userId: String): Flow<Subscription?> {
        return subscriptionDao.getSubscriptionForUser(userId)
            .map { it?.toModel() }
    }

    fun getPaymentHistory(subscriptionId: String): Flow<List<Payment>> {
        return paymentDao.getPaymentsForSubscription(subscriptionId)
            .map { payments -> payments.map { it.toModel() } }
    }

    suspend fun createSubscription(
        userId: String,
        tier: SubscriptionTier,
        autoRenew: Boolean = false
    ): Subscription {
        val subscription = Subscription(
            userId = userId,
            tier = tier,
            autoRenew = autoRenew
        )
        subscriptionDao.insertSubscription(subscription.toEntity())
        return subscription
    }

    suspend fun processPayment(
        subscriptionId: String,
        amount: Double,
        method: PaymentMethod,
        details: PaymentDetails
    ): Payment {
        val payment = Payment(
            subscriptionId = subscriptionId,
            amount = amount,
            method = method,
            status = PaymentStatus.PENDING,
            paymentDetails = details
        )
        paymentDao.insertPayment(payment.toEntity())
        return payment
    }

    suspend fun updatePaymentStatus(paymentId: String, status: PaymentStatus) {
        paymentDao.updatePaymentStatus(paymentId, status.name)
    }

    suspend fun cancelSubscription(subscriptionId: String) {
        subscriptionDao.updateSubscriptionStatus(subscriptionId, false)
    }

    private fun SubscriptionEntity.toModel() = Subscription(
        id = id,
        userId = userId,
        tier = tier,
        startDate = startDate,
        endDate = endDate,
        isActive = isActive,
        autoRenew = autoRenew
    )

    private fun Subscription.toEntity() = SubscriptionEntity(
        id = id,
        userId = userId,
        tier = tier,
        startDate = startDate,
        endDate = endDate,
        isActive = isActive,
        autoRenew = autoRenew
    )

    private fun PaymentEntity.toModel() = Payment(
        id = id,
        subscriptionId = subscriptionId,
        amount = amount,
        currency = currency,
        method = method,
        status = status,
        transactionDate = transactionDate,
        paymentDetails = gson.fromJson(paymentDetails, PaymentDetails::class.java)
    )

    private fun Payment.toEntity() = PaymentEntity(
        id = id,
        subscriptionId = subscriptionId,
        amount = amount,
        currency = currency,
        method = method,
        status = status,
        transactionDate = transactionDate,
        paymentDetails = gson.toJson(paymentDetails)
    )
} 
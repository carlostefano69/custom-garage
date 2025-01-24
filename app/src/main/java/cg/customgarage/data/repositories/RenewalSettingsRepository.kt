package cg.customgarage.data.repositories

import cg.customgarage.data.local.dao.RenewalSettingsDao
import cg.customgarage.data.local.entities.RenewalSettingsEntity
import cg.customgarage.data.models.PaymentMethod
import cg.customgarage.data.models.RenewalSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RenewalSettingsRepository @Inject constructor(
    private val renewalSettingsDao: RenewalSettingsDao
) {
    fun getRenewalSettings(subscriptionId: String): Flow<RenewalSettings?> {
        return renewalSettingsDao.getRenewalSettings(subscriptionId)
            .map { it?.toModel() }
    }

    suspend fun createRenewalSettings(
        subscriptionId: String,
        paymentMethod: PaymentMethod,
        renewalDate: Long? = null
    ) {
        val settings = RenewalSettings(
            subscriptionId = subscriptionId,
            paymentMethod = paymentMethod,
            renewalDate = renewalDate
        )
        renewalSettingsDao.insertRenewalSettings(settings.toEntity())
    }

    suspend fun updateAutoRenewal(subscriptionId: String, enabled: Boolean) {
        renewalSettingsDao.updateAutoRenewal(subscriptionId, enabled)
    }

    suspend fun recordFailedAttempt(settingsId: String) {
        renewalSettingsDao.incrementFailedAttempts(
            settingsId,
            System.currentTimeMillis()
        )
    }

    private fun RenewalSettingsEntity.toModel() = RenewalSettings(
        id = id,
        subscriptionId = subscriptionId,
        isEnabled = isEnabled,
        renewalDate = renewalDate,
        lastRenewalAttempt = lastRenewalAttempt,
        failedAttempts = failedAttempts,
        paymentMethod = paymentMethod
    )

    private fun RenewalSettings.toEntity() = RenewalSettingsEntity(
        id = id,
        subscriptionId = subscriptionId,
        isEnabled = isEnabled,
        renewalDate = renewalDate,
        lastRenewalAttempt = lastRenewalAttempt,
        failedAttempts = failedAttempts,
        paymentMethod = paymentMethod
    )
} 
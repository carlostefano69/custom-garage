package cg.customgarage.data.local.dao

import androidx.room.*
import cg.customgarage.data.local.entities.RenewalSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RenewalSettingsDao {
    @Query("SELECT * FROM renewal_settings WHERE subscriptionId = :subscriptionId")
    fun getRenewalSettings(subscriptionId: String): Flow<RenewalSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRenewalSettings(settings: RenewalSettingsEntity)

    @Query("UPDATE renewal_settings SET isEnabled = :enabled WHERE subscriptionId = :subscriptionId")
    suspend fun updateAutoRenewal(subscriptionId: String, enabled: Boolean)

    @Query("UPDATE renewal_settings SET failedAttempts = failedAttempts + 1, lastRenewalAttempt = :timestamp WHERE id = :settingsId")
    suspend fun incrementFailedAttempts(settingsId: String, timestamp: Long)
} 
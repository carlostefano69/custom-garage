package cg.customgarage.data.local.dao

import androidx.room.*
import cg.customgarage.data.local.entities.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions WHERE userId = :userId")
    fun getSubscriptionForUser(userId: String): Flow<SubscriptionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity)

    @Query("UPDATE subscriptions SET isActive = :isActive WHERE id = :subscriptionId")
    suspend fun updateSubscriptionStatus(subscriptionId: String, isActive: Boolean)
} 
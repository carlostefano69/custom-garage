package cg.customgarage.data.local.dao

import androidx.room.*
import cg.customgarage.data.local.entities.PaymentNotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentNotificationDao {
    @Query("SELECT * FROM payment_notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: String): Flow<List<PaymentNotificationEntity>>

    @Query("SELECT COUNT(*) FROM payment_notifications WHERE userId = :userId AND isRead = 0")
    fun getUnreadNotificationsCount(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: PaymentNotificationEntity)

    @Query("UPDATE payment_notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: String)

    @Query("DELETE FROM payment_notifications WHERE userId = :userId AND timestamp < :timestamp")
    suspend fun deleteOldNotifications(userId: String, timestamp: Long)
} 
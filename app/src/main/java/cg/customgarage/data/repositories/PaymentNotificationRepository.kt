package cg.customgarage.data.repositories

import cg.customgarage.data.local.dao.PaymentNotificationDao
import cg.customgarage.data.local.entities.PaymentNotificationEntity
import cg.customgarage.data.models.PaymentNotification
import cg.customgarage.data.models.PaymentNotificationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentNotificationRepository @Inject constructor(
    private val notificationDao: PaymentNotificationDao
) {
    fun getNotificationsForUser(userId: String): Flow<List<PaymentNotification>> {
        return notificationDao.getNotificationsForUser(userId)
            .map { entities -> entities.map { it.toModel() } }
    }

    fun getUnreadCount(userId: String): Flow<Int> {
        return notificationDao.getUnreadNotificationsCount(userId)
    }

    suspend fun createNotification(
        userId: String,
        type: PaymentNotificationType,
        message: String
    ) {
        val notification = PaymentNotification(
            userId = userId,
            type = type,
            message = message
        )
        notificationDao.insertNotification(notification.toEntity())
    }

    suspend fun markAsRead(notificationId: String) {
        notificationDao.markAsRead(notificationId)
    }

    suspend fun cleanOldNotifications(userId: String, olderThan: Long) {
        notificationDao.deleteOldNotifications(userId, olderThan)
    }

    private fun PaymentNotificationEntity.toModel() = PaymentNotification(
        id = id,
        userId = userId,
        type = type,
        message = message,
        timestamp = timestamp,
        isRead = isRead
    )

    private fun PaymentNotification.toEntity() = PaymentNotificationEntity(
        id = id,
        userId = userId,
        type = type,
        message = message,
        timestamp = timestamp,
        isRead = isRead
    )
} 
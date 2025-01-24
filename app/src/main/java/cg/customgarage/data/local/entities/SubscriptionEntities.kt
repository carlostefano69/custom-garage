package cg.customgarage.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import cg.customgarage.data.models.PaymentMethod
import cg.customgarage.data.models.PaymentStatus
import cg.customgarage.data.models.SubscriptionTier

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val tier: SubscriptionTier,
    val startDate: Long,
    val endDate: Long?,
    val isActive: Boolean,
    val autoRenew: Boolean
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String,
    val subscriptionId: String,
    val amount: Double,
    val currency: String,
    val method: PaymentMethod,
    val status: PaymentStatus,
    val transactionDate: Long,
    val paymentDetails: String // JSON string dei dettagli
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val name: String,
    val preferredPaymentMethod: PaymentMethod?
) 
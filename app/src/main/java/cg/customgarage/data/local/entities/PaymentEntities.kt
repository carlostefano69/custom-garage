package cg.customgarage.data.local.entities

import androidx.room.*
import cg.customgarage.data.models.*

// Notifiche
@Entity(tableName = "payment_notifications")
data class PaymentNotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val type: PaymentNotificationType,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean
)

// Rinnovo automatico
@Entity(tableName = "renewal_settings")
data class RenewalSettingsEntity(
    @PrimaryKey val id: String,
    val subscriptionId: String,
    val isEnabled: Boolean,
    val renewalDate: Long?,
    val lastRenewalAttempt: Long?,
    val failedAttempts: Int,
    val paymentMethod: PaymentMethod
)

// Rimborsi
@Entity(tableName = "refunds")
data class RefundEntity(
    @PrimaryKey val id: String,
    val paymentId: String,
    val amount: Double,
    val reason: RefundReason,
    val status: RefundStatus,
    val requestDate: Long,
    val processedDate: Long?,
    val notes: String?
)

// Fatture
@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey val id: String,
    val paymentId: String,
    val invoiceNumber: String,
    val userId: String,
    val amount: Double,
    val issueDate: Long,
    val dueDate: Long,
    val status: InvoiceStatus,
    @Embedded val billingDetails: BillingDetailsEntity
)

data class BillingDetailsEntity(
    val companyName: String?,
    val vatNumber: String?,
    val fiscalCode: String?,
    @Embedded val address: AddressEntity,
    val email: String
)

@Entity(tableName = "invoice_items")
data class InvoiceItemEntity(
    @PrimaryKey val id: String,
    val invoiceId: String,
    val description: String,
    val quantity: Int,
    val unitPrice: Double,
    val taxRate: Double,
    val total: Double
)

// Relazioni
data class InvoiceWithItems(
    @Embedded val invoice: InvoiceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "invoiceId"
    )
    val items: List<InvoiceItemEntity>
) 
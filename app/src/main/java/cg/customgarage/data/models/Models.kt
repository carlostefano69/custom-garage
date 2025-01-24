package cg.customgarage.data.models

import java.util.UUID

// Veicoli
data class Vehicle(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val make: String,
    val model: String,
    val year: Int,
    val imageUrl: String? = null,
    val maintenanceRecords: List<MaintenanceRecord> = emptyList(),
    val scheduledMaintenances: List<ScheduledMaintenance> = emptyList()
)

// Manutenzioni
data class ScheduledMaintenance(
    val id: String = UUID.randomUUID().toString(),
    val type: MaintenanceType,
    val description: String,
    val intervalMonths: Int,
    val intervalMileage: Int,
    val estimatedCost: Double?,
    val nextDueDate: Long? = null,
    val nextDueMileage: Int? = null
)

data class MaintenanceRecord(
    val id: String = UUID.randomUUID().toString(),
    val date: Long,
    val type: MaintenanceType,
    val description: String,
    val mileage: Int,
    val cost: Double,
    val notes: String = "",
    val images: List<String> = emptyList()
)

enum class MaintenanceType {
    ORDINARY,
    EXTRAORDINARY,
    INSPECTION,
    MODIFICATION
}

// Progetti
data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val vehicleId: String,
    val status: ProjectStatus = ProjectStatus.PLANNED,
    val modifications: List<Modification> = emptyList(),
    val inspirationImages: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class Modification(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val cost: Double,
    val status: ModificationStatus = ModificationStatus.PLANNED,
    val images: List<String> = emptyList()
)

enum class ProjectStatus {
    PLANNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

enum class ModificationStatus {
    PLANNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

// Galleria
data class GalleryImage(
    val id: String = UUID.randomUUID().toString(),
    val uri: String,
    val type: ImageType,
    val referenceId: String?, // ID del progetto o della manutenzione associata
    val description: String = "",
    val uploadDate: Long = System.currentTimeMillis()
)

enum class ImageType {
    PROJECT,
    MAINTENANCE,
    INSPIRATION
}

// ViewModels Data Classes
data class ProjectDetails(
    val project: Project,
    val vehicle: Vehicle,
    val totalCost: Double,
    val completedModifications: Int,
    val inspirationImages: List<GalleryImage>
)

data class VehicleWithMaintenance(
    val vehicle: Vehicle,
    val upcomingMaintenances: List<ScheduledMaintenance>,
    val maintenanceRecords: List<MaintenanceRecord>
)

// Settings
data class AppSettings(
    val darkMode: Boolean = true,
    val maintenanceNotifications: Boolean = true,
    val projectNotifications: Boolean = true,
    val language: String = "it"
)

enum class SubscriptionTier {
    FREE,      // Funzionalità base, max 1 veicolo
    PREMIUM,   // Funzionalità complete, max 5 veicoli
    PRO        // Funzionalità complete, veicoli illimitati
}

enum class PaymentMethod {
    PAYPAL,
    BANK_TRANSFER
}

data class Subscription(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val tier: SubscriptionTier,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val isActive: Boolean = true,
    val autoRenew: Boolean = false
)

data class Payment(
    val id: String = UUID.randomUUID().toString(),
    val subscriptionId: String,
    val amount: Double,
    val currency: String = "EUR",
    val method: PaymentMethod,
    val status: PaymentStatus,
    val transactionDate: Long = System.currentTimeMillis(),
    val paymentDetails: PaymentDetails
)

sealed class PaymentDetails {
    data class PayPal(
        val paypalEmail: String,
        val transactionId: String
    ) : PaymentDetails()

    data class BankTransfer(
        val accountHolder: String,
        val iban: String,
        val swift: String,
        val transferId: String?
    ) : PaymentDetails()
}

enum class PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED
}

// Aggiorniamo User per includere l'abbonamento
data class User(
    val id: String = UUID.randomUUID().toString(),
    val email: String,
    val name: String,
    val subscription: Subscription? = null,
    val preferredPaymentMethod: PaymentMethod? = null
)

// Notifiche pagamenti
data class PaymentNotification(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val type: PaymentNotificationType,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

enum class PaymentNotificationType {
    PAYMENT_DUE,        // Pagamento in scadenza
    PAYMENT_SUCCESSFUL, // Pagamento completato
    PAYMENT_FAILED,     // Pagamento fallito
    RENEWAL_UPCOMING,   // Rinnovo in arrivo
    REFUND_PROCESSED    // Rimborso elaborato
}

// Rinnovo automatico
data class RenewalSettings(
    val id: String = UUID.randomUUID().toString(),
    val subscriptionId: String,
    val isEnabled: Boolean = false,
    val renewalDate: Long? = null,
    val lastRenewalAttempt: Long? = null,
    val failedAttempts: Int = 0,
    val paymentMethod: PaymentMethod
)

// Analytics e reportistica
data class PaymentAnalytics(
    val totalRevenue: Double,
    val successfulPayments: Int,
    val failedPayments: Int,
    val averagePaymentAmount: Double,
    val subscriptionsByTier: Map<SubscriptionTier, Int>,
    val revenueByPeriod: Map<String, Double> // es: "2024-03" -> 1234.56
)

// Rimborsi
data class Refund(
    val id: String = UUID.randomUUID().toString(),
    val paymentId: String,
    val amount: Double,
    val reason: RefundReason,
    val status: RefundStatus,
    val requestDate: Long = System.currentTimeMillis(),
    val processedDate: Long? = null,
    val notes: String? = null
)

enum class RefundReason {
    CUSTOMER_REQUEST,
    SERVICE_ISSUE,
    DUPLICATE_PAYMENT,
    FRAUDULENT_CHARGE,
    OTHER
}

enum class RefundStatus {
    PENDING,
    APPROVED,
    PROCESSED,
    REJECTED
}

// Fatture
data class Invoice(
    val id: String = UUID.randomUUID().toString(),
    val paymentId: String,
    val invoiceNumber: String,
    val userId: String,
    val amount: Double,
    val issueDate: Long = System.currentTimeMillis(),
    val dueDate: Long,
    val status: InvoiceStatus,
    val items: List<InvoiceItem>,
    val billingDetails: BillingDetails
)

data class InvoiceItem(
    val description: String,
    val quantity: Int = 1,
    val unitPrice: Double,
    val taxRate: Double,
    val total: Double
)

data class BillingDetails(
    val companyName: String? = null,
    val vatNumber: String? = null,
    val fiscalCode: String? = null,
    val address: Address,
    val email: String
)

enum class InvoiceStatus {
    DRAFT,
    ISSUED,
    PAID,
    OVERDUE,
    CANCELLED
}

// Gateway di pagamento
sealed class PaymentGatewayResult {
    data class Success(
        val transactionId: String,
        val amount: Double,
        val timestamp: Long = System.currentTimeMillis()
    ) : PaymentGatewayResult()
    
    data class Error(
        val code: String,
        val message: String,
        val technical: String? = null
    ) : PaymentGatewayResult()
}

data class PaymentGatewayConfig(
    val gatewayType: PaymentGatewayType,
    val apiKey: String,
    val secretKey: String,
    val environment: GatewayEnvironment,
    val merchantId: String,
    val webhookUrl: String
)

enum class PaymentGatewayType {
    STRIPE,
    PAYPAL,
    SQUARE
}

enum class GatewayEnvironment {
    SANDBOX,
    PRODUCTION
} 
package cg.customgarage.data.repositories

import cg.customgarage.data.local.dao.InvoiceDao
import cg.customgarage.data.local.entities.InvoiceEntity
import cg.customgarage.data.local.entities.InvoiceItemEntity
import cg.customgarage.data.models.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvoiceRepository @Inject constructor(
    private val invoiceDao: InvoiceDao,
    private val gson: Gson
) {
    fun getInvoicesForUser(userId: String): Flow<List<Invoice>> {
        return invoiceDao.getInvoicesForUser(userId)
            .map { invoicesWithItems ->
                invoicesWithItems.map { it.toModel() }
            }
    }

    fun getInvoiceForPayment(paymentId: String): Flow<Invoice?> {
        return invoiceDao.getInvoiceForPayment(paymentId)
            .map { it?.toModel() }
    }

    suspend fun createInvoice(
        paymentId: String,
        userId: String,
        amount: Double,
        items: List<InvoiceItem>,
        billingDetails: BillingDetails
    ): Invoice {
        val invoice = Invoice(
            paymentId = paymentId,
            invoiceNumber = generateInvoiceNumber(),
            userId = userId,
            amount = amount,
            dueDate = LocalDateTime.now().plusDays(30)
                .toInstant(ZoneOffset.UTC).toEpochMilli(),
            status = InvoiceStatus.DRAFT,
            items = items,
            billingDetails = billingDetails
        )

        invoiceDao.insertInvoiceWithItems(
            invoice = invoice.toEntity(),
            items = items.map { it.toEntity(invoice.id) }
        )

        return invoice
    }

    suspend fun updateInvoiceStatus(invoiceId: String, status: InvoiceStatus) {
        invoiceDao.updateInvoiceStatus(invoiceId, status)
    }

    private fun generateInvoiceNumber(): String {
        val timestamp = System.currentTimeMillis()
        return "INV-${timestamp}"
    }

    private fun InvoiceEntity.toModel() = Invoice(
        id = id,
        paymentId = paymentId,
        invoiceNumber = invoiceNumber,
        userId = userId,
        amount = amount,
        issueDate = issueDate,
        dueDate = dueDate,
        status = status,
        items = emptyList(), // Sarà popolato dalla relazione
        billingDetails = billingDetails.toModel()
    )

    private fun Invoice.toEntity() = InvoiceEntity(
        id = id,
        paymentId = paymentId,
        invoiceNumber = invoiceNumber,
        userId = userId,
        amount = amount,
        issueDate = issueDate,
        dueDate = dueDate,
        status = status,
        billingDetails = billingDetails.toEntity()
    )

    private fun InvoiceItem.toEntity(invoiceId: String) = InvoiceItemEntity(
        id = UUID.randomUUID().toString(),
        invoiceId = invoiceId,
        description = description,
        quantity = quantity,
        unitPrice = unitPrice,
        taxRate = taxRate,
        total = total
    )
} 
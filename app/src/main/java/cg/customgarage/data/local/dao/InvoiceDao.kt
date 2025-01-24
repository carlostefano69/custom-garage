package cg.customgarage.data.local.dao

import androidx.room.*
import cg.customgarage.data.local.entities.InvoiceEntity
import cg.customgarage.data.local.entities.InvoiceItemEntity
import cg.customgarage.data.local.entities.InvoiceWithItems
import cg.customgarage.data.models.InvoiceStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    @Transaction
    @Query("SELECT * FROM invoices WHERE userId = :userId ORDER BY issueDate DESC")
    fun getInvoicesForUser(userId: String): Flow<List<InvoiceWithItems>>

    @Query("SELECT * FROM invoices WHERE paymentId = :paymentId")
    fun getInvoiceForPayment(paymentId: String): Flow<InvoiceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoiceItems(items: List<InvoiceItemEntity>)

    @Query("UPDATE invoices SET status = :status WHERE id = :invoiceId")
    suspend fun updateInvoiceStatus(invoiceId: String, status: InvoiceStatus)

    @Transaction
    suspend fun insertInvoiceWithItems(invoice: InvoiceEntity, items: List<InvoiceItemEntity>) {
        insertInvoice(invoice)
        insertInvoiceItems(items)
    }
} 
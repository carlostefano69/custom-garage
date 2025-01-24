package cg.customgarage.data.local.dao

import androidx.room.*
import cg.customgarage.data.local.entities.RefundEntity
import cg.customgarage.data.models.RefundStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface RefundDao {
    @Query("SELECT * FROM refunds WHERE paymentId = :paymentId")
    fun getRefundsForPayment(paymentId: String): Flow<List<RefundEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRefund(refund: RefundEntity)

    @Query("UPDATE refunds SET status = :status, processedDate = :processedDate WHERE id = :refundId")
    suspend fun updateRefundStatus(refundId: String, status: RefundStatus, processedDate: Long?)
} 
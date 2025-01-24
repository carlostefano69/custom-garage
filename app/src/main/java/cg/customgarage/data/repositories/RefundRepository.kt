package cg.customgarage.data.repositories

import cg.customgarage.data.local.dao.RefundDao
import cg.customgarage.data.local.entities.RefundEntity
import cg.customgarage.data.models.Refund
import cg.customgarage.data.models.RefundReason
import cg.customgarage.data.models.RefundStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefundRepository @Inject constructor(
    private val refundDao: RefundDao
) {
    fun getRefundsForPayment(paymentId: String): Flow<List<Refund>> {
        return refundDao.getRefundsForPayment(paymentId)
            .map { entities -> entities.map { it.toModel() } }
    }

    suspend fun requestRefund(
        paymentId: String,
        amount: Double,
        reason: RefundReason,
        notes: String? = null
    ): Refund {
        val refund = Refund(
            paymentId = paymentId,
            amount = amount,
            reason = reason,
            status = RefundStatus.PENDING,
            notes = notes
        )
        refundDao.insertRefund(refund.toEntity())
        return refund
    }

    suspend fun updateRefundStatus(refundId: String, status: RefundStatus) {
        val processedDate = if (status == RefundStatus.PROCESSED) {
            System.currentTimeMillis()
        } else null
        refundDao.updateRefundStatus(refundId, status, processedDate)
    }

    private fun RefundEntity.toModel() = Refund(
        id = id,
        paymentId = paymentId,
        amount = amount,
        reason = reason,
        status = status,
        requestDate = requestDate,
        processedDate = processedDate,
        notes = notes
    )

    private fun Refund.toEntity() = RefundEntity(
        id = id,
        paymentId = paymentId,
        amount = amount,
        reason = reason,
        status = status,
        requestDate = requestDate,
        processedDate = processedDate,
        notes = notes
    )
} 
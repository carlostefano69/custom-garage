package cg.customgarage.data.local.dao

import androidx.room.*
import cg.customgarage.data.local.entities.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE subscriptionId = :subscriptionId")
    fun getPaymentsForSubscription(subscriptionId: String): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Query("UPDATE payments SET status = :status WHERE id = :paymentId")
    suspend fun updatePaymentStatus(paymentId: String, status: String)
} 
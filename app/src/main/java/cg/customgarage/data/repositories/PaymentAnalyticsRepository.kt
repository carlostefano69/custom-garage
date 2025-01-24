package cg.customgarage.data.repositories

import cg.customgarage.data.local.dao.PaymentDao
import cg.customgarage.data.local.dao.SubscriptionDao
import cg.customgarage.data.models.PaymentAnalytics
import cg.customgarage.data.models.PaymentStatus
import cg.customgarage.data.models.SubscriptionTier
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentAnalyticsRepository @Inject constructor(
    private val paymentDao: PaymentDao,
    private val subscriptionDao: SubscriptionDao
) {
    fun getAnalytics(startDate: Long, endDate: Long): Flow<PaymentAnalytics> {
        return combine(
            getPaymentMetrics(startDate, endDate),
            getSubscriptionMetrics(),
            getRevenueByPeriod(startDate, endDate)
        ) { paymentMetrics, subscriptionsByTier, revenueByPeriod ->
            PaymentAnalytics(
                totalRevenue = paymentMetrics.totalRevenue,
                successfulPayments = paymentMetrics.successfulCount,
                failedPayments = paymentMetrics.failedCount,
                averagePaymentAmount = paymentMetrics.averageAmount,
                subscriptionsByTier = subscriptionsByTier,
                revenueByPeriod = revenueByPeriod
            )
        }
    }

    private fun getPaymentMetrics(startDate: Long, endDate: Long): Flow<PaymentMetrics> {
        return paymentDao.getAllPayments()
            .map { payments ->
                val filtered = payments.filter { 
                    it.transactionDate in startDate..endDate 
                }
                val successful = filtered.filter { 
                    it.status == PaymentStatus.COMPLETED 
                }
                
                PaymentMetrics(
                    totalRevenue = successful.sumOf { it.amount },
                    successfulCount = successful.size,
                    failedCount = filtered.count { 
                        it.status == PaymentStatus.FAILED 
                    },
                    averageAmount = if (successful.isNotEmpty()) {
                        successful.sumOf { it.amount } / successful.size
                    } else 0.0
                )
            }
    }

    private fun getSubscriptionMetrics(): Flow<Map<SubscriptionTier, Int>> {
        return subscriptionDao.getAllSubscriptions()
            .map { subscriptions ->
                subscriptions
                    .filter { it.isActive }
                    .groupBy { it.tier }
                    .mapValues { it.value.size }
            }
    }

    private fun getRevenueByPeriod(
        startDate: Long,
        endDate: Long
    ): Flow<Map<String, Double>> {
        return paymentDao.getAllPayments()
            .map { payments ->
                payments
                    .filter { 
                        it.status == PaymentStatus.COMPLETED &&
                        it.transactionDate in startDate..endDate
                    }
                    .groupBy {
                        YearMonth.from(
                            LocalDateTime.ofEpochSecond(
                                it.transactionDate / 1000,
                                0,
                                ZoneOffset.UTC
                            )
                        ).toString()
                    }
                    .mapValues { entry -> entry.value.sumOf { it.amount } }
            }
    }

    private data class PaymentMetrics(
        val totalRevenue: Double,
        val successfulCount: Int,
        val failedCount: Int,
        val averageAmount: Double
    )
} 
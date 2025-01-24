package cg.customgarage.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cg.customgarage.data.local.dao.PaymentDao
import cg.customgarage.data.local.dao.SubscriptionDao
import cg.customgarage.data.local.entities.PaymentEntity
import cg.customgarage.data.local.entities.SubscriptionEntity
import cg.customgarage.data.models.PaymentMethod
import cg.customgarage.data.models.PaymentStatus
import cg.customgarage.data.models.SubscriptionTier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PaymentDatabaseTest {
    private lateinit var db: AppDatabase
    private lateinit var paymentDao: PaymentDao
    private lateinit var subscriptionDao: SubscriptionDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        paymentDao = db.paymentDao()
        subscriptionDao = db.subscriptionDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadSubscription() = runBlocking {
        val subscription = SubscriptionEntity(
            id = "sub1",
            userId = "user1",
            tier = SubscriptionTier.PREMIUM,
            startDate = System.currentTimeMillis(),
            endDate = null,
            isActive = true,
            autoRenew = true
        )
        subscriptionDao.insertSubscription(subscription)
        val result = subscriptionDao.getSubscriptionForUser("user1").first()
        assertEquals(subscription, result)
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadPayment() = runBlocking {
        val payment = PaymentEntity(
            id = "pay1",
            subscriptionId = "sub1",
            amount = 9.99,
            currency = "EUR",
            method = PaymentMethod.PAYPAL,
            status = PaymentStatus.COMPLETED,
            transactionDate = System.currentTimeMillis(),
            paymentDetails = """{"paypalEmail":"test@example.com","transactionId":"TX123"}"""
        )
        paymentDao.insertPayment(payment)
        val payments = paymentDao.getPaymentsForSubscription("sub1").first()
        assertEquals(1, payments.size)
        assertEquals(payment, payments[0])
    }

    @Test
    @Throws(Exception::class)
    fun updatePaymentStatus() = runBlocking {
        val payment = PaymentEntity(
            id = "pay1",
            subscriptionId = "sub1",
            amount = 9.99,
            currency = "EUR",
            method = PaymentMethod.PAYPAL,
            status = PaymentStatus.PENDING,
            transactionDate = System.currentTimeMillis(),
            paymentDetails = """{"paypalEmail":"test@example.com","transactionId":"TX123"}"""
        )
        paymentDao.insertPayment(payment)
        paymentDao.updatePaymentStatus("pay1", PaymentStatus.COMPLETED.name)
        val updatedPayment = paymentDao.getPaymentsForSubscription("sub1").first()[0]
        assertEquals(PaymentStatus.COMPLETED, updatedPayment.status)
    }
} 
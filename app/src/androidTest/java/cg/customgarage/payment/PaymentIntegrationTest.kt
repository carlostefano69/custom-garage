package cg.customgarage.payment

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cg.customgarage.data.local.AppDatabase
import cg.customgarage.data.models.*
import cg.customgarage.data.repositories.SubscriptionRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PaymentIntegrationTest {
    private lateinit var db: AppDatabase
    private lateinit var repository: SubscriptionRepository
    private lateinit var payPalService: PayPalService
    private lateinit var paymentValidator: PaymentValidator

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        
        repository = SubscriptionRepository(
            subscriptionDao = db.subscriptionDao(),
            paymentDao = db.paymentDao(),
            gson = Gson()
        )
        payPalService = PayPalService(context)
        paymentValidator = PaymentValidator()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        db.close()
    }

    @Test
    fun completePaymentFlow() = runBlocking {
        // 1. Crea una sottoscrizione
        val subscription = repository.createSubscription(
            userId = "user1",
            tier = SubscriptionTier.PREMIUM
        )
        assertNotNull(subscription)
        assertEquals(SubscriptionTier.PREMIUM, subscription.tier)

        // 2. Processa un pagamento PayPal
        val paymentDetails = PaymentDetails.PayPal(
            paypalEmail = "test@example.com",
            transactionId = "TX123"
        )
        val payment = repository.processPayment(
            subscriptionId = subscription.id,
            amount = 9.99,
            method = PaymentMethod.PAYPAL,
            details = paymentDetails
        )
        assertNotNull(payment)
        assertEquals(PaymentStatus.PENDING, payment.status)

        // 3. Aggiorna lo stato del pagamento
        repository.updatePaymentStatus(payment.id, PaymentStatus.COMPLETED)
        val updatedPayments = repository.getPaymentHistory(subscription.id).first()
        assertEquals(1, updatedPayments.size)
        assertEquals(PaymentStatus.COMPLETED, updatedPayments[0].status)

        // 4. Verifica la sottoscrizione attiva
        val activeSubscription = repository.getActiveSubscription("user1").first()
        assertNotNull(activeSubscription)
        assertTrue(activeSubscription!!.isActive)
    }

    @Test
    fun bankTransferPaymentFlow() = runBlocking {
        // 1. Crea una sottoscrizione
        val subscription = repository.createSubscription(
            userId = "user1",
            tier = SubscriptionTier.PRO
        )

        // 2. Processa un pagamento con bonifico
        val paymentDetails = PaymentDetails.BankTransfer(
            accountHolder = "Mario Rossi",
            iban = "IT60X0542811101000000123456",
            swift = "UNCRITM1234",
            transferId = null
        )
        val payment = repository.processPayment(
            subscriptionId = subscription.id,
            amount = 19.99,
            method = PaymentMethod.BANK_TRANSFER,
            details = paymentDetails
        )

        // 3. Verifica stato iniziale
        assertEquals(PaymentStatus.PENDING, payment.status)
        val pendingPayments = repository.getPaymentHistory(subscription.id).first()
        assertEquals(1, pendingPayments.size)
        assertEquals(PaymentMethod.BANK_TRANSFER, pendingPayments[0].method)

        // 4. Simula conferma bonifico
        repository.updatePaymentStatus(payment.id, PaymentStatus.COMPLETED)
        val completedPayments = repository.getPaymentHistory(subscription.id).first()
        assertEquals(PaymentStatus.COMPLETED, completedPayments[0].status)
    }

    @Test
    fun validatePaymentData() {
        // Test validazione email PayPal
        assertTrue(paymentValidator.validatePayPalEmail("valid@email.com") is ValidationResult.Success)
        assertTrue(paymentValidator.validatePayPalEmail("invalid-email") is ValidationResult.Error)

        // Test validazione dati bonifico
        val validBankTransfer = paymentValidator.validateBankTransfer(
            accountHolder = "Mario Rossi",
            iban = "IT60X0542811101000000123456",
            swift = "UNCRITM1234"
        )
        assertTrue(validBankTransfer is ValidationResult.Success)

        val invalidBankTransfer = paymentValidator.validateBankTransfer(
            accountHolder = "",
            iban = "invalid-iban",
            swift = "invalid-swift"
        )
        assertTrue(invalidBankTransfer is ValidationResult.Error)
    }
} 
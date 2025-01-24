package cg.customgarage.ui.viewmodels

import app.cash.turbine.test
import cg.customgarage.data.models.*
import cg.customgarage.data.repositories.SubscriptionRepository
import cg.customgarage.payment.PayPalService
import cg.customgarage.payment.PaymentValidator
import cg.customgarage.payment.ValidationResult
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SubscriptionViewModelTest {
    private lateinit var repository: SubscriptionRepository
    private lateinit var payPalService: PayPalService
    private lateinit var paymentValidator: PaymentValidator
    private lateinit var viewModel: SubscriptionViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        payPalService = mockk()
        paymentValidator = mockk()
        viewModel = SubscriptionViewModel(repository, payPalService, paymentValidator)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `processPayPalPayment updates state correctly on success`() = runTest {
        // Given
        val email = "test@example.com"
        val amount = 9.99
        val orderId = "ORDER123"
        
        every { paymentValidator.validatePayPalEmail(email) } returns ValidationResult.Success
        coEvery { payPalService.createOrder(amount) } returns Result.success(orderId)
        coEvery { payPalService.captureOrder(orderId) } returns Result.success(true)

        // When
        viewModel.processPayPalPayment(amount, email)

        // Then
        viewModel.paymentState.test {
            assertEquals(PaymentState.Processing, awaitItem())
            assertEquals(PaymentState.Success, awaitItem())
        }
    }

    @Test
    fun `processPayPalPayment handles validation error`() = runTest {
        // Given
        val email = "invalid-email"
        val amount = 9.99
        val errorMessage = "Email non valida"
        
        every { paymentValidator.validatePayPalEmail(email) } returns 
            ValidationResult.Error(errorMessage)

        // When
        viewModel.processPayPalPayment(amount, email)

        // Then
        viewModel.validationState.test {
            val state = awaitItem()
            assertTrue(state is ValidationState.Invalid)
            assertEquals(errorMessage, (state as ValidationState.Invalid).message)
        }
    }

    @Test
    fun `processPayPalPayment handles payment error`() = runTest {
        // Given
        val email = "test@example.com"
        val amount = 9.99
        val errorMessage = "Payment failed"
        
        every { paymentValidator.validatePayPalEmail(email) } returns ValidationResult.Success
        coEvery { payPalService.createOrder(amount) } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.processPayPalPayment(amount, email)

        // Then
        viewModel.paymentState.test {
            assertEquals(PaymentState.Processing, awaitItem())
            val error = awaitItem()
            assertTrue(error is PaymentState.Error)
            assertEquals(errorMessage, (error as PaymentState.Error).message)
        }
    }

    @Test
    fun `subscription updates when user is set`() = runTest {
        // Given
        val userId = "user123"
        val subscription = Subscription(userId = userId, tier = SubscriptionTier.PREMIUM)
        coEvery { repository.getActiveSubscription(userId) } returns flowOf(subscription)

        // When
        viewModel.setUser(userId)

        // Then
        viewModel.subscription.test {
            val result = awaitItem()
            assertEquals(subscription, result)
        }
    }
} 
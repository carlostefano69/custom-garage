package cg.customgarage.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cg.customgarage.data.models.*
import cg.customgarage.data.repositories.SubscriptionRepository
import cg.customgarage.payment.PayPalService
import cg.customgarage.payment.PaymentValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val repository: SubscriptionRepository,
    private val payPalService: PayPalService,
    private val paymentValidator: PaymentValidator
) : ViewModel() {

    private val _currentUserId = MutableStateFlow<String?>(null)
    
    val subscription = _currentUserId
        .filterNotNull()
        .flatMapLatest { userId ->
            repository.getActiveSubscription(userId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val paymentHistory = subscription
        .filterNotNull()
        .flatMapLatest { sub ->
            repository.getPaymentHistory(sub.id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState = _paymentState.asStateFlow()

    private val _validationState = MutableStateFlow<ValidationState>(ValidationState.Idle)
    val validationState = _validationState.asStateFlow()

    fun setUser(userId: String) {
        _currentUserId.value = userId
    }

    fun subscribe(tier: SubscriptionTier, autoRenew: Boolean = false) {
        viewModelScope.launch {
            _currentUserId.value?.let { userId ->
                repository.createSubscription(userId, tier, autoRenew)
            }
        }
    }

    fun processPayment(
        amount: Double,
        method: PaymentMethod,
        details: PaymentDetails
    ) {
        viewModelScope.launch {
            subscription.value?.let { sub ->
                repository.processPayment(sub.id, amount, method, details)
            }
        }
    }

    fun cancelSubscription() {
        viewModelScope.launch {
            subscription.value?.let { sub ->
                repository.cancelSubscription(sub.id)
            }
        }
    }

    fun validatePayPalPayment(email: String): Boolean {
        val result = paymentValidator.validatePayPalEmail(email)
        _validationState.value = when (result) {
            is ValidationResult.Success -> ValidationState.Valid
            is ValidationResult.Error -> ValidationState.Invalid(result.message)
        }
        return result is ValidationResult.Success
    }

    fun validateBankTransfer(
        accountHolder: String,
        iban: String,
        swift: String
    ): Boolean {
        val result = paymentValidator.validateBankTransfer(accountHolder, iban, swift)
        _validationState.value = when (result) {
            is ValidationResult.Success -> ValidationState.Valid
            is ValidationResult.Error -> ValidationState.Invalid(result.message)
        }
        return result is ValidationResult.Success
    }

    fun processPayPalPayment(amount: Double, email: String) {
        if (!validatePayPalPayment(email)) return

        viewModelScope.launch {
            _paymentState.value = PaymentState.Processing
            try {
                val orderResult = payPalService.createOrder(amount)
                handlePayPalOrderResult(orderResult, amount, email)
            } catch (e: Exception) {
                handlePaymentError(e)
            }
        }
    }

    private suspend fun handlePayPalOrderResult(
        orderResult: Result<String>,
        amount: Double,
        email: String
    ) {
        orderResult.fold(
            onSuccess = { orderId ->
                try {
                    val captureResult = payPalService.captureOrder(orderId)
                    handlePayPalCaptureResult(captureResult, orderId, amount, email)
                } catch (e: Exception) {
                    handlePaymentError(e)
                }
            },
            onFailure = { handlePaymentError(it) }
        )
    }

    private suspend fun handlePayPalCaptureResult(
        captureResult: Result<Boolean>,
        orderId: String,
        amount: Double,
        email: String
    ) {
        captureResult.fold(
            onSuccess = { success ->
                if (success) {
                    completePayPalPayment(orderId, amount, email)
                } else {
                    _paymentState.value = PaymentState.Error("Cattura pagamento fallita")
                }
            },
            onFailure = { handlePaymentError(it) }
        )
    }

    private suspend fun completePayPalPayment(orderId: String, amount: Double, email: String) {
        subscription.value?.let { sub ->
            try {
                processPayment(
                    amount = amount,
                    method = PaymentMethod.PAYPAL,
                    details = PaymentDetails.PayPal(
                        paypalEmail = email,
                        transactionId = orderId
                    )
                )
                _paymentState.value = PaymentState.Success
            } catch (e: Exception) {
                handlePaymentError(e)
            }
        }
    }

    private fun handlePaymentError(error: Throwable) {
        _paymentState.value = PaymentState.Error(
            error.message ?: "Si è verificato un errore durante il pagamento"
        )
    }
}

sealed class PaymentState {
    object Idle : PaymentState()
    object Processing : PaymentState()
    object Success : PaymentState()
    data class Error(val message: String) : PaymentState()
}

sealed class ValidationState {
    object Idle : ValidationState()
    object Valid : ValidationState()
    data class Invalid(val message: String) : ValidationState()
} 
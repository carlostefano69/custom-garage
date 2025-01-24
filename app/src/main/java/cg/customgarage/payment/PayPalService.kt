package cg.customgarage.payment

import android.content.Context
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import com.paypal.checkout.config.SettingsConfig
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.order.Amount
import com.paypal.checkout.order.AppContext
import com.paypal.checkout.order.Order
import com.paypal.checkout.order.PurchaseUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class PayPalService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val CLIENT_ID = "YOUR_PAYPAL_CLIENT_ID"
        private const val RETURN_URL = "cg.customgarage://paypalpay"
    }

    init {
        val config = CheckoutConfig(
            application = context.applicationContext,
            clientId = CLIENT_ID,
            environment = Environment.SANDBOX, // Cambia in LIVE per la produzione
            returnUrl = RETURN_URL,
            currencyCode = CurrencyCode.EUR,
            userAction = UserAction.PAY_NOW,
            settingsConfig = SettingsConfig(
                loggingEnabled = true
            )
        )
        PayPalCheckout.setConfig(config)
    }

    suspend fun createOrder(amount: Double): Result<String> = suspendCancellableCoroutine { continuation ->
        try {
            PayPalCheckout.createOrder(
                CreateOrder { createOrderActions ->
                    val order = Order(
                        intent = OrderIntent.CAPTURE,
                        appContext = AppContext(userAction = UserAction.PAY_NOW),
                        purchaseUnitList = listOf(
                            PurchaseUnit(
                                amount = Amount(
                                    currencyCode = CurrencyCode.EUR,
                                    value = amount.toString()
                                )
                            )
                        )
                    )
                    createOrderActions.create(order)
                },
                onSuccess = { orderId ->
                    continuation.resume(Result.success(orderId))
                },
                onError = { errorInfo ->
                    continuation.resume(Result.failure(Exception(errorInfo.error.message)))
                }
            )
        } catch (e: Exception) {
            continuation.resume(Result.failure(e))
        }
    }

    suspend fun captureOrder(orderId: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        try {
            PayPalCheckout.captureOrder(
                orderId = orderId,
                onSuccess = {
                    continuation.resume(Result.success(true))
                },
                onError = { errorInfo ->
                    continuation.resume(Result.failure(Exception(errorInfo.error.message)))
                }
            )
        } catch (e: Exception) {
            continuation.resume(Result.failure(e))
        }
    }
} 
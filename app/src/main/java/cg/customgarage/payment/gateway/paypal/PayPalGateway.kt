package cg.customgarage.payment.gateway.paypal

import com.paypal.core.PayPalHttpClient
import com.paypal.http.HttpResponse
import com.paypal.orders.*
import cg.customgarage.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class PayPalGateway(private val config: PaymentGatewayConfig) : PaymentGateway {
    private val client: PayPalHttpClient by lazy {
        val environment = when (config.environment) {
            GatewayEnvironment.SANDBOX -> com.paypal.core.SandboxEnvironment(
                config.apiKey,
                config.secretKey
            )
            GatewayEnvironment.PRODUCTION -> com.paypal.core.LiveEnvironment(
                config.apiKey,
                config.secretKey
            )
        }
        PayPalHttpClient(environment)
    }

    override suspend fun processPayment(request: PaymentRequest): PaymentGatewayResult {
        return withContext(Dispatchers.IO) {
            try {
                // Crea l'ordine PayPal
                val order = createOrder(request)
                
                // Autorizza il pagamento
                val captureRequest = OrdersCaptureRequest(order.id)
                val capture = client.execute(captureRequest)

                when (capture.result().status) {
                    "COMPLETED" -> PaymentGatewayResult.Success(
                        transactionId = capture.result().id,
                        amount = request.amount
                    )
                    else -> PaymentGatewayResult.Error(
                        code = capture.result().status,
                        message = "Pagamento PayPal non completato",
                        technical = null
                    )
                }
            } catch (e: Exception) {
                PaymentGatewayResult.Error(
                    code = "error",
                    message = "Errore durante il pagamento PayPal",
                    technical = e.message
                )
            }
        }
    }

    override suspend fun processRefund(request: RefundRequest): PaymentGatewayResult {
        return withContext(Dispatchers.IO) {
            try {
                val refundRequest = RefundsPostRequest()
                    .requestBody(buildRefundRequest(request))

                val response = client.execute(refundRequest)

                when (response.result().status) {
                    "COMPLETED" -> PaymentGatewayResult.Success(
                        transactionId = response.result().id,
                        amount = request.amount
                    )
                    else -> PaymentGatewayResult.Error(
                        code = response.result().status,
                        message = "Rimborso PayPal non completato",
                        technical = null
                    )
                }
            } catch (e: Exception) {
                PaymentGatewayResult.Error(
                    code = "error",
                    message = "Errore durante il rimborso PayPal",
                    technical = e.message
                )
            }
        }
    }

    private suspend fun createOrder(request: PaymentRequest): Order {
        val orderRequest = OrdersCreateRequest()
        orderRequest.requestBody(buildOrderRequest(request))
        
        val response: HttpResponse<Order> = client.execute(orderRequest)
        return response.result()
    }

    private fun buildOrderRequest(request: PaymentRequest): OrderRequest {
        val orderItems = listOf(
            Item().apply {
                name = "Subscription"
                quantity = "1"
                unitAmount = Money().apply {
                    currencyCode = request.currency
                    value = request.amount.toString()
                }
            }
        )

        return OrderRequest().apply {
            checkoutPaymentIntent("CAPTURE")
            purchaseUnits = listOf(
                PurchaseUnitRequest().apply {
                    amountWithBreakdown(AmountWithBreakdown().apply {
                        currencyCode = request.currency
                        value = request.amount.toString()
                    })
                    items = orderItems
                }
            )
        }
    }

    private fun buildRefundRequest(request: RefundRequest): RefundRequest {
        return RefundRequest().apply {
            amount = Money().apply {
                currencyCode = "EUR"
                value = request.amount.toString()
            }
            reason = when (request.reason) {
                RefundReason.CUSTOMER_REQUEST -> "BUYER_REQUESTED"
                RefundReason.DUPLICATE_PAYMENT -> "DUPLICATE_TRANSACTION"
                else -> "OTHER"
            }
        }
    }

    override suspend fun validatePaymentMethod(
        method: PaymentMethod,
        details: PaymentDetails
    ): Boolean {
        if (method != PaymentMethod.PAYPAL) return false
        
        return when (details) {
            is PaymentDetails.PayPal -> {
                details.email.matches(Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"))
            }
            else -> false
        }
    }
} 
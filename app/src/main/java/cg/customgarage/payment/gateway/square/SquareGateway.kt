package cg.customgarage.payment.gateway.square

import com.squareup.square.SquareClient
import com.squareup.square.models.*
import cg.customgarage.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class SquareGateway(private val config: PaymentGatewayConfig) : PaymentGateway {
    private val client: SquareClient by lazy {
        SquareClient.Builder()
            .accessToken(config.apiKey)
            .environment(when (config.environment) {
                GatewayEnvironment.SANDBOX -> com.squareup.square.Environment.SANDBOX
                GatewayEnvironment.PRODUCTION -> com.squareup.square.Environment.PRODUCTION
            })
            .build()
    }

    override suspend fun processPayment(request: PaymentRequest): PaymentGatewayResult {
        return withContext(Dispatchers.IO) {
            try {
                val amountMoney = Money.Builder()
                    .amount((request.amount * 100).toLong())
                    .currency(request.currency)
                    .build()

                val paymentRequest = CreatePaymentRequest.Builder(
                    sourceId = getSquarePaymentSource(request.method, request.details),
                    amountMoney = amountMoney
                )
                    .idempotencyKey(UUID.randomUUID().toString())
                    .build()

                val response = client.paymentsApi.createPayment(paymentRequest)

                if (response.payment.status == "COMPLETED") {
                    PaymentGatewayResult.Success(
                        transactionId = response.payment.id,
                        amount = request.amount
                    )
                } else {
                    PaymentGatewayResult.Error(
                        code = response.payment.status,
                        message = "Pagamento Square non completato",
                        technical = null
                    )
                }
            } catch (e: Exception) {
                PaymentGatewayResult.Error(
                    code = "error",
                    message = "Errore durante il pagamento Square",
                    technical = e.message
                )
            }
        }
    }

    override suspend fun processRefund(request: RefundRequest): PaymentGatewayResult {
        return withContext(Dispatchers.IO) {
            try {
                val amountMoney = Money.Builder()
                    .amount((request.amount * 100).toLong())
                    .currency("EUR")
                    .build()

                val refundRequest = RefundPaymentRequest.Builder(
                    paymentId = request.transactionId,
                    amountMoney = amountMoney,
                    idempotencyKey = UUID.randomUUID().toString()
                ).build()

                val response = client.refundsApi.refundPayment(refundRequest)

                if (response.refund.status == "COMPLETED") {
                    PaymentGatewayResult.Success(
                        transactionId = response.refund.id,
                        amount = request.amount
                    )
                } else {
                    PaymentGatewayResult.Error(
                        code = response.refund.status,
                        message = "Rimborso Square non completato",
                        technical = null
                    )
                }
            } catch (e: Exception) {
                PaymentGatewayResult.Error(
                    code = "error",
                    message = "Errore durante il rimborso Square",
                    technical = e.message
                )
            }
        }
    }

    private fun getSquarePaymentSource(
        method: PaymentMethod,
        details: PaymentDetails
    ): String {
        // Implementazione della conversione del metodo di pagamento nel formato Square
        return when (method) {
            PaymentMethod.PAYPAL -> throw IllegalArgumentException("PayPal non supportato da Square")
            PaymentMethod.BANK_TRANSFER -> "cnon" // Card nonce per Square
        }
    }

    override suspend fun validatePaymentMethod(
        method: PaymentMethod,
        details: PaymentDetails
    ): Boolean {
        // Implementa la validazione specifica per Square
        return method == PaymentMethod.BANK_TRANSFER
    }
} 
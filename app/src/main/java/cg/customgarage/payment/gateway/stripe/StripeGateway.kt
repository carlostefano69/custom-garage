package cg.customgarage.payment.gateway.stripe

import com.stripe.Stripe
import com.stripe.model.PaymentIntent
import com.stripe.model.Refund
import com.stripe.param.PaymentIntentCreateParams
import com.stripe.param.RefundCreateParams
import cg.customgarage.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StripeGateway(private val config: PaymentGatewayConfig) : PaymentGateway {
    init {
        Stripe.apiKey = config.secretKey
    }

    override suspend fun processPayment(request: PaymentRequest): PaymentGatewayResult {
        return withContext(Dispatchers.IO) {
            try {
                val params = PaymentIntentCreateParams.builder()
                    .setAmount((request.amount * 100).toLong()) // Stripe usa i centesimi
                    .setCurrency(request.currency.lowercase())
                    .setPaymentMethod(getStripePaymentMethod(request.method, request.details))
                    .setConfirm(true)
                    .build()

                val paymentIntent = PaymentIntent.create(params)

                when (paymentIntent.status) {
                    "succeeded" -> PaymentGatewayResult.Success(
                        transactionId = paymentIntent.id,
                        amount = request.amount
                    )
                    else -> PaymentGatewayResult.Error(
                        code = paymentIntent.status,
                        message = "Pagamento non riuscito",
                        technical = paymentIntent.lastPaymentError?.message
                    )
                }
            } catch (e: Exception) {
                PaymentGatewayResult.Error(
                    code = "error",
                    message = "Errore durante il pagamento",
                    technical = e.message
                )
            }
        }
    }

    override suspend fun processRefund(request: RefundRequest): PaymentGatewayResult {
        return withContext(Dispatchers.IO) {
            try {
                val params = RefundCreateParams.builder()
                    .setPaymentIntent(request.transactionId)
                    .setAmount((request.amount * 100).toLong())
                    .setReason(getStripeRefundReason(request.reason))
                    .build()

                val refund = Refund.create(params)

                when (refund.status) {
                    "succeeded" -> PaymentGatewayResult.Success(
                        transactionId = refund.id,
                        amount = request.amount
                    )
                    else -> PaymentGatewayResult.Error(
                        code = refund.status,
                        message = "Rimborso non riuscito",
                        technical = refund.failureReason
                    )
                }
            } catch (e: Exception) {
                PaymentGatewayResult.Error(
                    code = "error",
                    message = "Errore durante il rimborso",
                    technical = e.message
                )
            }
        }
    }

    override suspend fun validatePaymentMethod(
        method: PaymentMethod,
        details: PaymentDetails
    ): Boolean {
        // Implementa la validazione specifica per Stripe
        return true // Per ora ritorna sempre true
    }

    private fun getStripePaymentMethod(
        method: PaymentMethod,
        details: PaymentDetails
    ): String {
        // Converti il metodo di pagamento nel formato Stripe
        return when (method) {
            PaymentMethod.PAYPAL -> "pm_card_visa" // Placeholder
            PaymentMethod.BANK_TRANSFER -> "pm_sepa_debit"
        }
    }

    private fun getStripeRefundReason(reason: RefundReason): String {
        return when (reason) {
            RefundReason.CUSTOMER_REQUEST -> "requested_by_customer"
            RefundReason.DUPLICATE_PAYMENT -> "duplicate"
            RefundReason.FRAUDULENT_CHARGE -> "fraudulent"
            else -> "other"
        }
    }
} 
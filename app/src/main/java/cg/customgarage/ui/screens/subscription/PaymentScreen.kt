package cg.customgarage.ui.screens.subscription

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cg.customgarage.R
import cg.customgarage.data.models.*
import cg.customgarage.ui.viewmodels.SubscriptionViewModel

@Composable
fun PaymentScreen(
    tier: SubscriptionTier,
    viewModel: SubscriptionViewModel = hiltViewModel(),
    onPaymentComplete: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var paypalEmail by remember { mutableStateOf("") }
    var bankAccountHolder by remember { mutableStateOf("") }
    var bankIban by remember { mutableStateOf("") }
    var bankSwift by remember { mutableStateOf("") }

    val amount = when (tier) {
        SubscriptionTier.FREE -> 0.0
        SubscriptionTier.PREMIUM -> 9.99
        SubscriptionTier.PRO -> 19.99
    }

    val paymentState by viewModel.paymentState.collectAsState()
    val validationState by viewModel.validationState.collectAsState()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(paymentState) {
        when (paymentState) {
            is PaymentState.Success -> {
                onPaymentComplete()
            }
            is PaymentState.Error -> {
                errorMessage = (paymentState as PaymentState.Error).message
            }
            else -> {}
        }
    }

    LaunchedEffect(validationState) {
        when (validationState) {
            is ValidationState.Invalid -> {
                errorMessage = (validationState as ValidationState.Invalid).message
            }
            else -> errorMessage = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.payment_method),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Importo
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(
                        when (tier) {
                            SubscriptionTier.FREE -> R.string.tier_free
                            SubscriptionTier.PREMIUM -> R.string.tier_premium
                            SubscriptionTier.PRO -> R.string.tier_pro
                        }
                    ),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.payment_amount, amount),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Metodi di pagamento
        PaymentMethodSelection(
            selectedMethod = selectedMethod,
            onMethodSelected = { selectedMethod = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Form specifico per il metodo selezionato
        when (selectedMethod) {
            PaymentMethod.PAYPAL -> PayPalForm(
                email = paypalEmail,
                onEmailChange = { paypalEmail = it }
            )
            PaymentMethod.BANK_TRANSFER -> BankTransferForm(
                accountHolder = bankAccountHolder,
                iban = bankIban,
                swift = bankSwift,
                onAccountHolderChange = { bankAccountHolder = it },
                onIbanChange = { bankIban = it },
                onSwiftChange = { bankSwift = it }
            )
            null -> {}
        }

        Spacer(modifier = Modifier.weight(1f))

        // Mostra errori
        errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Bottoni azione
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = onNavigateBack) {
                Text(stringResource(R.string.back))
            }

            Button(
                onClick = {
                    when (selectedMethod) {
                        PaymentMethod.PAYPAL -> {
                            if (viewModel.validatePayPalPayment(paypalEmail)) {
                                viewModel.processPayPalPayment(amount, paypalEmail)
                            }
                        }
                        PaymentMethod.BANK_TRANSFER -> {
                            if (viewModel.validateBankTransfer(
                                bankAccountHolder,
                                bankIban,
                                bankSwift
                            )) {
                                viewModel.processPayment(
                                    amount = amount,
                                    method = PaymentMethod.BANK_TRANSFER,
                                    details = PaymentDetails.BankTransfer(
                                        accountHolder = bankAccountHolder,
                                        iban = bankIban,
                                        swift = bankSwift,
                                        transferId = null
                                    )
                                )
                            }
                        }
                        null -> return@Button
                    }
                },
                enabled = when (selectedMethod) {
                    PaymentMethod.PAYPAL -> paypalEmail.isNotEmpty()
                    PaymentMethod.BANK_TRANSFER -> {
                        bankAccountHolder.isNotEmpty() &&
                        bankIban.isNotEmpty() &&
                        bankSwift.isNotEmpty()
                    }
                    null -> false
                }
            ) {
                if (paymentState is PaymentState.Processing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.confirm_payment))
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodSelection(
    selectedMethod: PaymentMethod?,
    onMethodSelected: (PaymentMethod) -> Unit
) {
    Column {
        PaymentMethod.values().forEach { method ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RadioButton(
                    selected = selectedMethod == method,
                    onClick = { onMethodSelected(method) }
                )
                Text(
                    text = stringResource(
                        when (method) {
                            PaymentMethod.PAYPAL -> R.string.payment_method_paypal
                            PaymentMethod.BANK_TRANSFER -> R.string.payment_method_bank
                        }
                    )
                )
            }
        }
    }
}

@Composable
private fun PayPalForm(
    email: String,
    onEmailChange: (String) -> Unit
) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("PayPal Email") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun BankTransferForm(
    accountHolder: String,
    iban: String,
    swift: String,
    onAccountHolderChange: (String) -> Unit,
    onIbanChange: (String) -> Unit,
    onSwiftChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = accountHolder,
            onValueChange = onAccountHolderChange,
            label = { Text("Account Holder") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = iban,
            onValueChange = onIbanChange,
            label = { Text("IBAN") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = swift,
            onValueChange = onSwiftChange,
            label = { Text("SWIFT/BIC") },
            modifier = Modifier.fillMaxWidth()
        )
    }
} 
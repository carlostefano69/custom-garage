package cg.customgarage.ui.screens.subscription

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cg.customgarage.R
import cg.customgarage.data.models.Payment
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PaymentHistorySection(
    payments: List<Payment>
) {
    Column {
        Text(
            text = stringResource(R.string.payment_history),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        payments.forEach { payment ->
            PaymentHistoryItem(payment = payment)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PaymentHistoryItem(
    payment: Payment
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.payment_amount, payment.amount),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(R.string.payment_status, payment.status.name),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(
                    R.string.payment_date,
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(Date(payment.transactionDate))
                ),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
} 
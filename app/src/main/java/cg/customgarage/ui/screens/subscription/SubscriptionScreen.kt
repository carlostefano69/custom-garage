package cg.customgarage.ui.screens.subscription

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cg.customgarage.R
import cg.customgarage.data.models.*
import cg.customgarage.ui.viewmodels.SubscriptionViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SubscriptionScreen(
    viewModel: SubscriptionViewModel = hiltViewModel(),
    onNavigateToPayment: (SubscriptionTier) -> Unit
) {
    val subscription by viewModel.subscription.collectAsState()
    val paymentHistory by viewModel.paymentHistory.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.subscription_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stato abbonamento corrente
        subscription?.let { sub ->
            CurrentSubscriptionCard(
                subscription = sub,
                onCancelClick = { viewModel.cancelSubscription() }
            )
        } ?: SubscriptionTiersSection(
            onSubscribeClick = { tier ->
                onNavigateToPayment(tier)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Storico pagamenti
        if (paymentHistory.isNotEmpty()) {
            PaymentHistorySection(payments = paymentHistory)
        }
    }
}

@Composable
private fun CurrentSubscriptionCard(
    subscription: Subscription,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(
                    when (subscription.tier) {
                        SubscriptionTier.FREE -> R.string.tier_free
                        SubscriptionTier.PREMIUM -> R.string.tier_premium
                        SubscriptionTier.PRO -> R.string.tier_pro
                    }
                ),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(
                    R.string.subscription_status,
                    if (subscription.isActive) 
                        stringResource(R.string.subscription_active)
                    else 
                        stringResource(R.string.subscription_inactive)
                )
            )

            subscription.endDate?.let { endDate ->
                Text(
                    text = stringResource(
                        R.string.subscription_expires,
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(endDate))
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCancelClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.cancel_subscription))
            }
        }
    }
}

@Composable
private fun SubscriptionTiersSection(
    onSubscribeClick: (SubscriptionTier) -> Unit
) {
    Column {
        SubscriptionTier.values().forEach { tier ->
            SubscriptionTierCard(
                tier = tier,
                onClick = { onSubscribeClick(tier) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SubscriptionTierCard(
    tier: SubscriptionTier,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
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
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = stringResource(
                    when (tier) {
                        SubscriptionTier.FREE -> R.string.tier_free_description
                        SubscriptionTier.PREMIUM -> R.string.tier_premium_description
                        SubscriptionTier.PRO -> R.string.tier_pro_description
                    }
                )
            )
        }
    }
} 
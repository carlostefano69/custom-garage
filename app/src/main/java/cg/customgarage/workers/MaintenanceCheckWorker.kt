package cg.customgarage.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import cg.customgarage.data.repositories.VehicleRepository
import cg.customgarage.notifications.NotificationService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class MaintenanceCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val vehicleRepository: VehicleRepository,
    private val notificationService: NotificationService
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val vehicles = vehicleRepository.getAllVehicles().first()
        
        vehicles.forEach { vehicle ->
            vehicle.scheduledMaintenances
                .filter { maintenance ->
                    maintenance.nextDueDate?.let { it <= System.currentTimeMillis() + NOTIFICATION_THRESHOLD } ?: false
                }
                .forEach { maintenance ->
                    notificationService.showMaintenanceNotification(
                        vehicleName = vehicle.name,
                        maintenanceDescription = maintenance.description
                    )
                }
        }

        return Result.success()
    }

    companion object {
        private const val NOTIFICATION_THRESHOLD = 7 * 24 * 60 * 60 * 1000L // 7 giorni in millisecondi
        private const val WORK_NAME = "maintenance_check"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val request = PeriodicWorkRequestBuilder<MaintenanceCheckWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }
    }
} 
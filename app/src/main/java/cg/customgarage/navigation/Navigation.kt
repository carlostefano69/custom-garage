package cg.customgarage.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Projects : Screen("projects")
    object ProjectDetail : Screen("project/{projectId}") {
        fun createRoute(projectId: String) = "project/$projectId"
    }
    object Gallery : Screen("gallery")
    object AddProject : Screen("add_project")
    object Settings : Screen("settings")
    
    // Nuove rotte per i veicoli
    object Vehicles : Screen("vehicles")
    object AddVehicle : Screen("add_vehicle")
    object VehicleDetail : Screen("vehicle/{vehicleId}") {
        fun createRoute(vehicleId: String) = "vehicle/$vehicleId"
    }
    object AddMaintenance : Screen("vehicle/{vehicleId}/add_maintenance") {
        fun createRoute(vehicleId: String) = "vehicle/$vehicleId/add_maintenance"
    }
} 
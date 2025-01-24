package cg.customgarage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navType.navArgument
import cg.customgarage.navigation.Screen
import cg.customgarage.ui.theme.CustomGarageTheme
import cg.customgarage.ui.screens.HomeScreen
import cg.customgarage.ui.screens.ProjectsScreen
import cg.customgarage.ui.screens.AddProjectScreen
import cg.customgarage.ui.screens.GalleryScreen
import cg.customgarage.ui.screens.vehicle.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomGarageTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.Home, contentDescription = stringResource(R.string.nav_home)) },
                                label = { Text(stringResource(R.string.nav_home)) },
                                selected = currentRoute == Screen.Home.route,
                                onClick = { navController.navigate(Screen.Home.route) }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.List, contentDescription = stringResource(R.string.nav_projects)) },
                                label = { Text(stringResource(R.string.nav_projects)) },
                                selected = currentRoute == Screen.Projects.route,
                                onClick = { navController.navigate(Screen.Projects.route) }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.nav_add)) },
                                label = { Text(stringResource(R.string.nav_add)) },
                                selected = currentRoute == Screen.AddProject.route,
                                onClick = { navController.navigate(Screen.AddProject.route) }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.Image, contentDescription = stringResource(R.string.nav_gallery)) },
                                label = { Text(stringResource(R.string.nav_gallery)) },
                                selected = currentRoute == Screen.Gallery.route,
                                onClick = { navController.navigate(Screen.Gallery.route) }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.DirectionsCar, contentDescription = stringResource(R.string.nav_vehicles)) },
                                label = { Text(stringResource(R.string.nav_vehicles)) },
                                selected = currentRoute == Screen.Vehicles.route,
                                onClick = { navController.navigate(Screen.Vehicles.route) }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                onProjectClick = { projectId ->
                                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                                }
                            )
                        }
                        composable(Screen.Projects.route) {
                            ProjectsScreen(
                                onProjectClick = { projectId ->
                                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                                }
                            )
                        }
                        composable(Screen.AddProject.route) {
                            AddProjectScreen(
                                onProjectCreated = {
                                    navController.navigate(Screen.Projects.route)
                                }
                            )
                        }
                        composable(Screen.Gallery.route) {
                            GalleryScreen(
                                onImageClick = { /* TODO: Gestire click immagine */ }
                            )
                        }
                        composable(Screen.ProjectDetail.route) {
                            // TODO: Implementare ProjectDetailScreen
                        }
                        composable(Screen.Vehicles.route) {
                            VehicleListScreen(
                                onVehicleClick = { vehicleId ->
                                    navController.navigate(Screen.VehicleDetail.createRoute(vehicleId))
                                },
                                onAddVehicleClick = {
                                    navController.navigate(Screen.AddVehicle.route)
                                }
                            )
                        }
                        composable(Screen.AddVehicle.route) {
                            AddVehicleScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(
                            route = Screen.VehicleDetail.route,
                            arguments = listOf(
                                navArgument("vehicleId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: return@composable
                            VehicleMaintenanceScreen(
                                vehicleId = vehicleId,
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(
                            route = Screen.AddMaintenance.route,
                            arguments = listOf(
                                navArgument("vehicleId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: return@composable
                            AddMaintenanceScreen(
                                vehicleId = vehicleId,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CustomGarageTheme {
        Greeting("Android")
    }
}
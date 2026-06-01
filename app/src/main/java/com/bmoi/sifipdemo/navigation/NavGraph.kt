package com.bmoi.sifipdemo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bmoi.sifipdemo.BmoiApplication
import com.bmoi.sifipdemo.DashboardViewModelFactory
import com.bmoi.sifipdemo.LoginViewModelFactory
import com.bmoi.sifipdemo.TransferViewModelFactory
import com.bmoi.sifipdemo.ui.dashboard.DashboardScreen
import com.bmoi.sifipdemo.ui.dashboard.DashboardViewModel
import com.bmoi.sifipdemo.ui.login.LoginScreen
import com.bmoi.sifipdemo.ui.login.LoginViewModel
import com.bmoi.sifipdemo.ui.splash.SplashScreen
import com.bmoi.sifipdemo.ui.transfer.TransferScreen
import com.bmoi.sifipdemo.ui.transfer.TransferViewModel

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val TRANSFER = "transfer"
}

@Composable
fun BmoiNavGraph() {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as BmoiApplication

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(onTimeout = {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }
        composable(Routes.LOGIN) {
            val vm: LoginViewModel = viewModel(factory = LoginViewModelFactory(app))
            LoginScreen(
                viewModel = vm,
                onAuthenticated = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.DASHBOARD) {
            val vm: DashboardViewModel = viewModel(factory = DashboardViewModelFactory(app))
            DashboardScreen(
                viewModel = vm,
                onTransferClicked = { navController.navigate(Routes.TRANSFER) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.TRANSFER) {
            val vm: TransferViewModel = viewModel(
                factory = TransferViewModelFactory(
                    app = app,
                    msisdn = "+261321234567",
                ),
            )
            TransferScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
            )
        }
    }
}

package com.bmoi.sifipdemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bmoi.sifipdemo.ui.dashboard.DashboardViewModel
import com.bmoi.sifipdemo.ui.login.LoginViewModel
import com.bmoi.sifipdemo.ui.transfer.TransferViewModel

/**
 * Light, hand-rolled factories — avoids pulling Hilt/Koin in for a 4-screen
 * demo. The DI surface is the [BmoiApplication] composition root.
 */

class LoginViewModelFactory(private val app: BmoiApplication) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(
            sifipApi = app.sifipMock,
            mockService = app.sifipMock,
        ) as T
    }
}

class DashboardViewModelFactory(private val app: BmoiApplication) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DashboardViewModel(app.bankRepository) as T
    }
}

class TransferViewModelFactory(
    private val app: BmoiApplication,
    private val msisdn: String,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TransferViewModel(
            sifipApi = app.sifipMock,
            userMsisdn = msisdn,
        ) as T
    }
}

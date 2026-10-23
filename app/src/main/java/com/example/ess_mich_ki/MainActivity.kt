package com.example.ess_mich_ki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.ess_mich_ki.ui.dashboard.DashboardViewModel
import com.example.ess_mich_ki.ui.navigation.AppNavigation
import com.example.ess_mich_ki.ui.theme.EssmichkiTheme

class MainActivity : ComponentActivity() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        dashboardViewModel = ViewModelProvider(
            this,
            DashboardViewModel.Factory(applicationContext)
        )[DashboardViewModel::class.java]

        setContent {
            EssmichkiTheme {
                AppNavigation(dashboardViewModel = dashboardViewModel)
            }
        }
    }
}

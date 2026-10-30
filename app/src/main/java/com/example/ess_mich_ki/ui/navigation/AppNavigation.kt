package com.example.ess_mich_ki.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ess_mich_ki.ui.dashboard.DashboardScreen
import com.example.ess_mich_ki.ui.dashboard.DashboardViewModel
import com.example.ess_mich_ki.ui.detail.FoodDetailScreen
import com.example.ess_mich_ki.ui.detail.FoodDetailViewModel
import com.example.ess_mich_ki.ui.progress.ProgressScreen
import com.example.ess_mich_ki.ui.progress.ProgressViewModel
import com.example.ess_mich_ki.ui.streaks.StreaksScreen
import com.example.ess_mich_ki.ui.theme.NothingRed

enum class NavTab {
    PLAN,
    PROGRESS
}

@Composable
fun AppNavigation(
    dashboardViewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var currentTab by remember { mutableStateOf(NavTab.PLAN) }
    var selectedFoodIdForDetail by remember { mutableLongStateOf(0L) }
    var isStreaksVisible by remember { mutableStateOf(false) }

    val progressViewModel: ProgressViewModel = viewModel(
        factory = ProgressViewModel.Factory(context)
    )

    val dashboardUiState by dashboardViewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (selectedFoodIdForDetail == 0L && !isStreaksVisible) {
                Surface(
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp
                    ) {
                        NavigationBarItem(
                            selected = currentTab == NavTab.PLAN,
                            onClick = { currentTab = NavTab.PLAN },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Plan") },
                            label = {
                                Text(
                                    text = "PLAN",
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 0.8.sp,
                                    fontWeight = if (currentTab == NavTab.PLAN) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                indicatorColor = NothingRed,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        NavigationBarItem(
                            selected = currentTab == NavTab.PROGRESS,
                            onClick = { currentTab = NavTab.PROGRESS },
                            icon = { Icon(Icons.Default.BarChart, contentDescription = "Fortschritt") },
                            label = {
                                Text(
                                    text = "FORTSCHRITT",
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 0.8.sp,
                                    fontWeight = if (currentTab == NavTab.PROGRESS) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                indicatorColor = NothingRed,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (isStreaksVisible) {
            StreaksScreen(
                currentLoggedStreak = dashboardUiState.currentLoggedStreak,
                bestLoggedStreak = dashboardUiState.bestLoggedStreak,
                currentPerfectStreak = dashboardUiState.currentPerfectStreak,
                bestPerfectStreak = dashboardUiState.bestPerfectStreak,
                monthlyCalendarData = dashboardUiState.monthlyCalendarData,
                onCloseClick = {
                    isStreaksVisible = false
                },
                modifier = Modifier.padding(innerPadding)
            )
        } else if (selectedFoodIdForDetail > 0L) {
            val foodDetailViewModel: FoodDetailViewModel = viewModel(
                key = "food_detail_$selectedFoodIdForDetail",
                factory = FoodDetailViewModel.Factory(selectedFoodIdForDetail, context)
            )

            FoodDetailScreen(
                viewModel = foodDetailViewModel,
                onBackClick = {
                    selectedFoodIdForDetail = 0L
                },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            when (currentTab) {
                NavTab.PLAN -> {
                    DashboardScreen(
                        viewModel = dashboardViewModel,
                        onStreakClick = {
                            isStreaksVisible = true
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                NavTab.PROGRESS -> {
                    ProgressScreen(
                        viewModel = progressViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

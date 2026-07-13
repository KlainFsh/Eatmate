package com.example.eatmate.navigation

import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.eatmate.ui.camera.CameraScreen
import com.example.eatmate.ui.diary.DiaryScreen
import com.example.eatmate.ui.home.HomeScreen
import com.example.eatmate.ui.profile.ProfileScreen
import com.example.eatmate.ui.result.ResultScreen
import com.example.eatmate.ui.theme.BrandOrange
import com.example.eatmate.ui.theme.SurfaceLight
import com.example.eatmate.ui.trends.TrendsScreen

private data class NavTab(
    val screen: Screen,
    val label: String
)

@Composable
fun EatmateNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // 4 normal tabs + camera center button
    val leftTabs = listOf(
        NavTab(Screen.Home, "首页"),
        NavTab(Screen.Diary, "日记")
    )
    val rightTabs = listOf(
        NavTab(Screen.Trends, "趋势"),
        NavTab(Screen.Profile, "我的")
    )

    // Only show bottom bar on main tabs, not on camera/result
    val allMainRoutes = (leftTabs + rightTabs).map { it.screen.route }
    val showBottomBar = currentDestination?.hierarchy?.any {
        it.route in allMainRoutes
    } == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Left: Home, Diary
                            leftTabs.forEach { tab ->
                                val selected = currentDestination?.hierarchy?.any {
                                    it.route == tab.screen.route
                                } == true

                                NavigationBarItem(
                                    selected = selected,
                                    onClick = {
                                        navController.navigate(tab.screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) tab.screen.selectedIcon
                                                else tab.screen.unselectedIcon,
                                            contentDescription = tab.label
                                        )
                                    },
                                    label = { Text(tab.label) },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent
                                    )
                                )
                            }

                            // Center: Camera button (floating above the bar)
                            Spacer(modifier = Modifier.weight(0.2f))
                            CameraNavButton(
                                onClick = { navController.navigate(Screen.Camera.route) }
                            )
                            Spacer(modifier = Modifier.weight(0.2f))

                            // Right: Trends, Profile
                            rightTabs.forEach { tab ->
                                val selected = currentDestination?.hierarchy?.any {
                                    it.route == tab.screen.route
                                } == true

                                NavigationBarItem(
                                    selected = selected,
                                    onClick = {
                                        navController.navigate(tab.screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) tab.screen.selectedIcon
                                                else tab.screen.unselectedIcon,
                                            contentDescription = tab.label
                                        )
                                    },
                                    label = { Text(tab.label) },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }

                    // Camera button is inline in the nav bar row above
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(onNavigateToCamera = {
                    navController.navigate(Screen.Camera.route)
                })
            }
            composable(Screen.Diary.route) {
                DiaryScreen()
            }
            composable(Screen.Camera.route) {
                CameraScreen(
                    onNavigateToResult = { imagePath ->
                        navController.navigate(Screen.Result.createRoute(imagePath))
                    }
                )
            }
            composable(Screen.Trends.route) {
                TrendsScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
            composable(Screen.Result.route) { backStackEntry ->
                val encoded = backStackEntry.arguments?.getString("imagePath") ?: ""
                val imagePath = Uri.decode(encoded)
                ResultScreen(
                    imagePath = imagePath,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun CameraNavButton(
    onClick: () -> Unit,
    isProminent: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .then(
                if (isProminent) Modifier
                    .size(56.dp)
                    .shadow(8.dp, CircleShape, ambientColor = BrandOrange, spotColor = BrandOrange)
                else Modifier.size(44.dp)
            )
            .clip(CircleShape)
            .background(BrandOrange)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.CameraAlt,
            contentDescription = "拍照",
            tint = Color.White,
            modifier = Modifier.size(if (isProminent) 28.dp else 22.dp)
        )
    }
}

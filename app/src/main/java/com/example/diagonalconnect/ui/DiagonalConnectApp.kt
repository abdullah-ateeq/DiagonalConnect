package com.example.diagonalconnect.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.diagonalconnect.ui.screens.AboutScreen
import com.example.diagonalconnect.ui.screens.GameScreen
import com.example.diagonalconnect.ui.screens.HomeScreen
import com.example.diagonalconnect.ui.screens.HowToPlayScreen
import com.example.diagonalconnect.ui.screens.ResultScreen
import com.example.diagonalconnect.ui.screens.SettingsScreen
import com.example.diagonalconnect.ui.screens.SplashScreen

private object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val GAME = "game"
    const val RESULT = "result"
    const val HOW_TO_PLAY = "how_to_play"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
}

/** Ignores repeated taps while a navigation transition is already under way. */
private fun NavBackStackEntry.isResumed(): Boolean =
    lifecycle.currentState == Lifecycle.State.RESUMED

private fun NavHostController.navigateFrom(entry: NavBackStackEntry, route: String) {
    if (entry.isResumed()) navigate(route) { launchSingleTop = true }
}

private fun NavHostController.popFrom(entry: NavBackStackEntry) {
    if (entry.isResumed()) popBackStack()
}

@Composable
fun DiagonalConnectApp(
    gameViewModel: GameViewModel,
    settingsViewModel: SettingsViewModel,
) {
    val navController = rememberNavController()
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
    val stats by settingsViewModel.stats.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = { fadeIn(tween(240)) },
        exitTransition = { fadeOut(tween(180)) },
        popEnterTransition = { fadeIn(tween(240)) },
        popExitTransition = { fadeOut(tween(180)) },
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(onFinished = {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }
        composable(Routes.HOME) { entry ->
            HomeScreen(
                stats = stats,
                onPlay = {
                    if (entry.isResumed()) {
                        gameViewModel.newMatch()
                        navController.navigateFrom(entry, Routes.GAME)
                    }
                },
                onHowToPlay = { navController.navigateFrom(entry, Routes.HOW_TO_PLAY) },
                onSettings = { navController.navigateFrom(entry, Routes.SETTINGS) },
                onAbout = { navController.navigateFrom(entry, Routes.ABOUT) },
            )
        }
        composable(Routes.GAME) { entry ->
            GameScreen(
                viewModel = gameViewModel,
                settings = settings,
                onMenu = { navController.popFrom(entry) },
                onShowResult = {
                    navController.navigate(Routes.RESULT) {
                        popUpTo(Routes.GAME) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.RESULT) { entry ->
            val summary by gameViewModel.summary.collectAsStateWithLifecycle()
            ResultScreen(
                summary = summary,
                showLastMove = settings.showLastMove,
                onPlayAgain = {
                    if (entry.isResumed()) {
                        gameViewModel.newMatch()
                        navController.navigate(Routes.GAME) {
                            popUpTo(Routes.HOME)
                        }
                    }
                },
                onHome = {
                    if (entry.isResumed()) navController.popBackStack(Routes.HOME, inclusive = false)
                },
            )
        }
        composable(Routes.HOW_TO_PLAY) { entry ->
            HowToPlayScreen(onBack = { navController.popFrom(entry) })
        }
        composable(Routes.SETTINGS) { entry ->
            SettingsScreen(
                settings = settings,
                stats = stats,
                viewModel = settingsViewModel,
                onBack = { navController.popFrom(entry) },
            )
        }
        composable(Routes.ABOUT) { entry ->
            AboutScreen(onBack = { navController.popFrom(entry) })
        }
    }
}

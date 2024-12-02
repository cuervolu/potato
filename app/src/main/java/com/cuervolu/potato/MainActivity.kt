package com.cuervolu.potato

import SideSheet
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cuervolu.potato.data.preferences.UserPreferencesManager
import com.cuervolu.potato.ui.components.CustomTopAppBar
import com.cuervolu.potato.ui.screens.characters.CharactersScreen
import com.cuervolu.potato.ui.screens.home.HomeScreen
import com.cuervolu.potato.ui.screens.notes.NoteEditorScreen
import com.cuervolu.potato.ui.screens.settings.SettingsScreen
import com.cuervolu.potato.ui.theme.PotatoTheme
import com.cuervolu.potato.ui.theme.rememberThemeState
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val preferencesManager: UserPreferencesManager by inject()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme = rememberThemeState(preferencesManager)

            PotatoTheme(
                darkTheme = isDarkTheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PotatoApp()
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PotatoApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isNoteEditor = currentRoute?.startsWith("note_editor") == true
    SideSheet(navController = navController) { openDrawer ->
        Scaffold(
            topBar = {
                if (!isNoteEditor) {
                    when (currentRoute) {
                        Screen.Home.route -> CustomTopAppBar(
                            title =  stringResource(R.string.home),
                            showMenuIcon = true,
                            openDrawer = openDrawer,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                        Screen.Characters.route -> CustomTopAppBar(
                            title =  stringResource(R.string.characters),
                            showMenuIcon = true,
                            openDrawer = openDrawer,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                        Screen.Settings.route -> CustomTopAppBar(
                            title = stringResource(R.string.settings),
                            showMenuIcon = true,
                            openDrawer = openDrawer,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(
                    if (isNoteEditor) PaddingValues(0.dp) else innerPadding
                )
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        onNoteClick = { noteId ->
                            navController.navigate("note_editor/$noteId")
                        },
                        onNewNoteClick = {
                            navController.navigate("note_editor/-1")
                        },
                    )
                }
                composable(Screen.Characters.route) {
                    CharactersScreen(
                        onCharacterClick = { /* TODO: Implement character details navigation */ },
                        onNewCharacterClick = { /* TODO: Implement new character creation */ },
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen()
                }
                composable(
                    route = "note_editor/{noteId}",
                    arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
                    NoteEditorScreen(
                        noteId = if (noteId != -1) noteId else null,
                        onBackClick = { navController.navigateUp() },
                    )
                }
            }
        }
    }
}

sealed class Screen(val route: String, val resourceId: Int, val icon: ImageVector) {
    data object Home : Screen("home", R.string.home, Icons.Filled.Home)
    data object Characters : Screen("characters", R.string.characters, Icons.Filled.Person)
    data object Settings : Screen("settings", R.string.settings, Icons.Filled.Settings)
}
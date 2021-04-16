package ie.redstar.camera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import ie.redstar.camera.ui.add_photo.AddPhotoScreen
import ie.redstar.camera.ui.home.HomeScreen
import ie.redstar.camera.ui.theme.CameraTheme

class MainActivity : ComponentActivity() {

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ProvideWindowInsets {
                CameraApp()
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun CameraApp() {
    val navController = rememberNavController()

    CameraTheme {
        NavHost(navController, startDestination = "home") {
            composable("home") { HomeScreen(navController) }
            composable("add") { AddPhotoScreen(navController) }
        }
    }
}
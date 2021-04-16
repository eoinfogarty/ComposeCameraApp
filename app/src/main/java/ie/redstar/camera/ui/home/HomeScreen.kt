package ie.redstar.camera.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.google.accompanist.coil.CoilImage
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.toPaddingValues
import ie.redstar.camera.R
import ie.redstar.camera.ui.util.getOutputDirectory
import java.io.File

@ExperimentalFoundationApi
@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val files = getOutputDirectory(context)
    val childFiles = files.listFiles()?.asList() ?: emptyList<File>()
    val sortedList = childFiles.sortedByDescending { it.lastModified() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("カメラアプリ")
                },
                modifier = Modifier.statusBarsPadding()
            )
        },
        content = {
            LazyVerticalGrid(
                cells = GridCells.Fixed(3),
                contentPadding = PaddingValues(
                    bottom = LocalWindowInsets.current.navigationBars.toPaddingValues()
                        .calculateBottomPadding()
                )
            ) {
                items(sortedList.size) {
                    Column(
                        horizontalAlignment = CenterHorizontally,
                        modifier = Modifier.aspectRatio(0.75f),
                        content = {
                            CoilImage(
                                data = sortedList[it].toUri(),
                                contentDescription = "My content description",
                                fadeIn = true
                            )
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            RequestPermissionHandler(
                onPermissionResult = { isGranted ->
                    if (isGranted) {
                        navController.navigate("add")
                    } else {
                        Toast.makeText(context, "Permission has been denied", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                content = { requestPermissionLauncher ->
                    FloatingActionButton(
                        onClick = {
                            if (!cameraPermissionsGranted(context)) {
                                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                            } else {
                                navController.navigate("add")
                            }
                        },
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.ic_round_add_a_photo_24),
                                contentDescription = "Add a picture"
                            )
                        },
                        modifier = Modifier.navigationBarsPadding()
                    )
                }
            )
        }
    )
}

@Composable
fun RequestPermissionHandler(
    onPermissionResult: (Boolean) -> Unit,
    content: @Composable (ActivityResultLauncher<String>) -> Unit
) {
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        onPermissionResult(isGranted)
    }

    content(requestPermissionLauncher)
}

private fun cameraPermissionsGranted(context: Context): Boolean {
    return arrayOf(Manifest.permission.CAMERA).all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}
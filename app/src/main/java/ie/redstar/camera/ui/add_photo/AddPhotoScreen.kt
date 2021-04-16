package ie.redstar.camera.ui.add_photo


import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import ie.redstar.camera.R
import ie.redstar.camera.databinding.CameraHostBinding
import ie.redstar.camera.ui.util.getOutputDirectory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

@Composable
fun AddPhotoScreen(navController: NavHostController) {
    val context = LocalContext.current
    var imageCapture: ImageCapture? = null

    ConstraintLayout {
        val (appBar, fab) = createRefs()

        CameraPreview(
            onCameraBound = {
                imageCapture = it
            }
        )
        TopAppBar(
            title = {
                Text("")
            },
            backgroundColor = Color.Black.copy(alpha = 0.50f),
            elevation = 0.dp,
            navigationIcon = {
                IconButton(
                    content = {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint = Color.White
                        )
                    },
                    onClick = {
                        navController.popBackStack()
                    },
                )
            },
            modifier = Modifier
                .constrainAs(appBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .statusBarsPadding()
        )
        FloatingActionButton(
            onClick = {
                takePicture(context, imageCapture, navController)
            },
            content = {
                Icon(
                    painter = painterResource(R.drawable.ic_outline_add_circle_outline_24),
                    contentDescription = "Take a picture",
                    tint = Color.Black
                )
            },
            backgroundColor = Color.Cyan,
            modifier = Modifier
                .constrainAs(fab) {
                    bottom.linkTo(parent.bottom, margin = 32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .navigationBarsPadding()
        )
    }
}

fun takePicture(context: Context, capture: ImageCapture?, navController: NavHostController) {
    val imageCapture = capture ?: return

    val fileName =
        SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
    val outputDirectory = getOutputDirectory(context)
    val photoFile = File(outputDirectory, fileName)
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                Log.d("ImageCapture", "Photo capture succeeded: $savedUri")
                Toast.makeText(context, "A picture was added", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("ImageCapture", "onError:", exception)
                Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

@Composable
fun CameraPreview(
    onCameraBound: (ImageCapture) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidViewBinding(CameraHostBinding::inflate) {
        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                val imageCapture = ImageCapture.Builder().build()

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                    onCameraBound(imageCapture)
                } catch (ex: Exception) {
                    Log.e("CameraPreview", "Error binding camera provider to lifecycle", ex)
                }
            },
            ContextCompat.getMainExecutor(context)
        )
    }
}
package ie.redstar.camera.ui.util

import android.content.Context
import android.os.Environment
import java.io.File

fun getOutputDirectory(context: Context): File {
  return context.getExternalFilesDirs(Environment.DIRECTORY_PICTURES).first()
}
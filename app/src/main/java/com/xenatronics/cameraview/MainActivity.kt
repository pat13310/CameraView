package com.xenatronics.cameraview

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.xenatronics.cameraview.ui.theme.CameraViewTheme

class MainActivity : ComponentActivity() {

    val permission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("Grant:", "OK")
            } else {
                Log.d("Grant:", "No")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraViewTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
        requestPermissionCamera()
    }
    fun requestPermissionCamera() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {

            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this as Activity,
                android.Manifest.permission.CAMERA
            ) -> {

            }
            else -> requestPermissionCamera()
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CameraViewTheme {
        Greeting("Android")
    }
}
package com.xenatronics.cameraview

import android.app.Application


import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig

class CameraApp : Application(), CameraXConfig.Provider {
    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }
}
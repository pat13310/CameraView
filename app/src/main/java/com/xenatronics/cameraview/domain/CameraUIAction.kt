package com.xenatronics.cameraview.domain

sealed class CameraUIAction {
    object OnCameraClick : CameraUIAction()
    object OnGalleryViewClick : CameraUIAction()
    object OnSwitchCameraClick : CameraUIAction()
    object OnFlashCameraClick : CameraUIAction()
    object OnZoomInClick : CameraUIAction()
    object OnZoomOutClick : CameraUIAction()
}

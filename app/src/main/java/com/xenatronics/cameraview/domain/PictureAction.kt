package com.xenatronics.cameraview.domain

sealed class PictureAction {
    object TextRecognition : PictureAction()
    object RotationImage : PictureAction()
    object BackAction : PictureAction()
    object CodeBarRecognition : PictureAction()
    object TranslateText : PictureAction()
}

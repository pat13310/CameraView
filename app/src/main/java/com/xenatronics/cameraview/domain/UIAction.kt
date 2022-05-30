package com.xenatronics.cameraview.domain

sealed class UIAction {
    object TextRecognition : UIAction()
    object RotationImage : UIAction()
    object BackAction : UIAction()
    object CodeBarRecognition : UIAction()
    object TranslateText : UIAction()
    object IncRecognition : UIAction()
    object MediaImage : UIAction()
}

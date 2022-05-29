package com.xenatronics.cameraview.presentation.screens.components

import com.xenatronics.cameraview.domain.PictureAction

data class ItemDetect(
    val nom: String = "",
    val action: PictureAction = PictureAction.BackAction

)

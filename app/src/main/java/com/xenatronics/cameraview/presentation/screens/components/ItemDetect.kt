package com.xenatronics.cameraview.presentation.screens.components

import com.xenatronics.cameraview.domain.UIAction

data class ItemDetect(
    val nom: String = "",
    val action: UIAction = UIAction.BackAction

)

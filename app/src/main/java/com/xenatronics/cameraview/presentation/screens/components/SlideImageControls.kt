package com.xenatronics.cameraview.presentation.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xenatronics.cameraview.domain.PictureAction

@Composable
fun SlideImageControls(
    modifier: Modifier,
    list: List<ItemDetect>,
    onAction: (PictureAction) -> Unit
) {
    LazyRow(
        modifier = modifier
    ) {
        items(list) { item ->
            Text(
                modifier =
                modifier
                    .clickable {
                        onAction(item.action)
                    }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                text = item.nom,
                color = MaterialTheme.colors.primary,
                fontWeight = FontWeight.Bold
                //fontStyle = MaterialTheme.typography.h2
            )
        }
    }
}

package com.walknxt.app.presentation.goals

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.walknxt.app.presentation.components.EmptyState

@Composable
fun GoalsScreen() {
    EmptyState(
        title = "Goals",
        description = "Set a daily walking target. (Coming soon to WalkNxt)"
    )
}

package com.aarw.dexanalyze.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoIconTooltip(
    text: String,
    title: String? = null,
    modifier: Modifier = Modifier
) {
    val state = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()

    TooltipBox(
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            RichTooltip(
                title = title?.let { { Text(it, style = MaterialTheme.typography.titleSmall) } }
            ) {
                Text(text, style = MaterialTheme.typography.bodySmall)
            }
        },
        state = state,
        modifier = modifier
    ) {
        IconButton(
            onClick = { scope.launch { state.show() } },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "More info",
                modifier = Modifier.size(15.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
            )
        }
    }
}

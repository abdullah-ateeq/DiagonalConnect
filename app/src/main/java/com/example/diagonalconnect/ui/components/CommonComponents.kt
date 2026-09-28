package com.example.diagonalconnect.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.diagonalconnect.game.Player

val Player.label: String
    get() = "PLAYER ${number}"

val Player.objective: String
    get() = if (this == Player.PLAYER_ONE) "Connect Top ↔ Bottom" else "Connect Left ↔ Right"

val Player.objectiveSpoken: String
    get() = if (this == Player.PLAYER_ONE) "connects top to bottom" else "connects left to right"

@Composable
fun PrimaryActionButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 0.dp),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun SecondaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 52.dp,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(height),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            if (enabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant,
        ),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
        contentPadding = PaddingValues(horizontal = 12.dp),
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium, maxLines = 1)
    }
}

@Composable
fun ScreenHeader(title: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack, modifier = Modifier.semantics { contentDescription = "Back" }) {
            BackChevron(color = MaterialTheme.colorScheme.onSurface)
        }
        Spacer(Modifier.width(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.semantics { heading() },
        )
    }
}

@Composable
private fun BackChevron(color: Color) {
    Canvas(Modifier.size(18.dp)) {
        val w = size.width
        val stroke = Stroke(width = w * 0.11f, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.68f, w * 0.14f), Offset(w * 0.3f, w * 0.5f), stroke.width, StrokeCap.Round)
        drawLine(color, Offset(w * 0.3f, w * 0.5f), Offset(w * 0.68f, w * 0.86f), stroke.width, StrokeCap.Round)
    }
}

@Composable
fun PlayerSymbol(player: Player, modifier: Modifier = Modifier, size: Dp = 28.dp) {
    Canvas(modifier.size(size)) {
        drawPiece(player, center, this.size.minDimension * 0.38f)
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.semantics { heading() },
    )
}

@Composable
fun StatBlock(value: String, label: String, modifier: Modifier = Modifier, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Column(
        modifier = modifier.semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(value, style = MaterialTheme.typography.headlineSmall, color = valueColor, textAlign = TextAlign.Center)
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

/** Thin bronze divider with a small diamond at its centre. */
@Composable
fun OrnamentDivider(modifier: Modifier = Modifier, width: Dp = 96.dp) {
    val color = MaterialTheme.colorScheme.primary
    Box(modifier.size(width, 10.dp)) {
        Canvas(Modifier.size(width, 10.dp)) {
            val cy = size.height / 2
            val cx = size.width / 2
            val d = size.height * 0.45f
            drawLine(color.copy(alpha = 0.5f), Offset(0f, cy), Offset(cx - d * 2f, cy), 1.dp.toPx())
            drawLine(color.copy(alpha = 0.5f), Offset(cx + d * 2f, cy), Offset(size.width, cy), 1.dp.toPx())
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(cx, cy - d); lineTo(cx + d, cy); lineTo(cx, cy + d); lineTo(cx - d, cy); close()
            }
            drawPath(path, color)
        }
    }
}

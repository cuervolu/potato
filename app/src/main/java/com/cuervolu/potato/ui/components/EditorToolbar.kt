package com.cuervolu.potato.ui.components

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatAlignLeft
import androidx.compose.material.icons.automirrored.outlined.FormatAlignRight
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.RichTextState


@Composable
fun AnimatedEditorToolbar(
    richTextState: RichTextState,
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    val keyboardVisible by rememberKeyboardState()
    val transition = updateTransition(
        targetState = visible && keyboardVisible,
        label = "toolbar"
    )

    val offsetY by transition.animateDp(label = "offsetY") { isVisible ->
        if (isVisible) 0.dp else 100.dp
    }

    Box(
        modifier = modifier
            .offset(y = offsetY)
            .graphicsLayer(
                alpha = if (visible) 1f else 0f,
                clip = true,
                scaleX = if (visible) 1f else 0.9f,
                scaleY = if (visible) 1f else 0.9f
            )
    ) {
        EditorToolbar(
            richTextState = richTextState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EditorToolbar(
    richTextState: RichTextState,
    modifier: Modifier = Modifier
) {
    Surface(
        tonalElevation = 6.dp,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
        modifier = modifier
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 8.dp)
        ) {
            // Text Alignment
            item {
                IconButton(
                    onClick = {
                        richTextState.toggleParagraphStyle(
                            ParagraphStyle(textAlign = TextAlign.Left)
                        )
                    }
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.FormatAlignLeft,
                        contentDescription = "Alinear a la izquierda",
                        tint = if (richTextState.currentParagraphStyle.textAlign == TextAlign.Left)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            item {
                IconButton(
                    onClick = {
                        richTextState.toggleParagraphStyle(
                            ParagraphStyle(textAlign = TextAlign.Center)
                        )
                    }
                ) {
                    Icon(
                        Icons.Outlined.FormatAlignCenter,
                        contentDescription = "Centrar",
                        tint = if (richTextState.currentParagraphStyle.textAlign == TextAlign.Center)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            item {
                IconButton(
                    onClick = {
                        richTextState.toggleParagraphStyle(
                            ParagraphStyle(textAlign = TextAlign.Right)
                        )
                    }
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.FormatAlignRight,
                        contentDescription = "Alinear a la derecha",
                        tint = if (richTextState.currentParagraphStyle.textAlign == TextAlign.Right)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Separator
            item {
                Box(
                    Modifier
                        .height(24.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }

            // Text formatting
            item {
                IconButton(
                    onClick = {
                        richTextState.toggleSpanStyle(
                            SpanStyle(fontWeight = FontWeight.Bold)
                        )
                    }
                ) {
                    Icon(
                        Icons.Outlined.FormatBold,
                        contentDescription = "Negrita",
                        tint = if (richTextState.currentSpanStyle.fontWeight == FontWeight.Bold)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            item {
                IconButton(
                    onClick = {
                        richTextState.toggleSpanStyle(
                            SpanStyle(fontStyle = FontStyle.Italic)
                        )
                    }
                ) {
                    Icon(
                        Icons.Outlined.FormatItalic,
                        contentDescription = "Cursiva",
                        tint = if (richTextState.currentSpanStyle.fontStyle == FontStyle.Italic)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            item {
                IconButton(
                    onClick = {
                        richTextState.toggleSpanStyle(
                            SpanStyle(textDecoration = TextDecoration.Underline)
                        )
                    }
                ) {
                    Icon(
                        Icons.Outlined.FormatUnderlined,
                        contentDescription = "Subrayado",
                        tint = if (richTextState.currentSpanStyle.textDecoration?.contains(
                                TextDecoration.Underline
                            ) == true
                        )
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Separator
            item {
                Box(
                    Modifier
                        .height(24.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }

            // Lists
            item {
                IconButton(
                    onClick = { richTextState.toggleUnorderedList() }
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.FormatListBulleted,
                        contentDescription = "Lista no ordenada",
                        tint = if (richTextState.isUnorderedList)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            item {
                IconButton(
                    onClick = { richTextState.toggleOrderedList() }
                ) {
                    Icon(
                        Icons.Outlined.FormatListNumbered,
                        contentDescription = "Lista numerada",
                        tint = if (richTextState.isOrderedList)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Separator
            item {
                Box(
                    Modifier
                        .height(24.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }

            // Code
            item {
                IconButton(
                    onClick = { richTextState.toggleCodeSpan() }
                ) {
                    Icon(
                        Icons.Outlined.Code,
                        contentDescription = "Código",
                        tint = if (richTextState.isCodeSpan)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun rememberKeyboardState(): State<Boolean> {
    val density = LocalDensity.current
    val ime = WindowInsets.ime

    return remember {
        derivedStateOf {
            ime.getBottom(density) > 0
        }
    }
}
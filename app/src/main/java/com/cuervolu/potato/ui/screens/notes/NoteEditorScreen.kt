package com.cuervolu.potato.ui.screens.notes

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cuervolu.potato.data.local.entity.NoteEntity
import com.cuervolu.potato.ui.components.AnimatedEditorToolbar
import com.cuervolu.potato.ui.components.NoteCoverHeader
import com.cuervolu.potato.ui.components.PermissionDialogData
import com.cuervolu.potato.ui.components.PermissionRequestDialog
import com.cuervolu.potato.utils.ImagePicker
import com.cuervolu.potato.utils.state.UIState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import com.skydoves.orbital.Orbital
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


private fun UIState<NoteEntity?>.hasImageChanged(currentNote: NoteEntity?): Boolean {
    return when (this) {
        is UIState.Success -> {
            val newImage = this.data?.coverImage
            val previousImage = currentNote?.coverImage
            newImage != null && newImage != previousImage
        }

        else -> false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: Int? = null,
    onBackClick: () -> Unit,
    viewModel: NoteEditorViewModel = koinViewModel()
) {
    var showPermissionDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val currentNote = (uiState as? UIState.Success)?.data
    val richTextState = rememberRichTextState()
    val scrollState = rememberLazyListState()
    val toolbarVisible = remember { mutableStateOf(false) }
    val scrollOffset by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var saveJob by remember { mutableStateOf<Job?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val imagePicker = ImagePicker.createImagePicker(
        context = context,
        onImagePicked = { uri ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Procesando imagen...",
                    duration = SnackbarDuration.Short
                )
                viewModel.onImageSelected(uri)
            }
        },
        onPermissionDenied = {
            showPermissionDialog = true
        }
    )


    var localTitle by remember(currentNote?.title) {
        mutableStateOf(currentNote?.title.orEmpty())
    }

    LaunchedEffect(noteId) {
        noteId?.let { viewModel.loadNote(it) }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is UIState.Success -> {
                if (currentNote?.content != richTextState.toHtml()) {
                    currentNote?.let {
                        richTextState.setHtml(it.content)
                    }
                }
                if (uiState.hasImageChanged(currentNote)) {
                    snackbarHostState.showSnackbar(
                        message = "Imagen actualizada correctamente",
                        duration = SnackbarDuration.Short
                    )
                }
            }

            is UIState.Error -> {
                snackbarHostState.showSnackbar(
                    message = "Error al guardar los cambios",
                    duration = SnackbarDuration.Long
                )
            }

            else -> { /* No hacemos nada para Loading */
            }
        }
    }

    LaunchedEffect(richTextState.annotatedString.text, localTitle) {
        saveJob?.cancel()

        if (richTextState.annotatedString.text.isNotEmpty() || localTitle.isNotEmpty()) {
            saveJob = launch {
                isSaving = true
                delay(2000) // 2 segundos
                viewModel.saveNote(
                    title = localTitle,
                    content = richTextState.toHtml()
                )
                isSaving = false
            }
        }
    }


    if (showPermissionDialog) {
        val permissionContent = PermissionDialogData.getStoragePermissionContent()
        PermissionRequestDialog(
            title = permissionContent.title,
            description = permissionContent.description,
            icon = {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            onConfirm = {
                showPermissionDialog = false
                imagePicker.pickImage()
            },
            onDismiss = {
                showPermissionDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Se requiere permiso para acceder a las imágenes",
                        actionLabel = "Configuración",
                        duration = SnackbarDuration.Long
                    ).let { result ->
                        if (result == SnackbarResult.ActionPerformed) {
                            context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            })
                        }
                    }
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Orbital {
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    NoteCoverHeader(
                        title = localTitle,
                        coverImage = currentNote?.coverImage,
                        scrollOffset = scrollOffset,
                        onBackClick = onBackClick,
                        onTitleChange = { newTitle ->
                            localTitle = newTitle
                        },
                        onImageClick = { imagePicker.pickImage() },
                        isLoading = uiState is UIState.Loading
                    )
                }

                item {
                    RichTextEditor(
                        state = richTextState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 200.dp)
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 80.dp)
                            .onFocusChanged { focusState ->
                                toolbarVisible.value = focusState.isFocused
                            },
                        placeholder = {
                            Text(
                                text = "Escribe aquí tu nota...",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        },
                        colors = RichTextEditorDefaults.richTextEditorColors(
                            textColor = MaterialTheme.colorScheme.onSurface,
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        if (toolbarVisible.value) {
            AnimatedEditorToolbar(
                richTextState = richTextState,
                visible = true,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .imePadding()
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
    LaunchedEffect(richTextState.annotatedString.text) {
        val isNearBottom = scrollState.firstVisibleItemIndex > 0 ||
                (scrollState.firstVisibleItemScrollOffset > 0 &&
                        richTextState.selection.max == richTextState.annotatedString.text.length)

        if (isNearBottom) {
            scrollState.animateScrollToItem(1)
        }
    }
}
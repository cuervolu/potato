package com.cuervolu.potato.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cuervolu.potato.R
import com.cuervolu.potato.ui.components.NoteItem
import com.cuervolu.potato.ui.components.SoundEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onNoteClick: (Int) -> Unit,
    onNewNoteClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val notes by viewModel.notes.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewNoteClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add note")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (notes.isEmpty()) {
            EmptyNotesWithEasterEgg()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = innerPadding,
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notes) { note ->
                    NoteItem(
                        note = note,
                        onNoteClick = { onNoteClick(note.id) },
                        onNotePinned = { viewModel.toggleNotePinStatus(note) },
                        onNoteArchived = { viewModel.archiveNote(note) }
                    )
                }
            }
        }
    }
}


@Composable
fun EmptyNotesWithEasterEgg(
    easterEggViewModel: EasterEggViewModel = koinViewModel()
) {
    val easterEggVisible by easterEggViewModel.easterEggVisible.collectAsState()
    val currentSound by easterEggViewModel.currentSound.collectAsState()

    // Lista de imágenes para el easter egg
    val easterEggImages = listOf(
        R.drawable.aaaaaaa,
        R.drawable.cute_aaa,
        R.drawable.goat,
        R.drawable.goat2
    )

    // Estado para la imagen actual
    var currentImage by remember { mutableIntStateOf(easterEggImages[0]) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.empty_notes),
                contentDescription = "No hay notas",
                modifier = Modifier
                    .size(200.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        currentImage = easterEggImages.random()
                        easterEggViewModel.triggerEasterEgg()
                    }
            )

            AnimatedVisibility(
                visible = easterEggVisible,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Image(
                    painter = painterResource(id = currentImage),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )
            }

            SoundEffect(currentSound)

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "¡Vaya, qué vacío está esto! 🤔",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Parece que tus notas se fueron de vacaciones. ¡Pulsa el botón + para crear una nueva!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
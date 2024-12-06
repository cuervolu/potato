package com.cuervolu.potato.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuervolu.potato.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EasterEggViewModel : ViewModel() {
    private val _easterEggVisible = MutableStateFlow(false)
    val easterEggVisible: StateFlow<Boolean> = _easterEggVisible

    private val _currentSound = MutableStateFlow<Int?>(null)
    val currentSound: StateFlow<Int?> = _currentSound

    private val sounds = listOf(
        R.raw.goatscreech1,
        R.raw.goatscreech2,
        R.raw.goatscreech3,
        R.raw.goatscreech4,
    )

    fun triggerEasterEgg() {
        _currentSound.value = sounds.random()
        _easterEggVisible.value = true

        viewModelScope.launch {
            delay(2000)
            _easterEggVisible.value = false
            _currentSound.value = null
        }
    }
}
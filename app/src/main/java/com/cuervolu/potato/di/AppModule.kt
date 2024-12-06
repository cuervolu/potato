package com.cuervolu.potato.di

import androidx.room.Room
import com.cuervolu.potato.data.local.PotatoDatabase
import com.cuervolu.potato.data.preferences.UserPreferencesManager
import com.cuervolu.potato.data.repository.CharacterRepository
import com.cuervolu.potato.data.repository.NoteRepository
import com.cuervolu.potato.ui.screens.characters.CharactersViewModel
import com.cuervolu.potato.ui.screens.home.EasterEggViewModel
import com.cuervolu.potato.ui.screens.home.HomeViewModel
import com.cuervolu.potato.ui.screens.notes.NoteEditorViewModel
import com.cuervolu.potato.ui.screens.settings.SettingsViewModel
import com.cuervolu.potato.utils.ImageStorageService
import com.cuervolu.potato.utils.error.ErrorHandler
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            PotatoDatabase::class.java,
            PotatoDatabase.DATABASE_NAME
        ).build()
    }

    single { ImageStorageService(get()) }
    // DAOs
    single { get<PotatoDatabase>().noteDao() }
    single { get<PotatoDatabase>().characterDao() }

    // Repositories
    single { NoteRepository(get(), get()) }
    single { CharacterRepository(get()) }

    // Error
    single { ErrorHandler(get()) }

    // Preferences
    single { UserPreferencesManager(get()) }

    viewModel { HomeViewModel(get()) }
    viewModel { CharactersViewModel(get()) }
    viewModel { NoteEditorViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel{ EasterEggViewModel() }
}
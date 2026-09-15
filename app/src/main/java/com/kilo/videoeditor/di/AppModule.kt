package com.kilo.videoeditor.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * Módulo de injeção de dependência de alto nível.
 * ViewModels são criados via @HiltViewModel nos arquivos de presentation.
 * Repositórios são fornecidos pelo DataModule.
 */
@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    // Placeholder para futuros módulos de aplicação (logging, analytics, etc.)
}
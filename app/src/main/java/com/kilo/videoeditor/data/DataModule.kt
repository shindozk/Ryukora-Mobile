package com.kilo.videoeditor.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import com.kilo.videoeditor.data.local.DatabaseProvider
import com.kilo.videoeditor.data.local.VideoEditorDatabase
import com.kilo.videoeditor.data.repository.AudioEffectRepositoryImpl
import com.kilo.videoeditor.data.repository.ClipRepositoryImpl
import com.kilo.videoeditor.data.repository.ExportRepositoryImpl
import com.kilo.videoeditor.data.repository.MediaRepositoryImpl
import com.kilo.videoeditor.data.repository.TextOverlayRepositoryImpl
import com.kilo.videoeditor.data.repository.VideoProjectRepositoryImpl
import com.kilo.videoeditor.domain.repository.AudioEffectRepository
import com.kilo.videoeditor.domain.repository.ClipRepository
import com.kilo.videoeditor.domain.repository.ExportRepository
import com.kilo.videoeditor.domain.repository.MediaRepository
import com.kilo.videoeditor.domain.repository.TextOverlayRepository
import com.kilo.videoeditor.domain.repository.VideoProjectRepository
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VideoEditorDatabase =
        DatabaseProvider.getDatabase(context)

    @Provides
    @Singleton
    fun provideVideoProjectRepository(db: VideoEditorDatabase): VideoProjectRepository =
        VideoProjectRepositoryImpl(db)

    @Provides
    @Singleton
    fun provideClipRepository(db: VideoEditorDatabase): ClipRepository =
        ClipRepositoryImpl(db)

    @Provides
    @Singleton
    fun provideTextOverlayRepository(db: VideoEditorDatabase): TextOverlayRepository =
        TextOverlayRepositoryImpl(db)

    @Provides
    @Singleton
    fun provideAudioEffectRepository(db: VideoEditorDatabase): AudioEffectRepository =
        AudioEffectRepositoryImpl(db)

    @Provides
    @Singleton
    fun provideMediaRepository(@ApplicationContext context: Context): MediaRepository =
        MediaRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideExportRepository(@ApplicationContext context: Context): ExportRepository =
        ExportRepositoryImpl(context)
}
package com.kilo.videoeditor.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var INSTANCE: VideoEditorDatabase? = null

    fun getDatabase(context: Context): VideoEditorDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                VideoEditorDatabase::class.java,
                "video_editor_db",
            )
                .fallbackToDestructiveMigration()
                .build()
            INSTANCE = instance
            instance
        }
    }
}
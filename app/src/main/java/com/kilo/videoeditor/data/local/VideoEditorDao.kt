package com.kilo.videoeditor.data.local

import androidx.room.*
import com.kilo.videoeditor.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoProjectDao {
    @Query("SELECT * FROM video_projects ORDER BY dateAdded DESC")
    fun getAllProjects(): Flow<List<VideoProjectEntity>>

    @Query("SELECT * FROM video_projects WHERE id = :id")
    suspend fun getProject(id: Long): VideoProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(entity: VideoProjectEntity): Long

    @Update
    suspend fun updateProject(entity: VideoProjectEntity)

    @Query("DELETE FROM video_projects WHERE id = :id")
    suspend fun deleteProject(id: Long)
}

@Dao
interface EditSettingsDao {
    @Query("SELECT * FROM edit_settings WHERE projectId = :projectId")
    suspend fun getSettings(projectId: Long): EditSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSettings(entity: EditSettingsEntity): Long

    @Query("DELETE FROM edit_settings WHERE projectId = :projectId")
    suspend fun deleteSettings(projectId: Long)
}

@Dao
interface ClipDao {
    @Query("SELECT * FROM clips WHERE projectId = :projectId ORDER BY `order`")
    suspend fun getClipsForProject(projectId: Long): List<ClipEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClip(entity: ClipEntity): Long

    @Delete
    suspend fun deleteClip(entity: ClipEntity)

    @Query("UPDATE clips SET `order` = :order WHERE id = :id")
    suspend fun updateOrder(id: Long, order: Int)
}

@Dao
interface TextOverlayDao {
    @Query("SELECT * FROM text_overlays WHERE projectId = :projectId ORDER BY startMs")
    suspend fun getOverlaysForProject(projectId: Long): List<TextOverlayEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOverlay(entity: TextOverlayEntity): Long

    @Update
    suspend fun updateOverlay(entity: TextOverlayEntity)

    @Delete
    suspend fun deleteOverlay(entity: TextOverlayEntity)
}

@Dao
interface AudioEffectDao {
    @Query("SELECT * FROM audio_effects WHERE projectId = :projectId ORDER BY startMs")
    suspend fun getEffectsForProject(projectId: Long): List<AudioEffectEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEffect(entity: AudioEffectEntity): Long

    @Delete
    suspend fun deleteEffect(entity: AudioEffectEntity)
}

@Database(
    entities = [
        VideoProjectEntity::class,
        EditSettingsEntity::class,
        ClipEntity::class,
        TextOverlayEntity::class,
        AudioEffectEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class VideoEditorDatabase : RoomDatabase() {
    abstract fun videoProjectDao(): VideoProjectDao
    abstract fun editSettingsDao(): EditSettingsDao
    abstract fun clipDao(): ClipDao
    abstract fun textOverlayDao(): TextOverlayDao
    abstract fun audioEffectDao(): AudioEffectDao
}
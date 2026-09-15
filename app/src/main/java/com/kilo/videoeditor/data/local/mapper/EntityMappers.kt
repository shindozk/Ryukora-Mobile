package com.kilo.videoeditor.data.local.mapper

import com.kilo.videoeditor.data.local.entity.AudioEffectEntity
import com.kilo.videoeditor.data.local.entity.ClipEntity
import com.kilo.videoeditor.data.local.entity.EditSettingsEntity
import com.kilo.videoeditor.data.local.entity.TextOverlayEntity
import com.kilo.videoeditor.data.local.entity.VideoProjectEntity
import com.kilo.videoeditor.domain.model.AudioEffect
import com.kilo.videoeditor.domain.model.AudioEffectType
import com.kilo.videoeditor.domain.model.Clip
import com.kilo.videoeditor.domain.model.EditSettings
import com.kilo.videoeditor.domain.model.TextOverlay
import com.kilo.videoeditor.domain.model.VideoFilter
import com.kilo.videoeditor.domain.model.VideoProject

fun VideoProjectEntity.toDomain(): VideoProject = VideoProject(
    id = id,
    uri = uri,
    name = name,
    durationMs = durationMs,
    width = width,
    height = height,
    sizeBytes = sizeBytes,
    thumbnailUri = thumbnailUri,
    dateAdded = dateAdded,
)

fun VideoProject.toEntity(): VideoProjectEntity = VideoProjectEntity(
    id = id,
    uri = uri,
    name = name,
    durationMs = durationMs,
    width = width,
    height = height,
    sizeBytes = sizeBytes,
    thumbnailUri = thumbnailUri,
    dateAdded = dateAdded,
)

fun EditSettingsEntity.toDomain(): EditSettings = EditSettings(
    brightness = brightness,
    contrast = contrast,
    saturation = saturation,
    speed = speed,
    volume = volume,
    mute = mute,
    trimStartMs = trimStartMs,
    trimEndMs = trimEndMs,
    rotation = rotation,
    filters = filters.split(",")
        .filter { it.isNotBlank() }
        .mapNotNull { runCatching { VideoFilter.valueOf(it) }.getOrNull() },
)

fun EditSettings.toEntity(projectId: Long): EditSettingsEntity = EditSettingsEntity(
    projectId = projectId,
    brightness = brightness,
    contrast = contrast,
    saturation = saturation,
    speed = speed,
    volume = volume,
    mute = mute,
    trimStartMs = trimStartMs,
    trimEndMs = trimEndMs,
    rotation = rotation,
    filters = filters.joinToString(",") { it.name },
)

fun ClipEntity.toDomain(): Clip = Clip(
    id = id,
    projectId = projectId,
    uri = uri,
    name = name,
    durationMs = durationMs,
    order = order,
    trimStartMs = trimStartMs,
    trimEndMs = trimEndMs,
)

fun Clip.toEntity(): ClipEntity = ClipEntity(
    id = id,
    projectId = projectId,
    uri = uri,
    name = name,
    durationMs = durationMs,
    order = order,
    trimStartMs = trimStartMs,
    trimEndMs = trimEndMs,
)

fun TextOverlayEntity.toDomain(): TextOverlay = TextOverlay(
    id = id,
    projectId = projectId,
    text = text,
    startMs = startMs,
    endMs = endMs,
    x = x,
    y = y,
    fontSize = fontSize,
    color = color,
    font = font,
)

fun TextOverlay.toEntity(): TextOverlayEntity = TextOverlayEntity(
    id = id,
    projectId = projectId,
    text = text,
    startMs = startMs,
    endMs = endMs,
    x = x,
    y = y,
    fontSize = fontSize,
    color = color,
    font = font,
)

fun AudioEffectEntity.toDomain(): AudioEffect = AudioEffect(
    id = id,
    projectId = projectId,
    type = runCatching { AudioEffectType.valueOf(type) }.getOrNull() ?: AudioEffectType.REVERB,
    startMs = startMs,
    endMs = endMs,
    intensity = intensity,
)

fun AudioEffect.toEntity(): AudioEffectEntity = AudioEffectEntity(
    id = id,
    projectId = projectId,
    type = type.name,
    startMs = startMs,
    endMs = endMs,
    intensity = intensity,
)
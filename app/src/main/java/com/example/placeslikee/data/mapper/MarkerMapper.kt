package com.example.placeslikee.data.mapper

import com.example.placeslikee.data.local.entities.marks.MarkerEntity
import com.example.placeslikee.data.local.entities.marks.MarkerWithAuthor
import com.example.placeslikee.data.local.entities.marks.SyncState
import com.example.placeslikee.data.remote.dto.RemoteMarker
import com.example.placeslikee.domain.models.UIMarker
import com.google.firebase.firestore.GeoPoint

fun MarkerEntity.toRemoteMarker(): RemoteMarker = RemoteMarker(
    id = id,
    authorId = authorId,
    coordinates = GeoPoint(lat, longitude),
    description = description,
    image = image,
    likesAmount = likesAmount,
    locationName = name,
    remoteTimestamp = localTimestamp
)

fun RemoteMarker.toMarkerEntity(): MarkerEntity = MarkerEntity(
    id = id,
    lat = coordinates.latitude,
    longitude = coordinates.longitude,
    name = locationName,
    authorId = authorId,
    description = description,
    likesAmount = likesAmount,
    synced = SyncState.SYNCED,
    image = image,
    localTimestamp = remoteTimestamp ?: 0
    )

fun MarkerWithAuthor.toUIMarker(): UIMarker = UIMarker(
    id = mark.id,
    lat = mark.lat,
    longitude = mark.longitude,
    name = mark.name,
    authorId = mark.authorId,
    authorName = author?.name ?: "Unknown",
    description = mark.description,
    likesAmount = mark.likesAmount,
    image = mark.image,
    uiTimestamp = mark.localTimestamp
)
fun UIMarker.toMarkerEntity(): MarkerEntity = MarkerEntity(
    id = id,
    lat = lat,
    longitude = longitude,
    name = name,
    authorId = authorId,
    description = description,
    likesAmount = likesAmount,
    likedByUser = likedByUser,
    image = image,
    localTimestamp = uiTimestamp

)
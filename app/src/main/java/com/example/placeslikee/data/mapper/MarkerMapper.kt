package com.example.placeslikee.data.mapper

import android.util.Log
import com.example.placeslikee.data.local.entities.UserEntity
import com.example.placeslikee.data.local.entities.marks.MarkerEntity
import com.example.placeslikee.data.local.entities.marks.MarkerWithAuthor
import com.example.placeslikee.data.local.entities.marks.SyncState
import com.example.placeslikee.data.remote.dto.RemoteMarker
import com.example.placeslikee.data.remote.dto.RemoteUser
import com.example.placeslikee.domain.models.UIMarker
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.auth.User

fun MarkerEntity.toRemoteMarker(): RemoteMarker = RemoteMarker(
    id = id.trim(),
    authorId = authorId ?: "",
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

fun MarkerWithAuthor.toUIMarker(): UIMarker {
    Log.d("my log", """ Mapper - 
        Marker: ${mark.id}
        AuthorId: '${mark.authorId}'
        Author: ${author?.id ?: "NULL"}
        Author name: ${author?.name ?: "NULL"}
    """.trimIndent())
    return UIMarker(
        id = mark.id,
        lat = mark.lat,
        longitude = mark.longitude,
        name = mark.name,
        authorId = mark.authorId ?: "",
        authorName = author?.name ?: "Неизвестный",
        description = mark.description,
        likesAmount = mark.likesAmount,
        image = mark.image,
        uiTimestamp = mark.localTimestamp
    )
}

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


fun RemoteUser.toUserEntity(): UserEntity = UserEntity(
    id = id.trim(),
    name = name,
    localTimestamp = remoteTimestamp
)
fun UserEntity.toRemoteUser(): RemoteUser = RemoteUser(
    id = id.trim(),
    name = name,
    remoteTimestamp = localTimestamp
)
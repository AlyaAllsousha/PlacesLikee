package com.example.placeslikee.data.mapper

import com.example.placeslikee.data.local.entities.FollowingEntity
import com.example.placeslikee.data.local.entities.LikesEntity
import com.example.placeslikee.data.local.entities.UserEntity
import com.example.placeslikee.data.local.entities.marks.MarkerEntity
import com.example.placeslikee.data.local.entities.marks.MarkerWithAuthor
import com.example.placeslikee.data.local.entities.SyncState
import com.example.placeslikee.data.remote.dto.RemoteFollowing
import com.example.placeslikee.data.remote.dto.RemoteLike
import com.example.placeslikee.data.remote.dto.RemoteMarker
import com.example.placeslikee.data.remote.dto.RemoteUser
import com.example.placeslikee.domain.models.UIMarker
import com.google.firebase.firestore.GeoPoint

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
    return UIMarker(
        id = mark.id,
        latitude = mark.lat,
        longitude = mark.longitude,
        name = mark.name,
        authorId = mark.authorId,
        authorName = author?.name ?: "Неизвестный",
        description = mark.description,
        likedByUser = mark.likedByUser,
        likesAmount = mark.likesAmount,
        image = mark.image,
        uiTimestamp = mark.localTimestamp
    )
}

fun UIMarker.toMarkerEntity(): MarkerEntity = MarkerEntity(
    id = id,
    lat = latitude,
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
    email = email,
    syncState = SyncState.SYNCED,
    localTimestamp = remoteTimestamp
)

fun UserEntity.toRemoteUser(): RemoteUser = RemoteUser(
    id = id.trim(),
    name = name,
    email = email,
    remoteTimestamp = localTimestamp
)

fun LikesEntity.toRemoteLike(): RemoteLike = RemoteLike(
    id = id,
    markerId = markerId,
    userId = userId,
    remoteTimestamp = localTimeStamp
)

fun RemoteLike.toLikeEntity(): LikesEntity = LikesEntity(
    id = id,
    markerId = markerId,
    userId = userId,
    syncState = SyncState.SYNCED,
    localTimeStamp = remoteTimestamp ?: System.currentTimeMillis()
)

fun RemoteFollowing.toFollowingEntity(): FollowingEntity = FollowingEntity(
    authorId = authorId,
    authorName = authorName,
    sync = SyncState.SYNCED,
    subscribedAt = subscribedAt?.time ?: System.currentTimeMillis(),
)

fun FollowingEntity.toRemoteFollowing(): RemoteFollowing = RemoteFollowing(
    authorId = authorId,
    authorName = authorName
)
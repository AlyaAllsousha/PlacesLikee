package com.example.placeslikee.data.mapper

import com.example.placeslikee.data.local.entities.marks.MarkerEntity
import com.example.placeslikee.data.local.entities.marks.MarkerWithAuthor
import com.example.placeslikee.data.remote.dto.RemoteMarker
import com.example.placeslikee.domain.models.UIMarker
import com.google.firebase.firestore.GeoPoint
import kotlin.String

fun MarkerEntity.toRemoteMarker(): RemoteMarker = RemoteMarker(
    id = remoteId,
    authorId = authorId,
    coordinates = GeoPoint(lat, longitude),
    description = description,
    image = image,
    likesAmount = likesAmount,
    locationName = name
)

fun RemoteMarker.toMarkerEntity(): MarkerEntity = MarkerEntity(
    remoteId = id,
    lat = coordinates.latitude,
    longitude = coordinates.longitude,
    name = locationName,
    authorId = authorId,
    description = description,
    likesAmount = likesAmount,
    image = image,
    )

fun MarkerWithAuthor.toUIMarker(): UIMarker = UIMarker(
    id = mark.remoteId,
    lat = mark.lat,
    longitude = mark.longitude,
    name = mark.name,
    authorName = author.name,
    description = mark.description,
    likesAmount = mark.likesAmount,
    image = mark.image
)
package com.example.placeslikee.data.local.entities.marks

import androidx.room.Embedded
import androidx.room.Relation
import com.example.placeslikee.data.local.entities.UserEntity

data class MarkerWithAuthor (
    @Embedded val mark: MarkEntity,
    @Relation(
        parentColumn = "authorId",
        entityColumn = "id"
    )
    val author: UserEntity?
)
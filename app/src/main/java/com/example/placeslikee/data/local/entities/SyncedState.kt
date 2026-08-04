package com.example.placeslikee.data.local.entities

enum class SyncState{
    SYNCED,
    PENDING_LIKED,
    PENDING_UNLIKED,
    PENDING_UPDATE,
    PENDING_CREATE,
    PENDING_DELETE
}
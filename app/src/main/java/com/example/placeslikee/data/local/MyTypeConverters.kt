package com.example.placeslikee.data.local

import androidx.room.TypeConverter
import com.example.placeslikee.data.local.entities.marks.SyncState

class MyTypeConverters {
    @TypeConverter
    fun fromSynState(value: SyncState):String{
        return value.name
    }
    @TypeConverter
    fun toSyncState(value : String): SyncState{
        return try{
            SyncState.valueOf(value)
        }
        catch (e: Exception){
            SyncState.PENDING_UPDATE
        }
    }
}
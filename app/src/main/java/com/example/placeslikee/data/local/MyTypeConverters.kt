package com.example.placeslikee.data.local

import androidx.room.TypeConverter
import com.example.placeslikee.data.local.entities.marks.SyncState
import com.example.placeslikee.data.local.entities.pending.PendingAction

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
            SyncState.SYNCED
        }
    }
    @TypeConverter
    fun fromPending(value: PendingAction): String{
        return value.name
    }
    @TypeConverter
    fun toPending(value: String):PendingAction{
        return PendingAction.valueOf(value)
    }
}
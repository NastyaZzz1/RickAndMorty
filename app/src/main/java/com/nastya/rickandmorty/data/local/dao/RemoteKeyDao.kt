package com.nastya.rickandmorty.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nastya.rickandmorty.data.local.entity.RemoteKeys

@Dao
interface RemoteKeyDao {
    @Query("SELECT * FROM remote_keys WHERE characterId = :id")
    suspend fun remoteKeysByCharacterId(id: Int): RemoteKeys?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRemote(list: List<RemoteKeys>)

    @Query("DELETE FROM remote_keys")
    suspend fun clearAll()
}
package com.nastya.rickandmorty.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nastya.rickandmorty.data.local.dao.CharacterDao
import com.nastya.rickandmorty.data.local.dao.RemoteKeyDao
import com.nastya.rickandmorty.data.local.entity.CharacterEntity
import com.nastya.rickandmorty.data.local.entity.RemoteKeys

@Database(
    entities = [CharacterEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class RickAndMortyDatabase : RoomDatabase()  {
    abstract val characterDao: CharacterDao
    abstract val remoteKeyDao: RemoteKeyDao

    companion object {
        @Volatile
        private var INSTANCE: RickAndMortyDatabase? = null

        fun getInstance(context: Context): RickAndMortyDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RickAndMortyDatabase::class.java,
                        "rick_and_morty_database"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
package com.example.moviesapp.data.local.room.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviesapp.data.local.room.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Query("SELECT * FROM movies WHERE isFavorite = 1")
    fun getFavoriteMovies(): Flow<List<MovieEntity>>

    @Query("UPDATE movies SET isFavorite = :isFavorite WHERE id = :movieId")
    suspend fun updateFavorite(movieId: Int, isFavorite: Boolean)

    @Query("SELECT * FROM movies WHERE cachedAt > :cacheThreshold")
    fun getCachedMovies(cacheThreshold: Long): Flow<List<MovieEntity>>

    @Query("DELETE FROM movies WHERE cachedAt < :cacheThreshold")
    suspend fun clearOldCache(cacheThreshold: Long)
}
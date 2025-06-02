package com.example.moviesapp.data.remote.api

import com.example.moviesapp.data.remote.model.dto.MovieDetailsDto
import com.example.moviesapp.data.remote.model.response.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

    @GET("discover/movie")
    suspend fun getMovies(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String
    ): MovieResponse

    @GET("movie/{movieId}")
    suspend fun getMovieDetails(
        @Path("movieId") movieId: Int,
        @Query("api_key") apiKey: String
    ): MovieDetailsDto
}
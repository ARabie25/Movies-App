package com.example.moviesapp.di

import com.example.moviesapp.BuildConfig
import com.example.moviesapp.data.local.room.dao.MovieDao
import com.example.moviesapp.data.local.room.db.AppDatabase
import com.example.moviesapp.data.remote.api.MovieApi
import com.example.moviesapp.data.repository.MovieRepositoryImpl
import com.example.moviesapp.domain.repository.MovieRepository
import com.example.moviesapp.domain.usecases.GetMovieDetailsUseCase
import com.example.moviesapp.domain.usecases.GetMoviesUseCase
import com.example.moviesapp.domain.usecases.ToggleFavoriteUseCase
import com.example.moviesapp.helper.utils.NetworkUtil
import com.example.moviesapp.ui.home.viewModel.MoviesViewModel
import com.example.moviesapp.ui.movieDetails.viewModel.MovieDetailsViewModel
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single { provideApiKey() }
    single { AppDatabase.getDatabase(get()).movieDao() }
    single { NetworkUtil(get()) }
    single<MovieRepository> { MovieRepositoryImpl(get(), get(), get(), get()) }
    single { GetMoviesUseCase(get()) }
    single { ToggleFavoriteUseCase(get()) }
    single { GetMovieDetailsUseCase(get()) }
    viewModel { MoviesViewModel(get(), get(), get()) }
    viewModel { MovieDetailsViewModel(get(), get(), get()) }
}

val networkModule = module {
    single { provideOkHttpClient() }
    single { provideRetrofit(get()) }
    single { get<Retrofit>().create(MovieApi::class.java) }
}

fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            chain.proceed(request)
        }
        .build()
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideApiKey(): String {
    return BuildConfig.TMDB_API_KEY
}

package com.nedaluof.qurany.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nedaluof.qurany.data.datasource.remotesource.api.ApiService
import com.nedaluof.qurany.data.datasource.remotesource.api.ApiService.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by nedaluof on 12/11/2020.
 */
@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

  @Singleton
  @Provides
  fun provideHttpLoggingInterceptor() =
    HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BODY
    }

  @Singleton
  @Provides
  fun provideOkHttpClient(interceptor: HttpLoggingInterceptor) =
    OkHttpClient.Builder()
      .addInterceptor(interceptor)
      .readTimeout(180, TimeUnit.SECONDS)
      .connectTimeout(180, TimeUnit.SECONDS)
      .build()

  @Singleton
  @Provides
  fun provideGson(): Gson = GsonBuilder().setLenient().create()!!

  @Singleton
  @Provides
  fun provideRetrofitClient(client: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()

  @Singleton
  @Provides
  fun provideApiService(client: Retrofit): ApiService =
    client.create(ApiService::class.java)
}

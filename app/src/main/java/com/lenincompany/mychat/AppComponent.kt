package com.lenincompany.mychat

import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.network.ApiService
import com.lenincompany.mychat.network.ApiServiceScalar
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideDataRepository(
        @Named("jsonApiService") apiService: ApiService,
        @Named("scalarApiService") apiServiceScalar: ApiServiceScalar
    ): DataRepository {
        return DataRepository(apiService)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:5093/"

    @Provides
    @Singleton
    @Named("httpLoggingInterceptor")
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    @Provides
    @Singleton
    @Named("httpClient")
    fun provideOkHttpClient(@Named("httpLoggingInterceptor") loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("jsonRetrofit")
    fun provideRetrofit(@Named("httpClient") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("scalarRetrofit")
    fun provideRetrofitScalar(@Named("httpClient") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("jsonApiService")
    fun provideApiService(@Named("jsonRetrofit") retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    @Named("scalarApiService")
    fun provideApiServiceScalar(@Named("scalarRetrofit") retrofitScalar: Retrofit): ApiServiceScalar {
        return retrofitScalar.create(ApiServiceScalar::class.java)
    }
}

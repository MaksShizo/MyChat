package com.lenincompany.mychat

import android.content.Context
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.network.ApiService
import com.lenincompany.mychat.network.ApiServiceScalar
import com.lenincompany.mychat.ui.auth.LoginActivity
import com.lenincompany.mychat.ui.auth.RegisterActivity
import com.lenincompany.mychat.ui.chat.ChatActivity
import com.lenincompany.mychat.ui.main.MainActivity
import com.lenincompany.mychat.ui.main.chats.ChatsFragment
import com.lenincompany.mychat.ui.main.contacts.ContactsFragment
import com.lenincompany.mychat.ui.main.settings.SettingsFragment
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, AndroidInjectionModule::class, ActivityBindingModule::class])
interface AppComponent : AndroidInjector<MainApplication> {

    fun inject(activity: MainActivity)
    fun inject(activity: ChatActivity)
    fun inject(activity: LoginActivity)
    fun inject(activity: RegisterActivity)
    fun inject(fragment: ChatsFragment)
    fun inject(fragment: SettingsFragment)
    fun inject(fragment: ContactsFragment)
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder // Передаем Context

        fun build(): AppComponent // Строим компонент
    }
}

@Module
object AppModule {

    @Provides
    fun provideDataRepository(@Named("jsonApiService") apiService: ApiService, @Named("scalarApiService") apiServiceScalar: ApiServiceScalar): DataRepository {
        return DataRepository(apiService, apiServiceScalar)
    }
}

@Module
class NetworkModule {
    companion object {
        private const val BASE_URL = "http://10.0.2.2:5093/"

        @Provides
        @Singleton
        @Named("httpLoggingInterceptor")
        fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY // Уровни: NONE, BASIC, HEADERS, BODY
            return interceptor
        }

        @Provides
        @Singleton
        @Named("httpClient")
        fun provideOkHttpClient(@Named("httpLoggingInterceptor") loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor) // Подключаем логирование
                .build()
        }

        @Provides
        @Singleton
        @Named("jsonRetrofit")
        fun provideRetrofit(
            @Named("httpClient") okHttpClient: OkHttpClient
        ): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient) // Используем клиент с логированием
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        // Retrofit для скалярных значений (например, для строк)
        @Provides
        @Singleton
        @Named("scalarRetrofit")
        fun provideRetrofitScalar(
            @Named("httpClient") okHttpClient: OkHttpClient
        ): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient) // Используем клиент с логированием
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        }

        // ApiService для работы с JSON-ответами
        @Provides
        @Singleton
        @Named("jsonApiService")
        fun provideApiService(@Named("jsonRetrofit") retrofit: Retrofit): ApiService {
            return retrofit.create(ApiService::class.java)
        }

        // ApiService для работы с скалярными значениями (строками)
        @Provides
        @Singleton
        @Named("scalarApiService")
        fun provideApiServiceScalar(@Named("scalarRetrofit") retrofitScalar: Retrofit): ApiServiceScalar {
            return retrofitScalar.create(ApiServiceScalar::class.java)
        }
    }
}

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    abstract fun bindChatsActivity(): MainActivity
    @ContributesAndroidInjector
    abstract fun bindChatActivity(): ChatActivity
    @ContributesAndroidInjector
    abstract fun bindLoginActivity(): LoginActivity
    @ContributesAndroidInjector
    abstract fun bindRegisterActivity(): RegisterActivity
    @ContributesAndroidInjector
    abstract fun bindChatsFragment(): ChatsFragment
    @ContributesAndroidInjector
    abstract fun bindSettingsFragment(): SettingsFragment
    @ContributesAndroidInjector
    abstract fun bindContactsFragment(): ContactsFragment
}
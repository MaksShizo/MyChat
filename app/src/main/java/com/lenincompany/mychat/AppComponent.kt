package com.lenincompany.mychat

import android.content.Context
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.network.ApiService
import com.lenincompany.mychat.ui.chat.ChatActivity
import com.lenincompany.mychat.ui.chats.ChatsActivity
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, AndroidInjectionModule::class, ActivityBindingModule::class])
interface AppComponent : AndroidInjector<MainApplication> {

    fun inject(activity: ChatsActivity)
    fun inject(activity: ChatActivity)
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
    fun provideDataRepository(apiService: ApiService): DataRepository {
        return DataRepository(apiService)
    }
}

@Module
class NetworkModule {
    companion object {
        private const val BASE_URL = "http://10.0.2.2:5093/"

        @Provides
        @Singleton
        fun provideRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
        }

        @Provides
        @Singleton
        fun provideApiService(retrofit: Retrofit): ApiService {
            return retrofit.create(ApiService::class.java)
        }
    }
}

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    abstract fun bindChatsActivity(): ChatsActivity
    @ContributesAndroidInjector
    abstract fun bindChatActivity(): ChatActivity
}
package android.example.caloriesburn.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.example.caloriesburn.db.RunningDatabase
import android.example.caloriesburn.other.Constants.KEY_FIRST_TIME_TOGGLE
import android.example.caloriesburn.other.Constants.KEY_NAME
import android.example.caloriesburn.other.Constants.KEY_WEIGHT
import android.example.caloriesburn.other.Constants.RUNNING_DATABASE_NAME
import android.example.caloriesburn.other.Constants.SHARED_PREFERENCES_NAME
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunningDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
       app,
       RunningDatabase::class.java,
        RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDAO(db: RunningDatabase) = db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context) =
         app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun providesName(sharedPref: SharedPreferences) =  sharedPref.getString(KEY_NAME, " ") ?: ""

    @Singleton
    @Provides
    fun providesWeight(sharedPref: SharedPreferences) =  sharedPref.getFloat(KEY_WEIGHT, 80f)

    @Singleton
    @Provides
    fun providesFirstTimeToogle(sharedPref: SharedPreferences) =
        sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE, true)
}












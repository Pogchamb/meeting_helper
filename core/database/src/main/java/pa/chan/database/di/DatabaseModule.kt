package pa.chan.database.di


import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pa.chan.database.AppDatabase
import pa.chan.database.RecordRepositoryImpl
import pa.chan.database.dao.RecordDao
import pa.chan.domain.repository.RecordRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "AudioRecord_DB").build()
        }

        @Provides
        fun provideDao(appDatabase: AppDatabase): RecordDao {
            return appDatabase.recordDao()
        }
    }


    @Binds
    abstract fun bindRecordRepository(impl: RecordRepositoryImpl): RecordRepository
}
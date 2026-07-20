package pa.chan.work.di

import android.content.Context
import androidx.work.WorkManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pa.chan.domain.schedulers.TranscriptionScheduler
import pa.chan.work.schedulers.TranscriptionSchedulerImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class WorkModule {

    companion object {
        @Provides
        @Singleton
        fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
            return WorkManager.getInstance(context)
        }
    }

    @Binds
    abstract fun bindTranscriptionScheduler(impl: TranscriptionSchedulerImpl): TranscriptionScheduler
}
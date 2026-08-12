package pa.chan.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pa.chan.data.AudioFileReaderImpl
import pa.chan.data.RecordRepositoryImpl
import pa.chan.data.SummaryRepositoryImpl
import pa.chan.domain.contracts.AudioFileReader
import pa.chan.domain.repository.SummaryRepository
import pa.chan.domain.repository.RecordRepository


@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindRecordRepository(impl: RecordRepositoryImpl): RecordRepository

    @Binds
    abstract fun bindAudioFileReader(impl: AudioFileReaderImpl): AudioFileReader

    @Binds
    abstract fun bindSummaryRepository(impl: SummaryRepositoryImpl): SummaryRepository
}
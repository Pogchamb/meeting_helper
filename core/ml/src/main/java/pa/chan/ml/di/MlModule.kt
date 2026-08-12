package pa.chan.ml.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pa.chan.domain.contracts.Transcriber
import pa.chan.ml.WhisperEngine


@Module
@InstallIn(SingletonComponent::class)
abstract class MlModule {

    @Binds
    abstract fun bindTranscriber(impl: WhisperEngine): Transcriber

}
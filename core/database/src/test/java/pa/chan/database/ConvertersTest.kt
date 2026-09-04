package pa.chan.database

import org.junit.Test

import org.junit.Assert.*
import pa.chan.domain.enums.RecordSessionStatus
import pa.chan.database.typeConverters.Converters

class ConvertersTest {

    @Test
    fun `status roundtrip is reversible`() {
        val converters = Converters()
        RecordSessionStatus.entries.forEach { status ->
            assertEquals(status, converters.stringToStatusEnum(converters.fromStatusEnum(status)))
        }
    }
}
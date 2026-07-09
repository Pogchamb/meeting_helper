package pa.chan.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import pa.chan.database.enums.RecordSessionStatus
import java.util.Date

@Entity
data class RecordSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val pathToFile: String,
    val status: RecordSessionStatus?,
    val text: String?,
    val date: Date
)

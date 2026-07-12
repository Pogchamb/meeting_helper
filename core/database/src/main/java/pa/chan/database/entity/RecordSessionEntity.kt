package pa.chan.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import pa.chan.domain.enums.RecordSessionStatus
import java.util.Date

@Entity
data class RecordSessionEntity(

    val pathToFile: String,
    val status: RecordSessionStatus,
    val text: String?,
    val date: Date
) {
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
}

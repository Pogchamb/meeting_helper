package pa.chan.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import pa.chan.domain.enums.RecordSessionStatus
import pa.chan.domain.models.RecordSessionModel
import java.util.Date

@Entity
data class RecordSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pathToFile: String,
    val status: RecordSessionStatus,
    val text: String?,
    val date: Date
)

fun RecordSessionEntity.toModel(): RecordSessionModel {
    return RecordSessionModel(
        this.id,
        this.pathToFile,
        this.status,
        this.text,
        this.date
    )
}

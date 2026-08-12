package pa.chan.data.mappers

import pa.chan.database.entity.RecordSessionEntity
import pa.chan.domain.models.RecordSessionModel

fun RecordSessionEntity.toModel(): RecordSessionModel {
    return RecordSessionModel(
        this.id,
        this.pathToFile,
        this.status,
        this.text,
        this.date,
        this.summary
    )
}
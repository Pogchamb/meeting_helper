package pa.chan.meeting_helper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import pa.chan.domain.models.RecordSessionModel

class RecordsAdapter(): ListAdapter<RecordSessionModel, RecordsViewHolder>(RecordDiffCallback()) {




    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecordsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_record, parent, false)
        return RecordsViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecordsViewHolder,
        position: Int
    ) {
        val record = getItem(position)

        val text = "ID: ${record.id} | Статус: ${record.status.name} \n Текст: ${record.text ?: "Ожидание..."}"

        holder.tvRecordInfo.text = text
    }

}
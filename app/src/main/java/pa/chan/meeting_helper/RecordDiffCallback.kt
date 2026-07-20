package pa.chan.meeting_helper

import androidx.recyclerview.widget.DiffUtil
import pa.chan.domain.models.RecordSessionModel

class RecordDiffCallback : DiffUtil.ItemCallback<RecordSessionModel>() {
    override fun areItemsTheSame(
        oldItem: RecordSessionModel,
        newItem: RecordSessionModel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: RecordSessionModel,
        newItem: RecordSessionModel
    ): Boolean {
        return oldItem == newItem
    }
}
package pa.chan.meeting_helper

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecordsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val tvRecordInfo: TextView = itemView.findViewById(R.id.tv_record_info)
}
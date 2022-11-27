package com.white.meters.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.white.meters.data.models.HistoryData
import com.white.meters.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat

class HistoryAdapter: RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var items: List<HistoryData> = emptyList()

    class HistoryViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val str = dateFormat.format(item.date)
            tvItemHistoryDate.text = str

            tvItemHistoryTitle1.text = "Ванная холодная"
            tvItemHistorySubtitle1.text = item.meterData.coldBathMeterData?.value

            tvItemHistoryTitle2.text = "Ванная горячая"
            tvItemHistorySubtitle2.text = item.meterData.hotBathMeterData?.value

            tvItemHistoryTitle3.text = "Кухня холодная"
            tvItemHistorySubtitle3.text = item.meterData.coldKitchenMeterData?.value

            tvItemHistoryTitle4.text = "Кухня горячая"
            tvItemHistorySubtitle4.text = item.meterData.hotKitchenMeterData?.value
        }
    }

    override fun getItemCount(): Int = items.size

    fun setItems(items: List<HistoryData>?) {
        if (items != null) {
            this.items = items
            notifyDataSetChanged()
        }
    }
}
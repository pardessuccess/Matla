package com.sensomedi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensomedi.data.Detail
import com.sensomedi.data.MatlaData
import com.sensomedi.matla.databinding.ItemDataBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DataAdapter(val clickListener: (Detail) -> Unit) : RecyclerView.Adapter<DataAdapter.DataItemViewHolder>() {

    private var dataList: ArrayList<Detail> = arrayListOf()

    inner class DataItemViewHolder(private val binding: ItemDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Detail) = with(binding) {
            itemCheckBtn.setOnClickListener {
                clickListener(data)
            }
            val timestamp: Long = data.arrayList[0].date
            val sdf = SimpleDateFormat("dd. MM. YYYY HH:mm")
            val date = Date()
            date.time = timestamp
            val dataTime: String = sdf.format(date)
            itemDataTv.text = dataTime
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DataItemViewHolder {
        val view = ItemDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataItemViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    fun setUpdateList(dataList: ArrayList<Detail>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

}
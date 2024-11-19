package com.example.testbackend.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testbackend.R
import com.example.testbackend.model.TestItem

class TsaAdapter(private val testList: List<TestItem>) :
    RecyclerView.Adapter<TsaAdapter.TsaViewHolder>() {

    inner class TsaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Ánh xạ các View từ item layout
        private val txtTestName: TextView = itemView.findViewById(R.id.txtTestOrder)
        private val txtTestDate: TextView = itemView.findViewById(R.id.txtTimeTest)
        private val txtRegistrationFee: TextView = itemView.findViewById(R.id.txtFeeRegister)

        // Gán dữ liệu vào các View
        fun bind(item: TestItem) {
            txtTestName.text = item.testName
            txtTestDate.text = item.testDate
            txtRegistrationFee.text = item.registrationFee
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TsaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tsa, parent, false)
        return TsaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TsaViewHolder, position: Int) {
        holder.bind(testList[position])
    }

    override fun getItemCount(): Int = testList.size
}


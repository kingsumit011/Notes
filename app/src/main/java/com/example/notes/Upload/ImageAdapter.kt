package com.example.notes.Upload


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.notes.R
import com.example.notes.data.local.model.NotesModel
import com.example.notes.utils.GenericClickListener
import com.example.notes.utils.URIPathHelper
import java.net.URI

class ImageAdapter(
    private var dataSet: List<String?>,
    private var listener: GenericClickListener<Int>,
    private var mContext:Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @BindView(R.id.add)
        lateinit var mAdd: TextView
        @BindView(R.id.action_image)
        lateinit var mImage:ImageView


        init {
            ButterKnife.bind(this, view)
            view.setOnClickListener {
                Log.d("Click " , "view click at position $adapterPosition")
                dataSet[adapterPosition].apply {

                    if(adapterPosition == dataSet.size-1 && adapterPosition <= 10){
                        Log.d("Adapter set " , "view click at position $adapterPosition")
                        listener.onClick(0) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_image, parent, false)
        Log.d("Dataset", "Size ${dataSet.size} + $dataSet")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        Log.d("position ", "position $position note = ${dataSet[position]}")
        holder.mAdd.visibility = View.VISIBLE
        holder.mImage.visibility = View.VISIBLE
        dataSet[position]?.apply {
            holder.mAdd.visibility = View.GONE
            holder.mImage.setImageBitmap(URIPathHelper.loadImageFromStorage(this))
        }
    }

    override fun getItemCount() = dataSet.size
    fun updateDataSet(data: List<String?>) {
        this.dataSet = data
    }
}
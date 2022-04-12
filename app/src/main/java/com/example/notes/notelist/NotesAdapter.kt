package com.example.notes.notelist
//
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.notes.R
import com.example.notes.data.local.model.NotesModel
import com.example.notes.utils.GenericClickListener

class NotesAdapter(
    private var dataSet: List<NotesModel>,
    private var listener: GenericClickListener<Int>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @BindView(R.id.title)
        lateinit var title: TextView


        init {
            ButterKnife.bind(this, view)
            view.setOnClickListener {
                dataSet[adapterPosition].apply {
                    Log.d("On Cliclk", "id = $id")
                    listener.onClick(id!!)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_notes_list, parent, false)
        Log.d("Dataset", "Size ${dataSet.size} + $dataSet")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        Log.d("position ", "position $position note = ${dataSet[position]}")
        dataSet[position].apply {
            holder.title.text = this.title
        }
    }

    override fun getItemCount() = dataSet.size
    fun updateDataSet(data: List<NotesModel>) {
        this.dataSet = data
    }
}
package com.example.notes.notelist

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.MainVeiwModel
import com.example.notes.R
import com.example.notes.Upload.NoteAddFragment
import com.example.notes.Upload.NoteUpdate
import com.example.notes.data.local.model.NotesModel
import com.example.notes.utils.GenericClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("NonConstantResourceId")
class NotesList: Fragment() {
    private val viewModel: MainVeiwModel by viewModels(ownerProducer = { requireActivity() })

    lateinit var mRecyclerView:RecyclerView

    lateinit var fab:FloatingActionButton

    private val list = mutableListOf<NotesModel>()

    var userId =""

    private val genericClickListener:GenericClickListener<Int> = object : GenericClickListener<Int>{
        override fun onClick(item: Int) {
            view?.let {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, NoteUpdate.newInstance(item,userId))
                    .commit()

            }
        }

    }
    var mAdapter:NotesAdapter = NotesAdapter(list ,genericClickListener )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notes_list, container, false)
        arguments?.let {
            userId = it.getString("EXTRA_ID").toString()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab = view.findViewById(R.id.fab)
        mRecyclerView = view.findViewById(R.id.list_recycle_view)
        fab.setOnClickListener { view ->
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, NoteAddFragment.newInstance(userId))
                .commit()

        }
        list.clear()

        list.addAll(viewModel.getNotes())
        Log.d("NOtes List", list.toString())
        mAdapter.notifyDataSetChanged()
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = mAdapter
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }
    companion object {
        fun newInstance(id:String) = NotesList().apply {
            arguments = Bundle().apply {
                putString("EXTRA_ID", id)
            }
        }
    }

}
package com.example.notes.notelist

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.notes.MainVeiwModel
import com.example.notes.R
import com.example.notes.Upload.NoteAddFragment
import com.example.notes.Upload.NoteUpdate
import com.example.notes.data.DataManager
import com.example.notes.data.local.model.NotesModel
import com.example.notes.utils.GenericClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("NonConstantResourceId")
class NotesList: Fragment() {
    private val viewModel: MainVeiwModel by viewModels(ownerProducer = { requireActivity() })

    lateinit var mRecyclerView:RecyclerView

    lateinit var fab:FloatingActionButton

    private val list = mutableListOf<NotesModel>()

    private val genericClickListener:GenericClickListener<Int> = object : GenericClickListener<Int>{
        override fun onClick(item: Int) {
            view?.let {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, NoteUpdate.newInstance(item))
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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab = view.findViewById(R.id.fab)
        mRecyclerView = view.findViewById(R.id.list_recycle_view)
        fab.setOnClickListener { view ->
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, NoteAddFragment.newInstance())
                .commit()

        }
        list.clear()
        list.addAll(viewModel.getNotes())
        mAdapter.notifyDataSetChanged()
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
            adapter = mAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
    companion object {
        fun newInstance() = NotesList()
    }

}
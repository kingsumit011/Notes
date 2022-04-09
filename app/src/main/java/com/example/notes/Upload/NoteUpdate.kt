package com.example.notes.Upload

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.notes.MainVeiwModel
import com.example.notes.R
import com.example.notes.data.local.model.NotesModel
import com.example.notes.notelist.NotesList
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.collect

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class NoteUpdate : Fragment() {
    private val viewModel: MainVeiwModel by viewModels(ownerProducer = { requireActivity() })

    lateinit var mTitle:TextInputEditText
    lateinit var mDescription:TextInputEditText
    lateinit var mTitleLayout:TextInputLayout
    lateinit var mDescriptionLayout: TextInputLayout
    lateinit var mSaveButton: Button
     var note:NotesModel? = null
    var mTitleString =""
    var mDescriptionString =""
    var mTitleB = false
    var mDescriptionB = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note_add, container, false)
        ButterKnife.bind(this, view)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSaveButton = view.findViewById(R.id.saved)
        mSaveButton.text = "Update"

        mTitle = view.findViewById(R.id.edittext_title)
        mTitleLayout = view.findViewById(R.id.title_edittext_layout)
        mTitle.addTextChangedListener{
            mTitleString = it.toString()
            if(mTitleString.length <5 || mTitleString.length >100){
                mTitleLayout.isErrorEnabled = true
                mTitleLayout.error ="Title should be min 5 and max 100 character long."
            }else{
                mTitleLayout.isErrorEnabled = false;
            }
            mTitleB = !(mTitleString.length <5 || mTitleString.length >100)
            mSaveButton.isClickable = mTitleB &&mDescriptionB
        }
        mDescription = view.findViewById(R.id.edittext_description)
        mDescriptionLayout = view.findViewById(R.id.description_edittext_layout)
        mDescription.addTextChangedListener{
            mDescriptionString = it.toString()
            if(mDescriptionString.length <100 || mDescriptionString.length >1000){
                mDescriptionLayout.isErrorEnabled = true
                mDescriptionLayout.error ="Title should be min 100 and max 1000 character long."
            }else{
                mDescriptionLayout.isErrorEnabled = false;
            }
            mDescriptionB =!(mDescriptionString.length <100 || mDescriptionString.length >1000)
            mSaveButton.isClickable = mTitleB &&mDescriptionB
        }
        note= viewModel.getNoteById(id)
        note?.let{
            mTitle.setText(it.title)
            mDescription.setText(it.description)
        }


    }
    @OnClick(
        value = [R.id.saved]
    )
    fun onClick(view:View){
        when (view.id){
            R.id.saved->{
                if(view.isClickable){
                    note?.title = mTitleString
                    note?.description = mDescriptionString
                    note?.let {
                        viewModel.updateNote(
                            note = it
                        )
                    }
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.container, NotesList.newInstance())
                        .commit()
                }
            }
        }
    }
    companion object{
        fun newInstance(id:Int)=NoteUpdate()
    }

}
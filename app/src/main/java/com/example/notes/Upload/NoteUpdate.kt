package com.example.notes.Upload

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.example.notes.MainVeiwModel
import com.example.notes.R
import com.example.notes.data.local.model.NotesModel
import com.example.notes.notelist.NotesList
import com.example.notes.utils.GenericClickListener
import com.example.notes.utils.URIPathHelper
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class NoteUpdate : Fragment() {
    private val viewModel: MainVeiwModel by viewModels(ownerProducer = { requireActivity() })
    val PERMISSION_REQUEST_CODE: Int = 10001
    val FILE_REQUEST_CODE = 10002
    lateinit var mTitle: TextInputEditText
    lateinit var mDescription: TextInputEditText
    lateinit var mTitleLayout: TextInputLayout
    lateinit var mDescriptionLayout: TextInputLayout
    lateinit var mSaveButton: Button
    lateinit var mRecyclerView: RecyclerView
    var note: NotesModel? = null
    var mTitleString = ""
    var mDescriptionString = ""
    var mTitleB = false
    var mDescriptionB = false
    var iD = 0
    var useriD = ""
    var list = mutableListOf<String?>()
    lateinit var mAdapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note_add, container, false)
        ButterKnife.bind(this, view)
        super.onCreate(savedInstanceState)
        arguments?.let {
            iD = it.getInt("EXTRA_ID")
            useriD = it.getString("EXTRA_USER_ID").toString()
        }
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSaveButton = view.findViewById(R.id.saved)
        mSaveButton.text = "Update"
        mRecyclerView = view.findViewById(R.id.list_recycle_view)
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
            if (mDescriptionString.length < 10|| mDescriptionString.length > 1000) {
                mDescriptionLayout.isErrorEnabled = true
                mDescriptionLayout.error = "Title should be min 10 and max 1000 character long."
            } else {
                mDescriptionLayout.isErrorEnabled = false;
            }
            mDescriptionB = !(mDescriptionString.length < 10 || mDescriptionString.length > 1000)
            mSaveButton.isClickable = mTitleB && mDescriptionB
        }
        note = viewModel.getNoteById(iD)
        Log.d("Note by id ", "ID $iD and Note = ${note.toString()}")
        note?.let {
            mTitle.setText(it.title)
            mDescription.setText(it.description)
            list = it.image as MutableList<String?>
        }
        mAdapter = ImageAdapter(
            list,
            object : GenericClickListener<Int> {
                override fun onClick(item: Int) {
                    if (askForPermissions() && list.last() == null) {
                        fireUploadIntent()
                    }
                    Log.d("ON CLICK ", "list = $list ")
                }

            },
            requireContext()
        )
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = mAdapter
        }
        mSaveButton.setOnClickListener {
            if (it.isClickable) {
                note?.title = mTitleString
                note?.description = mDescriptionString
                note?.let {
                    viewModel.updateNote(
                        note = it
                    )
                }
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, NotesList.newInstance(useriD))
                    .commit()
            }
        }


    }

    //    @OnClick(
//        value = [R.id.saved]
//    )
//    fun onClick(view:View){
//        when (view.id){
//            R.id.saved->{
//                if(view.isClickable){
//                    note?.title = mTitleString
//                    note?.description = mDescriptionString
//                    note?.let {
//                        viewModel.updateNote(
//                            note = it
//                        )
//                    }
//                    requireActivity().supportFragmentManager.beginTransaction()
//                        .replace(R.id.container, NotesList.newInstance(useriD))
//                        .commit()
//                }
//            }
//        }
//    }
    private fun fireUploadIntent() {
        val mimeTypes = arrayOf("image/*", "application/pdf")

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
        if (mimeTypes.isNotEmpty()) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        startActivityForResult(intent, FILE_REQUEST_CODE)
    }

    private fun isPermissionsAllowed(): Boolean {
        return context?.let {
            ContextCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE)
        } == PackageManager.PERMISSION_GRANTED
    }

    private fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            activity?.let {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        it,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    showPermissionDeniedDialog()
                } else {
                    requestRequiredPermission()
                }
            }
            return false
        }
        return true
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(context)
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions to proceed.")
            .setPositiveButton("App Settings") { _, _ -> requestRequiredPermission() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun requestRequiredPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission is granted, you can perform your operation here
                    fireUploadIntent()
                } else {
                    // permission is denied, you can ask for permission again,
                    askForPermissions()
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that the user selected.
            data?.data?.also { uri ->
                val type = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
                // Perform operations on the document using its URI.
                context?.let {
                    URIPathHelper.saveToInternalStorage(it, uri)?.let { bitmap ->
                        list.add(0, bitmap)
                        if (list.size > 10) {
                            list.removeLast()
                        }
                        mAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(id: Int, userID: String) =
            NoteUpdate().apply {
                arguments = Bundle().apply {
                    putInt("EXTRA_ID", id)
                    putString("EXTRA_USER_ID", userID)
                }
            }
    }
}

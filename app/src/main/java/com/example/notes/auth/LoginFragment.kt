package com.example.notes.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.example.notes.MainVeiwModel
import com.example.notes.R
import com.example.notes.notelist.NotesList
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {

    private lateinit var mId: TextInputEditText
    private lateinit var mPassword: TextInputEditText
    private lateinit var mIdLayout: TextInputLayout
    private lateinit var mPasswordLayout: TextInputLayout
    private lateinit var mLogin:Button
    private lateinit var mSignUp:Button
    private lateinit var mErrorView: TextView
    var iD =""
    var password = ""
    var iDB =false
    var passwordB = false
    private val viewModel: MainVeiwModel by viewModels(ownerProducer = { requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            mSignUp = findViewById(R.id.buttonRegister)
            mLogin = findViewById(R.id.buttonLogin)
            mId = findViewById(R.id.editEmail)
            mIdLayout = findViewById(R.id.email_text_input)
            mPassword = findViewById(R.id.editPassword)
            mPasswordLayout = findViewById(R.id.password_text_input)
            mErrorView = findViewById(R.id.error)
        }
        mSignUp.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, SignUpFragment.newInstance())
                .commit()
        }
        mLogin.setOnClickListener {
            login()
        }
    }
    fun login(){
        if(
            viewModel.checkPassword(
                password = mPassword.text.toString(),
                email = mId.text.toString(),
                phone = mId.text.toString()
            )
        ){
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, NotesList.newInstance(iD))
                .commit()
        }else{
            mIdLayout.isErrorEnabled = true
            mId.error ="User Not Exist"
            mPasswordLayout.isErrorEnabled = true
            mPassword.error ="Password Does'nt match"
        }
    }
    fun validator(){
        mId.addTextChangedListener {
            iD = it.toString()
            iDB = false
            if (iD.length == 0) {
                mIdLayout.isErrorEnabled = true
                mId.error ="Can't be null"
            } else {
                iDB = true
                 mIdLayout.isErrorEnabled = false;
            }
            mLogin.isClickable =iDB && passwordB

        }
        mPassword.addTextChangedListener{
            password = it.toString()
            passwordB = false
            if (password.length == 0) {
                mPasswordLayout.isErrorEnabled = true
                mPassword.error ="Can't be null"
            } else {
                passwordB = true
                mPasswordLayout.isErrorEnabled = false;
            }
            mLogin.isClickable =iDB && passwordB
        }
    }

    companion object {
        fun newInstance() = LoginFragment()
    }
}
package com.example.notes.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.example.notes.MainVeiwModel
import com.example.notes.R
import com.example.notes.data.local.model.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class SignUpFragment : Fragment() {

    private lateinit var mEmail: TextInputEditText
    private lateinit var mPassword: TextInputEditText
    private lateinit var mEmailLayout: TextInputLayout
    private lateinit var mPasswordLayout: TextInputLayout
    private lateinit var mPhone: TextInputEditText
    private lateinit var mName: TextInputEditText
    private lateinit var mPhoneLayout: TextInputLayout
    private lateinit var mNameLayout: TextInputLayout
    private lateinit var mSignUp: Button
    private val viewModel: MainVeiwModel by viewModels(ownerProducer = { requireActivity() })
    var email = ""
    var phone =""
    var name =""
    var password = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            mSignUp = findViewById(R.id.button_register)
            mEmail = findViewById(R.id.edit_email)
            mEmailLayout = findViewById(R.id.email_text_input)
            mPassword = findViewById(R.id.edit_password)
            mPasswordLayout = findViewById(R.id.password_text_input)
            mPhone = findViewById(R.id.edit_phone)
            mPhoneLayout = findViewById(R.id.phone_text_input)
            mName = findViewById(R.id.editName)
            mNameLayout = findViewById(R.id.name_text_input)
        }
        mSignUp.setOnClickListener {
            val user = User(name = mName.text.toString() , email = mEmail.text.toString() , phoneNo = (mPhone.text.toString()) , password = mPassword.text.toString())
            viewModel.register(user)
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .commit()
        }
        validator()
    }
    private fun validator(){
        mEmail.addTextChangedListener{
            email = it.toString()
        }
        mName.addTextChangedListener {
            name = it.toString()
        }
        mPassword.addTextChangedListener{
            password = it.toString()
        }
        mPhone.addTextChangedListener{
            phone = it.toString()
        }

    }
    companion object {
        @JvmStatic
        fun newInstance() = SignUpFragment()
    }
}
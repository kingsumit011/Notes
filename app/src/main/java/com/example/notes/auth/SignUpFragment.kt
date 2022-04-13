package com.example.notes.auth

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.notes.MainVeiwModel
import com.example.notes.R
import com.example.notes.data.local.model.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern


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
    var emailB = false
    var phoneB =false
    var nameB =false
    var passwordB = false
    private val PASSWORD_PATTERN =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>.]).{8,15}$"
//    private val PHONE_PATTER = "^[6-9]\\d{9}\$"
    private val PHONE_PATTER = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}\$"

    private val pattern = Pattern.compile(PASSWORD_PATTERN)
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
            val isExist = viewModel.UserExist(email = email , phone = phone)
            if(isExist != null){
                mEmailLayout.isErrorEnabled = true
                mEmail.error ="User Already exist"
                mPhoneLayout.isErrorEnabled = true
                mPhone.error ="User Already exist"
                return@setOnClickListener
            }
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
            emailB = false
            if (email.length == 0) {
                mEmailLayout.isErrorEnabled = true
                mEmail.error ="Can't be null"
            }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.length<=5 || email.length >= 25 ){
                mEmailLayout.isErrorEnabled = true
                mEmail.error ="Invalid Email"
                Log.d("invalid","email = $email + patter ${pattern.matcher(password)} + isvalid = ${Patterns.EMAIL_ADDRESS.matcher(email).matches()} ")

            } else {
                emailB = true
                mEmailLayout.isErrorEnabled = false;
            }

            mSignUp.isClickable = emailB && nameB && passwordB && phoneB
        }
        mName.addTextChangedListener {
            name = it.toString()
            nameB = false
            if (name.length == 0) {
                mNameLayout.isErrorEnabled = true
                mName.error ="Can't be null"
            } else {
                nameB = true
                mNameLayout.isErrorEnabled = false;
            }

            mSignUp.isClickable = emailB && nameB && passwordB && phoneB
        }
        mPassword.addTextChangedListener{
            password = it.toString()
            passwordB = false
            if (password.isEmpty()) {
                mPasswordLayout.isErrorEnabled = true
                mPassword.error ="Can't be null"
            }else if(!pattern.matcher(password).matches() || password.contains(name)){
                mPasswordLayout.isErrorEnabled = true
                mPassword.error ="Invalid Password"
                Log.d("invalid","password = $password + patter ${pattern.matcher(password)} + isvalid = ${pattern.matcher(password).matches()} ")

            } else {
                passwordB = true
                mPasswordLayout.isErrorEnabled = false;
            }

            mSignUp.isClickable = emailB && nameB && passwordB && phoneB
        }
        mPhone.addTextChangedListener{
            phone = it.toString()
            phoneB = false
            if (phone.isEmpty()) {
                mPhoneLayout.isErrorEnabled = true
                mPhone.error ="Can't be null"
            }else if(!Pattern.compile(PHONE_PATTER).matcher(phone).matches()){
                mPhoneLayout.isErrorEnabled = true
                mPhone.error ="Invalid PHone No"
                Log.d("invalid","phoneNo = $phone + isvalid = ${Pattern.compile(PHONE_PATTER).matcher(password).matches()} ")

            } else {
                phoneB = true
                mPhoneLayout.isErrorEnabled = false;
            }

            mSignUp.isClickable = emailB && nameB && passwordB && phoneB
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignUpFragment()
    }
}
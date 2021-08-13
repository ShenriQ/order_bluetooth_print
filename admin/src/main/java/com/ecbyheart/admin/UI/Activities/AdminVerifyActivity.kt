package com.ecbyheart.admin.UI.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.Nullable
import com.ecbyheart.admin.R
import core.Services.UserService
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import core.Listeners.ServiceListener
import core.Models.User
import core.UI.BaseActivity
import core.Utils.AppLog
import kotlinx.coroutines.*
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

//Coppied code from
//https://github.com/AbdullahAimen/firebaseCodeLab/blob/master/otpAuth/src/main/java/com/authentication/otp/MainActivity.kt
class AdminVerifyActivity : BaseActivity(), View.OnClickListener {
    override fun initializeComponents() {
    }

    override fun setupListeners() {
    }

    val TIME_OUT = 60
    lateinit var code1: EditText
    lateinit var code2: EditText
    lateinit var code3: EditText
    lateinit var code4: EditText
    lateinit var code5: EditText
    lateinit var code6: EditText
    lateinit var timer: TextView

    lateinit var mVerifyButton: Button
    lateinit var mResendButton: Button
    lateinit var backBtn: ImageButton

    private var mAuth: FirebaseAuth? = null
    private var mResendToken: ForceResendingToken? = null
    private var mCallbacks: OnVerificationStateChangedCallbacks? = null
    var mVerificationId: String? = null

    private val TAG = "PhoneAuthActivity"

    var job: Deferred<Unit>? = null
    var user: User? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)
        if (intent.hasExtra("user"))
            user = intent.getSerializableExtra("user") as User
        code1 = findViewById(R.id.code1)
        code2 = findViewById(R.id.code2)
        code3 = findViewById(R.id.code3)
        code4 = findViewById(R.id.code4)
        code5 = findViewById(R.id.code5)
        code6 = findViewById(R.id.code6)
        timer = findViewById(R.id.timer)

        backBtn = findViewById(R.id.back)

        focusInput(code1)
        code1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                if(s.length == 1) {
                    focusInput(code2)
                }
            }
            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if(s.length == 1) {
                    focusInput(code2)
                }
            }
        })
        code2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                if(s.length == 1) {
                    focusInput(code3)
                }
            }
            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if(s.length == 1) {
                    focusInput(code3)
                }
            }
        })
        code3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                if(s.length == 1) {
                    focusInput(code4)
                }
            }
            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if(s.length == 1) {
                    focusInput(code4)
                }
            }
        })
        code4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                if(s.length == 1) {
                    focusInput(code5)
                }
            }
            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if(s.length == 1) {
                    focusInput(code5)
                }
            }
        })
        code5.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                if(s.length == 1) {
                    focusInput(code6)
                }
            }
            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if(s.length == 1) {
                    focusInput(code6)
                }
            }
        })
        code6.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                if(s.length == 1) {
//                    code6.clearFocus()
                }
            }
            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if(s.length == 1) {
//                    code6.clearFocus()
                }
            }
        })

        mVerifyButton = findViewById(R.id.button_verify_phone)
        mResendButton = findViewById(R.id.button_resend)

        mVerifyButton.setOnClickListener(this)
        mResendButton.setOnClickListener(this)
        backBtn.setOnClickListener(this)

        mVerifyButton.isEnabled = true

        mAuth = FirebaseAuth.getInstance()
        mCallbacks = object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AppLog.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                AppLog.w(TAG, "onVerificationFailed", e)
                if (e is FirebaseAuthInvalidCredentialsException) {
                    timer.text = getString(R.string.invalid_phone_number)
                } else if (e is FirebaseTooManyRequestsException) {
                    Snackbar.make(
                        findViewById(R.id.content), "Quota exceeded.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                else
                {
                    showDialog("Verification Failed!", e.message, "OK", { dialogInterface, i ->
                        //
                    }, null, null)
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: ForceResendingToken
            ) {
                AppLog.d(TAG, "onCodeSent:$verificationId")
                mVerificationId = verificationId
                mResendToken = token

                job = if (job == null || job!!.isCancelled) {
                    countDown()
                } else {
                    job!!.cancel()
                    countDown()
                }
                mResendButton.isEnabled = false
            }
        }

        startPhoneNumberVerification(user?.phone!!)
    }

    private fun focusInput(input : EditText) {
        input.requestFocus()
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    AppLog.d(TAG, "signInWithCredential:success")
                    if (job != null && job!!.isActive)
                        job!!.cancel()
                    val firebaseUser = task.result!!.user
                    user?.id = firebaseUser?.uid
                    user?.token = User.getToken()
                    when (user?.name) {
                        null -> {//Login
                            /*UserService.startUserUpdates(object : ServiceListener<User?, String?> {
                                override fun success(success: User?) {
                                    AppLog.i("User Update Service: ", "Started!");

                                }

                                override fun error(error: String?) {
                                    AppLog.i("User Update Service: ", error);
                                    toast(error)
                                }
                            });*/
                            onStartActivityWithClearStack(Intent(this@AdminVerifyActivity, AdminHomeActivity::class.java))
                            finish()
                        }
                        else -> {//Register
                            showLoader()
                            UserService.registerUser(user!!,
                                object : ServiceListener<String, String> {
                                    override fun success(success: String) {
                                        hideLoader()
                                        toast(success)
                                        onStartActivityWithClearStack(
                                            Intent(
                                                this@AdminVerifyActivity,
                                                AdminHomeActivity::class.java
                                            )
                                        )
                                        finish()
                                    }

                                    override fun error(error: String) {
                                        hideLoader()
                                        toast(error)
                                    }

                                })
                        }
                    }

                } else {
                    AppLog.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        toast(getString(R.string.invalid_code))
                    }
                }
            }
    }


    private fun startPhoneNumberVerification(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,  // Phone number to verify
            TIME_OUT.toLong(),  // Timeout duration
            TimeUnit.SECONDS,  // Unit of timeout
            this,  // Activity (for callback binding)
            mCallbacks!!
        ) // OnVerificationStateChangedCallbacks
    }

    private fun verifyPhoneNumberWithCode(
        verificationId: String?,
        code: String
    ) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: ForceResendingToken?
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,  // Phone number to verify
            TIME_OUT.toLong(),  // Timeout duration
            TimeUnit.SECONDS,  // Unit of timeout
            this,  // Activity (for callback binding)
            mCallbacks!!,  // OnVerificationStateChangedCallbacks
            token
        ) // ForceResendingToken from callbacks
    }

/*    private fun validatePhoneNumber(): Boolean {
        val phoneNumber = mPhoneNumberField!!.text.toString()
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField!!.error = "Invalid phone number."
            return false
        }
        return true
    }*/


    override fun onClick(view: View) {
        when (view.id) {
/*            R.id.button_start_verification -> {
                if (!validatePhoneNumber()) {
                    return
                }
                startPhoneNumberVerification(mPhoneNumberField!!.text.toString())
            }*/
            R.id.button_verify_phone -> {
                val code1_txt: String = code1.getText().toString()
                val code2_txt: String = code2.getText().toString()
                val code3_txt: String = code3.getText().toString()
                val code4_txt: String = code4.getText().toString()
                val code5_txt: String = code5.getText().toString()
                val code6_txt: String = code6.getText().toString()

                if (TextUtils.isEmpty(code1_txt) ||
                    TextUtils.isEmpty(code2_txt) ||
                    TextUtils.isEmpty(code3_txt) ||
                    TextUtils.isEmpty(code4_txt) ||
                    TextUtils.isEmpty(code5_txt) ||
                    TextUtils.isEmpty(code6_txt)  ) {
                    return
                }
                verifyPhoneNumberWithCode(mVerificationId, code1_txt + code2_txt + code3_txt + code4_txt + code5_txt + code6_txt)
            }
            R.id.button_resend -> resendVerificationCode(
                user?.phone!!,
                mResendToken
            )
            R.id.back -> {
                onGoBack()
            }
        }
    }

    private fun countDown(): Deferred<Unit> {
        return GlobalScope.async(Dispatchers.IO) {
            closeKeyboard()

            repeat(TIME_OUT + 1) {
                val res = DecimalFormat("00").format(TIME_OUT - it)
                println("Kotlin Coroutines World! $res")
                withContext(Dispatchers.Main) {
                    timer.text = "00:$res"
                    if (res == "00") {
                        mResendButton.isEnabled = true
                        mVerifyButton.isEnabled = false
                    }

                }
                delay(1000)
            }
            println("finished")
        }
    }
}
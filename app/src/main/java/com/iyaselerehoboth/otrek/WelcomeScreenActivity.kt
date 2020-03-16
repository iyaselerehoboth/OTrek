package com.iyaselerehoboth.otrek

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.iyaselerehoboth.otrek.databinding.ActivityWelcomeScreenBinding
import java.util.*
import kotlin.concurrent.schedule

class WelcomeScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeScreenBinding
    private lateinit var mCallbackManager: CallbackManager
    private lateinit var mAuth: FirebaseAuth

    private val RC_SIGN_IN : Int = 9001
    //private val CHECKTAG : String = "OTrek Check"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome_screen)

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding.imgbtnFacebook.setOnClickListener { facebookLogin() }

        mAuth = FirebaseAuth.getInstance()

        //Display the login buttons after 1sec. but need to animate it
        Handler().postDelayed({
            runOnUiThread(
                Runnable {
                    binding.bodyLayout.visibility = View.VISIBLE
                })
        }, 1000)
    }

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth.currentUser
    }

    private fun facebookLogin(){
        //Initialize Facebook Login Button
        mCallbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
        LoginManager.getInstance().registerCallback(mCallbackManager, object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                handleFacebookAccessToken(result!!.accessToken)
            }

            override fun onCancel() {
                TODO("Not yet implemented")
            }

            override fun onError(error: FacebookException?) {
                Log.d("OTrek Check", "facebook:onError", error)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Pass the activity result back to the facebook sdk
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token : AccessToken){
        //Show progress bar here.

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this){task ->
                    if(task.isSuccessful){
                        val user = mAuth.currentUser
                    }else{
                        Log.d("OTrek Check", "signInWIthCredential:failure", task.exception)
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                    }

                    //Hide the progress bar here.
                }
    }

    fun signOut(){
        mAuth.signOut()

        //Facebook log out
        LoginManager.getInstance().logOut()
    }
}

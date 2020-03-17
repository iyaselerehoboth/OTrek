package com.iyaselerehoboth.otrek

import android.content.Intent
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.iyaselerehoboth.otrek.ui.RegisterActivity
import com.iyaselerehoboth.otrek.databinding.ActivityWelcomeScreenBinding
import com.iyaselerehoboth.otrek.ui.HomeActivity

class WelcomeScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeScreenBinding
    private lateinit var mCallbackManager: CallbackManager
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object{
        private const val RC_SIGN_IN = 9001
        private const val TAG = "OTrek Check"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Data binding declaration
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome_screen)

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(R.string.default_web_client_id.toString())
                .requestEmail()
                .requestProfile()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.imgbtnFacebook.setOnClickListener { facebookLogin() }
        binding.imgbtnGoogle.setOnClickListener { googleSignIn() }
        binding.btnAlreadyHaveAccount.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
        binding.btnSignIn.setOnClickListener { startActivity(Intent(this, HomeActivity::class.java)) }

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

    fun signUserOut(){
        mAuth.signOut()

        //Facebook log out
        LoginManager.getInstance()?.logOut()

        //Google sign out
        googleSignInClient.signOut()?.addOnCompleteListener {
            //Maybe do something
        }
    }

    private fun facebookLogin(){
        //Initialize Facebook Login Button
        mCallbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
        LoginManager.getInstance().registerCallback(mCallbackManager, object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                firebaseAuthWithFacebook(result!!.accessToken)
            }

            override fun onCancel() {
                TODO("Not yet implemented")
            }

            override fun onError(error: FacebookException?) {
                Log.d("OTrek Check", "facebook:onError", error)
            }
        })
    }

    private fun googleSignIn(){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                //Sign in was successful, authenticate with firebase
                val account = task?.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            }catch (e: ApiException){
                Log.d(TAG, "Google sign in failed", e)
            }

        }else{

            //Pass the activity result back to the facebook sdk
            mCallbackManager.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun firebaseAuthWithFacebook(token : AccessToken){
        //Show progress bar here.

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val user = mAuth.currentUser
                    }else{
                        Log.d("OTrek Check", "signInWIthCredential:failure", task.exception)
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                    }

                    //Hide the progress bar here.
                }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount){
        //Show progress bar here
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val user = mAuth.currentUser
                    }else{
                        Log.d(TAG, "signInWithCredential:failure", task.exception)
                    }

                    //Hide progress bar here
                }
    }

}

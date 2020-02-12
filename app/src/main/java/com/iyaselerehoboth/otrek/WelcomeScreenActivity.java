package com.iyaselerehoboth.otrek;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.iyaselerehoboth.otrek.Database.SessionManager;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeScreenActivity extends AppCompatActivity {
    @BindView(R.id.btn_facebook_login)
    MaterialButton btn_facebook_login;

    @BindView(R.id.btn_email_login)
    MaterialButton btn_email_login;

    @BindView(R.id.btn_google_login)
    MaterialButton btn_google_login;

    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    SessionManager session;
    private FirebaseAuth mAuth;
    private static String TAG = "OTrek CHECK";
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        ButterKnife.bind(this);
        session = new SessionManager(WelcomeScreenActivity.this);
        session.clearUserDetails();

        // In Activity's onCreate() for instance
        // To make the notification bar transparent.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        //Configure Google's sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.oauth2_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            saveUserAndRedirect(currentUser);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            saveUserAndRedirect(currentUser);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
        session.clearUserDetails();
    }

    @OnClick(R.id.btn_email_login)
    public void emailLogin(){
        startActivity(new Intent(WelcomeScreenActivity.this, ProfilePageActivity.class));
    }

    @OnClick(R.id.btn_facebook_login)
    public void facebookLogin(){
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(WelcomeScreenActivity.this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess" + loginResult);
                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(WelcomeScreenActivity.this, "Error: " + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.btn_google_login)
    public void googleLogin(){
        Intent googleSignInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(googleSignInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        Log.d(TAG, "firebaseAuthWithFacebook:" + token);

       AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveUserAndRedirect(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(WelcomeScreenActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        Log.d(TAG, "firebaseAuthWithGoogle: " + acct.getId());

       AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveUserAndRedirect(user);
                    }else{
                        Log.d(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(WelcomeScreenActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN && resultCode == RESULT_OK){
            //Response meant for Google.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                //Google sign in was successful, authenticate with Firebase.
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }catch (ApiException e){
                if(e.getStatusCode() == 7){
                    //Network Error. Retry with good network
                    Toast.makeText(this, "NETWORK ERROR, Kindly check your network and try again", Toast.LENGTH_LONG).show();
                }
                Log.d(TAG, "Google sign in failed ", e);
            }
        }else{
            //Response meant for Facebook.
            //Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void saveUserAndRedirect(FirebaseUser loggedInUser){
        try{
            //Save user basic details into shared preferences.
            session.setUserDetails(loggedInUser.getEmail(), loggedInUser.getDisplayName(), String.valueOf(loggedInUser.getPhotoUrl()));
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        startActivity(new Intent(WelcomeScreenActivity.this, ProfilePageActivity.class));
    }
}

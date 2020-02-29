package hr.markovicSoftware.MatchmakingDummy

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseDatabase : FirebaseDatabase
    private lateinit var authStateListener : FirebaseAuth.AuthStateListener
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    lateinit var viewModel: ViewModel2
    private val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = firebaseDatabase.getReference()
        val providers = listOf(
            AuthUI.IdpConfig.GoogleBuilder().build())
        authStateListener = FirebaseAuth.AuthStateListener {
            val user = firebaseAuth.currentUser
            if(user!=null){
            }
            else{
                startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(), RC_SIGN_IN)
            }
        }
        val popupWindow = PopupWindow(this)
        val popupWindowView = layoutInflater.inflate(R.layout.popup, null)
        popupWindow.contentView = popupWindowView
        popupWindow.isFocusable = true
        firebaseAuth.addAuthStateListener(authStateListener)

        viewModel = ViewModel2(databaseReference, firebaseAuth.currentUser?.uid ?: "", popupWindow, popupWindowView, window.decorView.rootView)
        viewModel.userChallenge.observe(this, Observer {
            if(it==null){
                viewModel.createChallenge()
                viewModel.removeEventListener()
            }
            if(it!=firebaseAuth.currentUser!!.uid && it != null){
                viewModel.removeEventListener()
                viewModel.createGameRoom(firebaseAuth.currentUser!!.uid, it)
            }
        })


        val button: Button = findViewById(R.id.start_match)
        button.setOnClickListener {
                    viewModel.checkExistingChallenges(viewModel.valueEventListenerChallenges)
        }


    }

    override fun onStart() {
        super.onStart()

    }

    override fun onPause() {
        super.onPause()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            if(resultCode == Activity.RESULT_OK){
                viewModel.userId = firebaseAuth.currentUser!!.uid
                Toast.makeText(this, "You just signed in!", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}

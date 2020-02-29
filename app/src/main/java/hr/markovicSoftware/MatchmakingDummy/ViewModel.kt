package hr.markovicSoftware.MatchmakingDummy

import android.provider.ContactsContract
import android.renderscript.Sampler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.popup.view.*

class ViewModel2(private val databaseReference: DatabaseReference,var userId : String,
                 popupWindow: PopupWindow, popupView: View, rootLayout : View
) : ViewModel() {
    val challenges = databaseReference.child("challenges")
    val challengeReference = challenges.child(userId)
    lateinit var dataSnapshot: DataSnapshot
    lateinit var gameRoomRef : DatabaseReference
    var gameRoomRefPath : String? = ""
    val zero = 0
    val userChallenge = MutableLiveData<String?>()
    lateinit var player1 : String
    lateinit var player2 : String

    val valueEventListenerChallenges = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            Log.d("msg", p0.message)
        }

        override fun onDataChange(p0: DataSnapshot) {
            if (p0.childrenCount != zero.toLong()) {
                dataSnapshot = p0.children.first()
                userChallenge.value = dataSnapshot.key
            } else {
                userChallenge.value = null
            }
        }
    }

    val valueEventListenerMatch = object : ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {
            Log.d("msg", p0.message)
        }

        override fun onDataChange(p0: DataSnapshot) {
            if(p0.child("gameRoomRef").getValue(String::class.java) != ""){
                gameRoomRefPath = p0.child("gameRoomRef").getValue(String::class.java)?: ""
                getUsersInRoom()


            }
        }
    }

    val valueEventListenerUsers = object : ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {
            Log.d("msg", p0.message)
        }

        override fun onDataChange(p0: DataSnapshot) {

            player1 = p0.child("player 1").getValue(String::class.java)!!
            player2 = p0.child("player 2").getValue(String::class.java)!!
            popupView.player_1.text = player1
            popupView.player_2.text = player2
            popupWindow.showAtLocation(rootLayout, Gravity.CENTER, 0,0)
            roomCreated()
        }
    }
    fun createGameRoom(currentUserId : String, foundUserId : String){
        gameRoomRef = databaseReference.child("GameRooms").child("$currentUserId"+ "$foundUserId")
        gameRoomRef.child("player 1").setValue(currentUserId)
        gameRoomRef.child("player 2").setValue(foundUserId)
        challenges.child(foundUserId).child("gameRoomRef").setValue(gameRoomRef.key)

    }

    private fun roomCreated(){
        challengeReference.removeEventListener(valueEventListenerMatch)
        challenges.child(userId).setValue(null)
    }
    fun checkExistingChallenges(
        valueEventListener: ValueEventListener
    ) {
        challenges.addValueEventListener(valueEventListener)

    }

    fun removeEventListener() {
        challenges.removeEventListener(valueEventListenerChallenges)
    }

    fun createChallenge() {
        challengeReference.child("gameRoomRef").setValue("")
        challengeReference.addValueEventListener(valueEventListenerMatch)
    }

    fun getUsersInRoom(){
        databaseReference.child("GameRooms").child(gameRoomRefPath!!).addValueEventListener(valueEventListenerUsers)
    }
}
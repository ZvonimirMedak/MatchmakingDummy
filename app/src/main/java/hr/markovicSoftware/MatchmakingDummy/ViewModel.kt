package hr.markovicSoftware.MatchmakingDummy

import android.provider.ContactsContract
import android.renderscript.Sampler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ViewModel2(databaseReference: DatabaseReference) : ViewModel() {
    val challenges = databaseReference.child("challenges")
    lateinit var challengeReference : DatabaseReference
    lateinit var dataSnapshot: DataSnapshot
    lateinit var gameRoomRef : DatabaseReference
    val zero = 0
    val userChallenge = MutableLiveData<String?>()
    val valueEventListenerChallenges = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            Log.d("msg", p0.message)
        }

        override fun onDataChange(p0: DataSnapshot) {
            if (p0.childrenCount != zero.toLong()) {
                dataSnapshot = p0.children.first()
                userChallenge.value = dataSnapshot.getValue(String::class.java)
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
            if(!p0.exists()){

            }
        }
    }
    fun createGameRoom(databaseReference: DatabaseReference, currentUserId : String, foundUserId : String){
        gameRoomRef = databaseReference.child("GameRooms").child("$currentUserId"+ "$foundUserId")
        gameRoomRef.child("player 1").setValue(currentUserId)
        gameRoomRef.child("player 2").setValue(foundUserId)
        roomCreated()
    }

    private fun roomCreated(){
        challengeReference.setValue(null)
    }
    fun checkExistingChallenges(
        valueEventListener: ValueEventListener
    ) {
        challenges.addValueEventListener(valueEventListener)

    }

    fun removeEventListener() {
        challenges.removeEventListener(valueEventListenerChallenges)
    }

    fun createChallenge(databaseReference: DatabaseReference, user: FirebaseUser) {
        challengeReference = challenges.child(user.uid)
        challengeReference.setValue(user.uid)
    }

}
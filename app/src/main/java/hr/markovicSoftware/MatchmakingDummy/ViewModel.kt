package hr.markovicSoftware.MatchmakingDummy

import android.provider.ContactsContract
import android.renderscript.Sampler
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ViewModel2(databaseReference: DatabaseReference, userId : String) : ViewModel() {
    val challenges = databaseReference.child("challenges")
    val challengeReference = challenges.child(userId)
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
            if(!p0.exists()){
                Log.d("Na promjenu", "obrisano")
            }
        }
    }
    fun createGameRoom(databaseReference: DatabaseReference, currentUserId : String, foundUserId : String){
        gameRoomRef = databaseReference.child("GameRooms").child("$currentUserId"+ "$foundUserId")
        gameRoomRef.child("player 1").setValue(currentUserId)
        gameRoomRef.child("player 2").setValue(foundUserId)
        challenges.child(foundUserId).child("gameRoomRef").setValue(gameRoomRef)
    }

    private fun roomCreated(foundUserId: String){
        challenges.child(foundUserId).setValue(null)
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

}
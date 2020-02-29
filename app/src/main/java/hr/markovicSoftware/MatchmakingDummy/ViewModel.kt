package hr.markovicSoftware.MatchmakingDummy

import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ViewModel2 : ViewModel(){

    lateinit var dataSnapshot: DataSnapshot
    val valueEventListenerChallenges = object : ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {
            Log.d("msg", p0.message)
        }

        override fun onDataChange(p0: DataSnapshot) {
            dataSnapshot = p0.children.first()
            userChallenge.value =  dataSnapshot.child("uid").getValue(String::class.java)
        }

    }
    val userChallenge = MutableLiveData<String?>()
    fun checkExistingChallenges(databaseReference: DatabaseReference){
        val challenges = databaseReference.child("challenges")
        challenges.addValueEventListener(valueEventListenerChallenges)
    }

    fun createChallenge(databaseReference: DatabaseReference, user : FirebaseUser){
        val challengeReference = databaseReference.child("challenges")
        challengeReference.child("uid").setValue(user.uid)
    }


}
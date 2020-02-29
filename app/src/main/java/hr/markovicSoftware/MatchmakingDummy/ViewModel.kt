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

class ViewModel2(databaseReference: DatabaseReference) : ViewModel() {
    val challenges = databaseReference.child("challenges")
    lateinit var dataSnapshot: DataSnapshot
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

    fun checkExistingChallenges(
        databaseReference: DatabaseReference,
        valueEventListener: ValueEventListener
    ) {
        challenges.addValueEventListener(valueEventListener)

    }

    fun removeEventListener() {
        challenges.removeEventListener(valueEventListenerChallenges)
    }

    fun createChallenge(databaseReference: DatabaseReference, user: FirebaseUser) {
        val challengeReference = databaseReference.child("challenges")
        challengeReference.child(user.uid).setValue(user.uid)
    }

}
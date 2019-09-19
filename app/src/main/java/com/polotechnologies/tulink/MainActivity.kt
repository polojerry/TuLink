package com.polotechnologies.tulink

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

class MainActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

    }

    override fun onStart() {
        super.onStart()

        if(mAuth.currentUser != null){
            val currentUserId = mAuth.currentUser?.uid
            if(currentUserId?.isNotEmpty()!!){
                databaseReference.child("users")
                    .child(currentUserId)
                    .child("online")
                    .setValue("online")
            }
        }


    }

    override fun onStop() {
        super.onStop()
        if(mAuth.currentUser != null){
            val currentUserId = mAuth.currentUser?.uid
            if(currentUserId?.isNotEmpty()!!){
                databaseReference.child("users")
                    .child(currentUserId)
                    .child("online")
                    .setValue(ServerValue.TIMESTAMP)
            }
        }

    }
}

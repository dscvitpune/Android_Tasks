package com.example.cloudfunctionssample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    lateinit var editText : EditText
    lateinit var rating : RatingBar
    lateinit var AddingButton : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editTextTextPersonName)
        rating = findViewById(R.id.ratingBar)
        AddingButton = findViewById(R.id.button)

        AddingButton.setOnClickListener{
            save()
        }

        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");

    }

    private fun save(){
        val name = editText.text.toString().trim()

        if(name.isEmpty()){
            editText.error = "please enter a value"
            return
        }



        val ref = FirebaseDatabase.getInstance().getReference("movies")
        val movieId = ref.push().key

        val movie = Movie(movieId, name, rating.numStars)

        ref.child(movieId!!).setValue(movie).addOnCompleteListener{
            Toast.makeText(applicationContext,"Movie saved successfully", Toast.LENGTH_LONG).show()
        }

    }
}
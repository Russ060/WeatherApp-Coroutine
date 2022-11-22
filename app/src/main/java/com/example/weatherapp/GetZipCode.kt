package com.example.weatherapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class GetZipCode : AppCompatActivity() {

    lateinit var userInput : EditText
    lateinit var submitButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_zip_code)

        userInput = findViewById(R.id.zipCodeInput)
        submitButton = findViewById(R.id.submitButton)

        submitButton.setOnClickListener { sendZipCode() }
    }

    private fun sendZipCode() {
        if (userInput.text.isNotEmpty()){
            var userZipCode = userInput.text.toString()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("zipCode",userZipCode)
            startActivity(intent)
        }else{
            Toast.makeText(this,"Please Enter your Zip code",Toast.LENGTH_LONG).show()
        }

    }
}
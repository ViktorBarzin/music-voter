package com.example.project.musicvoter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bundle = intent.extras

        val username = bundle.get("username")

        Toast.makeText(this, "Welcome $username", Toast.LENGTH_SHORT).show()


        joinButton.setOnClickListener{
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }

        createButton.setOnClickListener{
            val intent = Intent(this, CreateActivity::class.java)
            intent.putExtra("username", username.toString())
            startActivity(intent)
        }
    }



}

package com.example.project.musicvoter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_join.*

class JoinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        val rooms = arrayListOf<String>("Room1", "Room2", "Room3")
        var dataAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, rooms)
        spinnerJoin.adapter = dataAdapter
    }

}

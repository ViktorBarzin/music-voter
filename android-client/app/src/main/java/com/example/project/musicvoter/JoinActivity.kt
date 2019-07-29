package com.example.project.musicvoter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_join.*

class JoinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        val jsonHandler = JSONHandler()
        val info = jsonHandler.outputFromGet()

        val jsonParser = JSONParser()

        val rooms = jsonParser.parseJSON(info)
        var dataAdapter = ArrayAdapter<Room>(this, android.R.layout.simple_spinner_item, rooms)
        spinnerJoin.adapter = dataAdapter

    }

}

package com.example.project.musicvoter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_join.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create.*
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets


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

        val bundle = intent.extras
        val username = bundle.get("username")
        var roomName: String = ""
        var canJoin: Boolean = false

        spinnerJoin.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {

                roomName = dataAdapter.getItem(position).name
                canJoin = true
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {

            }

        }

        buttonJoin.setOnClickListener {
            if(canJoin){
                sendPost(username.toString(), roomName)

                val intent = Intent(this@JoinActivity, RoomActivity::class.java)
                intent.putExtra("username", username.toString())
                intent.putExtra("group", roomName)
                startActivity(intent)

            } else {
                Toast.makeText(this@JoinActivity, "Please select a Room to join", Toast.LENGTH_SHORT).show()
            }

        }

    }


    private fun sendPost(username: String, roomName: String){
        val url = URL("http://musicvoter.viktorbarzin.me/api/join/$roomName")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"

        val messageToSent = "username=$username"
        val postData: ByteArray = messageToSent.toByteArray(StandardCharsets.UTF_8)

        connection.setRequestProperty("charset", "utf-8")
        connection.setRequestProperty("Content-lenght", postData.size.toString())

        Thread(Runnable {
            try {
                val outputStream = DataOutputStream(connection.outputStream)
                outputStream.write(postData)
                outputStream.flush()
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {

            }
        }).start()
    }


}

package com.example.project.musicvoter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create.*
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*


class CreateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        val bundle = intent.extras

        val username = bundle.get("username")

        Toast.makeText(this, "Welcome $username", Toast.LENGTH_SHORT).show()

        //create a list of items for the spinner.
        val items = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        //set the spinners adapter to the previously created one.
        spinner1.adapter = adapter

        createRoomBtn.setOnClickListener{
            val name = roomName as EditText
            val pass = roomPassword as EditText



            val message = "name=${name.text}&owner_username=$username&password=${pass.text}"

            val url = URL("http://musicvoter.viktorbarzin.me/api/rooms")

            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"


            val postData: ByteArray = message.toByteArray(StandardCharsets.UTF_8)

            connection.setRequestProperty("charset", "utf-8")
            connection.setRequestProperty("Content-lenght", postData.size.toString())

            Thread(Runnable {
                try {
                    val outputStream: DataOutputStream = DataOutputStream(connection.outputStream)
                    outputStream.write(postData)
                    outputStream.flush()
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    try {


                        val reader: BufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
                        val output: String = reader.readLine()

                        println("There was error while connecting the chat $output")
                        System.exit(0)

                    } catch (exception: Exception) {
                        throw Exception("Exception while push the notification  $exception")
                    }
                }

                val intent = Intent(this, RoomActivity::class.java)
                intent.putExtra("username", username.toString())
                startActivity(intent)

            }).start()



        }
    }
}

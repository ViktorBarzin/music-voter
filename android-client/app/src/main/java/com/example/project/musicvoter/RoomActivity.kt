package com.example.project.musicvoter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.ClipboardManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.activity_room.*
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*


class RoomActivity : AppCompatActivity() {
    var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        if(savedInstanceState != null){
            val restored = savedInstanceState.get("username")
            val restored2 = savedInstanceState.getString("username")
        }

        val extras = intent.extras

        username = extras.get("username").toString()

        println("$username--------------------------")

        Toast.makeText(this, "Welcome $username", Toast.LENGTH_SHORT).show()

        if (extras.getString(Intent.EXTRA_TEXT) != null) {
            println("$username--------------------------")
            val link = extras.getString(Intent.EXTRA_TEXT)
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("link", link)
            clipboard.setPrimaryClip(clip)

            val item = clipboard.primaryClip.getItemAt(0)
            val songURL = item.text.toString()

            var title = ""

            val t = Thread( Runnable {
                title = JSONObject(URL("http://www.youtube.com/oembed?url=" +
                        songURL + "&format=json").readText()).getString("title")
            })

            t.start()
            t.join()

            linkContent.text = songURL
            songTitle.text = title

            val url = URL("http://6e43fba8.ngrok.io/api/vote/test")

            val message = "title=${URLEncoder.encode(title, "UTF-8")}&url=$songURL&username=${username}"

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

                print(connection.responseCode)
            }).start()

            Toast.makeText(this, "Text Copied", Toast.LENGTH_SHORT).show()
        }
    }

    @Override
    override fun onSaveInstanceState(savedInstanceState : Bundle){
        super.onSaveInstanceState(savedInstanceState)

        savedInstanceState.putString("username", username)
        println("override1 $username -------------------------------------------------")

    }
}

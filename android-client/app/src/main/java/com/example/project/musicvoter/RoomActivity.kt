package com.example.project.musicvoter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.ClipboardManager
import android.widget.CheckBox
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_room.*
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import android.util.DisplayMetrics
import android.view.ViewGroup


class RoomActivity : AppCompatActivity() {
    var MY_PREFS_NAME = "MyPrefsFile"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
    }

    private fun makeVote(link: String?, username: String, group: String){

        var clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        var clip = ClipData.newPlainText("link", link)
        clipboard.setPrimaryClip(clip)

        var item = clipboard.primaryClip.getItemAt(0)
        var songURL = item.text.toString()

        var title = ""

        var t = Thread( Runnable {
            title = JSONObject(URL("http://www.youtube.com/oembed?url=" +
                    songURL + "&format=json").readText()).getString("title")
        })

        t.start()
        t.join()

        tableLayout.addView(createNewRow(title))

        var url = URL("http://musicvoter.viktorbarzin.me/api/vote/$group")

        var message = "title=${URLEncoder.encode(title, "UTF-8")}&url=$songURL&username=$username"

        var connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"


        var postData: ByteArray = message.toByteArray(StandardCharsets.UTF_8)

        connection.setRequestProperty("charset", "utf-8")
        connection.setRequestProperty("Content-lenght", postData.size.toString())

        Thread(Runnable {
            try {
                var outputStream: DataOutputStream = DataOutputStream(connection.outputStream)
                outputStream.write(postData)
                outputStream.flush()
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            print(connection.responseCode)
        }).start()

        Toast.makeText(this, "Text Copied", Toast.LENGTH_SHORT).show()
    }

    private fun createNewRow(title: String): TableRow? {
        val row = TableRow(this)
        val layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        row.layoutParams = layoutParams
        val checkBox = CheckBox(this)
        val textView = TextView(this)   
        textView.width = (width * 0.8).toInt()
        textView.text = title

        row.addView(textView)
        row.addView(checkBox)

        return row
    }

    @Override
    override fun onStop() {
        super.onStop()

        var extras = intent.extras

        if(extras.get("username") != null){
            var editor = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit()
            editor.putString("username", extras.get("username").toString())
            editor.putString("group", extras.get("group").toString())
            editor.apply()
        }

        println("Application stopped.-----------------------------------------")
    }

    @Override
    override fun onResume() {
        super.onResume()

        println("Application resumed----------------------------------------------")

        var prefs = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)

        var extras = intent.extras

        var username: String
        var group: String

        if(extras.get("username") == null){
            username = prefs.getString("username","No name")
            group = prefs.getString("group","No name")
        }else{
            username = extras.get("username").toString()
            group = extras.get("group").toString()
        }

        if (extras.get(Intent.EXTRA_TEXT) != null) {

            var link = prefs.getString("url", extras.get(Intent.EXTRA_TEXT).toString())
            makeVote(link,username, group)
            extras.remove(Intent.EXTRA_TEXT)
        }

    }

    @Override
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        handleSendIntent(intent)
    }

    private fun handleSendIntent(intent: Intent?) {
        var editor = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit()

        if(intent != null){
            editor.putString("url", intent.extras.get(Intent.EXTRA_TEXT).toString())
            editor.apply()
        }

    }

    @Override
    override fun onDestroy() {
        super.onDestroy()

        getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()

    }
}

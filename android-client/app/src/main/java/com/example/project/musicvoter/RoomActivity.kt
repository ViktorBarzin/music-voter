package com.example.project.musicvoter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_room.*
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets


class RoomActivity : AppCompatActivity() {
    var MY_PREFS_NAME = "MyPrefsFile"
    private val titleUrlMap = HashMap<String, String>()
    private lateinit var username: String
    private lateinit var groupName: String

    //var title = "" //title of the song from YouTube

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        if (intent.extras != null && intent.extras.getString("username") != null) {
            GlobalState.username = intent.extras!!.getString("username")!!
        }

        if (intent.extras != null && intent.extras.getString("group") != null) {
            GlobalState.group = intent.extras!!.getString("group")!!
        }

        this.refreshRoomActivity()
    }

    private fun displayInfoForRoom(rooms: List<Room>){
        var myRoom: Room? = null
       // var targetName = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).getString("group", null)
       for(room in rooms){
            if(room.name == GlobalState.group){
                myRoom = room
                break
            }

        }

        if (myRoom != null) {
            for(voteOption in myRoom.votes){
                // Check if current user has voted
                var hasVoted = voteOption.value.any { x: User -> x.username == GlobalState.username }
                val songTitle = this.getSongTitle(voteOption.key)
                this.titleUrlMap[songTitle] = voteOption.key
                tableLayout.addView(createNewRow(getSongTitle(voteOption.key), voteOption.value.size, hasVoted))

            }
        }
    }

    private fun makeVote(song_url: String, username: String, group: String, addingVote: Boolean){

        val addVoteValue = if (addingVote) {
            "true"
        } else {
            "false"
        }


        var url = URL("http://musicvoter.viktorbarzin.me/api/vote/$group")

        var message = "url=$song_url&username=$username&add_vote=$addVoteValue"

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

    }

    private fun getSongTitle(songURL: String): String{
        var title: JSONObject? = null
        var response: String
        var t = Thread( Runnable {
            try {
                title = JSONObject(URL("""http://www.youtube.com/oembed?url=$songURL&format=json""").readText())
            } catch (e: Exception){
                title = null
            }


        })

        t.start()
        t.join()

        if(title == null){
            response = songURL
        } else{
            response = title!!.getString("title")

        }

        return response
    }

    private fun createNewRow(title: String, votes: Int, checked: Boolean): TableRow? {
        val row = TableRow(this)
        val layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT)


        row.layoutParams = layoutParams

        val textView = TitleTextView(this)
        textView.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 4.0f)
        textView.text = title

        val numVotes = TextView(this)
        numVotes.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 2.0f)
        numVotes.gravity = Gravity.CENTER

        val checkBox = CheckBox(this)
        checkBox.isChecked = checked
        checkBox.setOnCheckedChangeListener {buttonView, isChecked -> handleVoteCheckBoxChecked(buttonView, isChecked)
        }

        checkBox.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1.0f)

        numVotes.text = votes.toString()

        row.addView(textView)
        row.addView(numVotes)
        row.addView(checkBox)

        return row
    }

    /**
     * Handle when a voting check box is (un)selected.
     */
    private fun handleVoteCheckBoxChecked(buttonView: CompoundButton?, checked: Boolean) {
        val gridRowView = buttonView?.parent as TableRow

        // Find child that contains the title
        for (i in 0..gridRowView.childCount) {
            when (gridRowView.getChildAt(i)) {
                is TitleTextView -> {
                    val title: String = (gridRowView.getChildAt(i) as TextView).text.toString()
                    val songUrl = this.titleUrlMap.get(title)!!
                    makeVote(songUrl, GlobalState.username, GlobalState.group, checked)
                }
            }
        }
        this.refreshRoomActivity()
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
            makeVote(link,username, group, true)
            extras.remove(Intent.EXTRA_TEXT)

        }
        this.refreshRoomActivity()
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
            var videoURL = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).getString("url", null)
            sendVotingPost(GlobalState.username, GlobalState.group, videoURL)
        }

    }

    private fun sendVotingPost(username: String, roomName: String, videoURL:String){
        val url = URL("http://musicvoter.viktorbarzin.me/api/vote/$roomName")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"

        val messageToSent = "title=$title&url=$videoURL&username=$username"
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

    @Override
    override fun onDestroy() {
        super.onDestroy()

        getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()

    }

    fun getVoteOptions(view: View){
        Toast.makeText(this, "Refreshing", Toast.LENGTH_SHORT).show()
        this.refreshRoomActivity()
    }

    fun clearCurrentInfo(){
        tableLayout.removeAllViews()
    }

    private fun refreshRoomActivity() {
        this.clearCurrentInfo()
        val rooms = JSONParser().parseJSON(JSONHandler().outputFromGet())
        displayInfoForRoom(rooms)

    }
}


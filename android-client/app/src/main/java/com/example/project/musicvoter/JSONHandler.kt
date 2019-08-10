package com.example.project.musicvoter
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class JSONHandler {
    val httpURLConnectionurl = URL("http://musicvoter.viktorbarzin.me/api/rooms")
    val urlConnection = httpURLConnectionurl.openConnection() as HttpURLConnection
    var response = ""


    fun outputFromGet(): String{

       val t =  Thread(Runnable {
            val inputStream = BufferedInputStream(urlConnection.getInputStream())
            response  = httpURLConnectionurl.readText()

        })

        t.start()
        t.join()

        return response

    }
}
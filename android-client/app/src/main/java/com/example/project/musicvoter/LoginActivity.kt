package com.example.project.musicvoter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener{
            val username = usernameField.text

            val message = "username=$username"

            val url = URL("http://musicvoter.viktorbarzin.me/api/users")

            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"


            val postData: ByteArray = message.toByteArray(StandardCharsets.UTF_8)

            connection.setRequestProperty("charset", "utf-8")
            connection.setRequestProperty("Content-lenght", postData.size.toString())

            if(registerCheckBox.isChecked){
                Thread(Runnable {
                    try {
                        val outputStream: DataOutputStream = DataOutputStream(connection.outputStream)
                        outputStream.write(postData)
                        outputStream.flush()
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }

                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("username", username)
                        startActivity(intent)
                    }
                }).start()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
            }


        }
    }
}

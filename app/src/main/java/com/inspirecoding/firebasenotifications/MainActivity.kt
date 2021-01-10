package com.inspirecoding.firebasenotifications

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.inspirecoding.firebasenotifications.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

const val TOPIC = "/topics/myTopic"

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseService.sharedPref = getSharedPreferences(
            "sharedPref", Context.MODE_PRIVATE
        )

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
            binding.etToken.setText(it.token)
        }

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        binding.btnSend.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val message = binding.etMessage.text.toString().trim()
            val recipientToken = binding.etToken.text.toString().trim()

            if(title.isNotEmpty() && message.isNotEmpty()) {
                PushNotification(
                        NotificationData(
                                title = title,
                                message = message
                        ),
                        recipientToken
                ).also {
                    sendNotification(it)
                }
            }
        }
    }

    private fun sendNotification(
            notification: PushNotification
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {

            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                println("Response: ${Gson().toJson(response)}")
            } else {
                println("Response: ${response.errorBody()}")
            }

        } catch (exception: Exception) {



        }
    }
}
package com.example.projektstudie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projektstudie.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.dummyText.text = "Hallo Welt"
        CoroutineScope(IO).launch{
            fetchData()
        }
    }

    fun fetchData() {
        val url = URL("http://${}:3000/getrestaurants?latitude=123&longitude=123&searchTerm=Currywurst")

        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"  // optional default is GET

            println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")

            inputStream.bufferedReader().use {
                it.lines().forEach { line ->
                    println(line)
                }
            }
        }
    }
}
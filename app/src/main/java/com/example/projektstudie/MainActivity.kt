package com.example.projektstudie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projektstudie.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.dummyText.text = "Hallo Welt"
    }

}
package com.example.projektstudie

import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projektstudie.databinding.ActivityRestaurantBinding

class RestaurantActivity : AppCompatActivity() {

    private lateinit var restaurant: ResponseObject
    private lateinit var binding: ActivityRestaurantBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        restaurant = intent.getSerializableExtra("restaurant") as ResponseObject

        setUpUI()
    }

    private fun setUpUI() {
        binding.textName.text = restaurant.name

        val geocoder = Geocoder(this)

        binding.textLocation.text = geocoder.getFromLocation(restaurant.latitude, restaurant.longitude, 1)[0].getAddressLine(0)
        binding.txvPhone.text = restaurant.phoneNumber
    }
}
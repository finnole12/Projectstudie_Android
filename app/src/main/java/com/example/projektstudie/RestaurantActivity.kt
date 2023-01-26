package com.example.projektstudie

import android.location.Geocoder
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projektstudie.databinding.ActivityRestaurantBinding
import com.squareup.picasso.Picasso

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

        binding.rvwFoodMenu.apply {
            layoutManager = LinearLayoutManager(this@RestaurantActivity)
            adapter = FoodMenuAdapter(this@RestaurantActivity, restaurant.menu)
        }

        binding.btnExpandMenu.setOnClickListener {
            when (binding.rvwFoodMenu.visibility) {
                View.GONE -> {
                    binding.rvwFoodMenu.visibility = View.VISIBLE
                    binding.ivwExpCollIcon.setImageResource(R.drawable.ic_round_keyboard_arrow_up_24)
                }
                else -> {
                    binding.rvwFoodMenu.visibility = View.GONE
                    binding.ivwExpCollIcon.setImageResource(R.drawable.ic_round_keyboard_arrow_down_24)
                }
            }
        }

        binding.rvwRatings.apply {
            layoutManager = LinearLayoutManager(this@RestaurantActivity)
            adapter = RatingsAdapter(this@RestaurantActivity, restaurant.ratings)
            addItemDecoration(DividerItemDecoration(this@RestaurantActivity, LinearLayoutManager.VERTICAL))
        }

        Picasso.get().load(restaurant.picture).into(binding.ivwImage)
    }
}
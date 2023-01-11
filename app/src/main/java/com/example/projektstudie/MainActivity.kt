package com.example.projektstudie

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.example.projektstudie.databinding.ActivityMainBinding
import com.google.android.material.sidesheet.SideSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(),LocationListener {
    private lateinit var binding: ActivityMainBinding
    private var longitude: Double? = null
    private var latidude: Double? = null
    private lateinit var locationManager: LocationManager
    private var locationPermissionCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getLocation()
        val sideSheetDialog = SideSheetDialog(this)
        sideSheetDialog.setContentView(R.layout.fiter_side_sheet)

        binding.btnFilter.setOnClickListener {
            sideSheetDialog.show()
        }

        binding.inputSearchTerm.setOnQueryTextListener(object :  SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                CoroutineScope(IO).launch {
                    fetchData(query)
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
    }

    fun fetchData(searchTerm: String) {
        val url = URL("http://${BuildConfig.LOCAL_TEST_DOMAIN}:3000/getrestaurants?latitude=123&longitude=123&searchTerm=${searchTerm}")

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

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    override fun onLocationChanged(location: Location) {
        longitude = location.longitude
        latidude = location.latitude
    }
}
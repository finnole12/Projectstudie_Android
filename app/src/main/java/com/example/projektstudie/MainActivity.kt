package com.example.projektstudie

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projektstudie.databinding.ActivityMainBinding
import com.example.projektstudie.databinding.FiterSideSheetBinding
import com.google.android.material.sidesheet.SideSheetDialog
import com.google.android.material.slider.Slider
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class MainActivity : AppCompatActivity(),LocationListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sideSheetBinding: FiterSideSheetBinding
    private lateinit var sideSheetDialog: SideSheetDialog
    private var longitude: Double? = null
    private var latitude: Double? = null
    private lateinit var locationManager: LocationManager
    private var locationPermissionCode = 2
    private var currentFilter = Filter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getLocation()

        setupSideSheetUI()

        binding.btnFilter.setOnClickListener {
            sideSheetDialog.show()
        }

        binding.inputSearchTerm.setOnQueryTextListener(object :  SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                launchSearch()
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
    }

    private fun fetchData(
        searchTerm: String = "Burger",
        radius: Double = 300000.0,
        sortMethod: SortMethods,
        longitude: Double = 123.0,
        latitude: Double = 321.0,
        limit: Int = 20,
        offset: Int = 0,
    ): ResponseArray {
        val url = URL("${BuildConfig.DOMAIN}/getrestaurants?latitude=${latitude}&longitude=${longitude}&searchTerm=${searchTerm}&radius=${radius}&sortBy=${sortMethod}&limit=${limit}&offset=${offset}")

        val responseJson = with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            inputStream.bufferedReader().use { it.readText() }
        }
        return Gson().fromJson(responseJson, ResponseArray::class.java)
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
        latitude = location.latitude

        binding.txvLocationStatus.text = Geocoder(this)
            .getFromLocation(latitude!!, longitude!!, 1)[0].getAddressLine(0)

        launchSearch()
    }

    private fun launchSearch() {
        var responseArray: ResponseArray
        CoroutineScope(Main).launch {
            withContext(IO) {
                responseArray = fetchData(
                    searchTerm = binding.inputSearchTerm.query.toString(),
                    radius = currentFilter.radius.toDouble(),
                    sortMethod = currentFilter.sortMethod,
                    latitude = this@MainActivity.latitude!!,
                    longitude = this@MainActivity.longitude!!,
                    limit = 100
                )
            }
            displayItems(responseArray)
        }
    }

    private fun displayItems(responseArray: ResponseArray) {
        binding.rvwSearchItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ItemAdapter(context, responseArray)
        }
    }

    private fun setupSideSheetUI() {
        sideSheetDialog = SideSheetDialog(this)
        sideSheetBinding = FiterSideSheetBinding.inflate(sideSheetDialog.layoutInflater)
        sideSheetDialog.setContentView(sideSheetBinding.root)

        sideSheetBinding.txvRadiusValue.text = getString(
            R.string.kmString,
            sideSheetBinding.sliRadius.value.toString()
        )

        sideSheetBinding.sliRadius.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            sideSheetBinding.txvRadiusValue.text = getString(R.string.kmString, value.toString())
        })

        sideSheetBinding.rbtnDistance.isChecked = true

        sideSheetBinding.btnApply.setOnClickListener {
            currentFilter.apply {
                radius = sideSheetBinding.sliRadius.value
                sortMethod = when (sideSheetBinding.rgrpSortBy.checkedRadioButtonId) {
                    R.id.rbtnDistance -> SortMethods.Distance
                    R.id.rbtnRating -> SortMethods.Rating
                    R.id.rbtnPrice -> SortMethods.Price
                    R.id.rbtnPopularity -> SortMethods.Popularity
                    else -> {throw java.lang.IllegalArgumentException("cant be")}
                }
            }
            sideSheetDialog.hide()
            binding.btnFilter.setBackgroundColor(
                getColor(androidx.appcompat.R.color.material_blue_grey_950)
            )
            if (latitude != null && longitude != null) launchSearch()
        }


        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_enabled)
            ), intArrayOf(
                Color.WHITE,
                Color.WHITE
            )
        )

        sideSheetBinding.rbtnDistance.buttonTintList = colorStateList
        sideSheetBinding.rbtnPopularity.buttonTintList = colorStateList
        sideSheetBinding.rbtnPrice.buttonTintList = colorStateList
        sideSheetBinding.rbtnRating.buttonTintList = colorStateList


        sideSheetBinding.btnResetFilter.setOnClickListener {
            currentFilter = Filter()
            sideSheetBinding.rbtnDistance.isChecked = true
            sideSheetBinding.sliRadius.value = 1f
        }
    }
}
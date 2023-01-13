package com.example.projektstudie

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.net.UrlQuerySanitizer
import android.os.Bundle
import android.util.JsonReader
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projektstudie.databinding.ActivityMainBinding
import com.example.projektstudie.databinding.FiterSideSheetBinding
import com.google.android.material.sidesheet.SideSheetDialog
import com.google.android.material.slider.Slider
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(),LocationListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sideSheetBinding: FiterSideSheetBinding
    private lateinit var sideSheetDialog: SideSheetDialog
    private var longitude: Double? = null
    private var latidude: Double? = null
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
        val url = URL("http://${BuildConfig.LOCAL_TEST_DOMAIN}:3000/getrestaurants?latitude=${latitude}&longitude=${longitude}&searchTerm=${searchTerm}&radius=${radius}&sortBy=${sortMethod}&limit=${limit}&offset=${offset}")

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
        latidude = location.latitude
    }

    private fun launchSearch() {
        var responseArray: ResponseArray
        CoroutineScope(Main).launch {
            withContext(IO) {
                responseArray = fetchData(
                    searchTerm = binding.inputSearchTerm.query.toString(),
                    // TODO: //radius = currentFilter.radius,
                    sortMethod = currentFilter.sortMethod,
                    // TODO: limit
                    // TODO: offset
                )
            }
            println(responseArray)
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
            R.string.radiusValueLabelText,
            sideSheetBinding.sliRadius.value.toString()
        )

        sideSheetBinding.sliRadius.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            sideSheetBinding.txvRadiusValue.text = getString(R.string.radiusValueLabelText, value.toString())
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
            launchSearch()
        }

        sideSheetBinding.btnResetFilter.setOnClickListener {

        }
    }
}
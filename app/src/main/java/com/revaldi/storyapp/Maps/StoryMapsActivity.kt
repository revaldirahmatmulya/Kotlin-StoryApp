package com.revaldi.storyapp.Maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.revaldi.storyapp.Api.ApiConfig
import com.revaldi.storyapp.Helper.PreferenceManager
import com.revaldi.storyapp.R
import com.revaldi.storyapp.databinding.ActivityStoryMapsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class StoryMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoryMapsBinding
    private val boundsBuilder = LatLngBounds.Builder()
    private lateinit var sharedPreferencesManager: PreferenceManager
//    geocoder
    private lateinit var geocoder: Geocoder
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferencesManager = PreferenceManager(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true


        val dicodingSpace = LatLng(-6.8957643, 107.6338462)
        mMap.addMarker(
            MarkerOptions()
                .position(dicodingSpace)
                .title("Dicoding Space")
                .snippet("Batik Kumeli No.50")
        )

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dicodingSpace, 15f))


        mMap.setOnMapLongClickListener { latLng ->
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("New Marker")
                    .snippet("Lat: ${latLng.latitude} Long: ${latLng.longitude}")
                    .icon(vectorToBitmap(R.drawable.ic_location, Color.parseColor("#3DDC84")))
            )
        }
        mMap.setOnPoiClickListener { pointOfInterest ->
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(pointOfInterest.latLng)
                    .title(pointOfInterest.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            )
            poiMarker?.showInfoWindow()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val action = intent.getStringExtra("action")
        val latitudeVal = intent.getStringExtra("latitude")
        val longitudeVal = intent.getStringExtra("longitude")
        if (action == "getMyLocation") {
            getLastLocation()
        } else if (action == "getAll") {
            addManyMarker()
        } else if(action == "getStoryLocation"){
            getAddressFromCoordinates(this, latitudeVal!!.toDouble(), longitudeVal!!.toDouble())
        }

        getMyLocation()
        addManyMarker()
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    // Gunakan latitude dan longitude sesuai kebutuhan Anda
                }
            }
    }

    fun getAddressFromCoordinates(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var addressText = ""

        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1) as List<Address>
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                addressText = address.getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return addressText
    }



    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    private fun addManyMarker() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                delay(5000)
                val token = "Bearer ${sharedPreferencesManager.getToken()}"
                Log.e("Token Home", token)
                val response = ApiConfig.instanceRetrofit.getStories(token,null,null,1)
                if (!response.error) {
                    val stories = response.listStory
                    stories.forEach { item ->
                        val latLng = LatLng(item.lat, item.lon)
                        mMap.addMarker(
                            MarkerOptions().position(latLng).title(item.name)
                        )
                        boundsBuilder.include(latLng)
                        Log.d("API Success", item.toString())
                    }
                    val bounds: LatLngBounds = boundsBuilder.build()
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds,
                            resources.displayMetrics.widthPixels,
                            resources.displayMetrics.heightPixels,
                            300
                        )
                    )
                    Log.d("API Success", response.listStory.toString())
                } else {
                    Log.e("API Error", response.listStory.toString())
                }
            } catch (e: Exception) {
                Log.e("API Error", e.message ?: "Unknown error occurred")
            }
        }

    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}

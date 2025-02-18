package com.example.job3diptiictamadl40401.viewmodelDipti

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnCompleteListener
import java.io.IOException
import java.util.Locale

class LocationViewModelDiptiictamadl40401 : ViewModel() {
    private var fusedLocationClient: FusedLocationProviderClient? = null

    fun getLastLocation(context: Context,callback: (String) -> Unit) {
        fusedLocationClient?.lastLocation
            ?.addOnCompleteListener(OnCompleteListener {
                if (it.isSuccessful && it.result != null) {
                    val lastLocation = it.result
                    val latitude = lastLocation.latitude
                    val longitude = lastLocation.longitude
                    //val location = "Lat: $latitude, Long: $longitude"
                    val locationName = getLocationName(context,latitude, longitude)

                    callback(locationName)
                } else {
                    val locationName = getLocationName(context,37.4220936, -122.083922)
                    callback(locationName)
                }
            })
    }

    private fun getLocationName(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var locationName = "Unknown Location"
        try {
            val addressList: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addressList.isNullOrEmpty()) {
                val address: Address = addressList[0]
                locationName = address.getAddressLine(0)  // Get the complete address as a single string
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return locationName
    }

    fun initializeFusedLocationClient(client: FusedLocationProviderClient) {
        fusedLocationClient = client
    }
}
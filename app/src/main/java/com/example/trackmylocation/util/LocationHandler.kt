package com.example.trackmylocation.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

/** https://techpassmaster.com/get-current-location-in-android-studio-using-kotlin/  **/

const val locationPermissionId: Int = 12

fun Context.checkPermissions(): Boolean {
    if (ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        return true
    }
    return false
}

fun Context.isLocationEnabled(): Boolean {
    val locationManager: LocationManager =
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
    )
}

@SuppressLint("MissingPermission", "SetTextI18n")
fun Context.getLocation(activity: Activity) {
    var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)
    if (checkPermissions()) {
        if (isLocationEnabled()) {
            fusedLocationClient.lastLocation.addOnCompleteListener(activity) { task ->
                val location: Location? = task.result
                if (location != null) {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val list: List<Address> =
                        geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        ) as List<Address>
                    Log.e("CurrentLocation", getFormattedLocation(list))

                    Toast.makeText(this, getFormattedLocation(list), Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    } else {
        activity.requestPermissions()
    }
}

private fun Activity.requestPermissions() {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ),
        locationPermissionId
    )
}

fun getFormattedLocation(list: List<Address>): String {
    return if (list != null) {
        list[0].locality + ", " + list[0].adminArea + ", " + list[0].countryName
    } else {
        "Location could not be fetched..."
    }
}
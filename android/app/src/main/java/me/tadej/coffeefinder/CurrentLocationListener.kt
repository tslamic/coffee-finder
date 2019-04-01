package me.tadej.coffeefinder

import android.location.Location
import android.location.LocationListener

interface CurrentLocationListener : LocationListener {
    fun current(): Location?
    fun onNewLocation(location: Location)
    fun isBetter(new: Location, old: Location?): Boolean
}

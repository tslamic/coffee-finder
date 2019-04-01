package me.tadej.coffeefinder

import android.location.Location
import android.os.Bundle

abstract class CurrentLocator : CurrentLocationListener {
    private var current: Location? = null

    override fun current(): Location? = current

    override fun onLocationChanged(location: Location) {
        if (isBetter(location, current)) {
            current = location
            onNewLocation(location)
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // Do nothing.
    }

    override fun onProviderEnabled(provider: String?) {
        // Do nothing.
    }

    override fun onProviderDisabled(provider: String?) {
        // Do nothing.
    }

    // Inspired by https://developer.android.com/guide/topics/location/strategies.
    override fun isBetter(new: Location, old: Location?): Boolean {
        if (old == null) {
            return true
        }

        if (new.distanceTo(old) < MIN_DISTANCE) {
            return false
        }

        val timeDelta: Long = new.time - old.time
        val isSignificantlyNewer: Boolean = timeDelta > MIN_TIME
        val isSignificantlyOlder: Boolean = timeDelta < -MIN_TIME

        when {
            isSignificantlyNewer -> return true
            isSignificantlyOlder -> return false
        }

        val isNewer: Boolean = timeDelta > 0L
        val accuracyDelta: Float = new.accuracy - old.accuracy
        val isLessAccurate: Boolean = accuracyDelta > 0f
        val isMoreAccurate: Boolean = accuracyDelta < 0f
        val isSignificantlyLessAccurate: Boolean = accuracyDelta > 200f

        val isFromSameProvider: Boolean = new.provider == old.provider
        return when {
            isMoreAccurate -> true
            isNewer && !isLessAccurate -> true
            isNewer && !isSignificantlyLessAccurate && isFromSameProvider -> true
            else -> false
        }
    }

    companion object {
        private const val MIN_DISTANCE = 10
        private const val MIN_TIME = 1000 * 60 * 2
    }
}

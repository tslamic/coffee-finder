package me.tadej.coffeefinder

import androidx.annotation.WorkerThread
import coffeefinder.Finder
import java.io.Closeable

interface CoffeeFinderRepository : Closeable {
    @WorkerThread
    fun findNearbyCoffeeShops(lat: Double, lng: Double): List<Finder.CoffeeShop>
}

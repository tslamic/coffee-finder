package me.tadej.coffeefinder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import coffeefinder.Finder
import java.util.concurrent.Executors

class CoffeeFinderViewModule : ViewModel() {
    private val repo: CoffeeFinderRepository = CoffeeFinderRepo() // should be provided by DI
    private val exec = Executors.newSingleThreadExecutor() // should be provided by DI
    private val data = MutableLiveData<List<Finder.CoffeeShop>>()

    fun nearbyCoffeeShops(): LiveData<List<Finder.CoffeeShop>> = data

    fun findNearbyCoffeeShops(lat: Double, lng: Double) {
        exec.execute {
            val shops = repo.findNearbyCoffeeShops(lat, lng)
            data.postValue(shops)
        }
    }

    override fun onCleared() {
        super.onCleared()
        exec.shutdownNow()
        repo.close()
    }
}

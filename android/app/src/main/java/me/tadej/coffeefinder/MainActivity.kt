package me.tadej.coffeefinder

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coffeefinder.Finder
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), CoffeeShopAdapter.Listener {
    private lateinit var refresh: SwipeRefreshLayout.OnRefreshListener
    private lateinit var locator: CurrentLocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = CoffeeShopAdapter(this)
        recycler.adapter = adapter

        val manager = LinearLayoutManager(this)
        recycler.layoutManager = manager

        val divider = DividerItemDecoration(this, manager.orientation)
        recycler.addItemDecoration(divider)

        val model = ViewModelProviders.of(this).get(CoffeeFinderViewModule::class.java)
        model.nearbyCoffeeShops().observe(this, Observer {
            adapter.update(it)
            refresher.isRefreshing = false
        })

        locator = object : CurrentLocator() {
            override fun onNewLocation(location: Location) {
                refresher.isRefreshing = true
                model.findNearbyCoffeeShops(location.latitude, location.longitude)
            }
        }

        refresh = SwipeRefreshLayout.OnRefreshListener {
            val current = locator.current()
            if (current != null) {
                locator.onNewLocation(current)
            }
        }

        refresher.setOnRefreshListener(refresh)
    }

    override fun onStart() {
        super.onStart()
        if (hasLocationPermission()) {
            startLocationListener()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERM_ID)
        }
    }

    override fun onStop() {
        super.onStop()
        stopLocationListener()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (hasLocationPermission()) {
            startLocationListener()
        }
    }

    override fun onClick(coffeeShop: Finder.CoffeeShop) {
        val lat = coffeeShop.location.lat
        val lng = coffeeShop.location.lng
        val uri = Uri.parse("geo:$lat,$lng?q=${coffeeShop.name}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationListener() {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10_000, 0F, locator)
    }

    private fun stopLocationListener() {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        manager.removeUpdates(locator)
    }

    private fun hasLocationPermission(): Boolean {
        val perm = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return perm == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val PERM_ID = 0xB00B
    }
}

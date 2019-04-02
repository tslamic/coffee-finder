package me.tadej.coffeefinder

import androidx.annotation.WorkerThread
import coffeefinder.CoffeeServiceGrpc
import coffeefinder.Finder
import io.grpc.Deadline
import io.grpc.okhttp.OkHttpChannelBuilder
import java.util.concurrent.TimeUnit

class CoffeeFinderRepo : CoffeeFinderRepository {
    private val channel = OkHttpChannelBuilder
        .forAddress(BuildConfig.GRPC_HOST, BuildConfig.GRPC_PORT)
        .usePlaintext()
        .build()

    @WorkerThread
    override fun findNearbyCoffeeShops(lat: Double, lng: Double): List<Finder.CoffeeShop> {
        if (channel.isShutdown) return emptyList()
        val point = Finder.Point.newBuilder()
            .setLat(lat)
            .setLng(lng)
            .build()
        val deadline = Deadline.after(3, TimeUnit.SECONDS)
        val stub = CoffeeServiceGrpc.newBlockingStub(channel).withDeadline(deadline)
        return stub.coffeeShops(point)
            .asSequence()
            .toList()
    }

    override fun close() {
        channel.shutdown()
    }
}

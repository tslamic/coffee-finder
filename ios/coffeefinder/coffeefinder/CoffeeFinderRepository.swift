import Foundation
import SwiftGRPC
import SwiftProtobuf

protocol CoffeeShopCallback {
    func onSuccess(shops: [Coffeefinder_CoffeeShop])
    func onFailure(err: Error)
}

protocol CoffeeFinderRepository {
    func findNearbyCoffeeShops(lat: Double, lng: Double, callback: CoffeeShopCallback) throws
}

internal struct CoffeeFinderRepo: CoffeeFinderRepository {
    private var client: Coffeefinder_CoffeeServiceService

    init() {
        gRPC.initialize()
        client = Coffeefinder_CoffeeServiceServiceClient(address: ":8088", secure: false)
    }

    func findNearbyCoffeeShops(lat: Double, lng: Double, callback: CoffeeShopCallback) {
        DispatchQueue.global().async {
            var shops = [Coffeefinder_CoffeeShop]()

            var point = Coffeefinder_Point()
            point.lat = lat
            point.lng = lng

            do {
                var streaming = true
                let stream = try self.client.coffeeShops(point) { _ in
                    streaming = false
                }
                while streaming {
                    if let shop = try stream.receive() {
                        shops.append(shop)
                    }
                }
                callback.onSuccess(shops: shops)
            } catch {
                callback.onFailure(err: error)
            }
        }
    }
}

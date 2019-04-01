import CoreLocation
import MapKit
import UIKit

class CoffeeShopViewController: UITableViewController {
    private let locationManager = CLLocationManager()
    private let refresher = UIRefreshControl()
    private let repo = CoffeeFinderRepo()

    private var shops = [Coffeefinder_CoffeeShop]()
    private var current: CLLocation?

    override func viewDidLoad() {
        super.viewDidLoad()

        locationManager.delegate = self
        requestLocationUpdates()

        refresher.addTarget(self, action: #selector(refresh(_:)), for: UIControl.Event.valueChanged)
        tableView.addSubview(refresher)
    }

    override func numberOfSections(in _: UITableView) -> Int {
        return 1
    }

    override func tableView(_: UITableView, numberOfRowsInSection _: Int) -> Int {
        return shops.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let shop = shops[indexPath.row]

        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath)
        cell.textLabel?.text = shop.name
        cell.detailTextLabel?.text = String(shop.rating)
        return cell
    }

    override func tableView(_: UITableView, didSelectRowAt indexPath: IndexPath) {
        let shop = shops[indexPath.row]

        let coordinates = CLLocationCoordinate2DMake(shop.location.lat, shop.location.lng)
        let placemark = MKPlacemark(coordinate: coordinates, addressDictionary: nil)
        let item = MKMapItem(placemark: placemark)
        item.name = shop.name
        item.openInMaps()
    }

    @objc private func refresh(_: UIRefreshControl) {
        guard let c = current else {
            return
        }
        repo.findNearbyCoffeeShops(lat: c.coordinate.latitude, lng: c.coordinate.longitude, callback: self)
    }

    private func update(shops: [Coffeefinder_CoffeeShop]) {
        self.shops = shops
        tableView.reloadData()
    }
}

extension CoffeeShopViewController: CLLocationManagerDelegate {
    func requestLocationUpdates() {
        if CLLocationManager.locationServicesEnabled() {
            switch CLLocationManager.authorizationStatus() {
            case .authorizedAlways, .authorizedWhenInUse:
                startUpdatingLocation()
                return
            default:
                break
            }
        }
        locationManager.requestWhenInUseAuthorization()
    }

    func locationManager(_: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        switch status {
        case .authorizedWhenInUse:
            startUpdatingLocation()
        default:
            break
        }
    }

    func locationManager(_: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.first else {
            return
        }
        if isBetter(new: location, old: current) {
            current = location
            repo.findNearbyCoffeeShops(
                lat: location.coordinate.latitude,
                lng: location.coordinate.longitude,
                callback: self
            )
        }
    }

    private func startUpdatingLocation() {
        refresher.beginRefreshing()
        locationManager.startUpdatingLocation()
    }

    private func isBetter(new: CLLocation, old: CLLocation?) -> Bool {
        guard let o = old else {
            return true
        }
        let minLocationDistance = 20.0
        return o.distance(from: new) > minLocationDistance
    }
}

extension CoffeeShopViewController: CoffeeShopCallback {
    func onSuccess(shops: [Coffeefinder_CoffeeShop]) {
        DispatchQueue.main.async {
            print(shops)
            self.update(shops: shops)
            self.refresher.endRefreshing()
        }
    }

    func onFailure(err: Error) {
        print(err)
        refresher.endRefreshing()
    }
}

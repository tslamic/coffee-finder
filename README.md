# Coffee Shop Finder :coffee:

Coffee Shop Finder uses [Places API](https://developers.google.com/places/web-service/intro) to find the best (and currently open) coffee shops around you. It showcases the use of [gRPC](https://grpc.io/) with a [server](/server) and two consumer apps, [Android](/android) and [iOS](/ios).

The auto-generated gRPC stubs are already provided. In case you want to recreate them, you need a [Protobuf compiler](https://github.com/protocolbuffers/protobuf) along with the [Go](https://github.com/golang/protobuf) and [Swift](https://github.com/apple/swift-protobuf) plugins, and compile the `finder.proto` file. The Android app only requires a link to the [proto file](/android/app/src/main/proto) and Gradle will take care of the rest. 

The server requires a Google Maps API Key, which you can create by visiting [Google Developer Console](https://console.developers.google.com/apis/dashboard).
Run the server by executing `go run main.go -key GOOGLE_MAPS_KEY` 

## License

Copyright (c) 2019 Tadej Slamic. All rights reserved.
This work is licensed under the terms of the MIT license. For a copy, see https://opensource.org/licenses/MIT.

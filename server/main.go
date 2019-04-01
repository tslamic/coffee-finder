package main

import (
	"flag"
	"fmt"
	"github.com/tslamic/coffee-finder/server/coffeefinder"
	"google.golang.org/grpc"
	"googlemaps.github.io/maps"
	"log"
	"net"
)

var (
	apiKey = flag.String("key", "", "API key for using Google Maps API.")
	addr   = flag.String("addr", ":8088", "server port")
)

func main() {
	flag.Parse()

	if *apiKey == "" {
		log.Fatal("Google Maps API key required")
	}

	client, err := maps.NewClient(maps.WithAPIKey(*apiKey))
	if err != nil {
		log.Fatal(err)
	}

	listener, err := net.Listen("tcp", *addr)
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}

	fmt.Println("listening on", listener.Addr())

	srv := grpc.NewServer()
	server := coffeefinder.NewCoffeeFinder(client)
	coffeefinder.RegisterCoffeeServiceServer(srv, server)
	if err := srv.Serve(listener); err != nil {
		log.Fatalf("failed to serve: %v", err)
	}
}

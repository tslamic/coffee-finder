package coffeefinder

import (
	"context"
	"googlemaps.github.io/maps"
	"log"
	"sort"
	"time"
)

const (
	radius    = 500 // based on the average walking speed of 5 min, in meters.
	timeout   = 3 * time.Second
	minRating = 4.0
)

type finder struct {
	client *maps.Client
}

func NewCoffeeFinder(c *maps.Client) CoffeeServiceServer {
	return &finder{client: c}
}

func (f *finder) CoffeeShops(p *Point, s CoffeeService_CoffeeShopsServer) error {
	r := &maps.NearbySearchRequest{
		Location: &maps.LatLng{Lat: p.Lat, Lng: p.Lng},
		Radius:   radius,
		OpenNow:  true,
		Type:     maps.PlaceTypeCafe,
	}
	ctx, cancel := context.WithTimeout(s.Context(), timeout)
	defer cancel()
	resp, err := f.client.NearbySearch(ctx, r)
	if err != nil {
		return err
	}
	results := resp.Results
	sort.Slice(results, func(i, j int) bool {
		return results[i].Rating > results[j].Rating
	})
	for _, c := range resp.Results {
		select {
		case <-ctx.Done():
			break // Stop transmitting data if context is done/cancelled.
		default:
			if c.PermanentlyClosed || c.Rating < minRating {
				continue
			}
			location := c.Geometry.Location
			shop := &CoffeeShop{
				Id:       c.PlaceID,
				Rating:   c.Rating,
				Name:     c.Name,
				IconUrl:  c.Icon,
				Address:  c.FormattedAddress,
				Location: &Point{Lat: location.Lat, Lng: location.Lng},
			}
			err = s.Send(shop)
			if err != nil {
				log.Println(err) // log the error, but don't terminate
			}
		}
	}
	return nil
}

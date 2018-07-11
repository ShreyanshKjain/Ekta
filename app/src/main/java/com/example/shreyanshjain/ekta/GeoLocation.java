package com.example.shreyanshjain.ekta;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeoLocation {

    String city;
    double lat,lng;
    Context context;

    public GeoLocation(double lat, double lng, Context context) throws IOException {
        this.lat = lat;
        this.lng = lng;
        this.context = context;
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = gcd.getFromLocation(lat, lng, 1);
        if (addresses.size() > 0) {
            System.out.println(addresses.get(0).getLocality());
        }
        else {
            city = addresses.get(0).getAdminArea();
            // do your stuff
        }
    }

    public String getCity() {
        return city;
    }
}

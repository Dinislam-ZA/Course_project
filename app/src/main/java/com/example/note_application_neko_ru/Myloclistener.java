package com.example.note_application_neko_ru;

import android.location.Location;
import android.location.LocationListener;

import androidx.annotation.NonNull;

import java.util.List;

public class Myloclistener implements LocationListener {

    private LocListenerInterface locListenerInterface;

    @Override
    public void onLocationChanged(@NonNull Location location) {

        locListenerInterface.OnLocationChange(location);
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    public void setLocListenerInterface(LocListenerInterface locListenerInterface) {
        this.locListenerInterface = locListenerInterface;
    }
}

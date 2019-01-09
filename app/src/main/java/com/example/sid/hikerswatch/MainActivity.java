package com.example.sid.hikerswatch;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager lm;
    LocationListener ll;
    int time = 1000;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                Log.i("permission", "granted");
                startListening();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.i("info","not");
                    return;
                }
                else
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, 0, ll);
            }
        }
    }

    public void startListening(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            //lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, 0, ll);

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView latitude = findViewById(R.id.latitude);
        final TextView longitude = findViewById(R.id.longitude);
        final TextView accuracy = findViewById(R.id.accuracy);
        final TextView address = findViewById(R.id.address);
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        ll = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("location",location.toString());

                latitude.setText("Latitude: "+ location.getLatitude());
                longitude.setText("Longitude: "+ location.getLongitude());
                accuracy.setText("Accuracy: "+location.getAccuracy());

                Geocoder gc = new Geocoder(getApplicationContext(), Locale.getDefault());

                try{
                    List<Address> addr = gc.getFromLocation(location.getLatitude(), location.getLongitude(),1);

                    if(addr!=null && addr.size()>0){
                        Log.i("address",addr.get(0).toString());
                        address.setText("Address: "+ addr.get(0).getAddressLine(0));
                        //Toast.makeText(MapsActivity.this, addr.get(0).getAddressLine(0), Toast.LENGTH_LONG).show();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

                Log.i("info","status changed");
            }

            @Override
            public void onProviderEnabled(String provider) {

                Log.i("info",provider + "enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {

                Log.i("info",provider + "disabled");
            }
        };

        if(Build.VERSION.SDK_INT < 23) {
            try {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, 0, ll);
            } catch (SecurityException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }

        else{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                //ask for permission

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else
            {
                if( !lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Location not enabled");  // GPS not found
                    builder.setMessage("Want to enable"); // Want to enable?
                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),2);
                        }
                    });
                    builder.setNegativeButton("no", null);
                    builder.create().show();
                }
                else{
                    Log.i("permission"," already granted");
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, 0, ll);

                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if(location!=null){
                        latitude.setText("Latitude: "+ location.getLatitude());
                        longitude.setText("Longitude: "+ location.getLongitude());
                        accuracy.setText("Accuracy: "+location.getAccuracy());
                    }
                }


            }
        }
    }
}

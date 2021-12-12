package com.example.first_exersice;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Formatter;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.location.Location;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity implements LocationListener {

    Button ShowmapButton;

    TextView SpeedView, identification, AccelView;
    TextView TimeView;
    //latitude,longitude, //Velocity
    double x , y , Vel = 0 ;
    //Our initial Speed and the one we will be comparing to see if we had any Sudden movement
    int speed = 0 , currentspeed, initTime = 0 , finalTime;


    LocationManager locationManager;
    String timestamp, ID ;
    //Database References
    private FirebaseFirestore Db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get a random id for each app installation
        ID = UUID.randomUUID().toString();
        identification = this.findViewById(R.id.textView4);
        identification.setText(ID);


        TimeView = this.findViewById(R.id.speedview2);
        ShowmapButton = this.findViewById(R.id.ShowMap);
        SpeedView = (TextView) this.findViewById(R.id.speedview);
        SpeedView.setText("0.0 Km/h");
        AccelView = (TextView) this.findViewById(R.id.speedview2);
        //Setting up firebase
        Db = FirebaseFirestore.getInstance();


        //Initiate the location manager to get location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)){
            Toast.makeText(this, "Please Enable GPS to use the App.",Toast.LENGTH_SHORT).show();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},234);

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100, this);



    }
    public void OnClick(View view){
        startActivity(new Intent(this, MapsActivity.class));

    }
    //Method to check if Accelaration/Decelaration are sufficient to add to firestore
    public void CheckForSuddenChange(Double Vel,double x, double y){


        //If the Velocity is beyond the limit then insert event to database

        if(Vel > 20 || Vel < -20) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            timestamp = date.format(calendar.getTime());


            //Add instance to firebase
            Map<String, Object> event = new HashMap<>();

            event.put("ID", ID);
            event.put("latitude", x);
            event.put("Longitude", y);
            event.put("Previous speed", speed);
            event.put("Current Speed", currentspeed);
            event.put("Acceleration", Vel);
            event.put("Time Of Incident", timestamp);
            Db.collection("Incident_File")
                    .add(event)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(@NonNull DocumentReference documentReference) {
                            Toast.makeText(MainActivity.this, "Incident Catalogued", Toast.LENGTH_SHORT);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Failure to add event", Toast.LENGTH_SHORT);
                }
            });
        }


    }

    public double CalVelocity(int initTime , int finalTime , int initspeed, int currentspeed){

        if( (finalTime/3600 - initTime/3600 == 0) || (currentspeed*3.6 - initspeed*3.6)==0 ){
            return 0 ;
        }else{
            Vel = (currentspeed*3.6 - initspeed*3.6)/(finalTime/3600 - initTime/3600);
            return Vel;
        }

    }
    @Override
    public void onLocationChanged(Location location) {


            //Get Device Location
            x = location.getLatitude();
            y = location.getLongitude();

            currentspeed = (int)location.getSpeed();
            finalTime = (int)location.getTime();
            Vel = CalVelocity(initTime, finalTime,speed, currentspeed);

            AccelView.setText((int)Vel+" Km/h^2");

            CheckForSuddenChange(Vel,x,y);
            SpeedView.setText((currentspeed*3600)/1000+" Km/h");

            speed = currentspeed;
            initTime = finalTime;
    }
}
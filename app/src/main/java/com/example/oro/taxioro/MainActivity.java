package com.example.oro.taxioro;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LocationListener {

    TextView textTotal, textInicio, textFin;
    EditText editBanderazo, editCostoMetro;
    Button buttonIniciar, buttonDetener;
    LocationManager gps;

    int cont = 0;
    List<Double> latList = new ArrayList<>();
    List<Double> longList = new ArrayList<>();
    boolean inicio = false;
    double banderazo, costoMetro, total = 0;

    private static double valor = 6371;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        //setContentView(R.layout.activity_main);

        textTotal = (TextView) findViewById(R.id.textTotal);
        textInicio = (TextView) findViewById(R.id.textInicio);
        textFin = (TextView) findViewById(R.id.textFin);
        editBanderazo = (EditText) findViewById(R.id.textBanderazo);
        editCostoMetro = (EditText) findViewById(R.id.textCostoMetro);
        buttonIniciar = (Button) findViewById(R.id.buttonIniciar);
        buttonDetener = (Button) findViewById(R.id.buttonDetener);


        gps = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        gps.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        /*gps = (LocationManager) getSystemService(LOCATION_SERVICE);*/
       /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
        // gps.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 3, this);
        buttonIniciar.setEnabled( false );
        buttonDetener.setEnabled( false );



        editCostoMetro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                buttonIniciar.setEnabled( true );
            }
        });


        buttonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                banderazo =  Double.parseDouble(editBanderazo.getText().toString());
                costoMetro = Double.parseDouble(editCostoMetro.getText().toString());
                inicio = true;
                buttonDetener.setEnabled( true );
                longList.clear();
                latList.clear();
                textInicio.setText("Empezando GPS y conteo");
                textFin.setText("");
                textTotal.setText("");
            }
        });


        buttonDetener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonDetener.setEnabled( false );
                int finListLat = latList.size();
                int finListLon = longList.size();
                textFin.setText("*Ubicación Final Lat: " + latList.get( finListLat - 1) + "\n");
                textFin.append("*Long: " + longList.get( finListLon - 1 ));
                inicio = false;
                total += banderazo;
                DecimalFormat dosDecimales = new DecimalFormat("#.00");
                textTotal.setText("**Cuenta:  $ " + dosDecimales.format( total ) );
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if( location.getLatitude() != 0 && inicio ){
            latList.add( location.getLatitude() );
            longList.add( location.getLongitude() );
            textInicio.setText("*LATITUD INICIAL: " + latList.get(0) + "\n" );
            textInicio.append("*LONGITUD INICIAL: " + longList.get(0) + "\n" );
            if( latList.size() > 1 ){
                double distanciaMetros = distanceBetween( latList.get( cont ), longList.get( cont ),
                        latList.get( cont + 1), longList.get( cont + 1) );
                cont += 1;
                total( distanciaMetros );
            }
        }
    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        textInicio.setText("Prendido y Trabajando :)");
    }

    @Override
    public void onProviderDisabled(String s) {
        textInicio.setText("Necesito que prendas tu GPS");

    }



    public double total(double distanciaMetros){
        double total1 = distanciaMetros * costoMetro;
        total = total + total1;
        return total;
    }
    public double distanceBetween( double lat1, double lon1, double lat2, double lon2){
        double dLat = toRadians( lat2 - lat1 );
        double dLon = toRadians( lon2 - lon1);

        double a = Math.sin( dLat / 2) * Math.sin( dLat / 2 ) + Math.cos( toRadians(lat1) ) * Math.cos( toRadians(lat2) ) * Math.sin( dLon / 2 ) * Math.sin( dLon / 2 );
        double c = 2 * Math.atan2( Math.sqrt( a ), Math.sqrt( 1 - a ));
        double d = valor * c;

        total( d );
        return d;
    }

    private double toRadians( double degrees ) {
        return degrees * ( Math.PI / 180 );
    }
}





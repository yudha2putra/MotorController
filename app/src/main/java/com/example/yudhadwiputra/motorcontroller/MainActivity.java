package com.example.yudhadwiputra.motorcontroller;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;






public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter mBtAdapter;
    private TextView TextEmail;
    private TextView TextPlate;
    private Button BookingButton;
    private EditText loginInputIDUser;
    Spinner spinner;
    ArrayList<String> PlateNumber;
    String url;
    String id_user;
    String plate;
    String check_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextEmail = (TextView) findViewById( R.id.textViewEmail );
        TextPlate = (TextView) findViewById( R.id.textViewPlate );
        BookingButton = (Button) findViewById( R.id.btn_booking );
        loginInputIDUser   = (EditText)findViewById(R.id.email_input);

        BookingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setContentView(R.layout.activity_loading);
                id_user = loginInputIDUser.getText().toString();
                SetBooking();
                advertise(plate + "," + id_user);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CheckBooking();
//                        if(": 0".equalsIgnoreCase(check_in))
//                        {
//                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
//                            Toast.makeText(getApplicationContext(),"Booking gagal. Coba lagi",Toast.LENGTH_LONG).show();
//                        }
//                        else if(": 1".equalsIgnoreCase(check_in))
//                        {
//                            Intent i = new Intent(MainActivity.this, ControlActivity.class);
//                            i.putExtra("id_user", id_user);
//                            i.putExtra("plate", plate);
//                            startActivity(i);
//                        }
                    }
                }, 3000L); //3000 L = 3 detik
            }
        });

        final BluetoothManager btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = btManager.getAdapter();
        if (mBtAdapter == null || !mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            int REQUEST_ENABLE_BT = 1;
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

//        checkBt(); // called at the end of onCreate

        PlateNumber=new ArrayList<>();
        spinner=(Spinner)findViewById(R.id.plate_number);
        url = "https://tandebike.id/api/Data/getAvailablePlate";
        loadSpinnerData(url);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                plate = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode == 1){
            Toast.makeText(getApplicationContext(),"OKE",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"GAK",Toast.LENGTH_LONG).show();
        }
    }

    private void loadSpinnerData(String url) {
        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray jsonArray=new JSONArray(response);
                    Log.i("Something", "response");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        String plate=jsonObject1.getString("plateNo");
                        PlateNumber.add(plate);
                    }

                    spinner.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, PlateNumber));
                }catch (JSONException e){e.printStackTrace();}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    private void SetBooking(){
        url = "https://tandebike.id/IOT/insertBooking/" + plate +"?id_user=" + id_user;
        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray showInfo = new JSONArray(response);
                    Log.i("Something", "response");
                } catch (Exception e) {
                    Log.i("Error", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG", ""+error);
            }
        });
        Volley.newRequestQueue(this).add(sr);

        Toast.makeText(getApplicationContext(),id_user + " Membooking " + plate,Toast.LENGTH_LONG).show();
    }

    private void CheckBooking(){
        url = "https://tandebike.id/api/Data/getBookingStatus/plateNo/" + plate +"/id_user/" + id_user;
        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonCheck = jsonArray.getJSONObject(0);
                    check_in = jsonCheck.getString("check_in");
                    Toast.makeText(getApplicationContext(),check_in,Toast.LENGTH_LONG).show();
                    Log.i("Check in = ", check_in);
                } catch (Exception e) {
                    Log.i("Error", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG", ""+error);
            }
        });
        Volley.newRequestQueue(this).add(sr);
    }

    public void advertise(String dataService) {
        final Handler handler = new Handler();

        final BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

        ParcelUuid pUuid = new ParcelUuid( UUID.fromString( getString( R.string.ble_uuid_oke ) ) );

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName( true )
                .addServiceUuid( pUuid )
                .addServiceData( pUuid, dataService.getBytes(Charset.forName("UTF-8") ) )
                .build();

        AdvertisingSetParameters parameters = (new AdvertisingSetParameters.Builder())
                .setLegacyMode(true) // True by default, but set here as a reminder.
                .setConnectable(false)
                .setInterval(AdvertisingSetParameters.INTERVAL_LOW)
                .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_MEDIUM)
                .build();

        final AdvertisingSetCallback callback = new AdvertisingSetCallback() {
            @Override
            public void onAdvertisingSetStarted(AdvertisingSet advertisingSet, int txPower, int status) {
                Log.i("BLE", "onAdvertisingSetStarted(): txPower:" + txPower + " , status: "
                        + status);
//                advertising = advertisingSet;
            }

            @Override
            public void onAdvertisingDataSet(AdvertisingSet advertisingSet, int status) {
                Log.i("BLE", "onAdvertisingDataSet() :status:" + status);
            }

            @Override
            public void onScanResponseDataSet(AdvertisingSet advertisingSet, int status) {
                Log.i("BLE", "onScanResponseDataSet(): status:" + status);
            }

            @Override
            public void onAdvertisingSetStopped(AdvertisingSet advertisingSet) {
                Log.i("BLE", "onAdvertisingSetStopped():");
            }
        };

        advertiser.startAdvertisingSet(parameters, data, null, null, null, callback);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                advertiser.stopAdvertisingSet(callback);
            }
        }, 2000);
    }
}
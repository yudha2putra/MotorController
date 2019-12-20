package com.example.yudhadwiputra.motorcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.nio.charset.Charset;
import java.util.UUID;

public class ControlActivity extends AppCompatActivity implements View.OnClickListener {


    private Button mOnButton;
    private Button mOffButton;
    private Button InternetOnButton;
    private Button InternetOffButton;
    private Button StopBookingButton;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    int Seconds = 0, Minutes = 0, Hour = 0, MilliSeconds = 0 ;
    String id_user,plate;

    String url;
    TextView timerTextView;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Hour = Minutes / 60;

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);

            timerTextView.setText(Hour + ":" + Minutes + ":" + String.format("%02d", Seconds));

            timerHandler.postDelayed(this, 0);

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        mOffButton = (Button) findViewById( R.id.off_btn );
        mOnButton = (Button) findViewById( R.id.on_btn );
        InternetOffButton = (Button) findViewById( R.id.off_btn_internet );
        InternetOnButton = (Button) findViewById( R.id.on_btn_internet );
        StopBookingButton = (Button) findViewById( R.id.stop_booking );
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        timerHandler.postDelayed(timerRunnable, 0);
        StartTime = SystemClock.uptimeMillis();

        mOffButton.setOnClickListener( this );
        mOnButton.setOnClickListener( this );
        InternetOffButton.setOnClickListener( this );
        InternetOnButton.setOnClickListener( this );
        StopBookingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SetStopBooking();
                advertise(plate + "," + id_user + ",e");
                Intent i = new Intent(ControlActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        Bundle extras = getIntent().getExtras();
        id_user = extras.getString("id_user");
        plate = extras.getString("plate");
    }


    public void advertise(String dataService) {
        final Handler handler = new Handler();

        final BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode( AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY )
                .setTxPowerLevel( AdvertiseSettings.ADVERTISE_TX_POWER_HIGH )
                .setConnectable(false)
                .build();

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

        AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e( "BLE", "Advertising onStartFailure: " + errorCode );
                super.onStartFailure(errorCode);
            }
        };

//        public AdvertisingSet advertisingSet = new AdvertisingSet();

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

    public void SetStatus(String dataService){
        //Log.i("Made it", "You made it this far");
        url = "https://tandebike.id/IOT/UpdateDevice/1?status=" + dataService;
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
    }

    private void SetStopBooking(){
        url = "https://tandebike.id/IOT/stopBooking/" + plate ;
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

    }



    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.off_btn ) {
            advertise(plate + "," + id_user + ",0");
        }
        else if( v.getId() == R.id.on_btn ) {
            advertise(plate + "," + id_user + ",1");
        }
        else if( v.getId() == R.id.off_btn_internet ) {
            SetStatus("off");
        }
        else if( v.getId() == R.id.on_btn_internet ) {
            SetStatus("on");
        }

    }


}
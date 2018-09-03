package com.example.mohammadrezasadeghi.parkml;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import hrituc.studenti.uniroma1.it.generocityframework.Constants;
import hrituc.studenti.uniroma1.it.generocityframework.ParkingLocation;
import hrituc.studenti.uniroma1.it.generocityframework.TripPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FunctionCallsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Constants.LOGS) Log.e( "FunctionCallsReceiver", "onReceive called" );
        String action = intent.getAction();
        if (Constants.DID_ENTER_CAR.equals( action )) {

            //get the bluetooth device that the user is connected to. BluetoothDevice.EXTRA_DEVICE is
            //an android constant, so since it was there I decided to use it.
            BluetoothDevice connectedDevice = intent.getParcelableExtra( BluetoothDevice.EXTRA_DEVICE );

            NotificationCompat.Builder builder = new NotificationCompat.Builder( context, "isInCar" );
            builder.setContentText( "Did enter car" );
            builder.setSmallIcon( hrituc.studenti.uniroma1.it.generocityframework.R.mipmap.ic_launcher );
            builder.setContentTitle( "FunctionCallsReceiver" );
            NotificationManagerCompat.from( context ).notify( 7, builder.build() );
        } else if (Constants.DID_EXIT_CAR.equals( action )) {
            //extract the parking position
            ParkingLocation parkingLocation = ParkingLocation.fromIntent( context, intent );

            //get the device that the user was connected to
            BluetoothDevice previouslyConnectedDevice = intent.getParcelableExtra( BluetoothDevice.EXTRA_DEVICE );

            //The parking location may be null in some rare cases, like if the user manually disables
            //the position setting on his phone
            double latitude = -1;
            double longitude = -1;
            if (parkingLocation != null) {
                latitude = parkingLocation.getLatitude();
                longitude = parkingLocation.getLongitude();
                if (Constants.LOGS) Log.e( "FunctionCallsReceiver", "parking location extracted" );
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder( context, "isInCar" );
            builder.setContentText( "Did exit car on lat: " + latitude + " - lon: " + longitude );
            builder.setSmallIcon( hrituc.studenti.uniroma1.it.generocityframework.R.mipmap.ic_launcher );
            builder.setContentTitle( "FunctionCallsReceiver" );
            NotificationManagerCompat.from( context ).notify( 8, builder.build() );
        } else if (Constants.WILL_EXIT_CAR.equals( action )) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder( context, "willExit" );
            builder.setContentText( "Will exit car" );
            builder.setSmallIcon( hrituc.studenti.uniroma1.it.generocityframework.R.mipmap.ic_launcher );
            builder.setContentTitle( "FunctionCallsReceiver" );
            NotificationManagerCompat.from( context ).notify( 9, builder.build() );
        } else if (Constants.TRIP_POINT.equals( action )) {
            //extract the data from the intent
            TripPoint tripPoint = TripPoint.fromIntent( intent );
            double lat = tripPoint.getLatitude();
            double lon = tripPoint.getLongitude();
            float speed = tripPoint.getSpeed();
            Timestamp timestamp = tripPoint.getTimestamp();
            float Parked = tripPoint.getSpeed();

          

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://172.27.1.120:8080/").
                            addConverterFactory( GsonConverterFactory.create())
                    .build();

            ParkMLAPIInterface intf = retrofit.create( ParkMLAPIInterface.class );

            Map<String, Object> postData = new HashMap<>();
            postData.put( "Latitude", lat );
            postData.put( "Longitude", lon );
            postData.put( "Speed", speed );
            postData.put( "Timestamp", timestamp );
            postData.put( "Parked", Parked);

            Call<String> res = intf.api( postData );
            res.enqueue( new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    Log.d("FunctionCallsReceiver", "Response: " + response.body());
                    if ("1".equals(response.body())) {


                    } else {

                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("FunctionCallsReceiver", "Error: " + t.getMessage());
                }
            } );

            

            if (Constants.LOGS) Log.e( "FunctionCallsReceiver", "trip-point extracted" );
        } else if (Constants.WILL_ENTER_CAR.equals( action )) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder( context, "willEnter" );
            builder.setContentText( "Will enter car" );
            builder.setSmallIcon( hrituc.studenti.uniroma1.it.generocityframework.R.mipmap.ic_launcher );
            builder.setContentTitle( "FunctionCallsReceiver" );
            NotificationManagerCompat.from( context ).notify( 10, builder.build() );
        } else if (action.equals( "UPDATE_GEOFENCE_LIST" )) {
            String id = intent.getStringExtra( "id" );
            double latitude = intent.getDoubleExtra( "latitude", -1 );
            double longitude = intent.getDoubleExtra( "longitude", -1 );
            // MainActivity.updateGefenceMap(id,latitude,longitude);
        }
    }
}



//do stuff with these

            /*

            JSONObject json = new JSONObject();
            try {
                json.put("latitude", lat);
                json.put("longitude", lon);
                json.put("speed", speed);
                json.put("timestamp", timestamp);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = "";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( Request.Method.POST, url, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            serverResp.setText("String Response : "+ response.toString());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    serverResp.setText("Error getting response");
                }
            });
            jsonObjectRequest.setTag(REQ_TAG);
            requestQueue.add(jsonObjectRequest);

////////////////    */


            
            
            
            /*JSONObject jObjectData = new JSONObject();
            BufferedReader reader = null;

            // Create Json Object using TripPoint  Data
            try {
                jObjectData.put( "latitude", lat );
                jObjectData.put( "longitude", lon );
                jObjectData.put( "speed", speed );
                jObjectData.put( "timestamp", timestamp );
                //jObjectData.put( "parked", parked ) ;
                try {
                    // Defined URL  where to send data
                    URL url = new URL( "https://server-parkml.apps.os.sapienzaapps.it/api" );

                    // Send POST data request

                    URLConnection conn = url.openConnection();
                    conn.setDoOutput( true );
                    OutputStreamWriter wr = new OutputStreamWriter( conn.getOutputStream() );
                    wr.write( jObjectData.toString() );
                    wr.flush();

                    // Get the server response

                    reader = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        // Append server response in string
                        sb.append( line + "\n" );
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {

                        reader.close();
                    } catch (Exception ex) {
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

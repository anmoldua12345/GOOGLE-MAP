package com.example.joginderpal.map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joginderpal on 31-12-2016.
 */
public class second extends Activity {
    ProgressDialog progressDialog;
   EditText ed1,ed2;
    Button b1;
    String url,origi,destinatio;
    ListView ls;
    String dis,dur;
    List<Double> startlatlist;
    List<Double> startlnglist;
    List<Double> endlatlist;
    List<Double> endlnglist;
    RequestQueue requestQueue;
    TextView tx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);
        ed1= (EditText) findViewById(R.id.ed1);
        ed2= (EditText) findViewById(R.id.ed2);
        b1= (Button) findViewById(R.id.b1);
     //  ls= (ListView) findViewById(R.id.listView);
        tx= (TextView) findViewById(R.id.textView);
        startlatlist=new ArrayList<Double>();
        startlnglist=new ArrayList<Double>();
        endlatlist=new ArrayList<Double>();
        endlnglist=new ArrayList<Double>();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  origi=ed1.getText().toString();
                destinatio=ed2.getText().toString();
                String origin= null;
                String destination=null;
                try {
                    origin = URLEncoder.encode(origi,"UTF-8");
                     destination=URLEncoder.encode(destinatio,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                url="https://maps.googleapis.com/maps/api/directions/json?origin="+origin+"&destination="+destination+"&key=AIzaSyCE03QyYeT7R9Cr2kFNfhjsY7uEUaYczYA";


                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url, null,

                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                   String check=response.getString("status");
                                    if (check.equals("OK")) {
                                        JSONArray array = response.getJSONArray("routes");
                                        //check = array.getString(1);
                                        // tx.setText(check);
                                        for (int j = 0; j < array.length(); j++) {
                                            JSONObject o = array.getJSONObject(j);
                                            JSONArray legs = o.getJSONArray("legs");
                                            JSONObject o1 = legs.getJSONObject(0);
                                            JSONObject distance=o1.getJSONObject("distance");
                                            dis = distance.getString("text");
                                            JSONObject duration=o1.getJSONObject("duration");
                                             dur=duration.getString("text");
                                            JSONArray steps = o1.getJSONArray("steps");
                                            for (int i = 1; i < steps.length(); i++) {
                                                JSONObject object = steps.getJSONObject(i);
                                                JSONObject start = object.getJSONObject("start_location");
                                                double startlat = start.getDouble("lat");
                                                startlatlist.add(startlat);
                                                double startlng = start.getDouble("lng");
                                                startlnglist.add(startlng);
                                                JSONObject end = object.getJSONObject("end_location");
                                                double endlat = end.getDouble("lat");
                                                endlatlist.add(endlat);
                                                double endlng = end.getDouble("lng");
                                                endlnglist.add(endlng);
                                            }
                                        }
                                        Intent intent = new Intent(second.this, MapsActivity.class);
                                        intent.putExtra("startlat", (Serializable) startlatlist);
                                        intent.putExtra("startlng", (Serializable) startlnglist);
                                        intent.putExtra("endlat", (Serializable) endlatlist);
                                        intent.putExtra("endlng", (Serializable) endlnglist);
                                        intent.putExtra("distance",dis);
                                        intent.putExtra("duration",dur);
                                        startActivity(intent);

                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),"Please type valid Origin and Destination",Toast.LENGTH_LONG).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                            }
                        }


                );

                requestQueue.add(jsonObjectRequest);


            }
        });


        requestQueue= Volley.newRequestQueue(this);

    }}


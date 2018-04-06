package com.cursosdedesarrollo.peticionjsonandroid;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    String url = "http://cursosdedesarrollo.com/pactometro/resultados.json";

    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_petition) {
            Log.d("app","Relizando Petición");
            if(isConnected()){
                requestLaunch();
            }else {
                Log.d("App","No hay conexión");
            }

            return true;
        }
        if (id == R.id.action_download_json) {
            Log.d("app","Relizando Petición");
            if(isConnected()){
                downloadJson();
            }else {
                Log.d("App","No hay conexión");
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void downloadJson() {
        RequestQueue queue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        queue = new RequestQueue(cache, network);

        // Start the queue
        queue.start();
        JsonArrayRequest jsonArrayRequest= new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray array) {
                        Log.d("app","Response: " + array.toString());
                        for (int i=0;i<array.length();i++){
                            try {
                                JSONObject objeto= (JSONObject) array.get(i);
                                Log.d("app",objeto.toString());
                                Log.d("app:nombre:",objeto.get("nombre").toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ,new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.d("app","Error: " + error.getLocalizedMessage());
            }
        });

        queue.add(jsonArrayRequest);
    }

    private void requestLaunch() {

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(),
                1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        queue = new RequestQueue(cache, network);

        // Start the queue
        queue.start();

        //RequestQueue queue = Volley.newRequestQueue(this);

        Response.Listener<String> successListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the first 500 characters of the response string.
                Toast.makeText(MainActivity.this,"Response is: "+ response.substring(0,500),Toast.LENGTH_LONG).show();
                Log.d("app","Response is: "+ response.substring(0,500));
                //Snackbar.make(toolbar,"Response is: "+ response.substring(0,500) , Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }
        };
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                successListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Petition Error:"+error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                Log.d("app","Response is: "+ "Petition Error:"+error.getLocalizedMessage());
                //Snackbar.make(toolbar, "Petition Error:"+error.getLocalizedMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        Log.d("app","lanzando Petición");
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            return true;
        }else {
            return false;
        }
    }
}

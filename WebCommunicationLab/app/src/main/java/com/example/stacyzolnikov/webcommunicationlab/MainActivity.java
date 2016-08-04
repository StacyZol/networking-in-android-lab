package com.example.stacyzolnikov.webcommunicationlab;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView mListView;

    Button mTea;
    Button mChocolate;
    Button mCereal;
    ArrayList<String> mList;
    ArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTea = (Button) findViewById(R.id.TeaButton);
        mCereal = (Button) findViewById(R.id.CerealButton);
        mChocolate = (Button) findViewById(R.id.ChocolateButton);
        mListView = (ListView) findViewById(R.id.ListViewID);
        mList = new ArrayList<>();

        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, mList);

        mListView.setAdapter(mAdapter);


        mTea.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mList.clear();
                new DownloadTask().execute("http://api.walmartlabs.com/v1/search?query=tea&format=json&apiKey=hc6gwayrtfv7mk4ffztn82ss");
            }

        });

        mCereal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mList.clear();
                new DownloadTask().execute("http://api.walmartlabs.com/v1/search?query=cereal&format=json&apiKey=hc6gwayrtfv7mk4ffztn82ss");
            }
        });

        mChocolate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mList.clear();
                new DownloadTask().execute("http://api.walmartlabs.com/v1/search?query=chocolate&format=json&apiKey=hc6gwayrtfv7mk4ffztn82ss");
            }
        });


        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
     //   if (networkInfo != null && networkInfo.isConnected()) {
     //       Toast.makeText(MainActivity.this, "Network is active", Toast.LENGTH_SHORT).show();
     //       new DownloadTask().execute(URL);
     //   } else {
     //       Toast.makeText(this, "check network connection", Toast.LENGTH_LONG).show();
     //   }
    }


    public void downloadURL(String myURL) throws IOException,JSONException {
        InputStream is = null;

        try {
            URL url = new URL(myURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            is = conn.getInputStream();

            String contentAsString = readIt(is);
            parseJson(contentAsString);
        } finally {
            if (is != null) {
                is.close();
            }

        }
    }

    private void parseJson(String contentAsString) throws JSONException{
        JSONObject search = new JSONObject(contentAsString);
        JSONArray items = search.getJSONArray("items");
        for (int i =0; i<items.length(); i++){
            JSONObject item = items.getJSONObject(i);
            mList.add(item.getString("name"));
        }
    }
    private class DownloadTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {
            try {
                downloadURL(strings[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
        }
    }

    private String readIt(InputStream is) throws IOException{
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String read;

        while((read=br.readLine()) != null) {
            sb.append(read);
        }
        return sb.toString();

    }


}


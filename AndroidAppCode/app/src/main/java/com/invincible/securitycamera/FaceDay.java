package com.invincible.securitycamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class FaceDay extends AppCompatActivity {
    int icons[];
    String ip;
    CustomAdapter adapter;
    ListView listView;
     ArrayList<String> bitmaps;
     String images,day;
     String[] imagesList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.faces_day);
        ip = getString(R.string.ipAddress);
        listView = findViewById(R.id.faceList);
        BackgroundDataRetrieve bgr = new BackgroundDataRetrieve();
        day = getIntent().getStringExtra("DAY");
        bitmaps = new ArrayList<String>();
        bgr.execute(ip+"api/v1/resources/folders/facesFiles?faces="+day);
    }
    private class BackgroundDataRetrieve extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            System.out.println(String.valueOf(objects[0]));

            try {
                URL url = new URL((String) objects[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data!=-1)
                {
                    char current = (char)data;
                    images += current;
                    data = reader.read();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            imagesList = images.split(" ");
            System.out.println("Images Faces =  " + String.valueOf(images));
            for ( String image : imagesList)
            {
                try {
                    //InputStream input = new java.net.URL("http://192.168.240.12:5000/api/v1/resources/files/faces/?faces="+day+ "/"+ image).openStream();
                    System.out.println(image);
                    bitmaps.add(ip+"api/v1/resources/files/faces/?faces="+day+ "/"+ String.valueOf(image));
                }catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }
            }

            publishProgress("got Images");
            System.out.println("Images = "+imagesList);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            adapter = new CustomAdapter(getApplicationContext(),imagesList,icons,"FaceDay",bitmaps);
            listView.setAdapter(adapter);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            listView.invalidate();
        }
    }
}

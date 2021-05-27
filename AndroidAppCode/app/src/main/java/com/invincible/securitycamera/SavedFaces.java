package com.invincible.securitycamera;

import android.content.Intent;
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

public class SavedFaces extends AppCompatActivity
{   String ip;
    String[] folderList;
    int [] icons;
    CustomAdapter adapter;
    ListView listView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.saved_faces);
        ip = getString(R.string.ipAddress);
        Intent intent = getIntent();
        BackgroundDataRetrieve bg = new BackgroundDataRetrieve();
        bg.execute(ip + "api/v1/resources/foldersFace");
        listView = findViewById(R.id.saved_faceList);
    }

    private class BackgroundDataRetrieve extends AsyncTask {
        String folders = "";
        String [] foldersList;

        @Override
        protected Object doInBackground(Object[] objects) {
            System.out.println(String.valueOf(objects[0]));
        /*try {
            Document doc = Jsoup.connect((String)objects[0]).get();
            folders = doc.text();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
            try {
                URL url = new URL((String) objects[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data!=-1)
                {
                    char current = (char)data;
                    folders += current;
                    data = reader.read();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            foldersList = folders.split(" ");
            publishProgress("got Folders");
            System.out.println("Folders = "+folders);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            folderList = foldersList;
            adapter = new CustomAdapter(getApplicationContext(),folderList,icons,"SavedFaces",null);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(SavedFaces.this,FaceDay.class);
                    intent.putExtra("DAY", folderList[i]);
                    SavedFaces.this.startActivity(intent);
                }
            });
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

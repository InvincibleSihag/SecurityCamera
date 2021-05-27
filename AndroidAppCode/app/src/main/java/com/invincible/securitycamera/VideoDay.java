package com.invincible.securitycamera;
import android.content.Intent;
import android.graphics.Bitmap;
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
import java.util.BitSet;

public class VideoDay extends AppCompatActivity {
    ListView listView;
    String [] file;
    Bitmap[] bitmaps;
    int [] icons;
    String ip;
    CustomAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.video_day);
        ip = getString(R.string.ipAddress);
        listView = findViewById(R.id.video_dayList);
        final String day = getIntent().getStringExtra("DAY");
        BackgroundDataRetrieve br = new BackgroundDataRetrieve();
        br.execute(ip+"api/v1/resources/?day="+day);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(VideoDay.this,VideoPlayer.class);
                intent.putExtra("DIRECT",day+"/"+file[i]);
                VideoDay.this.startActivity(intent);
            }
        });
    }
    private class BackgroundDataRetrieve extends AsyncTask {
        String files = "";
        String [] fileList;
        Bitmap[] thumbnails;
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
                    files += current;
                    data = reader.read();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileList = files.split(" ");
            publishProgress("got Files");
            System.out.println("Files = "+fileList);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            file = fileList;
            adapter = new CustomAdapter(getApplicationContext(),file,icons,"VideoDay",null);
            listView.setAdapter(adapter);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }
}

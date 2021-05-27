package com.invincible.securitycamera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ListActivity extends AppCompatActivity
{
    String ip;
    ListView featureList;
    WebView videoView;
    String features[] = {"SAVED VIDEOS","DETECTED FACES"};
    int icons[] = {R.drawable.videos,R.drawable.face};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.list_main);
        videoView = findViewById(R.id.webView);
        videoView.setWebViewClient(new WebViewClient());
        videoView.getSettings().setJavaScriptEnabled(true);
        ip = getString(R.string.ipAddress);
        videoView.loadUrl(ip+"api/videoStream/");
        Intent intent = getIntent();
        String value = intent.getStringExtra("key");
        featureList = findViewById(R.id.listView);
        CustomAdapter adapter = new CustomAdapter(getApplicationContext(),features,icons,"ListActivity",null);
        featureList.setAdapter(adapter);
        featureList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0)
                {
                    Intent intent2 = new Intent(ListActivity.this,SavedVideos.class);
                    ListActivity.this.startActivity(intent2);
                }
                else if (i==1)
                {
                    Intent intent1 = new Intent(ListActivity.this,SavedFaces.class);
                    ListActivity.this.startActivity(intent1);
                }
            }
        });

    }

}

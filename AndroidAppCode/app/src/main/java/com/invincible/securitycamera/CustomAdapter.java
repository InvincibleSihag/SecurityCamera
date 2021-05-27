package com.invincible.securitycamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    WebView webView;
    Context context;
    String featureList[];
    int icons[];
    String whichAcctivity;
    LayoutInflater inflter;
    ArrayList<String> bitmaps;
    public CustomAdapter(Context applicationContext, String[] featureList, int[] icons, String whichActivity, ArrayList<String> bitmaps) {
        this.context = context;
        this.featureList = featureList;
        this.icons = icons;
        this.whichAcctivity = whichActivity;
        this.bitmaps = bitmaps;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return featureList.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (whichAcctivity == "FaceDay") {
            System.out.println(bitmaps.isEmpty());
            view = inflter.inflate(R.layout.face,null);
            webView = view.findViewById(R.id.faceView);
            //webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(bitmaps.get(i));
            return view;
        } else {
            view = inflter.inflate(R.layout.features_list, null);
            TextView text = (TextView) view.findViewById(R.id.textView3);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            text.setText(featureList[i]);
            if (whichAcctivity == "ListActivity") {
                imageView.setImageResource(icons[i]);
            } else if (whichAcctivity == "SavedVideos") {
                imageView.setImageResource(icons[0]);
            } else if (whichAcctivity == "SavedFaces")
            {
                imageView.setImageResource(R.drawable.folder);
            }
            else if(whichAcctivity == "VideoDay")
            {
                imageView.setImageResource(R.drawable.vlc);
            }
            return view;
        }
    }
}

package com.example.playersample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<VideoModel> videoList;
    private ViewPager2 videoViewPager;
    ViewPagerVideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoViewPager = findViewById(R.id.videoViewPager);

        videoList = new ArrayList<>();
        for (int i =0; i<100; i++){
            videoList.add(new VideoModel("https://github.com/13yadav/API/raw/main/VideoDummyApi/data/arcoiblue___CI8NFTIHz5C___.mp4", 0));
            videoList.add(new VideoModel("https://github.com/13yadav/API/raw/main/VideoDummyApi/data/jerajrockzzz___CJQc3KenyuN___.mp4", 0));
            videoList.add(new VideoModel("https://github.com/13yadav/API/raw/main/VideoDummyApi/data/chau_codes___CJEc2MPgeMN___.mp4", 0));
        }
        adapter = new ViewPagerVideoAdapter(videoList, this);

        videoViewPager.setAdapter(adapter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter.simpleExoplayer!=null){
            adapter.simpleExoplayer.stop();
        }
    }
}
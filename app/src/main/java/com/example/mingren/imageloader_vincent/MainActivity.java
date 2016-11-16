package com.example.mingren.imageloader_vincent;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = (ImageView) findViewById(R.id.iv_test);
        String url = "http://imgsrc.baidu.com/forum/w%3D580/sign=c1c6791a42166d223877159c76220945/9f842da4462309f7046f53c3730e0cf3d6cad62a.jpg";
        ImageLoader.getInstance(this).loadImage(url, imageView);
    }
}

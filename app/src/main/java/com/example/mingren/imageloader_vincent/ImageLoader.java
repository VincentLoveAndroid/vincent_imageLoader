package com.example.mingren.imageloader_vincent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by vincent on 2016/11/15.
 * email-address:674928145@qq.com
 * description:自定义图片缓存框架，内存-磁盘 二级缓存
 */

public class ImageLoader {

    private static ImageLoader mImageLoader;
    private static Context mContext;
    public static final int MAX_CAPACITY = 20;
    private static LinkedHashMap<String, SoftReference<Bitmap>> mCacheMap = new LinkedHashMap<String, SoftReference<Bitmap>>() {

        @Override
        protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> eldest) {
            if (mCacheMap.size() > MAX_CAPACITY) {//缓存超过20个，回收最旧的元素
                cacheDisk(eldest.getKey(), eldest.getValue());//缓存到磁盘
                return true;
            }
            return false;
        }
    };

    public static ImageLoader getInstance(Context context) {
        mContext = context;
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader();
        }
        return mImageLoader;
    }


    //将图片从内存写到磁盘里面去
    private static void cacheDisk(String key, SoftReference<Bitmap> value) {
        String md5FileName = MD5Utils.getMD5Str32byte(key);
        String filePath = mContext.getCacheDir().getAbsolutePath() + "/" + md5FileName;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            if (value.get() != null) {
                value.get().compress(Bitmap.CompressFormat.JPEG, 100, fos);//压缩bitmap并根据fos流生成对应的文件
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadImage(String url, ImageView imageView) {
        synchronized (imageView) {
            Bitmap result = getCacheFromMemory(url) != null ? getCacheFromMemory(url) : getCacheFromDisk(url);
            if (result != null) imageView.setImageBitmap(result);
            else {
                //下载图片
                DownLoadAsyncTask downLoadAsyncTask = new DownLoadAsyncTask(imageView);
                downLoadAsyncTask.execute(url);
            }
        }
    }

    private Bitmap getCacheFromDisk(String key) {
        if (key == null) return null;
        String md5FileName = MD5Utils.getMD5Str32byte(key);
        String filePath = mContext.getCacheDir().getAbsolutePath() + "/" + md5FileName;
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = new FileInputStream(new File(filePath));
            bitmap = BitmapFactory.decodeStream(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    private Bitmap getCacheFromMemory(String key) {
        Bitmap result = null;
        synchronized (mCacheMap) {
            if (mCacheMap.get(key) != null) {
                result = mCacheMap.get(key).get();
            }
            return result;
        }
    }


    private class DownLoadAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView imageView;
        private String key;

        public DownLoadAsyncTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            key = strings[0];
            InputStream is = null;
            Bitmap bitmap = null;
            try {
                is = HttpUtil.downLoadFile(key);
                bitmap = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                addFirstCache(key, bitmap);
            }
        }
    }

    private void addFirstCache(String key, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (mCacheMap) {
                mCacheMap.put(key, new SoftReference(bitmap));
            }
        }
    }
}

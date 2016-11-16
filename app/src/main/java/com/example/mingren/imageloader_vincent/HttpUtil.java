package com.example.mingren.imageloader_vincent;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by vincent on 2016/11/15.
 * email-address:674928145@qq.com
 * description:
 */

public class HttpUtil {

    public static InputStream downLoadFile(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();//注意不是URlConnection
        return conn.getInputStream();
    }
}

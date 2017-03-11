package com.example.nirmal.ilistensinch;

/**
 * Created by nirmal on 8/3/17.
 */
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.android.volley.toolbox.HurlStack;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

public class okhttpstack extends HurlStack {
    private final OkUrlFactory mFactory;

    public okhttpstack() {
        this(new OkHttpClient());
    }

    public okhttpstack(OkHttpClient client) {
        if (client == null) {
            throw new NullPointerException("Client must not be null.");
        }
        mFactory = new OkUrlFactory(client);
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        return mFactory.open(url);
    }
}
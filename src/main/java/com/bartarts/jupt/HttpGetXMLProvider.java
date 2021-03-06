package com.bartarts.jupt;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * Created by bartek on 01.03.15.
 */
public class HttpGetXMLProvider extends UpdateInfoProvider {
    protected URL url;
    protected List<UpdateInfo> updateInfoList;
    protected static final String requestDelimiter = "&";

    public HttpGetXMLProvider(URL address) {
        if(address == null)
            throw new NullPointerException();
        url = address;
    }

    public HttpGetXMLProvider(String address) throws MalformedURLException {
        this(new URL(address));
    }

    public HttpGetXMLProvider(URI address) throws MalformedURLException {
        this(address.toURL());
    }

    /**
     * Wczytuje liste klas z serwera, uzywajac zapytania GET z ustawionymi parametrami z UpdateInfoProvider {@see UpdateInfoProvider}.
     */
    public void reloadList() {
        for(int i = 1; i <= NUM_OF_ATTEMPTS; ++i) {
            try {
                String params = joinParameters(requestDelimiter);
                URL allIn = params.isEmpty() ? url : new URL(String.join("?", url.toExternalForm(), params));
                HttpURLConnection con = (HttpURLConnection) allIn.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                int response = con.getResponseCode();
                if (response != 200) return;
                InputStream inputStream = con.getInputStream();
                List<UpdateInfo> l = UpdateInfoParsers.parseXML(inputStream);
                updateInfoList = l;
                inputStream.close();
                break;
            } catch (Exception e) {
                try { Thread.sleep(i * 1000); } catch (InterruptedException ie) {}
                continue;
            }
        }
    }

    @Override
    public List<UpdateInfo> getList() {
        if(updateInfoList != null)
            return updateInfoList;
        reloadList();
        return updateInfoList;
    }
}

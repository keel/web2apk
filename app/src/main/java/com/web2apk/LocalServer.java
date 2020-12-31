package com.web2apk;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class LocalServer extends NanoHTTPD {


    public static int localServerPort = 8888;
    public static int timeOut = 5000;
    public static String webAssetsRoot = "web";


    private LocalServer(int port) {
        super(port);
    }
    private static LocalServer me;
    private static Context ctx;

    public static final LocalServer create(Context context){
        ctx = context;
        if(me != null){
            return  me;
        }
        me = new LocalServer(localServerPort);
        return me;
    }


    public static final void exit(){
        if(me == null){
            return;
        }
        me.stop();
        me = null;
        Log.e("====>","LocalServer exited.");
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        return  defaultRespond(session,uri);
    }


    protected Response getForbiddenResponse(String s) {
        return newFixedLengthResponse(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: " + s);
    }

    protected Response getInternalErrorResponse(String s) {
        return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "INTERNAL ERROR: " + s);
    }

    protected Response getNotFoundResponse() {
        return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Error 404, file not found.");
    }

    private Response assetsFileResponse(String filePath, String mime) {
        Response res;
        AssetManager am = ctx.getResources().getAssets();
        filePath = webAssetsRoot+filePath;
        try {
            AssetFileDescriptor afd = am.openFd(filePath);
            FileInputStream fin = afd.createInputStream();
//            Log.e("====>","assetsFileResponse:"+filePath);
            res = newFixedLengthResponse(Response.Status.OK, mime, fin, afd.getLength());
            res.addHeader("Content-Length", "" + afd.getLength());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("====>","assetsFileResponse ERROR.filePath:"+filePath+" e:"+e);
//            return getInternalErrorResponse("read file");
            return getNotFoundResponse();
        }

        return res;
    }

    //
    private Response crossOutUrl(IHTTPSession session, String uri){
        String method = session.getMethod().toString();
        Log.i("====>","crossOutUrl:" + uri+","+method);
        Response res;
        try {
            Map<String, List<String>> paras = session.getParameters();
            uri = (paras.get("u")).get(0);
            Log.i("====>","new uri:[" + uri+"]");

            Map<String, String> headers = session.getHeaders();


            URL url = new URL(uri);
            URLConnection connection = url.openConnection();
            HttpURLConnection http =  (HttpURLConnection) connection;
            http.setRequestMethod(method);
            headers.remove("referer");
            headers.remove("host");
            headers.remove("remote-addr");
            headers.remove("origin");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
//                Log.i("====>","HEADER:" + entry.getKey() + "=" + entry.getValue());
                http.setRequestProperty(entry.getKey(),entry.getValue());
            }
            http.setReadTimeout(timeOut);
//          http.setConnectTimeout(5000);
//          http.setDoInput(true);//default is true, don't need
            if(method.equals("POST")){
                http.setDoOutput(true);
                http.setUseCaches(false);
                OutputStream outputStream = http.getOutputStream();
                int maxLen = session.getInputStream().available();
                int bufLen = (maxLen>= 0 && maxLen < 1024) ? maxLen : 1024;
                byte[] bys = new byte[bufLen];
//                Log.e("====>","maxLen::"+maxLen+",bufLen:"+bufLen);
                int i = 0;
                for (int j = 0; j < maxLen; j++) {
                    if (i >= bufLen){
                        outputStream.write(bys,0,i);
//                        Log.e("====>","write bys1:"+new String(bys)+",i:"+i);
                        i = 0;
                    }
                    byte bt = (byte)session.getInputStream().read();
                    bys[i] = (byte)bt;
                    i++;
                }
                if(i>0){
                    outputStream.write(bys,0,i);
//                    Log.e("====>","write bys2:"+new String(bys)+",i:"+i);
                }
                outputStream.flush();
//                Log.e("====>","write done!!!!");
                outputStream.close();
            }
            InputStream inputStream = http.getInputStream();
            final int httpResStatus = http.getResponseCode();
            res = newChunkedResponse(Response.Status.lookup(httpResStatus),getMimeTypeForFile(uri),inputStream);

        } catch (Exception e) {
            Log.e("====>","ERR-crossOutUrl:"+method+":" + uri);
            e.printStackTrace();
            return getInternalErrorResponse("cross uri:"+uri);
        }
        return res;
    }


    private Response defaultRespond(IHTTPSession session, String uri) {
        if(this.ctx ==null){
            return getInternalErrorResponse("ctx");
        }
        if (Method.OPTIONS.equals(session.getMethod())) {
            return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, null, 0);
        }
        if(uri.indexOf("__cross__") > 0){
            return crossOutUrl(session,uri);
        }


        String mimeTypeForFile = getMimeTypeForFile(uri);
        Response response = assetsFileResponse(uri, mimeTypeForFile);

//        Log.e("====>","defaultRespond res done:"+uri);
        return response != null ? response : getNotFoundResponse();
    }




}

package com.web2apk;



import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;



public class MainActivity extends Activity {


    //the tip of "Press back again to exit"
    private static String exitTip = "Press back again to exit"; //可改为"再按一次退出"
    private static int exitConfirmTime = 2000;
    static {
        // local server's port, keep it same as "ajax-cross.js";
        // 本地服务器的端口, 修改后注意需要与ajax-cross.js中的相同;
        LocalServer.localServerPort = 8888;
        LocalServer.timeOut = 5000;
        LocalServer.webAssetsRoot = "web";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(startLocalServer()){
            loadUrl("http://localhost:"+LocalServer.localServerPort+"/index.html");
        }

    }

    public static void hideNavKey(Context context) {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = ((Activity) context).getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = ((Activity) context).getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    private final boolean  startLocalServer(){
        try {
            LocalServer.create(this).start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("====>","startLocalServer ERROR:"+e);
            Toast.makeText(this,"start failed!",Toast.LENGTH_SHORT).show();
            return false;
        }

        Log.e("====>","startLocalServer OK!");
        return  true;
    }

    private WebView rootWebView;


    public void loadUrl(String url) {
        rootWebView = new WebView(MainActivity.this);
        rootWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e("====>","onReceivedError:"+errorCode+","+description);
                Toast.makeText(MainActivity.this,"request error!",Toast.LENGTH_SHORT);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(!url.startsWith("http://localhost")){
                    Log.e("====>","shouldOverrideUrlLoading:"+url);
                    view.loadUrl(url);
                    return true;
                }
                return false;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    if(request.getUrl()!=null && request.getUrl().getPath() !=null && request.getUrl().getPath().endsWith("/favicon.ico")) {
                        try {
//                            Log.e("====>","shouldInterceptRequest stop favicon.ico");
                            return new WebResourceResponse("image/png", null, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }
        });

        rootWebView.getSettings().setJavaScriptEnabled(true);
        rootWebView.getSettings().setAllowFileAccess(true);
        rootWebView.getSettings().setAppCacheEnabled(true);
        rootWebView.getSettings().setDomStorageEnabled(true);
        rootWebView.getSettings().setDatabaseEnabled(true);
//        rootWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        rootWebView.loadUrl(url);
        setContentView(rootWebView);
        hideNavKey(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalServer.exit();
    }


    /***
     * click back to nav back.
     * 单击返回
     * **/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && rootWebView!=null && rootWebView.canGoBack()){
            rootWebView.goBack();
            return true;
        }else if (keyCode==KeyEvent.KEYCODE_BACK){
            exitByDoubleClick();
        }
        return false;
    }

    /**
     * double click to exit.
     * 双击退出(在最初始页才有效)
     */
    private static Boolean isExit = false;
    private void exitByDoubleClick() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true;
            Toast.makeText(this, exitTip, Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, exitConfirmTime);
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavKey(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }
}

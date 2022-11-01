package sd.rittal.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import sd.rittal.app.helper.FormSession;
import sd.rittal.app.helper.LoginSession;

import static com.android.volley.VolleyLog.TAG;

public class AppApplication extends Application {

    private RequestQueue mRequestQueue;
    private static AppApplication mInstance;
    public static final int MY_SOCKET_TIMEOUT_MS = 160000;

   // private String baseUrl = "https://mob.rittal-sd.com";

    //test environment
//    private String baseUrl = "http://mob.rittal-sd.com:8080";
//    private String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==";
//    private String publicKeyForIPIN = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjqmUrQ1w1rOH+kmj4dk3C3duIl1trVRr91EJKhphlWjOs2qqba9I2WmRH3CVUuBmkIEojYrnKtXiQBqMOTANzZuly6vZY3AOT/JVDtKy3/z60mUWUi+ILidh5dZz7ZYKsPupXRz5XQXELLpI4XN58N1oiNY95GutJ/hjqE0qqufQ1ie/hU5Dbof9F+Ug3wSJOzba+IVve09Yx76KsFBtc+0qn2/JI7YPca9s8h4ZKaVo7qvFZNScTieoR5f3vaKUsYNTdRK7HMXEU/BMrg8hzh04+ShtDtKtQ02pvObQ+Kha/oV54bdP5e6m9NpXupoL0ydQIDNZ6B76yVEe5WJM2wIDAQAB";

    //live environment
      private String baseUrl = "https://mob.rittal-sd.com";
      private String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANx4gKYSMv3CrWWsxdPfxDxFvl+Is/0kc1dvMI1yNWDXI3AgdI4127KMUOv7gmwZ6SnRsHX/KAM0IPRe0+Sa0vMCAwEAAQ==";
      private String publicKeyForIPIN = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjqmUrQ1w1rOH+kmj4dk3C3duIl1trVRr91EJKhphlWjOs2qqba9I2WmRH3CVUuBmkIEojYrnKtXiQBqMOTANzZuly6vZY3AOT/JVDtKy3/z60mUWUi+ILidh5dZz7ZYKsPupXRz5XQXELLpI4XN58N1oiNY95GutJ/hjqE0qqufQ1ie/hU5Dbof9F+Ug3wSJOzba+IVve09Yx76KsFBtc+0qn2/JI7YPca9s8h4ZKaVo7qvFZNScTieoR5f3vaKUsYNTdRK7HMXEU/BMrg8hzh04+ShtDtKtQ02pvObQ+Kha/oV54bdP5e6m9NpXupoL0ydQIDNZ6B76yVEe5WJM2wIDAQAB";
    Timer timer;
    FormSession listener;
    LoginSession listener2;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        handleSSLHandshake();

    }
    public void startUserSession() {
        cancelTimer();
        timer= new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                listener.onFormSessionTimeOut();

            }
        },120000);
    }

    public void startLoginSession() {
        cancelTimer();
        timer= new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                listener2.onLoginSessionTimeOut();

            }
        },360000);
    }

    public static synchronized AppApplication getInstance() {
        return mInstance;
    }
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {

                    if(arg0.contains("rittal-sd.com")){
                        return true;
                    }else{
                        return false;
                    }
                }
            });
        } catch (Exception ignored) {
        }
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public String getBaseUrl(){
        return baseUrl;
    }

    public String getPublicKey(){
        return publicKey;
    }


    private void cancelTimer() {
        if(timer !=null)timer.cancel();
    }

    public void registerSessionListener(FormSession listener) {
        this.listener = listener;
    }

    public void registerSessionListener2(LoginSession listener) {
        this.listener2 = listener;
    }

    public void onUserInteracted() {
        startUserSession();
        startLoginSession();
    }

    public String getPublicKeyForIPIN() {
        return publicKeyForIPIN;
    }
}

package sd.rittal.app.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import sd.rittal.app.AppApplication;
import sd.rittal.app.R;
import sd.rittal.app.helper.DataStorage;
import sd.rittal.app.helper.FormSession;
import sd.rittal.app.helper.LoginSession;
import sd.rittal.app.helper.Rittal;

public class AboutActivity extends AppCompatActivity implements FormSession,LoginSession {

    String TAG = AboutActivity.class.getSimpleName().toUpperCase();
    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;
    Bundle extras;
    AppApplication application;
    String lang = "";

    public static String FACEBOOK_URL = "https://www.facebook.com/RittalTech/";
    public static String FACEBOOK_PAGE_ID = "RittalTech";
    public static String TWITTER_URL = "https://twitter.com/Rittal_Tec";
    public static String TWITTER_ID = "790894707027152896";
    public static String INSTAGRAM_URL = "https://www.instagram.com/rittal_tec/";
    public static String WEB_SITE_URL = "http://www.rittal.sd";

    int MY_PERMISSIONS_REQUEST_CALL_PHONE = 100;

    TextView version_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initiateContext();
        init();

        version_name.append(" "+rittal.getVersionName());
    }
    private void initiateContext(){
        activity = this;
        rittal = new Rittal(activity);
        dataStorage = new DataStorage(activity);
        application = new AppApplication();

        ((AppApplication)getApplication()).registerSessionListener(this);
        ((AppApplication)getApplication()).startUserSession();

        ((AppApplication)getApplication()).registerSessionListener2(this);
        ((AppApplication)getApplication()).startLoginSession();
    }
    public void init(){
        version_name = (TextView) findViewById(R.id.version_name);
    }

    public void goToWebSite(View v){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(WEB_SITE_URL)));
    }

    public void goToFacebook(View v){
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        String facebookUrl = getFacebookPageURL(this);
        facebookIntent.setData(Uri.parse(facebookUrl));
        startActivity(facebookIntent);
    }

    //method to get the right URL to use in the intent
    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

    public void goToUserManual(View v) {
                Intent i = new Intent(activity,ListViewActivity.class);
        startActivity(i);
    }

    public void goToTwitter(View v){
        Intent intent = null;
        try {
            // get the Twitter app if possible
            this.getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id="+TWITTER_ID));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(TWITTER_URL));
        }
        this.startActivity(intent);
    }

    public void goToInstgram(View v){
        Uri uri = Uri.parse(INSTAGRAM_URL);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage("com.instagram.android");

        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(INSTAGRAM_URL)));
        }


    }

    public void call(View v) {

        String main_contact = "2955";
        rittal.log("Call",main_contact);
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+main_contact));
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
            return;
        }else{
            startActivity(callIntent);
        }

    }

    public void goToMap(View v){
        try {
            //15.531608,32.553793
            String _lat = "15.531608";//location.split(",")[0];
            String _long = "32.553793";//location.split(",")[1];
            String label = getString(R.string.app_name);
            Uri gmmIntentUri = Uri.parse("geo:"+_lat+","+_long);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(gmmIntentUri+"?q="+ Uri.encode( _lat+","+_long+"("+label+")")));
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivity(mapIntent);
            }else{
                rittal.rittalToast(getString(R.string.notـsupported),"1","i");
            }
        }catch (Exception e){

        }
    }

    public void onUserInteraction() {
        super.onUserInteraction();
        ( (AppApplication)getApplication()).onUserInteracted();
    }

    @Override
    public void onFormSessionTimeOut() {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                // UI code goes here
                rittal.log("formTimeOut","clearing");


            }
        });
    }

    @Override
    public void onLoginSessionTimeOut() {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                // UI code goes here
                rittal.log("sessionTimeOut","clearing");

                rittal.logOut();

            }
        });

    }

}

//https://www.facebook.com/RittalTech/
//https://twitter.com/Rittal_Tec?lang=en
//https://www.instagram.com/rittal_tec/
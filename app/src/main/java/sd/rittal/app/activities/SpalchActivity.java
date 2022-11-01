package sd.rittal.app.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import sd.rittal.app.R;
import sd.rittal.app.helper.Rittal;

public class SpalchActivity extends AppCompatActivity implements Animation.AnimationListener {

    ImageView imageIcon1;
    ImageView imageIcon;
    // Animation
    Animation animMoveToTop,animMoveToDown;
    TextView version_name,copyright;
    PackageInfo pinfo;
    Rittal rittal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalch);
        rittal = new Rittal(this);

        imageIcon1 = (ImageView) findViewById(R.id.icon1);
        imageIcon = (ImageView) findViewById(R.id.icon);
        version_name = (TextView) findViewById(R.id.version_name);
        copyright = (TextView) findViewById(R.id.copyright);
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        rittal.log("APP_LANGUAGE_1",rittal.getValueOfKey("app_language"));

        if(rittal.getValueOfKey("app_language").equals("en")){
            rittal.setValueToKey("app_language","en");
           Locale locale = new Locale("en");
            rittal.log("APP_LANGUAGE_IF",rittal.getValueOfKey("app_language"));
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            // Toast.makeText(this, getResources().getString(R.string.lbl_langSelectEnglis), Toast.LENGTH_SHORT).show();
        }
        else{
            rittal.setValueToKey("app_language","ar");
            Locale locale = new Locale("ar");
            rittal.log("APP_LANGUAGE_ELSE",rittal.getValueOfKey("app_language"));
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            //  Toast.makeText(this, getResources().getString(R.string.lbl_langSelecURdu), Toast.LENGTH_SHORT).show();
        }
        // load the animation
        animMoveToTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_rittal_logo);
        animMoveToDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_rita_logo);
        // set animation listener
        animMoveToTop.setAnimationListener(this);
        imageIcon1.setVisibility(View.VISIBLE);
        imageIcon.setVisibility(View.VISIBLE);
        // start the animation
        imageIcon1.startAnimation(animMoveToDown);
        imageIcon.startAnimation(animMoveToTop);
        getVersionCode();
        version_name.setText(version_name.getText()+" "+getVersionName());
    }

    @Override
    public void onAnimationStart(Animation animation) {
        if (animation == animMoveToTop) {
            //Toast.makeText(getApplicationContext(), "Animation Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        version_name.setVisibility(View.VISIBLE);
        copyright.setVisibility(View.VISIBLE);
        moveToNext();

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void moveToNext(){
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    //sessionManager.setFirstTimeLaunch(true);

                        Intent i = new Intent(SpalchActivity.this,MainActivity.class);
                        startActivity(i);
                        finish();
                }
            }
        };
        timerThread.start();
    }

    // return string => version name in 1.0.0 format
    public String getVersionName(){
        try {
            rittal.setValueToKey("versionName",pinfo.versionName);
        }catch (Exception e){}
        return pinfo.versionName;
    }
    // return int => version code in 1 format
    public int getVersionCode(){
        try {
            rittal.setValueToKey("versionCode",String.valueOf(pinfo.versionCode));
        }catch (Exception e){}
        return pinfo.versionCode;
    }

}

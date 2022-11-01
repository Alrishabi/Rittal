package sd.rittal.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import sd.rittal.app.AppApplication;
import sd.rittal.app.R;
import sd.rittal.app.helper.FormSession;
import sd.rittal.app.helper.LoginSession;
import sd.rittal.app.helper.Rittal;


public class SettingActivity extends AppCompatActivity implements FormSession,LoginSession {

    Activity activity;
    Rittal rittal;
    ArrayList<String> langList;

    TextView current_language;
    Switch sw_card_layout;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        langList = new ArrayList<String>();

        langList.add(getString(R.string.select_question));
        langList.add(getString(R.string.old_password));
        langList.add(getString(R.string.best_friend));

        initContext();
        initLayout();
        currentSetting();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(activity,HomeActivity.class);
        startActivity(i);
        finish();
    }

    /**
     ** bind layout with activity class
     **/
    public void initLayout(){

        current_language = (TextView) findViewById(R.id.current_language);
        sw_card_layout = (Switch) findViewById(R.id.sw_card_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(getString(R.string.setting));



        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sw_card_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sw_card_layout.isChecked()){
                    rittal.setValueToKey("CARD_LAYOUT","");

                }else {
                    rittal.setValueToKey("CARD_LAYOUT","SPINNER");
                }
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    /**
     ** initiation objects
     **/
    public void initContext(){
        activity = this;
        rittal = new Rittal(activity);
        ( (AppApplication)getApplication()).registerSessionListener(this);
        ( (AppApplication)getApplication()).startUserSession();

        ( (AppApplication)getApplication()).registerSessionListener2(this);
        ( (AppApplication)getApplication()).startLoginSession();

    }
    /**
     ** explain current setting
     **/
    public void currentSetting(){
        if(rittal.getValueOfKey("app_language").equals("ar")){
            current_language.setText(getText(R.string.arabic));
        }else if(rittal.getValueOfKey("app_language").equals("en")){
            current_language.setText(getText(R.string.english));
        }
        if(rittal.getValueOfKey("CARD_LAYOUT").equals("SPINNER")){
            sw_card_layout.setChecked(false);
        }else {
            sw_card_layout.setChecked(true);
        }

    }
    /**
     ** sub setting activities
     **/
    public void goTospecialPayment(View v){
        Intent i = new Intent(activity,ServicePaymentActivity.class);
        startActivity(i);
        finish();
    }
    public void goToLanguageOptions(View v){
        Intent i = new Intent(activity,LanguageOptionsActivity.class);
        startActivity(i);
        finish();
    }
    public void goAccessoriesSetting(View v){
        Intent i = new Intent(activity,AccessoriesActivity.class);
        startActivity(i);
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

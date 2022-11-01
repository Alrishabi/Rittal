package sd.rittal.app.activities;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import sd.rittal.app.AppApplication;
import sd.rittal.app.R;
import sd.rittal.app.helper.FormSession;
import sd.rittal.app.helper.LoginSession;
import sd.rittal.app.helper.Rittal;


public class AccessoriesActivity extends AppCompatActivity implements FormSession,LoginSession  {
    Activity activity;
    Rittal rittal;

    Switch switch_bluetooth_printer,switch_usb_printer,switch_s1000_printer,switch_magnetic_card_swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessories);
        initContex();
        initLayout();
        ( (AppApplication)getApplication()).registerSessionListener(this);
        ( (AppApplication)getApplication()).startUserSession();

        ( (AppApplication)getApplication()).registerSessionListener2(this);
        ( (AppApplication)getApplication()).startLoginSession();

        currentAccessoriesSetting();
        listenerForNewSetting();
    }

    /**
     ** bind layout with activity class
     **/
    public void initLayout(){
        switch_bluetooth_printer = (Switch) findViewById(R.id.switch_bluetooth_printer);
        switch_usb_printer = (Switch) findViewById(R.id.switch_usb_printer);
        switch_s1000_printer = (Switch) findViewById(R.id.switch_s1000_printer);
        switch_magnetic_card_swipe = (Switch) findViewById(R.id.switch_magnetic_card_swipe);

    }
    /**
     ** initiation objects
     **/
    public void initContex(){
        activity = this;
        rittal = new Rittal(activity);

    }
    /**
     ** explain current accessories setting
     **/
    public void currentAccessoriesSetting(){
        if(rittal.getValueOfKey("bluetooth_printer_feature").equals("ALLOW")){switch_bluetooth_printer.setChecked(true);}
        if(rittal.getValueOfKey("usb_printer_feature").equals("ALLOW")){switch_usb_printer.setChecked(true);}
        if(rittal.getValueOfKey("s1000_printer_feature").equals("ALLOW")){switch_s1000_printer.setChecked(true);}
        if(rittal.getValueOfKey("s1000_magnetic_card_swipe_feature").equals("ALLOW")){switch_magnetic_card_swipe.setChecked(true);}
    }
    /**
     ** execute any change on setting , allow/prevent any feature
     **/
    public void listenerForNewSetting(){

        switch_bluetooth_printer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    rittal.setValueToKey("bluetooth_printer_feature","ALLOW");
                }else{
                    rittal.setValueToKey("bluetooth_printer_feature","");
                }

            }
        });

        switch_usb_printer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    rittal.setValueToKey("usb_printer_feature","ALLOW");
                }else{
                    rittal.setValueToKey("usb_printer_feature","");
                }

            }
        });

        switch_s1000_printer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    rittal.setValueToKey("s1000_printer_feature","ALLOW");
                }else{
                    rittal.setValueToKey("s1000_printer_feature","");
                }

            }
        });

        switch_magnetic_card_swipe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    rittal.setValueToKey("s1000_magnetic_card_swipe_feature","ALLOW");
                }else{
                    rittal.setValueToKey("s1000_magnetic_card_swipe_feature","");
                }

            }
        });
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

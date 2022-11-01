package sd.rittal.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Locale;

import sd.rittal.app.AppApplication;
import sd.rittal.app.R;
import sd.rittal.app.helper.FormSession;
import sd.rittal.app.helper.LoginSession;
import sd.rittal.app.helper.Rittal;

public class LanguageOptionsActivity extends AppCompatActivity implements FormSession,LoginSession {

    Activity activity;
    Rittal rittal;

    ListView language_option_list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_options);
        View toastView = getLayoutInflater().inflate(R.layout.custom_toast, null);

        initContext();
        initLayout();
        fillLanguageList();

    }
    public void initLayout(){
        language_option_list_view = (ListView) findViewById(R.id.language_option_list_view);
    }
    public void initContext(){
        activity = this;
        rittal = new Rittal(activity);

        ( (AppApplication)getApplication()).registerSessionListener(this);
        ( (AppApplication)getApplication()).startUserSession();

        ( (AppApplication)getApplication()).registerSessionListener2(this);
        ( (AppApplication)getApplication()).startLoginSession();

    }

    public void fillLanguageList(){
        ArrayAdapter<CharSequence> aa = ArrayAdapter.createFromResource(this, R.array.language_options, android.R.layout.simple_list_item_1);
        language_option_list_view.setAdapter(aa);
        language_option_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {

                String item = ((TextView)view).getText().toString();
                if(position == 0){
                    rittal.setValueToKey("app_language","ar");
                    Locale locale = new Locale("ar");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                  //  Toast.makeText(this, getResources().getString(R.string.lbl_langSelecURdu), Toast.LENGTH_SHORT).show();
                }else if(position == 1){
                    rittal.setValueToKey("app_language","en");
                    Locale locale = new Locale("en");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                   // Toast.makeText(this, getResources().getString(R.string.lbl_langSelectEnglis), Toast.LENGTH_SHORT).show();
                }

                rittal.rittalToast(item + " " + position,"0","s");
                Intent i = new Intent(activity,SettingActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();

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

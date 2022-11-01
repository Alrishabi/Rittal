package sd.rittal.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import sd.rittal.app.AppApplication;
import sd.rittal.app.R;
import sd.rittal.app.adapters.ContactAdapter;
import sd.rittal.app.helper.DataStorage;
import sd.rittal.app.helper.FormSession;
import sd.rittal.app.helper.LoginSession;
import sd.rittal.app.helper.Rittal;
import sd.rittal.app.objects.Contact;

public class BeneficiaryListActivity extends AppCompatActivity implements FormSession, LoginSession {

    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;
    ContactAdapter contactAdapter;


    ListView list_view_cards;
    LinearLayout layout_fully,layout_empty;
    Toolbar toolbar;

    public ArrayList<Contact> contact_list = new ArrayList<Contact>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beneficiary_list);

        initContext();
        initLayout();
//        fillCardList();
    }

    /**
     ** bind layout with activity class
     **/
    public void initLayout(){
        list_view_cards = (ListView) findViewById(R.id.list_view_cards);
        layout_fully = (LinearLayout) findViewById(R.id.layout_fully);
        layout_empty = (LinearLayout) findViewById(R.id.layout_empty);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(getString(R.string.beneficiary_list));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     ** initiation objects
     **/

    public void initContext(){
        activity = this;
        rittal = new Rittal(activity);
        dataStorage = new DataStorage(activity);
        contactAdapter = new ContactAdapter(activity,contact_list,null);

        ( (AppApplication)getApplication()).registerSessionListener(this);
        ( (AppApplication)getApplication()).startUserSession();

        ( (AppApplication)getApplication()).registerSessionListener2(this);
        ( (AppApplication)getApplication()).startLoginSession();
      }
    public void newBeneficiary(View v){
            Intent i = new Intent(activity,AddUpdateBeneficiary.class);
            i.putExtra("process","ADD");
            startActivity(i);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

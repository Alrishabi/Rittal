package sd.rittal.app.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

import sd.rittal.app.AppApplication;
import sd.rittal.app.R;
import sd.rittal.app.adapters.TransactionsAdapter;
import sd.rittal.app.helper.DataStorage;
import sd.rittal.app.helper.FormSession;
import sd.rittal.app.helper.LoginSession;
import sd.rittal.app.helper.Rittal;
import sd.rittal.app.objects.Transactions;

public class ReportActivity extends AppCompatActivity implements FormSession,LoginSession {

    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;

    TransactionsAdapter transactionsAdapter;
    ListView list_view_transactions;

    Toolbar toolbar;

    public ArrayList<Transactions> transactionsArrayList = new ArrayList<Transactions>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        initContext();
        initLayout();
    }
    @Override
    protected void onResume() {
        super.onResume();
        getTransactions();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public void initLayout(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        list_view_transactions = (ListView) findViewById(R.id.list_view_transactions);
        //layout_fully = (LinearLayout) findViewById(R.id.layout_fully);
        //layout_empty = (LinearLayout) findViewById(R.id.layout_empty);
        toolbar.setTitle(getString(R.string.report));

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
        transactionsAdapter = new TransactionsAdapter(activity,transactionsArrayList,null,"");
        ( (AppApplication)getApplication()).registerSessionListener(this);
        ( (AppApplication)getApplication()).startUserSession();

        ( (AppApplication)getApplication()).registerSessionListener2(this);
        ( (AppApplication)getApplication()).startLoginSession();


    }
    public ArrayList<Transactions> getTransactions(){
        transactionsArrayList.clear();
        Cursor cards_date = dataStorage.get_transactions();
        cards_date.moveToFirst();
        if (cards_date.moveToFirst()) {
            do {

                int id              = Integer.valueOf(cards_date.getString(cards_date.getColumnIndex("id")));
                String response     = cards_date.getString(cards_date.getColumnIndex("response"));
                String service_id   = cards_date.getString(cards_date.getColumnIndex("service_id"));
                String user_id      = cards_date.getString(cards_date.getColumnIndex("user_id"));
                String op_id        = cards_date.getString(cards_date.getColumnIndex("op_id"));

                Log.d("response from DB",response);

                Transactions transactions = new Transactions(id,response,service_id,user_id,op_id);
                transactionsArrayList.add(transactions);

            }while (cards_date.moveToNext());
        }

        Collections.reverse(transactionsArrayList);
        Log.d("TAg card_list size is ",transactionsArrayList.size()+"");
        transactionsAdapter = new TransactionsAdapter(activity,transactionsArrayList,null,"" );
        list_view_transactions.setAdapter(transactionsAdapter);
        transactionsAdapter.notifyDataSetChanged() ;
//        if(transactionsArrayList.size() > 0){
//            layout_empty.setVisibility(View.GONE);
//            layout_fully.setVisibility(View.VISIBLE);
//        }else{
//            layout_empty.setVisibility(View.VISIBLE);
//            layout_fully.setVisibility(View.GONE);
//        }
        return  transactionsArrayList;
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

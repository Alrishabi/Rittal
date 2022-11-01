package sd.rittal.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

import sd.rittal.app.AppApplication;
import sd.rittal.app.adapters.CardsAdapter;
import sd.rittal.app.helper.Encryption;
import sd.rittal.app.helper.FormSession;
import sd.rittal.app.helper.LoginSession;
import sd.rittal.app.objects.Card;
import sd.rittal.app.R;
import sd.rittal.app.helper.DataStorage;
import sd.rittal.app.helper.Rittal;

public class CardsListActivity extends AppCompatActivity implements FormSession,LoginSession {
    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;
    CardsAdapter cardsAdapter;

    ListView list_view_cards;
    LinearLayout layout_fully,layout_empty;
    Toolbar toolbar;

    public ArrayList<Card> card_list = new ArrayList<Card>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards_list);

        initContext();
        initLayout();
        fillCardList();
    }
    @Override
    protected void onResume() {
        super.onResume();
        fillCardList();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    /**
     ** bind layout with activity class
     **/
    public void initLayout(){
        list_view_cards = (ListView) findViewById(R.id.list_view_cards);
        layout_fully = (LinearLayout) findViewById(R.id.layout_fully);
        layout_empty = (LinearLayout) findViewById(R.id.layout_empty);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(getString(R.string.my_card));
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
        cardsAdapter = new CardsAdapter(activity,card_list,null);

        ( (AppApplication)getApplication()).registerSessionListener(this);
        ( (AppApplication)getApplication()).startUserSession();

        ( (AppApplication)getApplication()).registerSessionListener2(this);
        ( (AppApplication)getApplication()).startLoginSession();


    }
    /**
     ** move to AddUpdateCard activity with process flag (ADD)
     **/
    public void newCard(View v){
        Intent i = new Intent(activity,AddUpdateCard.class);
        i.putExtra("process","ADD");
        startActivity(i);
    }
    /**
     ** move to AddUpdateCard activity with process flag (UPDATE)
     ** all Card data must be sent to AddUpdateCard activity
     **/
    public void updateCard(String id, String name, String pan,String expiry_date,String background){
        Intent i = new Intent(activity,AddUpdateCard.class);
        i.putExtra("process","UPDATE");
        i.putExtra("id",id);
        i.putExtra("name",name);
        i.putExtra("pan",pan);
        i.putExtra("expiry_date",expiry_date);
        i.putExtra("background",background);
        startActivity(i);
    }

    public void fillCardList(){
        card_list.clear();
        Cursor cards_date = dataStorage.get_cards();
        cards_date.moveToFirst();
        if (cards_date.moveToFirst()) {
            do {

                int id              = Integer.valueOf(cards_date.getString(cards_date.getColumnIndex("id")));
                String name         = cards_date.getString(cards_date.getColumnIndex("name"));
                String pan          = "";
                rittal.log("PAN_", cards_date.getString(cards_date.getColumnIndex("pan")));
                try {
                    pan = Encryption.Decrypt(cards_date.getString(cards_date.getColumnIndex("pan")));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String expiry_date  = cards_date.getString(cards_date.getColumnIndex("expiry_date"));
                String color        = cards_date.getString(cards_date.getColumnIndex("color"));

                Log.d("pan",pan);

                Card card = new Card(id,name,pan,expiry_date,color);
                card_list.add(card);

            }while (cards_date.moveToNext());
        }

        Collections.reverse(card_list);
        cardsAdapter = new CardsAdapter(activity,card_list,null);
        list_view_cards.setAdapter(cardsAdapter);
        cardsAdapter.notifyDataSetChanged() ;

        if(card_list.size() > 0){
            layout_empty.setVisibility(View.GONE);
            layout_fully.setVisibility(View.VISIBLE);
        }else{
            layout_empty.setVisibility(View.VISIBLE);
            layout_fully.setVisibility(View.GONE);
        }

    }

    public void notifyChanged(){


//        adapter = new CardsAdapter(activity,progressBar,list,responseDialog);
//        listView.setAdapter(adapter);
//
//        if(list.size() == 0 ) {
//            progressBar.setVisibility(View.GONE);
//        }
//        if(list.size() > 0){
//            progressBar.setVisibility(View.GONE);
//        }
//        adapter.notifyDataSetChanged() ;
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
    public void newContact(View view) {
        Intent i = new Intent(activity,AddUpdateCard.class);
        i.putExtra("process","ADD");
        startActivity(i);
    }
}

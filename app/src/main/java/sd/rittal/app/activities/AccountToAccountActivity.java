package sd.rittal.app.activities;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import sd.rittal.app.AppApplication;
import sd.rittal.app.R;
import sd.rittal.app.helper.DataStorage;
import sd.rittal.app.helper.FormSession;
import sd.rittal.app.helper.LoginSession;
import sd.rittal.app.helper.Rittal;

public class AccountToAccountActivity extends AppCompatActivity implements FormSession, LoginSession {

    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;
    AppApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_to_account);
    }

    private void initiateContext(){
        activity        = this;
        rittal          = new Rittal(activity);
        dataStorage     = new DataStorage(activity);
        application     = new AppApplication();

        ((AppApplication)getApplication()).registerSessionListener(this);
        ((AppApplication)getApplication()).startUserSession();

        ((AppApplication)getApplication()).registerSessionListener2(this);
        ((AppApplication)getApplication()).startLoginSession();


    }

    private void initiateLayout() {

//        user_name        = (TextView) findViewById(R.id.user_name);
//        user_code        = (TextView) findViewById(R.id.user_code);
//        iv_qr_code       = (ImageView) findViewById(R.id.iv_qr_code);
//
//        btn_cashout       = (Button) findViewById(R.id.btn_cashOut);
//        ban_accountToCard = (Button) findViewById(R.id.ban_accountToCard);
//        btn_cardToAccount       = (Button) findViewById(R.id.btn_cardToAccount);
//        btn_knwonGenerateVoucher       = (Button) findViewById(R.id.btn_knwonGenerateVoucher);
//        btn_voucherCashOut = (Button) findViewById(R.id.btn_voucherCashOut);
//        btn_logout       = (Button) findViewById(R.id.btn_logout);
//
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//
//        toolbar.setTitle(getString(R.string.title_rittal_account));
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//        btn_logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                rittal.logOutFromAccount();
//                finish();
//                startActivity( new Intent(activity,LoginActivity.class));
//            }
//        });
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

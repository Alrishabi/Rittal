package sd.rittal.app.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import sd.rittal.app.AppApplication;
import sd.rittal.app.R;
import sd.rittal.app.adapters.CarouselPagerAdapter;
import sd.rittal.app.helper.DataStorage;
import sd.rittal.app.helper.FormSession;
import sd.rittal.app.helper.IPINBlockGenerator;
import sd.rittal.app.helper.LoginSession;
import sd.rittal.app.helper.ResponseDialog;
import sd.rittal.app.helper.Rittal;



public class CashoutActivity extends AppCompatActivity implements FormSession,LoginSession {

    String TAG = CashoutActivity.class.getSimpleName().toUpperCase();
    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;
    Bundle extras;
    AppApplication application;
    ProgressDialog progressDialog;
    Dialog dialog;

    LinearLayout layout_card_data,layout_card_secret;
    EditText et_card_pan,et_card_expiry_date,et_card_pin,et_amount;
    Button btn_cashOut;
    Switch switch_use_saved_card;

    Toolbar toolbar;

    public CarouselPagerAdapter adapter;
    public ViewPager pager;
    Spinner sp_cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashout);

        initiateContext();
        initiateLayout();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void initiateContext(){
        activity = this;
        rittal = new Rittal(activity);
        dataStorage = new DataStorage(activity);
        application = new AppApplication();

        ( (AppApplication)getApplication()).registerSessionListener(this);
        ( (AppApplication)getApplication()).startUserSession();

        ( (AppApplication)getApplication()).registerSessionListener2(this);
        ( (AppApplication)getApplication()).startLoginSession();


        dialog = new Dialog(activity, R.style.DialogTheme);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
    }

    private void initiateLayout(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        switch_use_saved_card   = (Switch)findViewById(R.id.switch_use_saved_card);
        et_card_pan             = (EditText) findViewById(R.id.et_card_pan);
        et_card_expiry_date     = (EditText) findViewById(R.id.et_card_expiry_date);
        et_card_pin             = (EditText) findViewById(R.id.et_card_pin);
        et_amount                  = (EditText) findViewById(R.id.et_amount);
        btn_cashOut                = (Button) findViewById(R.id.btn_cashOut);
        sp_cards                = (Spinner) findViewById(R.id.sp_cards);
        pager                   = (ViewPager) findViewById(R.id.myviewpager);
        layout_card_data        = (LinearLayout) findViewById(R.id.layout_card_data);
        layout_card_secret      = (LinearLayout) findViewById(R.id.layout_card_secret);

        rittal.myCards("C",adapter,getSupportFragmentManager(),switch_use_saved_card,sp_cards,pager,et_card_pan,et_card_expiry_date,layout_card_data,layout_card_secret);

        toolbar.setTitle(getString(R.string.cash_out));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        et_card_expiry_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//        createDialogWithoutDateField().show();
                MonthYearPickerDialog pickerDialog = new MonthYearPickerDialog();
                pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int i2) {
                        String y=String.valueOf(year);
                        if(month>9) {
                            Toast.makeText(activity, y.substring(2) + month, Toast.LENGTH_SHORT).show();
                            et_card_expiry_date.setText(y.substring(2) + month);
                        }else {
                            Toast.makeText(activity, y.substring(2) + "0"+month, Toast.LENGTH_SHORT).show();
                            et_card_expiry_date.setText(y.substring(2) + "0"+month);
                        }
                    }
                });
                pickerDialog.show(getSupportFragmentManager(), "Expire date picker ");
            }
        });

        btn_cashOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pan      = et_card_pan.getText().toString().trim().replace(" ","");
                String expiry_date = et_card_expiry_date.getText().toString().trim().replace("/","");
                String ipin   = et_card_pin.getText().toString().trim();
                String amount   = et_amount.getText().toString().trim();


                if(validation(pan,expiry_date,ipin,amount)){
                    clearForm();
                    cashout(pan,expiry_date,ipin,amount);
                }
            }
        });

    }

    public void clearForm(){
        et_card_pan.setText("");
        et_card_expiry_date.setText("");
        et_card_pin.setText("");
        et_amount.setText("");
    }

    public Boolean validation(String pan, String expiry_date, String ipin,String amount){
        // check if transaction from account or card , ACCC mean account transaction
        if(!expiry_date.equals("ACCC")) {
            // transaction is from card
            if(pan.length() == 0 || expiry_date.length() == 0 || ipin.length() == 0 || amount.length()==0){
                if(pan.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pan),"1","w"); return false;}
                if(expiry_date.length() == 0){rittal.rittalToast(getString(R.string.enter_card_expiry_date),"1","w"); return false;}
                if(ipin.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pin),"1","w"); return false;}
                if(amount.length() == 0){rittal.rittalToast(getString(R.string.enter_amount),"1","w"); return false;}
                return false;

            }else{
                if(pan.length() == 16 || pan.length() == 19){
                    if(expiry_date.length() == 4){
                        if(ipin.length() == 4){
                            if(amount.length() !=0){
                                return true;
                            }else {
                                rittal.rittalToast(getString(R.string.enter_amount),"1","w");
                                return false;
                            }
                        }else {
                            rittal.rittalToast(getString(R.string.card_ipin_lengh),"1","w");
                            return false;
                        }
                    }else{
                        rittal.rittalToast(getString(R.string.card_expiry_date_length),"1","w");
                        return false;
                    }
                }else{
                    rittal.rittalToast(getString(R.string.card_pan_length),"1","w");
                    return false;
                }
            }
        }else{
            // transaction from account
            if(amount.length()==0){
                if(amount.length() == 0){rittal.rittalToast(getString(R.string.enter_amount),"1","w"); return false;}
                return false;

            }else{
                if(amount.length() !=0){
                    return true;
                }else {
                    rittal.rittalToast(getString(R.string.enter_amount),"1","w");
                    return false;
                }
            }

        }

    }
    public void clearForm(View V){
        clearForm();
    }

    private void cashout(String pan , final String exdate , String ipin, String amount ) {

        et_card_pan.setText("");
        et_card_expiry_date.setText("");
        et_card_pin.setText("");
        et_amount.setText("");
//        et_otp.setText("");
        final String uuid = UUID.randomUUID().toString();

        String ipin_ = "";

        rittal.log("normal pkey",rittal.getValueOfKey("pk"));
        rittal.log("normal uuid",uuid);
        rittal.log("normal ipin",ipin);
        rittal.log("normal pan",pan);
        rittal.log("normal ipin",exdate);
        String pk = application.getPublicKey();//"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==";//rittal.getValueOfKey("pk");// "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANx4gKYSMv3CrWWsxdPfxDxFvl+Is/0kc1dvMI1yNWDXI3AgdI4127KMUOv7gmwZ6SnRsHX/KAM0IPRe0+Sa0vMCAwEAAQ==";

        ipin_ = IPINBlockGenerator.getIPINBlock(ipin,pk,uuid);

//         ipin_ = IPINBlockGenerator.getIPINBlock(ipin,"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==",uuid);;
        String url = application.getBaseUrl()+"/consumer_services.aspx";
        //?service=2&cust_id=1&pan="+Uri.encode(pan)+"&ex_date="+Uri.encode(exdate)+"&
        // ipin="+Uri.encode(text)+"&uuid="+Uri.encode(uuid)+"&p_key="+pk;
        rittal.log("pubKeyValue",rittal.getValueOfKey("pubKeyValue"));
        rittal.log("URL",url);
        rittal.log("ipin",ipin_);

        final String service = "12";
        final String op_id = "";


        showprogres();
        JSONObject js = new JSONObject();
        try {
            js.put("service",service);
            js.put("customerID",rittal.getValueOfKey("user_id"));
            js.put("fromCard",pan);
            js.put("expiryDate",exdate);
            js.put("ipin",ipin_);
            js.put("uuid",uuid);
            js.put("tranAmount",amount);
            js.put("publicKey",pk);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject error = new JSONObject();


        rittal.log("OBJ",js.toString());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, js,

                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        rittal.log("TAG Response", response.toString());
                        String transactionSource = "";
                        if(exdate.toUpperCase().equals("ACCC")){
                            transactionSource = "ACCOUNT";
                        }else{
                            transactionSource = "CARD";
                        }
                        try {
                            dataStorage.add_transaction(response.toString(),service,rittal.getValueOfKey("user_id"),op_id,transactionSource);
                        }catch (Exception e){

                        }

                        try {
                            ResponseDialog responseDialog = new ResponseDialog(activity,dialog,response,service,"","","","","","","",null,transactionSource);
                            responseDialog.show();

                        } catch (Exception e) {
                            e.printStackTrace();
                            rittal.rittalToast(getString(R.string.error_in_data_processing),"1","w");
                        }
                        dismsprogres();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        rittal.log("TAG Respons ErrorListener", error.toString());
                        rittal.rittalToast(getString(R.string.error_on_network_connection),"1","w");
                        dismsprogres();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                return params;
            }
        };
        req.setRetryPolicy(new DefaultRetryPolicy(
                AppApplication.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        req.setShouldCache(false);
        AppApplication.getInstance().addToRequestQueue(req);
    }
    public void showprogres(){
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
    }
    public void dismsprogres(){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
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

                clearForm();

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

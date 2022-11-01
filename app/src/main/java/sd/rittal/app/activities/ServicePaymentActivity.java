package sd.rittal.app.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

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


public class ServicePaymentActivity extends AppCompatActivity implements FormSession,LoginSession {

    String TAG = CardToCardTransferActivity.class.getSimpleName().toUpperCase();
    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;
    Bundle extras;
    AppApplication application;
    ProgressDialog progressDialog;
    Dialog dialog;

    Toolbar toolbar;
    public CarouselPagerAdapter adapter;
    Spinner sp_cards;
    public ViewPager pager;

    LinearLayout layout_card_data,layout_card_secret;
    EditText et_card_pan,et_card_expiry_date,et_card_pin,et_amount,et_provider_id;
    Button btn_request;
    Switch switch_use_saved_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_payment);
        initiateContext();
        initiateLayout();

    }

    @Override
    protected void onResume() {
        super.onResume();
        clearForm();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initiateContext(){
        activity        = this;
        rittal          = new Rittal(activity);
        dataStorage     = new DataStorage(activity);
        application     = new AppApplication();

        ( (AppApplication)getApplication()).registerSessionListener(this);
        ( (AppApplication)getApplication()).startUserSession();

        ( (AppApplication)getApplication()).registerSessionListener2(this);
        ( (AppApplication)getApplication()).startLoginSession();

        dialog          = new Dialog(activity, R.style.DialogTheme);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
    }

    private void initiateLayout(){
        toolbar    = (Toolbar) findViewById(R.id.toolbar);
        switch_use_saved_card   = (Switch)findViewById(R.id.switch_use_saved_card);
        et_card_pan             = (EditText) findViewById(R.id.et_card_pan);
        et_card_expiry_date     = (EditText) findViewById(R.id.et_card_expiry_date);
        et_card_pin             = (EditText) findViewById(R.id.et_card_pin);
        et_provider_id             = (EditText) findViewById(R.id.et_provider_id);
        et_amount             = (EditText) findViewById(R.id.et_amount);
        btn_request             = (Button) findViewById(R.id.btn_request);
        sp_cards                = (Spinner) findViewById(R.id.sp_cards);
        pager                   = (ViewPager) findViewById(R.id.myviewpager);
        layout_card_data        = (LinearLayout) findViewById(R.id.layout_card_data);
        layout_card_secret      = (LinearLayout) findViewById(R.id.layout_card_secret);


        rittal.myCards("C",adapter,getSupportFragmentManager(),switch_use_saved_card,sp_cards,pager,et_card_pan,et_card_expiry_date,layout_card_data,layout_card_secret);

        toolbar.setTitle(getString(R.string.generate_voucher));
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

        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pan                      = et_card_pan.getText().toString().trim().replace(" ","");
                String expiry_date              = et_card_expiry_date.getText().toString().trim().replace("/","");
                String pin                      = et_card_pin.getText().toString().trim();
                String provider_id              = et_provider_id.getText().toString().trim();
                String amount                   = et_amount.getText().toString().trim();


                if(validation(pan,expiry_date,pin,amount)){
                    clearForm();
                    servicePayment(pan,expiry_date,pin,provider_id,amount);
                }




                rittal.log(TAG+"_PAN",pan);
                rittal.log(TAG+"_EXPIRY DATA",expiry_date);
                rittal.log(TAG+"_PIN",pin);
            }
        });


    }
    public void clearForm(){
        et_card_pan.setText("");
        et_card_expiry_date.setText("");
        et_card_pin.setText("");
        et_provider_id.setText("");
        et_amount.setText("");
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

    private boolean validation(String pan , String expiry_date , String ipin , String amount){
        Boolean validate = false;

        // check if transaction from account or card , ACCC mean account transaction
        if(!expiry_date.equals("ACCC")){
            // transaction is from card
            if(pan.length() == 0 || expiry_date.length() == 0  || ipin.length() == 0 ||  amount.length() ==0){
                if(pan.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pan),"1","w"); return false;}
                if(expiry_date.length() == 0){rittal.rittalToast(getString(R.string.enter_card_expiry_date),"1","w"); return false;}
                if(ipin.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pin),"1","w"); return false;}
                if(amount.length() == 0){rittal.rittalToast(getString(R.string.enter_amount),"1","w"); return false;}
                return false;
            }else{
                if(pan.length() == 16 || pan.length() == 19){
                    if(expiry_date.length() == 4){
                        if(ipin.length() == 4){
                            return true;

                        }else{
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
        }else {
            //transaction is from account
            if( amount.length() ==0){
                if(amount.length() == 0){rittal.rittalToast(getString(R.string.enter_amount),"1","w"); return false;}
                return false;
            }else{
                return true;
            }

        }




    }



    private void servicePayment(String pan , final String exdate , String ipin,String provider, String amount) {

        final String uuid = UUID.randomUUID().toString();

        String ipin_ = "";
        String pk = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==";//"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANx4gKYSMv3CrWWsxdPfxDxFvl+Is/0kc1dvMI1yNWDXI3AgdI4127KMUOv7gmwZ6SnRsHX/KAM0IPRe0+Sa0vMCAwEAAQ==";
        //String pk = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANx4gKYSMv3CrWWsxdPfxDxFvl+Is/0kc1dvMI1yNWDXI3AgdI4127KMUOv7gmwZ6SnRsHX/KAM0IPRe0+Sa0vMCAwEAAQ==";

        ipin_ = IPINBlockGenerator.getIPINBlock(ipin,pk,uuid);

        String url = application.getBaseUrl()+"/consumer_services.aspx";
        //?service=3&cust_id=1&pan="+ Uri.encode(pan)+"&ex_date="+Uri.encode(exdate)+"
        // &ipin="+Uri.decode(ipin_)+"&uuid="+Uri.encode(uuid)+"
        // &tranAmount="+Uri.encode(amount)+"&toCard="+Uri.encode(other_card);
        rittal.log("pubKeyValue",pk);
        rittal.log("URL",url);
        final String service = "18";
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
            js.put("opId","6600000000");
            js.put("tranAmount",amount);
            js.put("publicKey",pk);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

                            ResponseDialog responseDialog = new ResponseDialog(activity,dialog,response,service,op_id,"","","","","","",null,transactionSource);
                            responseDialog.show();
                            if(response.getString("responseCode").equals("100")){
//                                rittal.rittalToast(response.getString("responseMessage"),"1","s");
//                                Intent i = new Intent(activity,GenerateIPINActivity.class);
//                                startActivity(i);
//                                finish();
                            }else{
//                                rittal.rittalToast(response.getString("responseMessage"),"1","w");
                            }
                        } catch (JSONException e) {
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


    public void clearForm(View V){
        clearForm();
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
/*

{"responseMessage":"Approved","responseMessageArabic":null,
"responseCode":0,"responseStatus":"Successful","pubKeyValue":null,
"tranDateTime":"190319021808","creationDate":null,"panCategory":null,"
balance":"6.4,6.4","payeesCount":0,"payeesList":null,"billInfo":null,
"RespPAN":null,"RespExpDate":null,"RespIPIN":null,
"regType":null,"financialInstitutionId":null,
"respUserName":null,"respUserPassword":null,
"RespVoucherNumber":"808120913091","voucherCode":"080990",
"serviceInfo":null,"originalTransaction":null,"originalTranType":null,
"originalTranUUID":null,"respUUID":null,"resp_acqTranFee":0,"resp_issuerTranFee":0,
"resp_fromAccount":null,"resp_accountCurrency":null,"resp_tranAmount":0}
 */

//{"responseMessage":"Approved","responseMessageArabic":null,"responseCode":0,"responseStatus":"Successful",
// "pubKeyValue":null,"tranDateTime":"300719105109","creationDate":null,"panCategory":null,
// "balance":"96528701.15,-174409.09","payeesCount":0,"payeesList":null,"billInfo":null,"RespPAN":"9888190130385682",
// "RespExpDate":"2202","RespIPIN":null,"regType":null,"financialInstitutionId":null,"respUserName":null,
// "respUserPassword":null,"RespVoucherNumber":null,"voucherCode":null,"serviceInfo":null,"originalTransaction":null,
// "originalTranType":null,"originalTranUUID":null,"respUUID":"395147e7-cc7a-4ddf-a490-a4791e4e77a6",
// "resp_acqTranFee":0,"resp_issuerTranFee":-1.5,"resp_fromAccount":"EPGL190193800000029",
// "resp_accountCurrency":"SDG","resp_tranAmount":50,"resp_paymentInfo":null,"toCard":null,"rittalFee":0,
// "clientFee":0,"voucherNumber":null}

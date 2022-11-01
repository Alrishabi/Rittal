package sd.rittal.app.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import sd.rittal.app.AppApplication;
import sd.rittal.app.R;
import sd.rittal.app.helper.DataStorage;
import sd.rittal.app.helper.FormSession;
import sd.rittal.app.helper.LoginSession;
import sd.rittal.app.helper.ResponseDialog;
import sd.rittal.app.helper.Rittal;




public class GenerateOTPActivit extends AppCompatActivity implements FormSession,LoginSession {
    String TAG = GenerateOTPActivit.class.getSimpleName().toUpperCase();

    Activity activity;

    Rittal rittal;
    DataStorage dataStorage;
    Dialog dialog;
    AppApplication application;
    Thread timerThread;

    Toolbar toolbar;

    Button btn_generate_otp;
    EditText et_card_pan,et_card_expiry_date,et_phone_number;

    ProgressDialog progressDialog;
    String lang = "";

    private int lastInteractionTime;
    private Boolean isScreenOff = false;
    private Boolean isInForeGrnd = false;
    Date date1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_otp);
        initContext();
        initLayout();



        lang = Resources.getSystem().getConfiguration().locale.getLanguage();
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

    public void initContext(){
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

    public void initLayout(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        et_card_pan = (EditText) findViewById(R.id.et_card_pan);
        et_card_expiry_date = (EditText) findViewById(R.id.et_card_expiry_date);
        et_phone_number = (EditText) findViewById(R.id.et_phone);
        btn_generate_otp = (Button) findViewById(R.id.btn_generate_otp);


        toolbar.setTitle(getString(R.string.generate_otp));
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

        btn_generate_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pan = et_card_pan.getText().toString().replace(" ","").toString();
                String exdate = et_card_expiry_date.getText().toString().replace("/","").trim();
                String phone = et_phone_number.getText().toString().trim();

                if(pan.length() == 0 || exdate.length() == 0 || phone.length() == 0){
                    if(pan.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pan),"1","w");}
                    if(exdate.length() == 0){rittal.rittalToast(getString(R.string.enter_card_expiry_date),"1","w");}
                    if(phone.length() == 0){rittal.rittalToast(getString(R.string.enter_phone_number),"1","w");}
                }else{
                    if(pan.length() == 16 || pan.length() == 19){
                        if(exdate.length() == 4){
                            if(phone.length() == 12){
                                request_otp(pan,exdate,phone);
                            }else {
                                rittal.rittalToast(getString(R.string.phone_number_lenght_otp),"1","w");
                            }

                        }else{
                            rittal.rittalToast(getString(R.string.card_expiry_date_length),"1","w");
                        }

                    }else{
                        rittal.rittalToast(getString(R.string.card_pan_length),"1","w");
                    }
                }



            }
        });
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

    public void goToGenerateIPIN(View v){
        Intent i = new Intent(activity,GenerateIPINActivity.class);
        startActivity(i);
    }
    private void request_otp(String pan , final String exdate , final String phone) {

        et_card_pan.setText("");
        et_card_expiry_date.setText("");
        et_phone_number.setText("");

        final String uuid = UUID.randomUUID().toString();
        String url = application.getBaseUrl()+"/consumer_services.aspx";//
        //String url = application.getBaseUrl()+"/generateIPinRequest.aspx?cust_id=1&pan="+pan+"&ex_date="+exdate+"&phone_number="+phone+"&uuid="+uuid;
        rittal.log("URL",url);
        final String step = "1";
        final String service = "1";

        JSONObject js = new JSONObject();
        try {
            js.put("customerID",rittal.getValueOfKey("user_id"));
            js.put("fromCard",pan);
            js.put("expiryDate",exdate);
            js.put("phoneNumber",phone);
            js.put("uuid", uuid);
            js.put("step", step);
            js.put("service", service);
            js.put("version","1");
        } catch (JSONException e) {
            e.printStackTrace();
            rittal.log("Object Ex",e.toString());
        }
        showprogres();
        rittal.log("OBJ",js.toString());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, js,

                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        //display response
                        rittal.log("TAG Response", response.toString());
                        rittal.log("generate otp service", service);
                        rittal.log("generate otp step", step);

                        ResponseDialog responseDialog = new ResponseDialog(activity,dialog,response,service,step,"","","","","","",null,"");
                        responseDialog.show();

                        dismsprogres();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        rittal.log("TAG Respons ErrorListener", error.toString());
                        rittal.rittalToast(getString(R.string.noInternetConnection),"1","w");
                        dismsprogres();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("cust_id","#");
                params.put("pan","pan");
                params.put("ex_date",exdate);
                params.put("phone_number",phone);
                params.put("uuid",uuid);
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

    public void clearForm(){
        et_card_pan.setText("");
        et_card_expiry_date.setText("");
        et_phone_number.setText("");


    }




    /*
    {"regType":null,"RespPAN":null,"resp_accountCurrency":null,
    "panCategory":null,"payeesList":null,"resp_acqTranFee":0,
    "respUserPassword":null,"resp_fromAccount":null,"responseMessage":null,
    "balance":null,"billInfo":null,"payeesCount":0,"respUserName":null,
    "responseMessageArabic":null,"responseCode":0,"financialInstitutionId":null,
    "originalTranType":null,"voucherCode":null,"originalTranUUID":null,"resp_tranAmount":0,
    "resp_issuerTranFee":0,"pubKeyValue":null,"serviceInfo":null,"RespIPIN":null,
    "tranDateTime":null,"creationDate":null,"respUUID":null,"responseStatus":null,
    "RespExpDate":null,"originalTransaction":null,"RespVoucherNumber":null}

    */
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

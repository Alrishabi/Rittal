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
import android.util.Log;
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

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import sd.rittal.app.AppApplication;
import sd.rittal.app.R;
import sd.rittal.app.adapters.CarouselPagerAdapter;
import sd.rittal.app.helper.DataStorage;
import sd.rittal.app.helper.IPINBlockGenerator;
import sd.rittal.app.helper.FormSession;
import sd.rittal.app.helper.LoginSession;
import sd.rittal.app.helper.ResponseDialog;
import sd.rittal.app.helper.Rittal;


public class BalanceInquiryActivity extends AppCompatActivity implements FormSession,LoginSession {

    String TAG = BalanceInquiryActivity.class.getSimpleName().toUpperCase();
    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;
    Bundle extras;
    AppApplication application;
    ProgressDialog progressDialog;
    Dialog dialog;
    static final int DATE_DIALOG_ID = 1;

    LinearLayout layout_card_data,layout_card_secret;
    EditText et_card_pan,et_card_expiry_date,et_card_pin;
    Button btn_inquiry;
    Switch switch_use_saved_card;

    Toolbar toolbar;

    public CarouselPagerAdapter adapter;
    public ViewPager pager;
    Spinner sp_cards;
    private int mYear;
    private int mMonth;
    private int mDay;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_inquiry);
        initiateContext();
        initiateLayout();
        //initCards();

        ( (AppApplication)getApplication()).registerSessionListener(this);
        ( (AppApplication)getApplication()).startUserSession();

        ( (AppApplication)getApplication()).registerSessionListener2(this);
        ( (AppApplication)getApplication()).startLoginSession();

        rittal.log("normal pkey","is"+rittal.getValueOfKey("pk"));

        get_publicKey();

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
        activity = this;
        rittal = new Rittal(activity);
        dataStorage = new DataStorage(activity);
        application = new AppApplication();
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
        btn_inquiry             = (Button) findViewById(R.id.btn_inquiry);
        sp_cards                = (Spinner) findViewById(R.id.sp_cards);
        pager                   = (ViewPager) findViewById(R.id.myviewpager);
        layout_card_data        = (LinearLayout) findViewById(R.id.layout_card_data);
        layout_card_secret      = (LinearLayout) findViewById(R.id.layout_card_secret);



        rittal.myCards("A&C",adapter,getSupportFragmentManager(),switch_use_saved_card,sp_cards,pager,et_card_pan,et_card_expiry_date,layout_card_data,layout_card_secret);

        toolbar.setTitle(getString(R.string.balance_inquery));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
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
        btn_inquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pan              = et_card_pan.getText().toString().trim().replace(" ","");
                String expiry_date      = et_card_expiry_date.getText().toString().trim().replace("/","");
                String ipin             = et_card_pin.getText().toString().trim();


                if(validation(pan,expiry_date,ipin)){
                    clearForm();
                    balance(pan,expiry_date,ipin);
                }
            }
        });


    }

    public Boolean validation(String pan, String expiry_date, String ipin){

        // check if transaction from account or card , ACCC mean account transaction
        if(!expiry_date.equals("ACCC")){
            // transaction is from card
            if(pan.length() == 0 || expiry_date.length() == 0 || ipin.length() == 0){
                if(pan.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pan),"1","w");}
                if(expiry_date.length() == 0){rittal.rittalToast(getString(R.string.enter_card_expiry_date),"1","w");}
                if(ipin.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pin),"1","w");}
                return false;

            }else{
                if(pan.length() == 16 || pan.length() == 19){
                    if(expiry_date.length() == 4){
                        if(ipin.length() == 4){
                            return true;
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
        }else {
            //transaction is from account
            return true;
        }


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

    private void balance(String pan , final String exdate , String ipin) {

//        et_card_pan.setText("");
//        et_card_expiry_date.setText("");
//        et_ipin.setText("");
//        et_confirm_ipin.setText("");
//        et_otp.setText("");
        final String uuid = UUID.randomUUID().toString();

        String ipin_ = "";

        rittal.log("normal pkey",rittal.getValueOfKey("pk"));
        rittal.log("normal uuid",uuid);
        rittal.log("normal ipin",ipin);
        rittal.log("normal pan",pan);
        rittal.log("normal ipin",exdate);
        String pk = application.getPublicKey(); //"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==";//rittal.getValueOfKey("pk");// "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANx4gKYSMv3CrWWsxdPfxDxFvl+Is/0kc1dvMI1yNWDXI3AgdI4127KMUOv7gmwZ6SnRsHX/KAM0IPRe0+Sa0vMCAwEAAQ==";

        ipin_ = IPINBlockGenerator.getIPINBlock(ipin,pk,uuid);

        String text = ipin_+"XAXA"+uuid;
//         ipin_ = IPINBlockGenerator.getIPINBlock(ipin,"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==",uuid);;
        String url = application.getBaseUrl()+"/consumer_services.aspx";
        //?service=2&cust_id=1&pan="+Uri.encode(pan)+"&ex_date="+Uri.encode(exdate)+"&
        // ipin="+Uri.encode(text)+"&uuid="+Uri.encode(uuid)+"&p_key="+pk;
        rittal.log("pubKeyValue",rittal.getValueOfKey("pubKeyValue"));
        rittal.log("URL",url);
        rittal.log("ipin",ipin_);
        rittal.log("uuid",uuid);
        final String service = "2";
        final String op_id = "";


        showprogres();
        JSONObject js = new JSONObject();
        try {
            js.put("service",service);
            js.put("customerID",rittal.getValueOfKey("user_id"));
            js.put("fromCard",pan);
            js.put("expiryDate",exdate);
            js.put("ipin",text);
            js.put("uuid",uuid);
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

    private void get_publicKey() {

        String url = application.getBaseUrl()+"/get_pKey.aspx";
        rittal.log("URL",url);

        JSONObject js = new JSONObject();
        try {
            js.put("version","1");
        } catch (JSONException e) {
            e.printStackTrace();
            rittal.log("Object Ex",e.toString());
        }
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,

                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        rittal.log("TAG Response", response.toString());

                        try {
                            if(response.getString("responseCode").equals("0")){
                                rittal.log("pk",response.getString("pubKeyValue"));
                                rittal.setValueToKey("pk",response.getString("pubKeyValue"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        rittal.log("TAG Respons ErrorListener", error.toString());
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
    public void clearForm(){
        et_card_pan.setText("");
        et_card_expiry_date.setText("");
        et_card_pin.setText("");
    }
    public void clearForm(View V){
        clearForm();
    }
    @Override
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

    private DatePickerDialog createDialogWithoutDateField() {
        DatePickerDialog dpd = new DatePickerDialog(this, null, 2014, 1, 24);
        try {
            Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (Field datePickerField : datePickerFields) {
                        Log.i("test", datePickerField.getName());
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
        }
        return dpd;
    }

    DatePickerDialog.OnDateSetListener mDateSetListner = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDate();
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                /*
                 * return new DatePickerDialog(this, mDateSetListner, mYear, mMonth,
                 * mDay);
                 */
                DatePickerDialog datePickerDialog = this.customDatePicker();
                return datePickerDialog;
        }
        return null;
    }

    protected void updateDate() {
        int localMonth = (mMonth + 1);
        String monthString = localMonth < 10 ? "0" + localMonth : Integer
                .toString(localMonth);
        String localYear = Integer.toString(mYear).substring(2);
        et_card_expiry_date.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(monthString).append("/").append(localYear).append(" "));
        showDialog(DATE_DIALOG_ID);
    }

    private DatePickerDialog customDatePicker() {
        DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListner,
                mYear, mMonth, mDay);
        try {

            Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField
                            .get(dpd);
                    Field datePickerFields[] = datePickerDialogField.getType()
                            .getDeclaredFields();
                    for (Field datePickerField : datePickerFields) {
                        if ("mDayPicker".equals(datePickerField.getName())
                                || "mDaySpinner".equals(datePickerField
                                .getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = new Object();
                            dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }

            }
        } catch (Exception ex) {
        }
        return dpd;
    }

}
/*
 https://mob.rittal-sd.com/consumer_services.aspx?service=2&cust_id=1&pan=6391750800438891&ex_date=2003&ipin=UWag0cL5YI7cYZakgIghSTWsecFSHCsnvEgHRUs52Pt2hNmOklb1lZx/I17TXegWTJadIDCbTy4F6sayh6HZYA==&uuid=c75bd43e-b170-4ef6-aec3-cae38f59fc63&p_key=MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANx4gKYSMv3CrWWsxdPfxDxFvl+Is/0kc1dvMI1yNWDXI3AgdI4127KMUOv7gmwZ6SnRsHX/KAM0IPRe0+Sa0vMCAwEAAQ==
03-03 12:41:20.382 16128-16146/sd.rittal.consummer I/System.out: ##### jtk OpenSSLUnionpayCheck #######
03-03 12:41:22.632 16128-16128/sd.rittal.consummer D/TAG Response: {"regType":null,"RespPAN":"6391750800438891","resp_accountCurrency":null,"panCategory":null,"payeesList":null,"resp_acqTranFee":0,"respUserPassword":null,"resp_fromAccount":null,"responseMessage":"Approved","balance":"3.41","billInfo":null,"payeesCount":0,"respUserName":null,"responseMessageArabic":null,"responseCode":0,"financialInstitutionId":null,"originalTranType":null,"voucherCode":null,"originalTranUUID":null,"resp_tranAmount":0,"resp_issuerTranFee":0,"pubKeyValue":null,"serviceInfo":null,"RespIPIN":null,"tranDateTime":"030319123222","creationDate":null,"respUUID":"c75bd43e-b170-4ef6-aec3-cae38f59fc63","responseStatus":"Successful","RespExpDate":"2003","originalTransaction":null,"RespVoucherNumber":null}
03-03 12:41:22.632 16128-16128/sd.rittal.consummer I/TAG Response: {"regType":null,"RespPAN":"6391750800438891","resp_accountCurrency":null,"panCategory":null,"payeesList":null,"resp_acqTranFee":0,"respUserPassword":null,"resp_fromAccount":null,"responseMessage":"Approved","balance":"3.41","billInfo":null,"payeesCount":0,"respUserName":null,"responseMessageArabic":null,"responseCode":0,"financialInstitutionId":null,"originalTranType":null,"voucherCode":null,"originalTranUUID":null,"resp_tranAmount":0,"resp_issuerTranFee":0,"pubKeyValue":null,"serviceInfo":null,"RespIPIN":null,"tranDateTime":"030319123222","creationDate":null,"respUUID":"c75bd43e-b170-4ef6-aec3-cae38f59fc63","responseStatus":"Successful","RespExpDate":"2003","originalTransaction":null,"RespVoucherNumber":null}

*/


//Response: {"responseMessage":"Approved",
// "responseMessageArabic":null,
// "responseCode":0,
// "responseStatus":"Successful",
// "pubKeyValue":null,
// "tranDateTime":"090319143700",
// "creationDate":null,
// "panCategory":null,
// "balance":"1.41",
// "payeesCount":0,
// "payeesList":null,
// "billInfo":null,
// "RespPAN":"6391750800438891",
// "RespExpDate":"2003",
// "RespIPIN":null,
// "regType":null,
// "financialInstitutionId":null,
// "respUserName":null,
// "respUserPassword":null
// ,"RespVoucherNumber":null,
// "voucherCode":null,
// "serviceInfo":null,
// "originalTransaction":null,
// "originalTranType":null,
// "originalTranUUID":null,
// "respUUID":"dbeed517-3429-4d30-80e4-790b6a6fcebc",
// "resp_acqTranFee":0,
// "resp_issuerTranFee":0,
// "resp_fromAccount":null,
// "resp_accountCurrency":null,
// "resp_tranAmount":0}


/*
https://mob.rittal-sd.com/consumer_services.aspx?service=2&cust_id=1&pan=5818588880475558&ex_date=2107&ipin=WziKS9xIQJgV+wAvOWETpyNS6i3WYjhgnp4OigHsC9U866rNONlUpIZowGMHQ8t3pV+rW1bUfo2ne5s0nX9zYw==#A#A14144da8-8dff-4a93-a4e7-d97bc8ef29fd&uuid=14144da8-8dff-4a93-a4e7-d97bc8ef29fd&p_key=MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANx4gKYSMv3CrWWsxdPfxDxFvl+Is/0kc1dvMI1yNWDXI3AgdI4127KMUOv7gmwZ6SnRsHX/KAM0IPRe0+Sa0vMCAwEAAQ==
*/
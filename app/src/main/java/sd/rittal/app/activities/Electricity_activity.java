package sd.rittal.app.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
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
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
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
import sd.rittal.app.objects.Card;

public class Electricity_activity extends AppCompatActivity implements FormSession,LoginSession {

    String TAG = BalanceInquiryActivity.class.getSimpleName().toUpperCase();
    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;
    Bundle extras;
    AppApplication application;

    Toolbar toolbar;

    LinearLayout layout_card_data,layout_card_secret;
    EditText et_card_pan,et_card_expiry_date,et_card_pin,et_counter_num,et_amount;
    Button btn_purchase;
    Switch switch_use_saved_card;
    ProgressDialog progressDialog;
    ImageView iv_add;
    Dialog dialog;


    public CarouselPagerAdapter adapter;
    public ViewPager pager;
    Spinner sp_cards;

    public ArrayList<Card> card_list = new ArrayList<Card>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electricity);
        initiateContext();
        initiateLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        rittal.log("APP_LANGUAGE_1",rittal.getValueOfKey("app_language"));

        if(rittal.getValueOfKey("app_language").equals("en")){
            rittal.setValueToKey("app_language","en");
            Locale locale = new Locale("en");
            rittal.log("APP_LANGUAGE_IF",rittal.getValueOfKey("app_language"));
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            // Toast.makeText(this, getResources().getString(R.string.lbl_langSelectEnglis), Toast.LENGTH_SHORT).show();

        }else{
            rittal.setValueToKey("app_language","ar");
            Locale locale = new Locale("ar");
            rittal.log("APP_LANGUAGE_ELSE",rittal.getValueOfKey("app_language"));
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            //  Toast.makeText(this, getResources().getString(R.string.lbl_langSelecURdu), Toast.LENGTH_SHORT).show();

        }

        clearForm();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void clearForm(){
        et_card_pan.setText("");
        et_card_expiry_date.setText("");
        et_card_pin.setText("");
        et_counter_num.setText("");
        et_amount.setText("");
    }
    ///////////////////////////////////////////////////////////////////////////////
    private void initiateLayout(){
        switch_use_saved_card   = (Switch)findViewById(R.id.switch_use_saved_card);
        et_card_pan             = (EditText) findViewById(R.id.et_card_pan);
        et_card_expiry_date     = (EditText) findViewById(R.id.et_card_expiry_date);
        et_card_pin             = (EditText) findViewById(R.id.et_card_pin);
        et_counter_num          = (EditText) findViewById(R.id.et_counter_num);
        et_amount               = (EditText) findViewById(R.id.et_amount);
        btn_purchase            = (Button) findViewById(R.id.btn_purchase);
        sp_cards                = (Spinner) findViewById(R.id.sp_cards);
        pager                   = (ViewPager) findViewById(R.id.myviewpager);
        layout_card_data        = (LinearLayout) findViewById(R.id.layout_card_data);
        layout_card_secret      = (LinearLayout) findViewById(R.id.layout_card_secret);

        iv_add      = (ImageView) findViewById(R.id.iv_add);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.electrivity));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        rittal.myCards("A&C",adapter,getSupportFragmentManager(),switch_use_saved_card,sp_cards,pager,et_card_pan,et_card_expiry_date,layout_card_data,layout_card_secret);

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

        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rittal.rittalToast(getString(R.string.soon),"1","i");            }
        });
        btn_purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String pan      = et_card_pan.getText().toString().trim().replace(" ","");
                final String expiry_date   = et_card_expiry_date.getText().toString().trim().replace("/","");
                final String pin   = et_card_pin.getText().toString().trim();
                final String meter_no   = et_counter_num.getText().toString().trim();
                final String amount   = et_amount.getText().toString().trim();
                if(validation(pan,expiry_date,pin,meter_no,amount)){
                    electricity(pan,expiry_date,pin,meter_no,amount);
                }
                rittal.log(TAG+"_PAN",pan);
                rittal.log(TAG+"_EXPIRY DATA",expiry_date);
                rittal.log(TAG+"_PIN",pin);
                rittal.log(TAG+"_COUNTER_NUM",meter_no);
                rittal.log(TAG+"_AMOUNT",amount);
            }
        });
    }
    private void initiateContext(){
        activity = this;
        rittal = new Rittal(activity);
        dataStorage = new DataStorage(activity);
        application = new AppApplication();
        dialog          = new Dialog(activity, R.style.DialogTheme);

        ( (AppApplication)getApplication()).registerSessionListener(this);
        ( (AppApplication)getApplication()).startUserSession();

        ( (AppApplication)getApplication()).registerSessionListener2(this);
        ( (AppApplication)getApplication()).startLoginSession();

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
    }
    public void pressBtnPurchase(View view) {
       // btn_purchase.setOnClickListener();
        Toast.makeText(this,"NOT YET",Toast.LENGTH_LONG).show();
    }

    public ArrayList<Card> getCardList(){
        card_list.clear();
        Cursor cards_date = dataStorage.get_cards();
        cards_date.moveToFirst();
        if (cards_date.moveToFirst()) {
            do {

                int id              = Integer.valueOf(cards_date.getString(cards_date.getColumnIndex("id")));
                String name         = cards_date.getString(cards_date.getColumnIndex("name"));
                String pan          = cards_date.getString(cards_date.getColumnIndex("pan"));
                String expiry_date  = cards_date.getString(cards_date.getColumnIndex("expiry_date"));
                String color        = cards_date.getString(cards_date.getColumnIndex("color"));

                Log.d("pan",pan);

                Card card = new Card(id,name,pan,expiry_date,color);
                card_list.add(card);

            }while (cards_date.moveToNext());
        }

        //Collections.reverse(card_list);
        Log.d("TAg card_list size is ",card_list.size()+"");
        return  card_list;
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
    private boolean validation(String pan , String expiry_date , String pin , String counter_num, String amount){

        Boolean validate = false;
        // check if transaction from account or card , ACCC mean account transaction
        if(!expiry_date.equals("ACCC")){
            // transaction is from card
            if(pan.length() == 0 || expiry_date.length() == 0  || pin.length() == 0 || counter_num.length() == 0|| amount.length() ==0){
                if(pan.length() == 0){rittal.rittalToast(activity.getString(R.string.enter_card_pan),"1","w"); return false;}
                if(expiry_date.length() == 0){rittal.rittalToast(activity.getString(R.string.enter_card_expiry_date),"1","w"); return false;}
                if(pin.length() == 0){rittal.rittalToast(activity.getString(R.string.enter_card_pin),"1","w");return false;}
                if(counter_num.length() == 0){rittal.rittalToast(activity.getString(R.string.enter_meter_number),"1","w");return false;}
                if(amount.length() == 0){rittal.rittalToast(activity.getString(R.string.enter_amount),"1","w");return false;}
                return false;
            }else{
                if(pan.length() == 16 || pan.length() == 19){
                    if(expiry_date.length() == 4){
                        if(expiry_date.length() == 4){

                                validate = true;
                                return true;


                        }else{
                            rittal.rittalToast(activity.getString(R.string.card_ipin_lengh),"1","w");
                            validate = false;
                            return false;
                        }
                    }else{
                        rittal.rittalToast(activity.getString(R.string.card_expiry_date_length),"1","w");
                        validate = false;
                        return false;
                    }
                }else{
                    rittal.rittalToast(activity.getString(R.string.card_pan_length),"1","w");
                    validate = false;
                    return false;
                }
            }
        }else {
            //transaction is from account
            if(counter_num.length() == 0|| amount.length() ==0){
                if(counter_num.length() == 0){rittal.rittalToast(activity.getString(R.string.enter_meter_number),"1","w");return false;}
                if(amount.length() == 0){rittal.rittalToast(activity.getString(R.string.enter_amount),"1","w");return false;}
                return false;
            }else{
                if(counter_num.length()==11){

                    validate = true;
                    return true;

                }else{
                    rittal.rittalToast(activity.getString(R.string.counter_number),"1","w");
                    validate = false;
                    return false;
                }
            }

        }

    }

    private void electricity(final String pan , final String expiry_date , final String ipin , final String counter_num, final String amount) {

        final String uuid = UUID.randomUUID().toString();

        String ipin_ = "";


        String pk =application.getPublicKey();// "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==";//rittal.getValueOfKey("pk");// "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANx4gKYSMv3CrWWsxdPfxDxFvl+Is/0kc1dvMI1yNWDXI3AgdI4127KMUOv7gmwZ6SnRsHX/KAM0IPRe0+Sa0vMCAwEAAQ==";

        ipin_ = IPINBlockGenerator.getIPINBlock(ipin,pk,uuid);

//         ipin_ = IPINBlockGenerator.getIPINBlock(ipin,"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==",uuid);;
        String url = application.getBaseUrl()+"/consumer_services.aspx";
        //?service=2&cust_id=1&pan="+Uri.encode(pan)+"&ex_date="+Uri.encode(exdate)+"&
        // ipin="+Uri.encode(text)+"&uuid="+Uri.encode(uuid)+"&p_key="+pk;

        rittal.log("pubKeyValue",rittal.getValueOfKey("pubKeyValue"));
        rittal.log("URL",url);
        rittal.log("ipin",ipin_);
        final String service = "11";
        final String op_id = "G";


        showprogres();
        JSONObject js = new JSONObject();
        try {
            js.put("service",service);
            js.put("customerID",rittal.getValueOfKey("user_id"));
            js.put("fromCard",pan);
            js.put("expiryDate",expiry_date);
            js.put("ipin",ipin_);
            js.put("uuid",uuid);
            js.put("meterNumber",counter_num);
            js.put("tranAmount",amount);
            js.put("flag",op_id);
        }
        catch (JSONException e) {
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
                        if(expiry_date.toUpperCase().equals("ACCC")){
                            transactionSource = "ACCOUNT";
                        }else{
                            transactionSource = "CARD";
                        }
                        try {
                            dataStorage.add_transaction(response.toString(),service,rittal.getValueOfKey("user_id"),op_id,transactionSource);
                        }catch (Exception e){

                        }

                        try {

                            ResponseDialog responseDialog = new ResponseDialog(activity,dialog,response,service,op_id,pan,expiry_date,ipin,counter_num,"",amount,progressDialog,transactionSource);
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
                            rittal.rittalToast(activity.getString(R.string.error_in_data_processing),"1","w");
                        }
                        dismsprogres();

                    }


                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        rittal.log("TAG Respons ErrorListener", error.toString());
                        rittal.rittalToast(activity.getString(R.string.error_on_network_connection),"1","w");
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
    private void electricity_payment(final String pan , final String expiry_date , final String ipin , final String counter_num, final String amount) {

        final String uuid = UUID.randomUUID().toString();

        String ipin_ = "";
        String pk = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==";//rittal.getValueOfKey("pk");// "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANx4gKYSMv3CrWWsxdPfxDxFvl+Is/0kc1dvMI1yNWDXI3AgdI4127KMUOv7gmwZ6SnRsHX/KAM0IPRe0+Sa0vMCAwEAAQ==";

        ipin_ = IPINBlockGenerator.getIPINBlock(ipin,pk,uuid);

        String url = application.getBaseUrl()+"/consumer_services.aspx";//
        // ?service=9&cust_id=1&pan="+Uri.encode(pan)+"&ex_date="+Uri.encode(exdate)+"
        // &ipin="+Uri.decode(ipin_)+"&uuid="+Uri.encode(uuid)+"&serviceId="+Uri.encode("2")+"&paymentInfo="+Uri.encode(phone)+"&invoiceNumber="+Uri.encode(invoice_number);
        rittal.log("pubKeyValue",pk);
        rittal.log("URL",url);
        //rittal.log("ipin",text);
        //final String paymentInfo = "SETNUMBER="+seater_number +"/STUDCOURSEID="+ course_id +"/STUDFORMKIND="+ application_type_id;
        final String service = "5";
        final String opId = "7";

        showprogres();
        JSONObject js = new JSONObject();

        try {
            js.put("service",service);
            js.put("customerID","1");
            js.put("fromCard",pan);
            js.put("expiryDate",expiry_date);
            js.put("ipin",ipin_);
            js.put("uuid",uuid);
            js.put("opId",opId);
            js.put("paymentInfo",counter_num);
            js.put("tranAmount",amount);
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
                        if(expiry_date.toUpperCase().equals("ACCC")){
                            transactionSource = "ACCOUNT";
                        }else{
                            transactionSource = "CARD";
                        }
                        try {

                            ResponseDialog responseDialog = new ResponseDialog(activity,dialog,response,service,opId,pan,expiry_date,ipin,"",counter_num,amount,progressDialog,transactionSource);
                            responseDialog.show();
                            if(response.getString("responseCode").equals("0")){
//                                rittal.rittalToast(response.getString("responseMessage"),"1","s");
//                                Intent i = new Intent(activity,GenerateIPINActivity.class);
//                                startActivity(i);
//                                finish();
                            }else{
//                                rittal.rittalToast(response.getString("responseMessage"),"1","w");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dismsprogres();

                    }


                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        rittal.log("TAG Respons ErrorListener", error.toString());
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
{"RRN":null,"MeterNO":null,"Amount":null,"PAmount":null,"STS":null,"Rslt_Code":"-1",
"Rslt_message":"System.ArgumentNullException: String reference not set to an instance of a String.
\r\nParameter name: s\r\n   at System.Text.Encoding.GetBytes(String s)\r\n   at MCSC.Crypto.Encrypt(String inputText)
 in C:\\Users\\Administrator\\Desktop\\projects 2017\\MCSC v3.0- client enc\\MCSC -Consumer\\MCSC\\Crypto.cs:line 181
 \r\n   at MCSC.EncDecElec.EncGetInfoReq(GetInfoReq GetInfoReq) in C:\\Users\\Administrator\\Desktop\\projects
 2017\\MCSC v3.0- client enc\\MCSC -Consumer\\MCSC\\Crypto.cs:line 30\r\n   at
 MCSC.accountServices.Elec(String custId, String PAN, String iPIN, String expDate, String tranAmount, String UUID,
  String meterNo, String flag) in C:\\Users\\Administrator\\Desktop\\projects 2017\\MCSC v3.0- client enc\\
  MCSC -Consumer\\MCSC\\accountServices.cs:line 244\r\n   at
   MCSC.Service1.elec(String custId, String PAN, String expDate, String enc_ipin, String tranAmount, String meterNo,
    String flag, String UUID) in C:\\Users\\Administrator\\Desktop\\projects 2017\\MCSC v3.0- client enc\\MCSC
    -Consumer\\MCSC\\Service1.asmx.cs:line 5741","Units":null,"Token":null,"ServiceCharge":null,"CustomerName":null,
    "Tarrif":null,"units":null,"stateId":null,"Curent_Balance":0,"Invoice":null,"calcQty":null,"buyTimes":null,
    "VAt":null,"verifyCode":null,"stampTax":null,"code":null,"netAMT":0,"commAMT":0,"refCode":null,
    "regCode":null,"Rslt_State":null,"vendQty":null,"vendAMT":0,"supplyAMT":0,"transTime":null,"transID":null,
    "feesAMT":null,"checkCode":null,"Chanel":null,"FisrtPowerID":null,"FisrtPoweramt":null,"FisrtPowerKwt":null,
    "FisrtPowerPrice":null,"SecPowerID":null,"SecPoweramt":null,"SecPowerKwt":null,"SecPowerPrice":null,
    "DetailedArreas":null,"ArrearsItems":null,"MIN_AMT":null,"keyId_1":null,"keyName_1":null,"KeyToken_1":null,
    "keyId_2":null,"keyName_2":null,"keyToken_2":null,"KeyId_2":null}

 */

/*
{"service":"11","customerID":"6f69601a-00ea-4d74-9677-4fc47eccf359","fromCard":"6391750800438891","expiryDate":"2003","ipin":"aEx0rcfukNUSYJgB2dnoO7XbBMdV81zVhqgiL9AGD7wXKhI+d1KpnbZIc6lMEtwW1kXcyTZ33bDaEcg5pvdaRQ==","uuid":"e1abaa33-901a-4d01-b95a-3dc418dca2f6","meterNumber":"37158876914","transAmount":"3","flag":"G"}


 {"RRN":null,"MeterNO":null,"Amount":null,"PAmount":null,"STS":null,"Rslt_Code":"-1",
 "Rslt_message":"System.ArgumentNullException: String reference not set to an instance of a String.
 \r\nParameter name: s\r\n   at System.Text.Encoding.GetBytes(String s)\r\n   at MCSC.Crypto.Encrypt(String inputText)
  in C:\\Users\\Administrator\\Desktop\\projects 2017\\MCSC v3.0- client enc\\MCSC -Consumer\\MCSC\\Crypto.cs:line 181\r\n
    at MCSC.EncDecElec.EncGetInfoReq(GetInfoReq GetInfoReq) in C:\\Users\\Administrator\\Desktop\\projects 2017\\MCSC v3.0-
     client enc\\MCSC -Consumer\\MCSC\\Crypto.cs:line 30\r\n
       at MCSC.accountServices.Elec(String custId, String PAN, String iPIN, String expDate, String tranAmount, String UUID, String meterNo, String flag) in C:\\Users\\Administrator\\Desktop\\projects 2017\\MCSC v3.0- client enc\\MCSC -Consumer\\MCSC\\accountServices.cs:line 244\r\n   at MCSC.Service1.elec(String custId, String PAN, String expDate, String enc_ipin, String tranAmount, String meterNo, String flag, String UUID) in C:\\Users\\Administrator\\Desktop\\projects 2017\\MCSC v3.0- client enc\\MCSC -Consumer\\MCSC\\Service1.asmx.cs:line 5741","Units":null,"Token":null,"ServiceCharge":null,"CustomerName":null,"Tarrif":null,"units":null,"stateId":null,"Curent_Balance":0,"Invoice":null,"calcQty":null,"buyTimes":null,"VAt":null,"verifyCode":null,"stampTax":null,"code":null,"netAMT":0,"commAMT":0,"refCode":null,"regCode":null,"Rslt_State":null,"vendQty":null,"vendAMT":0,"supplyAMT":0,"transTime":null,"transID":null,"feesAMT":null,"checkCode":null,"Chanel":null,"FisrtPowerID":null,"FisrtPoweramt":null,"FisrtPowerKwt":null,"FisrtPowerPrice":null,"SecPowerID":null,"SecPoweramt":null,"SecPowerKwt":null,"SecPowerPrice":null,"DetailedArreas":null,"ArrearsItems":null,"MIN_AMT":null,"keyId_1":null,"keyName_1":null,"KeyToken_1":null,"keyId_2":null,"keyName_2":null,"keyToken_2":null,"KeyId_2":null}


Inquiry
{"RRN":"c82d5d90-3ade-4ac3-aaa1-8c04ebd11e80","MeterNO":"37158876914","Amount":"2000",
"PAmount":"1998","STS":null,"responseMessage":"Confirmed","responseCode":1,"responseMessageArabic":null,
"Units":"1557.8","Token":null,"ServiceCharge":"0.00","CustomerName":"OMAR SHAMS ALDIN ALABEID AHMED",
"Tarrif":null,"units":null,"stateId":null,"Curent_Balance":0,"Invoice":null,"calcQty":null,
"buyTimes":null,"VAt":null,"verifyCode":null,"stampTax":null,"code":null,"netAMT":0,"commAMT":0,
"refCode":null,"regCode":null,"Rslt_State":null,"vendQty":null,"vendAMT":0,"supplyAMT":0,"transTime":null,
"transID":null,"feesAMT":"0","checkCode":null,"Chanel":null,"FisrtPowerID":"1","FisrtPoweramt":"560.405",
"FisrtPowerKwt":"659.3","FisrtPowerPrice":"0.8500","SecPowerID":"2","SecPoweramt":"1437.595","SecPowerKwt":"898.5",
"SecPowerPrice":"1.6000","DetailedArreas":null,"ArrearsItems":"","MIN_AMT":null,"keyId_1":null,"keyName_1":null,
"KeyToken_1":null,"keyId_2":null,"keyName_2":null,"keyToken_2":null,"KeyId_2":null,"elec_fees":null,"ct_fees":null,
"PAN":null,"tranDateTime":"221019115100"}

 */
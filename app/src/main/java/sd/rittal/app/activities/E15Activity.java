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
import android.widget.RelativeLayout;
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
import java.util.List;
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


public class E15Activity extends AppCompatActivity implements FormSession,LoginSession {
    String TAG = E15Activity.class.getSimpleName().toUpperCase();
    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;
    Bundle extras;
    AppApplication application;
    ProgressDialog progressDialog;
    Dialog dialog;

    List<String> provider;

    Toolbar toolbar;

    LinearLayout layout_card_data,layout_card_secret;
    EditText et_card_pan,et_card_expiry_date,et_card_pin,et_phone_number,et_invoice,et_amount;
    RelativeLayout ly_amount;
    Button btn_request;
    Switch switch_use_saved_card,switch_process;

    public CarouselPagerAdapter adapter;
    public ViewPager pager;
    Spinner sp_cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e15);
        initiateContext();
        initiateLayout();

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

    private void initiateLayout(){
        toolbar                 = (Toolbar) findViewById(R.id.toolbar);
        switch_use_saved_card   = (Switch)findViewById(R.id.switch_use_saved_card);
        switch_process          = (Switch)findViewById(R.id.switch_process);
        et_card_pan             = (EditText) findViewById(R.id.et_card_pan);
        et_card_expiry_date     = (EditText) findViewById(R.id.et_card_expiry_date);
        et_card_pin             = (EditText) findViewById(R.id.et_card_pin);
        et_amount               = (EditText) findViewById(R.id.et_amount);
        et_invoice              = (EditText) findViewById(R.id.invoice_num);
        et_phone_number         = (EditText) findViewById(R.id.et_phone_number);
        sp_cards                = (Spinner) findViewById(R.id.sp_cards);
        pager                   = (ViewPager) findViewById(R.id.myviewpager);
        btn_request             = (Button) findViewById(R.id.btn_request);
        ly_amount               = (RelativeLayout) findViewById(R.id.ly_amount);
        layout_card_data        = (LinearLayout) findViewById(R.id.layout_card_data);
        layout_card_secret      = (LinearLayout) findViewById(R.id.layout_card_secret);

        toolbar.setTitle(getString(R.string.title_e15));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        switch_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switch_process.isChecked()){
                    ly_amount.setVisibility(View.GONE);
                    btn_request.setText(activity.getString(R.string.inquiry));
                }else{
                    ly_amount.setVisibility(View.VISIBLE);
                    btn_request.setText(activity.getString(R.string.pay));
                }
            }
        });

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

        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pan                      = et_card_pan.getText().toString().trim().replace(" ","");
                String expiry_date              = et_card_expiry_date.getText().toString().trim().replace("/","");
                String pin                      = et_card_pin.getText().toString().trim();
                String phone_number             = et_phone_number.getText().toString().trim();
                String invoice_num              = et_invoice.getText().toString().trim();
                String amount                   = et_amount.getText().toString().trim();


                if(switch_process.isChecked()){
                    if(validation_inquiry(pan,expiry_date,pin,invoice_num,phone_number)){
                        clearForm();

                        bill_inquiry(pan,expiry_date,pin,invoice_num,phone_number);
                        rittal.log(TAG+"_PAN",pan);
                        rittal.log(TAG+"_EXPIRY DATA",expiry_date);
                        rittal.log(TAG+"_PIN",pin);
                        rittal.log(TAG+"_INVOICE",invoice_num);
                        rittal.log(TAG+"_PHONE",phone_number);
                    }

                }else{
                    if(validation_bill(pan,expiry_date,pin,invoice_num,phone_number,amount)){
                        clearForm();
                        bill_payment(pan,expiry_date,pin,invoice_num,phone_number,amount);
                        rittal.log(TAG+"_PAN",pan);
                        rittal.log(TAG+"_EXPIRY DATA",expiry_date);
                        rittal.log(TAG+"_PIN",pin);
                        rittal.log(TAG+"_INVOICE",invoice_num);
                        rittal.log(TAG+"_PHONE",phone_number);
                        rittal.log(TAG+"_AMOUNT",amount);
                    }

                }


            }
        });


    }

    private boolean validation_bill(String pan , String expiry_date , String pin , String invoice_num , String phone_number,String amount){
        Boolean validate = false;

        if(!expiry_date.toUpperCase().equals("ACCC")){
            if(pan.length() == 0 || expiry_date.length() == 0  || pin.length() == 0 || invoice_num.length() == 0|| phone_number.length() ==0 || amount.length() == 0){
                if(pan.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pan),"1","w"); return false;}
                if(expiry_date.length() == 0){rittal.rittalToast(getString(R.string.enter_card_expiry_date),"1","w"); return false;}
                if(pin.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pin),"1","w"); return false;}
                if(invoice_num.length() == 0){rittal.rittalToast(getString(R.string.enter_invoice_number),"1","w"); return false;}
                if(phone_number.length() == 0){rittal.rittalToast(getString(R.string.enter_phone_number),"1","w"); return false;}
                if(amount.length() == 0){rittal.rittalToast(getString(R.string.enter_amount),"1","w"); return false;}
                return false;
            }else{
                if(pan.length() == 16 || pan.length() == 19){
                    if(expiry_date.length() == 4){
                        if(pin.length() == 4){
                            if(phone_number.length() == 12){
                                return true;
                            }else{
                                rittal.rittalToast(getString(R.string.e15_phone_number_lenght),"1","w");
                                return false;
                            }
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
        }else{
            if(invoice_num.length() == 0|| phone_number.length() ==0 || amount.length() == 0){
                if(invoice_num.length() == 0){rittal.rittalToast(getString(R.string.enter_invoice_number),"1","w"); return false;}
                if(phone_number.length() == 0){rittal.rittalToast(getString(R.string.enter_phone_number),"1","w"); return false;}
                if(amount.length() == 0){rittal.rittalToast(getString(R.string.enter_amount),"1","w"); return false;}
                return false;
            }else{
                if(invoice_num.length() == 18){
                    if(phone_number.length() == 12){
                        return true;
                    }else{
                        rittal.rittalToast(getString(R.string.e15_phone_number_lenght),"1","w");
                        return false;
                    }
                }else{
                    rittal.rittalToast(getString(R.string.invoice_length),"1","w");
                    return false;
                }
            }

        }


    }
    private boolean validation_inquiry(String pan , String expiry_date , String pin , String invoice_num , String phone_number){
        Boolean validate = false;
        // check if transaction from account or card , ACCC mean account transaction
        if(!expiry_date.equals("ACCC")) {
            // transaction is from card
            if(pan.length() == 0 || expiry_date.length() == 0  || pin.length() == 0 || invoice_num.length() == 0|| phone_number.length() ==0){
                if(pan.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pan),"1","w"); return false;}
                if(expiry_date.length() == 0){rittal.rittalToast(getString(R.string.enter_card_expiry_date),"1","w"); return false;}
                if(pin.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pin),"1","w");}
                if(invoice_num.length() == 0){rittal.rittalToast(getString(R.string.enter_invoice_number),"1","w"); return false;}
                if(phone_number.length() == 0){rittal.rittalToast(getString(R.string.enter_phone_number),"1","w"); return false;}
                return false;
            }else{
                if(pan.length() == 16 || pan.length() == 19){
                    if(expiry_date.length() == 4){
                        if(pin.length() == 4){
                            if(phone_number.length() == 12){
                                return true;
                            }else{
                                rittal.rittalToast(getString(R.string.e15_phone_number_lenght),"1","w");
                                return false;
                            }
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
            if( invoice_num.length() == 0|| phone_number.length() ==0){
                if(invoice_num.length() == 0){rittal.rittalToast(getString(R.string.enter_invoice_number),"1","w"); return false;}
                if(phone_number.length() == 0){rittal.rittalToast(getString(R.string.enter_phone_number),"1","w"); return false;}
                return false;
            }else{
                if(invoice_num.length() == 18){
                    if(phone_number.length() == 12){
                        return true;
                    }else{
                        rittal.rittalToast(getString(R.string.e15_phone_number_lenght),"1","w");
                        return false;
                    }
                }else{
                    rittal.rittalToast(getString(R.string.invoice_length),"1","w");
                   return false;
                }
            }
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

    private void bill_inquiry(String pan , final String exdate , String ipin, final String invoice_number, String phone) {

        final String uuid = UUID.randomUUID().toString();

        String ipin_ = "";
        String pk = application.getPublicKey();//"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==";//rittal.getValueOfKey("pk");// "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANx4gKYSMv3CrWWsxdPfxDxFvl+Is/0kc1dvMI1yNWDXI3AgdI4127KMUOv7gmwZ6SnRsHX/KAM0IPRe0+Sa0vMCAwEAAQ==";

        ipin_ = IPINBlockGenerator.getIPINBlock(ipin,pk,uuid);

        String url = application.getBaseUrl()+"/consumer_services.aspx";//
        // ?service=9&cust_id=1&pan="+Uri.encode(pan)+"&ex_date="+Uri.encode(exdate)+"
        // &ipin="+Uri.decode(ipin_)+"&uuid="+Uri.encode(uuid)+"&serviceId="+Uri.encode("2")+"&paymentInfo="+Uri.encode(phone)+"&invoiceNumber="+Uri.encode(invoice_number);
        rittal.log("pubKeyValue",pk);
        rittal.log("URL",url);
        //rittal.log("ipin",text);
        final String serviceId = "2";
        final String service = "9";
        final String op_id = serviceId;


        showprogres();
        JSONObject js = new JSONObject();

        try {
            js.put("service",service);
            js.put("customerID",rittal.getValueOfKey("user_id"));
            js.put("fromCard",pan);
            js.put("expiryDate",exdate);
            js.put("ipin",ipin_);
            js.put("uuid",uuid);
            js.put("serviceId",serviceId);
            js.put("paymentInfo",phone);
            js.put("invoiceNumber",invoice_number);
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

                            ResponseDialog responseDialog = new ResponseDialog(activity,dialog,response,"9",serviceId,"","","","","","",null,transactionSource);
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
    private void bill_payment(String pan , final String exdate , String ipin, final String invoice_number, String phone, String amount) {

        final String uuid = UUID.randomUUID().toString();

        String ipin_ = "";
        String pk = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==";//rittal.getValueOfKey("pk");// "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANx4gKYSMv3CrWWsxdPfxDxFvl+Is/0kc1dvMI1yNWDXI3AgdI4127KMUOv7gmwZ6SnRsHX/KAM0IPRe0+Sa0vMCAwEAAQ==";

        ipin_ = IPINBlockGenerator.getIPINBlock(ipin,pk,uuid);



        String url = application.getBaseUrl()+"/consumer_services.aspx";//
        // ?service=9&cust_id=1&pan="+Uri.encode(pan)+"&ex_date="+Uri.encode(exdate)+"&
        // ipin="+Uri.decode(ipin_)+"&uuid="+Uri.encode(uuid)+"&serviceId="+Uri.encode("6")+"
        // &paymentInfo="+Uri.encode(phone)+"&invoiceNumber="+Uri.encode(invoice_number)+"&
        // tranAmount="+Uri.encode(amount);
        rittal.log("pubKeyValue",pk);
        rittal.log("URL",url);
        //rittal.log("ipin",text);
        final String serviceId = "6";
        final String service = "10";
        final String op_id = serviceId;


        showprogres();
        JSONObject js = new JSONObject();
        try {
            js.put("service",service);
            js.put("customerID",rittal.getValueOfKey("user_id"));
            js.put("fromCard",pan);
            js.put("expiryDate",exdate);
            js.put("ipin",ipin_);
            js.put("uuid",uuid);
            js.put("serviceId",serviceId);
            js.put("paymentInfo",phone);
            js.put("invoiceNumber",invoice_number);
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

                            ResponseDialog responseDialog = new ResponseDialog(activity,dialog,response,service,serviceId,"","","","","","",null,transactionSource);
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


        /*
        {"responseMessage":"Approved","responseMessageArabic":null,"responseCode":-1,"responseStatus":"Successful",
        "pubKeyValue":null,"tranDateTime":"290719135152","creationDate":null,"panCategory":null,"balance":null,"payeesCount":0,
        "payeesList":null,"billInfo":null,"RespPAN":null,"RespExpDate":null,"RespIPIN":null,"regType":null,
        "financialInstitutionId":null,"respUserName":null,"respUserPassword":null,"RespVoucherNumber":null,"voucherCode":null,
        "serviceInfo":null,"originalTransaction":null,"originalTranType":null,"originalTranUUID":null,"respUUID":null,
        "resp_acqTranFee":0,"resp_issuerTranFee":0,"resp_fromAccount":null,"resp_accountCurrency":null,"resp_tranAmount":0,
        "resp_paymentInfo":null,"toCard":null,"rittalFee":0,"clientFee":0,"voucherNumber":null}
        */
    }

    public void clearForm(){
        et_card_pan.setText("");
        et_card_expiry_date.setText("");
        et_card_pin.setText("");
        et_amount .setText("");
        et_invoice.setText("");
        et_phone_number.setText("");
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
"tranDateTime":"200319102259","creationDate":null,"panCategory":null,
"balance":null,"payeesCount":0,"payeesList":null,
"billInfo":"{UnitName:\"محور   التدريب\",ServiceName:\"\",TotalAmount:\"1\",InvoiceExpiry:\"2020-12-04\",DueAmount:\"0\",ReferenceId:\"2\",InvoiceStatus:\"2\",PayerName:\"تجربة فواتير\"}",
"RespPAN":null,"RespExpDate":null,"RespIPIN":null,"regType":null,
"financialInstitutionId":null,"respUserName":null,"respUserPassword":null,
"RespVoucherNumber":null,"voucherCode":null,"serviceInfo":null,
"originalTransaction":null,"originalTranType":null,"originalTranUUID":null,
"respUUID":null,"resp_acqTranFee":0,"resp_issuerTranFee":0,"resp_fromAccount":null,
"resp_accountCurrency":null,"resp_tranAmount":0}

{"PAN":null,"userPassword":null,"UUID":null,"panCategory":null,"expDate":null,"IPIN":null,
"responseMessage":"Approved","balance":null,"acqTranFee":0,"userName":null,"billInfo":null,
"responseCode":0,"financialInstitutionId":null,"accountCurrency":null,"originalTranType":null,
"voucherCode":null,"issuerTranFee":0,"originalTranUUID":null,"payees":null,
"pubKeyValue":"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANx4gKYSMv3CrWWsxdPfxDxFvl+Is\/0kc1dvMI1yNWDXI3AgdI4127KMUOv7gmwZ6SnRsHX\/KAM0IPRe0+Sa0vMCAwEAAQ==",
"serviceInfo":null,"tranDateTime":null,"creationDate":null,"tranAmount":0,
"responseStatus":null,"voucherNumber":null,"fromAccount":null,"originalTransaction":null}



{"responseMessage":"Approved","responseMessageArabic":null,"responseCode":0,"responseStatus":"Successful",
"pubKeyValue":null,"tranDateTime":"280319111853","creationDate":null,"panCategory":null,"balance":"{leger:75.39,available:75.39}",
"payeesCount":0,"payeesList":null,
"billInfo":"{UnitName:\"محور   التدريب\",ServiceName:\" \",TotalAmount:\"2\",ReferenceId:\"123\",PayerName:\"شركة ريتال للطرفيات\"}",
"RespPAN":"6391750800438891","RespExpDate":null,"RespIPIN":null,"regType":null,"financialInstitutionId":null,
"respUserName":null,"respUserPassword":null,"RespVoucherNumber":null,"voucherCode":null,"serviceInfo":null,
"originalTransaction":null,"originalTranType":null,"originalTranUUID":null,"respUUID":null,"resp_acqTranFee":0,
"resp_issuerTranFee":0,"resp_fromAccount":null,"resp_accountCurrency":null,"resp_tranAmount":0,
"resp_paymentInfo":null,"toCard":null}


07-30 13:06:52.475 1005-1005/sd.rittal.consumer D/TAG Response:
{"responseMessage":"Approved","responseMessageArabic":null,"responseCode":0,"responseStatus":"Successful",
"pubKeyValue":null,"tranDateTime":"300719140435","creationDate":null,"panCategory":null,
"balance":"{leger:-177180.67,available:96525929.57}","payeesCount":0,"payeesList":null,
"billInfo":"{UnitName:\"سداد\",ServiceName:\" \",TotalAmount:\"3\",ReferenceId:\"0912345678\",PayerName:\"sara babiker\"}"
,"RespPAN":"9888190130385682","RespExpDate":null,"RespIPIN":null,"regType":null,"financialInstitutionId":null,
"respUserName":null,"respUserPassword":null,"RespVoucherNumber":null,"voucherCode":null,"serviceInfo":null,
"originalTransaction":null,"originalTranType":null,"originalTranUUID":null,"respUUID":null,"resp_acqTranFee":0,
"resp_issuerTranFee":-1.5,"resp_fromAccount":null,"resp_accountCurrency":null,"resp_tranAmount":0,
"resp_paymentInfo":null,"toCard":null,"rittalFee":0,"clientFee":0,"voucherNumber":null}


"responseMessage":"Approved","responseMessageArabic":null,"responseCode":0,"responseStatus":"Successful",
"pubKeyValue":null,"tranDateTime":"300719140802","creationDate":null,"panCategory":null,"balance":null,"payeesCount":0,
"payeesList":null,"billInfo":"{UnitName:\"سداد\",ServiceName:\"\",TotalAmount:\"3\",InvoiceExpiry:\"2020-01-01\",DueAmount:\"3\",
ReferenceId:\"1\",InvoiceStatus:\"1\",PayerName:\"sara babiker\"}","RespPAN":"9888190130385682",
"RespExpDate":null,"RespIPIN":null,"regType":null,"financialInstitutionId":null,"respUserName":null,
"respUserPassword":null,"RespVoucherNumber":null,"voucherCode":null,"serviceInfo":null,"originalTransaction":null,
"originalTranType":null,"originalTranUUID":null,"respUUID":null,"resp_acqTranFee":0,"resp_issuerTranFee":0,
"resp_fromAccount":null,"resp_accountCurrency":null,"resp_tranAmount":0,"resp_paymentInfo":null,"toCard":null,
"rittalFee":0,"clientFee":0,"voucherNumber":null}


 */

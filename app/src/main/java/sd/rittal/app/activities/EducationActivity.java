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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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


public class EducationActivity extends AppCompatActivity implements FormSession,LoginSession {
    String TAG = EducationActivity.class.getSimpleName().toUpperCase();
    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;
    Bundle extras;
    AppApplication application;
    ProgressDialog progressDialog;
    Dialog dialog;

    Toolbar toolbar;

    public CarouselPagerAdapter adapter;
    public ViewPager pager;
    Spinner sp_cards;

    String course_id = "0";
    String course_title = "0";
    String application_type_id = "0";
    String application_type_title = "0";

    RadioGroup rg_certificate_type;
    RadioButton rb_sudanese_certificate,rb_arab_certificate;


    LinearLayout layout_card_data,layout_card_secret;
    EditText et_card_pan,et_card_expiry_date,et_card_pin,et_seater_number,et_student_name,et_phone_number,et_amount;
    Spinner sp_course,sp_application_type;
    Button btn_request;
    Switch switch_use_saved_card;
    RelativeLayout layout_phone_number,layout_seater_number,layout_student_name;

    List<String> LIST_SERVICES_CODE,LIST_SERVICES_NAME,LIST_FORMS_CODE,LIST_FORMS_NAME,LIST_FORMS_LOCAL_FEES,LIST_FORMS_FORING_FEES;

    ArrayAdapter<String> ARRAY_ADAPTER_SERVICES,ARRAY_ADAPTER_FORMS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);
        initiateContext();
        initiateLayout();

        getMOHEIntialData();
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

        LIST_SERVICES_CODE  = new ArrayList<String>();
        LIST_SERVICES_NAME  = new ArrayList<String>();
        LIST_FORMS_CODE     = new ArrayList<String>();
        LIST_FORMS_NAME     = new ArrayList<String>();
        LIST_FORMS_LOCAL_FEES     = new ArrayList<String>();
        LIST_FORMS_FORING_FEES     = new ArrayList<String>();


    }

    public void clearForm(){
        et_card_pan.setText("");
        et_card_expiry_date.setText("");
        et_card_pin.setText("");

        et_seater_number.setText("");
        et_student_name.setText("");
        et_phone_number.setText("");
        et_amount.setText("");
    }
    private void initiateLayout(){

        toolbar                 = (Toolbar) findViewById(R.id.toolbar);
        switch_use_saved_card   = (Switch)findViewById(R.id.switch_use_saved_card);
        et_card_pan             = (EditText) findViewById(R.id.et_card_pan);
        et_card_expiry_date     = (EditText) findViewById(R.id.et_card_expiry_date);
        et_card_pin             = (EditText) findViewById(R.id.et_card_pin);

        sp_course               = (Spinner) findViewById(R.id.sp_course);
        sp_application_type     = (Spinner) findViewById(R.id.sp_application_type);
        et_seater_number         = (EditText) findViewById(R.id.et_seater_number);
        et_student_name         = (EditText) findViewById(R.id.et_student_name);
        et_phone_number         = (EditText) findViewById(R.id.et_phone_number);
        et_amount               = (EditText) findViewById(R.id.et_amount);
        layout_phone_number               = (RelativeLayout) findViewById(R.id.layout_phone_number);
        layout_seater_number               = (RelativeLayout) findViewById(R.id.layout_seater_number);
        layout_student_name               = (RelativeLayout) findViewById(R.id.layout_student_name);

        rg_certificate_type     = (RadioGroup) findViewById(R.id.rg_certificate_type);
        rb_sudanese_certificate = (RadioButton) findViewById(R.id.rb_sudanese_certificate);
        rb_arab_certificate     = (RadioButton) findViewById(R.id.rb_arab_certificate);

        sp_cards                = (Spinner) findViewById(R.id.sp_cards);
        pager                   = (ViewPager) findViewById(R.id.myviewpager);

        layout_card_data        = (LinearLayout) findViewById(R.id.layout_card_data);
        layout_card_secret      = (LinearLayout) findViewById(R.id.layout_card_secret);

        toolbar.setTitle(getString(R.string.title_high_education));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rb_sudanese_certificate.setChecked(true);

        if(rb_sudanese_certificate.isChecked()){
            layout_seater_number.setVisibility(View.VISIBLE);
            layout_phone_number.setVisibility(View.GONE);
            layout_student_name.setVisibility(View.GONE);
        }else if(rb_sudanese_certificate.isChecked()){
            layout_seater_number.setVisibility(View.GONE);
            layout_phone_number.setVisibility(View.VISIBLE);
            layout_student_name.setVisibility(View.VISIBLE);

        }else{
            layout_seater_number.setVisibility(View.GONE);
            layout_phone_number.setVisibility(View.GONE);
            layout_student_name.setVisibility(View.GONE);
        }

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


        rb_sudanese_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rb_sudanese_certificate.isChecked()){
                    Log.d("TEST","rb_sudanese_certificate");
                    layout_seater_number.setVisibility(View.VISIBLE);
                    layout_phone_number.setVisibility(View.GONE);
                    layout_student_name.setVisibility(View.GONE);
                }else if(rb_arab_certificate.isChecked()){
                    Log.d("TEST","rb_sudanese_certificate");
                    layout_seater_number.setVisibility(View.GONE);
                    layout_phone_number.setVisibility(View.VISIBLE);
                    layout_student_name.setVisibility(View.VISIBLE);

                }else{
                    layout_seater_number.setVisibility(View.GONE);
                    layout_phone_number.setVisibility(View.GONE);
                    layout_student_name.setVisibility(View.GONE);
                }
            }
        });
        rb_arab_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rb_sudanese_certificate.isChecked()){
                    Log.d("TEST","rb_sudanese_certificate");
                    layout_seater_number.setVisibility(View.VISIBLE);
                    layout_phone_number.setVisibility(View.GONE);
                    layout_student_name.setVisibility(View.GONE);
                }else if(rb_arab_certificate.isChecked()){
                    Log.d("TEST","rb_sudanese_certificate");
                    layout_seater_number.setVisibility(View.GONE);
                    layout_phone_number.setVisibility(View.VISIBLE);
                    layout_student_name.setVisibility(View.VISIBLE);

                }else{
                    layout_seater_number.setVisibility(View.GONE);
                    layout_phone_number.setVisibility(View.GONE);
                    layout_student_name.setVisibility(View.GONE);
                }
            }
        });



        btn_request             = (Button) findViewById(R.id.btn_request);

        rittal.myCards("A&C",adapter,getSupportFragmentManager(),switch_use_saved_card,sp_cards,pager,et_card_pan,et_card_expiry_date,layout_card_data,layout_card_secret);


        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pan                      = et_card_pan.getText().toString().trim().replace(" ","");
                String expiry_date              = et_card_expiry_date.getText().toString().trim().replace("/","");
                String pin                      = et_card_pin.getText().toString().trim();

                String seater_number            = et_seater_number.getText().toString().trim();
                String student_name             = et_student_name.getText().toString().trim();
                String phone_number             = et_phone_number.getText().toString().trim();
                String amount                   = et_amount.getText().toString().trim();

                if(rb_sudanese_certificate.isChecked()){

                    rittal.log("data", pan+"-"+expiry_date+"-"+pin+"-"+seater_number+"-"+amount);
                    rittal.rittalToast("sudanese","0","i");
                    if(validation_sudanese_certificate(pan,expiry_date,pin,seater_number,amount)){
                        clearForm();
                        doSudaneseCertificateInquiry(pan,expiry_date,pin,seater_number,amount,course_id,application_type_id);
                    }

                }else if(rb_arab_certificate.isChecked()){
                    rittal.log("data", pan+"-"+expiry_date+"-"+pin+"-"+student_name+"-"+phone_number+"-"+amount);
                    rittal.rittalToast("arab","0","i");
                    if(validation_arab_certificate(pan,expiry_date,pin,student_name,phone_number,amount)){
                        clearForm();
                        String paymentInfo = student_name +"/"+ phone_number +"/"+ course_id +"/"+ application_type_id;
                        doArabCertificateInquiry(pan,expiry_date,pin,student_name,phone_number,amount,course_id,application_type_id);

                    }
                }else {

                }

                rittal.log(TAG+"_PAN",pan);
                rittal.log(TAG+"_EXPIRY DATA",expiry_date);
                rittal.log(TAG+"_PIN",pin);
            }
        });


    }
    private boolean validation_sudanese_certificate(String pan , String expiry_date , String pin , String seater_number ,String amount){
        Boolean validate = false;
        // check if transaction from account or card , ACCC mean account transaction
        if(!expiry_date.equals("ACCC")) {
            // transaction is from card
            if(pan.length() == 0 || expiry_date.length() == 0  || pin.length() == 0 || seater_number.length() == 0||  amount.length() == 0){
                if(pan.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pan),"1","w"); return false;}
                if(expiry_date.length() == 0){rittal.rittalToast(getString(R.string.enter_card_expiry_date),"1","w"); return false;}
                if(pin.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pin),"1","w"); return false;}
                if(seater_number.length() == 0){rittal.rittalToast(getString(R.string.enter_seater_number),"1","w"); return false;}
                if(amount.length() == 0){rittal.rittalToast(getString(R.string.enter_amount),"1","w"); return false;}
                return false;
            }else{
                if(pan.length() == 16 || pan.length() == 19){
                    if(expiry_date.length() == 4){
                        if(pin.length() == 4){
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
        }else{
            //transaction is from account
            return true;
        }

    }

    private boolean validation_arab_certificate(String pan , String expiry_date , String pin , String student_name , String phone_number,String amount){
        Boolean validate = false;
        // check if transaction from account or card , ACCC mean account transaction
        if(!expiry_date.equals("ACCC")) {
            // transaction is from card
            if(pan.length() == 0 || expiry_date.length() == 0  || pin.length() == 0 || student_name.length() == 0|| phone_number.length() ==0 || amount.length() == 0){
                if(pan.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pan),"1","w"); return false;}
                if(expiry_date.length() == 0){rittal.rittalToast(getString(R.string.enter_card_expiry_date),"1","w"); return false;}
                if(pin.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pin),"1","w"); return false;}
                if(student_name.length() == 0){rittal.rittalToast(getString(R.string.enter_student_name),"1","w"); return false;}
                if(phone_number.length() == 0){rittal.rittalToast(getString(R.string.enter_phone_number),"1","w");return false;}
                if(amount.length() == 0){rittal.rittalToast(getString(R.string.enter_amount),"1","w"); return false;}
                return false;
            }else{
                if(pan.length() == 16 || pan.length() == 19){
                    if(expiry_date.length() == 4){
                        if(pin.length() == 4){
                            if(phone_number.length() == 10){
                                return true;
                            }else{
                                rittal.rittalToast(getString(R.string.phone_number_lenght),"1","w");
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
            //transaction is from account
            if(pan.length() == 0 || expiry_date.length() == 0  || pin.length() == 0 || student_name.length() == 0|| phone_number.length() ==0 || amount.length() == 0){
                if(pan.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pan),"1","w"); return false;}
                if(expiry_date.length() == 0){rittal.rittalToast(getString(R.string.enter_card_expiry_date),"1","w"); return false;}
                if(pin.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pin),"1","w"); return false;}
                if(student_name.length() == 0){rittal.rittalToast(getString(R.string.enter_student_name),"1","w"); return false;}
                if(phone_number.length() == 0){rittal.rittalToast(getString(R.string.enter_phone_number),"1","w");return false;}
                if(amount.length() == 0){rittal.rittalToast(getString(R.string.enter_amount),"1","w"); return false;}
                return false;
            }else{
                if(phone_number.length() == 10){
                    return true;
                }else{
                    rittal.rittalToast(getString(R.string.phone_number_lenght),"1","w");
                    return false;
                }
            }

        }



    }

    private void doSudaneseCertificateInquiry(final String pan , final String exdate , final String ipin, String seater_number , final String amount , String course_id, String application_type_id) {

        final String uuid = UUID.randomUUID().toString();

        String ipin_ = "";
        String pk =application.getPublicKey();// "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==";//rittal.getValueOfKey("pk");// "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANx4gKYSMv3CrWWsxdPfxDxFvl+Is/0kc1dvMI1yNWDXI3AgdI4127KMUOv7gmwZ6SnRsHX/KAM0IPRe0+Sa0vMCAwEAAQ==";

        ipin_ = IPINBlockGenerator.getIPINBlock(ipin,pk,uuid);

        String url = application.getBaseUrl()+"/consumer_services.aspx";//
        // ?service=9&cust_id=1&pan="+Uri.encode(pan)+"&ex_date="+Uri.encode(exdate)+"
        // &ipin="+Uri.decode(ipin_)+"&uuid="+Uri.encode(uuid)+"&serviceId="+Uri.encode("2")+"&paymentInfo="+Uri.encode(phone)+"&invoiceNumber="+Uri.encode(invoice_number);
        rittal.log("pubKeyValue",pk);
        rittal.log("URL",url);
        //rittal.log("ipin",text);
        final String paymentInfo = "SETNUMBER="+seater_number +"/STUDCOURSEID="+ course_id +"/STUDFORMKIND="+ application_type_id;
        final String service = "4";
        final String opId = "8";

        showprogres();
        JSONObject js = new JSONObject();

        try {
            js.put("service",service);
            js.put("customerID","1");
            js.put("fromCard",pan);
            js.put("expiryDate",exdate);
            js.put("ipin",ipin_);
            js.put("uuid",uuid);
            js.put("opId",opId);
            js.put("paymentInfo",paymentInfo);
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
                        if(exdate.toUpperCase().equals("ACCC")){
                            transactionSource = "ACCOUNT";
                        }else{
                            transactionSource = "CARD";
                        }
                        try {

                            ResponseDialog responseDialog = new ResponseDialog(activity,dialog,response,service,opId,pan,exdate,ipin,"",paymentInfo,amount,progressDialog,transactionSource);
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

    private void doArabCertificateInquiry(final String pan , final String exdate , final String ipin, String student_name, String phone_number , final String amount , String course_id, String application_type_id) {

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
        final String paymentInfo = "STUCNAME="+student_name +"/STUCPHONE="+ phone_number +"/STUDCOURSEID="+ course_id +"/STUDFORMKIND="+ application_type_id;
        final String service = "4";
        final String opId = "10";
        showprogres();
        JSONObject js = new JSONObject();

        try {
            js.put("service",service);
            js.put("customerID","1");
            js.put("fromCard",pan);
            js.put("expiryDate",exdate);
            js.put("ipin",ipin_);
            js.put("uuid",uuid);
            js.put("opId",opId);
            js.put("paymentInfo",paymentInfo);
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
                        if(exdate.toUpperCase().equals("ACCC")){
                            transactionSource = "ACCOUNT";
                        }else{
                            transactionSource = "CARD";
                        }
                        try {
                            dataStorage.add_transaction(response.toString(),service,rittal.getValueOfKey("user_id"),opId,transactionSource);
                        }catch (Exception e){

                        }
                        try {

                            ResponseDialog responseDialog = new ResponseDialog(activity,dialog,response,service,opId,pan,exdate,ipin,"",paymentInfo,amount,progressDialog,transactionSource);
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


    private void getMOHEIntialData() {
        RequestQueue rq = Volley.newRequestQueue(this);

        String url = application.getBaseUrl()+"/get_mohe_services.aspx";
        rittal.log("URL",url);

        JSONObject js = new JSONObject();
        try {
            js.put("version","1");
        } catch (JSONException e) {
            e.printStackTrace();
            rittal.log("Object Ex",e.toString());
        }
        StringRequest req=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                rittal.log("Response",response);
                try {
                    String resArray[] = response.split("#");
                     String tempData = resArray[1];
                    JSONArray moheData = new JSONArray(tempData);


                    //reach courses and forms
                    JSONObject coursesObj = new JSONObject(moheData.get(0).toString());
                    JSONArray coursesArray = coursesObj.getJSONArray("courses");

                    JSONObject formsObj = new JSONObject(moheData.get(1).toString());
                    JSONArray formsArray = formsObj.getJSONArray("forms");

                    //courses array
                    for(int j = 0 ; j < coursesArray.length() ; j++){
                        JSONObject coursesSmallObj = new JSONObject(coursesArray.get(j).toString());
                        JSONObject obj = coursesSmallObj.getJSONObject("course");
                        //rittal.log("c_code", obj.getString("c_code"));
                        //rittal.log("c_name", obj.getString("c_name"));
                        LIST_SERVICES_CODE.add(obj.getString("c_code"));
                        LIST_SERVICES_NAME.add(obj.getString("c_name"));

                    }

                    //forms array
                    for(int j = 0 ; j < formsArray.length() ; j++){
                        JSONObject formsSmallObj = new JSONObject(formsArray.get(j).toString());
                        JSONObject obj = formsSmallObj.getJSONObject("form");
                        //rittal.log("c_code", obj.getString("c_code"));
                        //rittal.log("c_name", obj.getString("c_name"));
                        //rittal.log("fee", obj.getString("fee"));
                        //rittal.log("f_fee", obj.getString("f_fee"));

                        if(!obj.getString("f_fee").equals("-1")){
                            LIST_FORMS_CODE.add(obj.getString("c_code"));
                            LIST_FORMS_NAME.add(obj.getString("c_name"));
                            LIST_FORMS_LOCAL_FEES.add(obj.getString("fee"));
                            LIST_FORMS_FORING_FEES.add(obj.getString("f_fee"));
                        }

                    }



                    init();

                }catch (Exception e){
                    rittal.log("read mohe service exception", e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(
                AppApplication.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        req.setShouldCache(false);
        AppApplication.getInstance().addToRequestQueue(req);
    }

    public void init(){
        if(LIST_FORMS_NAME.size() > 0) {

            try{
                ARRAY_ADAPTER_FORMS = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, LIST_FORMS_NAME);
                ARRAY_ADAPTER_FORMS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_application_type.setAdapter(ARRAY_ADAPTER_FORMS);
                sp_application_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        int position = sp_application_type.getSelectedItemPosition();
                        //ShowMsg(spinner_forms.getSelectedItem().toString());

                        application_type_id = String.valueOf(LIST_FORMS_CODE.get(position));
                        course_title = sp_application_type.getSelectedItem().toString();

                        et_amount.setText(String.valueOf(LIST_FORMS_LOCAL_FEES.get(position)));


                        //ShowMsg(form_code);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        int position = 0;
//
                        application_type_id = "";
                        course_title = "";
                        et_amount.setText("");

                    }
                });
            }catch (Exception e){
            }

        }

        if(LIST_SERVICES_NAME.size() > 0) {

            try{
                ARRAY_ADAPTER_SERVICES = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, LIST_SERVICES_NAME);
                ARRAY_ADAPTER_SERVICES.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_course.setAdapter(ARRAY_ADAPTER_SERVICES);
                sp_course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        int position = sp_course.getSelectedItemPosition();


                        course_id = ""+LIST_SERVICES_CODE.get(position);
                        course_title = sp_course.getSelectedItem().toString();


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        int position = 0;

                        course_id = "";
                        course_title = "";


                    }
                });
            }catch (Exception e){
            }

        }
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

//Arabic Inquiry
//{"responseMessage":"Approved","responseMessageArabic":null,"responseCode":0,"responseStatus":"Successful",
// "pubKeyValue":null,"tranDateTime":"300719090851","creationDate":null,"panCategory":null,"balance":null,
// "payeesCount":0,"payeesList":null,
// "billInfo":"{englishName:\"Aqtest\",studentNo:\"0\",receiptNo:\"0\",arabicName:\"Aqtest\",dueAmount:\"50\",
// formNo:\"0\"}","RespPAN":"9888190130385682","RespExpDate":null,"RespIPIN":null,"regType":null,
// "financialInstitutionId":null,"respUserName":null,"respUserPassword":null,"RespVoucherNumber":null,
// "voucherCode":null,"serviceInfo":null,"originalTransaction":null,"originalTranType":null,
// "originalTranUUID":null,"respUUID":null,"resp_acqTranFee":0,"resp_issuerTranFee":0,
// "resp_fromAccount":null,"resp_accountCurrency":null,"resp_tranAmount":0,
// "resp_paymentInfo":"STUCNAME=Aqtest\/STUCPHONE=0118185260\/STUDCOURSEID=1\/STUDFORMKIND=1",
// "toCard":null,"rittalFee":0,"clientFee":0,"voucherNumber":null}


//{"responseMessage":"Approved","responseMessageArabic":null,"responseCode":0,"responseStatus":"Successful",
// "pubKeyValue":null,"tranDateTime":"300719110253","creationDate":null,"panCategory":null,"balance":null,
// "payeesCount":0,"payeesList":null,"billInfo":"{englishName:\"\",receiptNo:\"0\",arabicName:\"أحمد علي عبدالله أحمد\",
// dueAmount:\"50\",formNo:\"0\"}","RespPAN":"9888190130385682","RespExpDate":null,"RespIPIN":null,"regType":null,
// "financialInstitutionId":null,"respUserName":null,"respUserPassword":null,"RespVoucherNumber":null,
// "voucherCode":null,"serviceInfo":null,"originalTransaction":null,"originalTranType":null,"originalTranUUID":null,
// "respUUID":null,"resp_acqTranFee":0,"resp_issuerTranFee":0,"resp_fromAccount":null,"resp_accountCurrency":null,
// "resp_tranAmount":0,"resp_paymentInfo":"SETNUMBER=2015\/STUDCOURSEID=1\/STUDFORMKIND=1","toCard":null,"rittalFee":0,
// "clientFee":0,"voucherNumber":null}


//sudanies bill payment
//{"responseMessage":"Approved","responseMessageArabic":null,"responseCode":0,"responseStatus":"Successful",
// "pubKeyValue":null,"tranDateTime":"290719153953","creationDate":null,"panCategory":null,
// "balance":"{leger:-174111.09,available:96528999.15}",
// "payeesCount":0,"payeesList":null,
// "billInfo":"{englishName:\"\",receiptNo:\"6\",arabicName:\"أحمد علي عبدالله أحمد\",formNo:\"370323\"}",
// "RespPAN":"9888190130385682","RespExpDate":null,"RespIPIN":null,"regType":null,"financialInstitutionId":null,
// "respUserName":null,"respUserPassword":null,"RespVoucherNumber":null,"voucherCode":null,"serviceInfo":null,
// "originalTransaction":null,"originalTranType":null,"originalTranUUID":null,"respUUID":null,"resp_acqTranFee":0,
// "resp_issuerTranFee":-1.5,"resp_fromAccount":null,"resp_accountCurrency":null,"resp_tranAmount":50,
// "resp_paymentInfo":"SETNUMBER=2015\/STUDCOURSEID=1\/STUDFORMKIND=1","toCard":null,"rittalFee":0,
// "clientFee":0,"voucherNumber":null}

//Arabic bill payment
//{"responseMessage":"Approved","responseMessageArabic":null,"responseCode":0,"responseStatus":"Successful",
// "pubKeyValue":null,"tranDateTime":"290719154136","creationDate":null,"panCategory":null,
// "balance":"{leger:-174162.59,available:96528947.65}","payeesCount":0,"payeesList":null,
// "billInfo":"{englishName:\"QAtest\",studentNo:\"126703\",receiptNo:\"17\",arabicName:\"QAtest\",formNo:\"48205\"}",
// "RespPAN":"9888190130385682","RespExpDate":null,"RespIPIN":null,"regType":null,"financialInstitutionId":null,
// "respUserName":null,"respUserPassword":null,"RespVoucherNumber":null,"voucherCode":null,"serviceInfo":null,
// "originalTransaction":null,"originalTranType":null,"originalTranUUID":null,"respUUID":null,"resp_acqTranFee":0,
// "resp_issuerTranFee":-1.5,"resp_fromAccount":null,"resp_accountCurrency":null,"resp_tranAmount":50,
// "resp_paymentInfo":"STUCNAME=QAtest\/STUCPHONE=0118185260\/STUDCOURSEID=1\/STUDFORMKIND=1","toCard":null,
// "rittalFee":0,"clientFee":0,"voucherNumber":null}
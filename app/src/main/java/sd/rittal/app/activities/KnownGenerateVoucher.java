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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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




public class KnownGenerateVoucher extends AppCompatActivity implements FormSession,LoginSession {

    String TAG = CashoutActivity.class.getSimpleName().toUpperCase();
    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;
    Bundle extras;
    AppApplication application;
    ProgressDialog progressDialog;
    Dialog dialog;

    List<String> IDList;

    LinearLayout layout_card_data,layout_card_secret;
    EditText et_card_pan,et_card_expiry_date,et_card_pin;
    EditText et_amount,et_sender_name,et_sender_id,et_sender_phone_num
            ,et_receiver_name,et_receiver_id,et_receiver_phone_num,et_voucher_key;
    Spinner sp_reciverID_type,sp_senderID_type;
    Button btn_send;
    Switch switch_use_saved_card;
    Toolbar toolbar;
    public CarouselPagerAdapter adapter;
    public ViewPager pager;
    Spinner sp_cards;

    String selected_sender_id_type,selected_receiver_id_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_known_generate_voucher);
        IDList = new ArrayList<String>();

        IDList.add(getString(R.string.natonal_id));
        IDList.add(getString(R.string.pasport));

        initiateContext();
        initiateLayout();

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void knownGenerateVoucher(String pan , final String exdate , String ipin, String amount, String s_name, String s_id_type, String s_id, String s_phone, String r_name, String r_id_type, String r_id, String r_phone, String voucher_key ) {
         clearForm();

       final String uuid = UUID.randomUUID().toString();

        String ipin_ = "";

        rittal.log("normal pkey",rittal.getValueOfKey("pk"));

        String pk =application.getPublicKey();// "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==";//rittal.getValueOfKey("pk");//  "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANx4gKYSMv3CrWWsxdPfxDxFvl+Is/0kc1dvMI1yNWDXI3AgdI4127KMUOv7gmwZ6SnRsHX/KAM0IPRe0+Sa0vMCAwEAAQ==";

        ipin_ = IPINBlockGenerator.getIPINBlock(ipin,pk,uuid);

        String url = application.getBaseUrl()+"/consumer_services.aspx";
        rittal.log("pubKeyValue",rittal.getValueOfKey("pubKeyValue"));
        rittal.log("URL",url);
        //  rittal.log("ipin",ipin_);

        final String service = "15";
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
//            put sender information to josn
            js.put("senderName",s_name);
            js.put("senderIdType",s_id_type);
            js.put("senderId",s_id);
            js.put("senderPhone",s_phone);
//            put receiver information to josn
            js.put("receiverName",r_name);
            js.put("receiverPhone",r_phone);
            js.put("receiverIdType",r_id_type);
            js.put("receiverId",r_id);
            js.put("voucherPassword",voucher_key);
        }catch (JSONException e) {
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
        switch_use_saved_card      = (Switch)findViewById(R.id.switch_use_saved_card);
        et_card_pan                = (EditText) findViewById(R.id.et_card_pan);
        et_card_expiry_date        = (EditText) findViewById(R.id.et_card_expiry_date);
        et_card_pin                = (EditText) findViewById(R.id.et_card_pin);
        sp_reciverID_type                = (Spinner) findViewById(R.id.sp_reciverID_type);
        sp_senderID_type                = (Spinner) findViewById(R.id.sp_senderID_type);
        et_amount                  = (EditText) findViewById(R.id.et_amount);
        et_sender_name             = (EditText) findViewById(R.id.et_senderName);
        et_sender_id               = (EditText) findViewById(R.id.et_senderID_number);
        et_sender_phone_num        = (EditText) findViewById(R.id.et_sender_phone);
        et_receiver_name           = (EditText) findViewById(R.id.et_reciverName);
        et_receiver_id             = (EditText) findViewById(R.id.et_receiverID);
        et_receiver_phone_num      = (EditText) findViewById(R.id.et_receiver_phone);
        et_voucher_key             = (EditText) findViewById(R.id.et_voucherKey);
        btn_send                   = (Button) findViewById(R.id.btn_knwonGenerateVoucher);
        sp_cards                   = (Spinner) findViewById(R.id.sp_cards);
        pager                      = (ViewPager) findViewById(R.id.myviewpager);
        layout_card_data           = (LinearLayout) findViewById(R.id.layout_card_data);
        layout_card_secret         = (LinearLayout) findViewById(R.id.layout_card_secret);


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, IDList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        sp_senderID_type.setAdapter(dataAdapter);
        sp_reciverID_type.setAdapter(dataAdapter);

        sp_senderID_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected_provider = adapterView.getItemAtPosition(i).toString();
                rittal.log("selected_provider",selected_provider.toUpperCase());
                if(selected_provider.trim().toUpperCase().equals(getString(R.string.natonal_id).toUpperCase())){
                    selected_sender_id_type = "1";
                }else if(selected_provider.trim().toUpperCase().equals(getString(R.string.pasport).toUpperCase())){
                    selected_sender_id_type = "2";
                }else{
                    selected_sender_id_type = "0";
                }
                rittal.log("selected_sender_id_type",selected_sender_id_type);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selected_sender_id_type = "0";
            }
        });

        sp_reciverID_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected_provider = adapterView.getItemAtPosition(i).toString();
                rittal.log("selected_provider",selected_provider.toUpperCase());
                if(selected_provider.trim().toUpperCase().equals(getString(R.string.natonal_id).toUpperCase())){
                    selected_receiver_id_type= "1";
                }else if(selected_provider.trim().toUpperCase().equals(getString(R.string.pasport).toUpperCase())){
                    selected_receiver_id_type = "2";
                }else{
                    selected_receiver_id_type = "0";
                }
                rittal.log("selected_receiver_id_type",selected_receiver_id_type);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selected_receiver_id_type = "0";
            }
        });


        rittal.myCards("A&C",adapter,getSupportFragmentManager(),switch_use_saved_card,sp_cards,pager,et_card_pan,et_card_expiry_date,layout_card_data,layout_card_secret);

        toolbar.setTitle(getString(R.string.knownGenerateVoucher));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pan              = et_card_pan.getText().toString().trim().replace(" ","");
                String expiry_date      = et_card_expiry_date.getText().toString().trim().replace("/","");
                String ipin             = et_card_pin.getText().toString().trim();

                String amount           = et_amount.getText().toString().trim();
                 //          get sender data
                String sender_name              = et_sender_name.getText().toString().trim();
                String sender_id                = et_sender_id.getText().toString().trim();
                String s_phone                  = et_sender_phone_num.getText().toString().trim();
                //           get receiver data
                String receiver_name            = et_receiver_name.getText().toString().trim();
                String receiver_id              = et_receiver_id.getText().toString().trim();
                String receiver_phone           = et_receiver_phone_num.getText().toString().trim();
                String voucher_key              = et_voucher_key.getText().toString().trim();

                if(validation(pan,expiry_date,ipin,amount,sender_name,selected_sender_id_type,sender_id,s_phone,receiver_name,selected_receiver_id_type,receiver_id,receiver_phone,voucher_key)){
                    clearForm();
                   knownGenerateVoucher(pan,expiry_date,ipin,amount,sender_name,selected_sender_id_type,sender_id,s_phone,receiver_name,selected_receiver_id_type,receiver_id,receiver_phone,voucher_key);
                }
            }
        });

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

    }
    public void clearForm(){

        et_card_pan.setText("");
        et_card_expiry_date.setText("");
        et_card_pin.setText("");

        et_amount.setText("");
        et_sender_name.setText("");
        et_sender_id.setText("");
        et_sender_phone_num.setText("");
        et_receiver_id.setText("");
        et_receiver_name.setText("");
        et_receiver_phone_num.setText("");
        et_voucher_key.setText("");
    }

    public Boolean validation(String pan, String expiry_date, String ipin,String amount,
                              String s_name,String s_id_type,String s_id,String s_phone
            ,String r_name,String r_id_type,String r_id,String r_phone,String voucher_key){
        // check if transaction from account or card , ACCC mean account transaction
        if(!expiry_date.equals("ACCC")){
            // transaction is from card
            if(pan.length() ==0 || expiry_date.length() == 0 || ipin.length() == 0 || amount.length()==0||
                    s_name.length() == 0 || s_id_type.length() == 0 || s_id.length() == 0 || s_phone.length()==0||
                    r_name.length() == 0 || r_id_type.length() == 0 || r_id.length() == 0 || r_phone.length()==0|| voucher_key.length()==0){

                if(pan.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pan),"1","w"); return false;}
                if(expiry_date.length() == 0){rittal.rittalToast(getString(R.string.enter_card_expiry_date),"1","w"); return false;}
                if(ipin.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pin),"1","w"); return false;}

                if(amount.length() == 0){rittal.rittalToast(getString(R.string.enter_amount),"1","w"); return false;}
                //            wiarrnig about sender information
                if(s_name.length() == 0){rittal.rittalToast(getString(R.string.enter_sender_name),"1","w"); return false;}
                if(s_id_type.length() == 0){rittal.rittalToast(getString(R.string.enter_sender_id_type),"1","w"); return false;}
                if(s_id.length() == 0){rittal.rittalToast(getString(R.string.enter_sender_id),"1","w"); return false;}
                if(s_phone.length() == 0){rittal.rittalToast(getString(R.string.enter_sender_number),"1","w"); return false;}
                //            wiarrnig about receiver information
                if(r_name.length() == 0){rittal.rittalToast(getString(R.string.enter_receiver_name),"1","w"); return false;}
                if(r_id_type.length() == 0){rittal.rittalToast(getString(R.string.enter_receiver_id_type),"1","w"); return false;}
                if(r_id.length() == 0){rittal.rittalToast(getString(R.string.enter_receiver_id),"1","w"); return false;}
                if(r_phone.length() == 0){rittal.rittalToast(getString(R.string.enter_receiver_number),"1","w"); return false;}
                if(voucher_key.length() == 0){rittal.rittalToast(getString(R.string.enter_voucher_key),"1","w"); return false;}
                return false;

            }else{
                if(pan.length() == 16 || pan.length() == 19){
                    if(expiry_date.length() == 4){
                        if(ipin.length() == 4){
                            if(amount.length() !=0){
                                if(s_name.length() !=0){
                                    if(s_id_type.length() !=0){
                                        if(s_id.length() !=0){
                                            if(s_phone.length() !=0){
                                                if(r_name.length() !=0){
                                                    if(r_id_type.length()!=0){
                                                        if( r_id.length() !=0){
                                                            if(r_phone.length() !=0){
                                                                if(voucher_key.length() !=0){
                                                                    return true;
                                                                }else {
                                                                    rittal.rittalToast(getString(R.string.enter_voucher_key),"1","w");
                                                                    return false;
                                                                }                                                        }else {
                                                                rittal.rittalToast(getString(R.string.enter_receiver_number),"1","w");
                                                                return false;
                                                            }
                                                        }else {
                                                            rittal.rittalToast(getString(R.string.enter_receiver_id),"1","w");
                                                            return false;
                                                        }
                                                    }else {
                                                        rittal.rittalToast(getString(R.string.enter_receiver_id_type),"1","w");
                                                        return false;
                                                    }
                                                }else {
                                                    rittal.rittalToast(getString(R.string.enter_receiver_name),"1","w");
                                                    return false;
                                                }
                                            }else {
                                                rittal.rittalToast(getString(R.string.enter_sender_number),"1","w");
                                                return false;
                                            }
                                        }else {
                                            rittal.rittalToast(getString(R.string.enter_sender_id),"1","w");
                                            return false;
                                        }
                                    }else {
                                        rittal.rittalToast(getString(R.string.enter_sender_id_type),"1","w");
                                        return false;
                                    }
                                }else {
                                    rittal.rittalToast(getString(R.string.enter_sender_name),"1","w");
                                    return false;
                                }
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
        }else {
            //transaction is from account
            if(amount.length()==0||
                    s_name.length() == 0 || s_id_type.length() == 0 || s_id.length() == 0 || s_phone.length()==0||
                    r_name.length() == 0 || r_id_type.length() == 0 || r_id.length() == 0 || r_phone.length()==0|| voucher_key.length()==0){


                if(amount.length() == 0){rittal.rittalToast(getString(R.string.enter_amount),"1","w"); return false;}
                //            wiarrnig about sender information
                if(s_name.length() == 0){rittal.rittalToast(getString(R.string.enter_sender_name),"1","w"); return false;}
                if(s_id_type.length() == 0){rittal.rittalToast(getString(R.string.enter_sender_id_type),"1","w"); return false;}
                if(s_id.length() == 0){rittal.rittalToast(getString(R.string.enter_sender_id),"1","w"); return false;}
                if(s_phone.length() == 0){rittal.rittalToast(getString(R.string.enter_sender_number),"1","w"); return false;}
                //            wiarrnig about receiver information
                if(r_name.length() == 0){rittal.rittalToast(getString(R.string.enter_receiver_name),"1","w"); return false;}
                if(r_id_type.length() == 0){rittal.rittalToast(getString(R.string.enter_receiver_id_type),"1","w"); return false;}
                if(r_id.length() == 0){rittal.rittalToast(getString(R.string.enter_receiver_id),"1","w"); return false;}
                if(r_phone.length() == 0){rittal.rittalToast(getString(R.string.enter_receiver_number),"1","w"); return false;}
                if(voucher_key.length() == 0){rittal.rittalToast(getString(R.string.enter_voucher_key),"1","w"); return false;}
                return false;

            }else{
                if(amount.length() !=0){
                    if(s_name.length() !=0){
                        if(s_id_type.length() !=0){
                            if(s_id.length() !=0){
                                if(s_phone.length() !=0){
                                    if(r_name.length() !=0){
                                        if(r_id_type.length()!=0){
                                            if( r_id.length() !=0){
                                                if(r_phone.length() !=0){
                                                    if(voucher_key.length() !=0){
                                                        return true;
                                                    }else {
                                                        rittal.rittalToast(getString(R.string.enter_voucher_key),"1","w");
                                                        return false;
                                                    }                                                        }else {
                                                    rittal.rittalToast(getString(R.string.enter_receiver_number),"1","w");
                                                    return false;
                                                }
                                            }else {
                                                rittal.rittalToast(getString(R.string.enter_receiver_id),"1","w");
                                                return false;
                                            }
                                        }else {
                                            rittal.rittalToast(getString(R.string.enter_receiver_id_type),"1","w");
                                            return false;
                                        }
                                    }else {
                                        rittal.rittalToast(getString(R.string.enter_receiver_name),"1","w");
                                        return false;
                                    }
                                }else {
                                    rittal.rittalToast(getString(R.string.enter_sender_number),"1","w");
                                    return false;
                                }
                            }else {
                                rittal.rittalToast(getString(R.string.enter_sender_id),"1","w");
                                return false;
                            }
                        }else {
                            rittal.rittalToast(getString(R.string.enter_sender_id_type),"1","w");
                            return false;
                        }
                    }else {
                        rittal.rittalToast(getString(R.string.enter_sender_name),"1","w");
                        return false;
                    }
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

package sd.rittal.app.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
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


public class BillPaymentActivity extends AppCompatActivity implements FormSession,LoginSession {

    String TAG = BillPaymentActivity.class.getSimpleName().toUpperCase();
    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;
    Bundle extras;
    AppApplication application;
    ProgressDialog progressDialog;
    Dialog dialog;
    ImageView iv_add;


    List<String> provider;

    String service_provider_id = "0";
    Toolbar toolbar;
    public CarouselPagerAdapter adapter;
    public ViewPager pager;
    Spinner sp_cards;

    private static final int PICK_CONTACT =1 ;
    String cNumber="";

    LinearLayout layout_card_data,layout_card_secret;
    EditText et_card_pan,et_card_expiry_date,et_card_pin,et_phone_number,et_amount;
    Spinner sp_service_provider;
    Button btn_request;
    Switch switch_use_saved_card;
    int MY_PERMISSIONS_REQUEST_RED_CONTACT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_payment);

        initiateContext();
        provider = new ArrayList<String>();
        provider.add(getString(R.string.zain));
        provider.add(getString(R.string.mtn));
        provider.add(getString(R.string.sudani));
        initiateLayout();

    }

    @Override
    protected void onResume() {
        super.onResume();
        clearForm();
        if(cNumber != null){
            et_phone_number.setText(cNumber);
        }
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

        toolbar                 = (Toolbar) findViewById(R.id.toolbar);
        switch_use_saved_card   = (Switch) findViewById(R.id.switch_use_saved_card);
        et_card_pan             = (EditText) findViewById(R.id.et_card_pan);
        et_card_expiry_date     = (EditText) findViewById(R.id.et_card_expiry_date);
        et_card_pin             = (EditText) findViewById(R.id.et_card_pin);
        sp_service_provider     = (Spinner) findViewById(R.id.sp_service_provider);
        et_amount               = (EditText) findViewById(R.id.et_amount);
        et_phone_number         = (EditText) findViewById(R.id.et_phone_number);
        sp_cards                = (Spinner) findViewById(R.id.sp_cards);
        pager                   = (ViewPager) findViewById(R.id.myviewpager);
        btn_request             = (Button) findViewById(R.id.btn_request);
        layout_card_data        = (LinearLayout) findViewById(R.id.layout_card_data);
        layout_card_secret      = (LinearLayout) findViewById(R.id.layout_card_secret);
        iv_add      = (ImageView) findViewById(R.id.iv_add);


        toolbar.setTitle(getString(R.string.title_bill_payment));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rittal.myCards("A&C",adapter,getSupportFragmentManager(),switch_use_saved_card,sp_cards,pager,et_card_pan,et_card_expiry_date,layout_card_data,layout_card_secret);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, provider);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        sp_service_provider.setAdapter(dataAdapter);
        sp_service_provider.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected_provider = adapterView.getItemAtPosition(i).toString();
                rittal.log("selected_provider",selected_provider.toUpperCase());
                if(selected_provider.trim().toUpperCase().equals(getString(R.string.zain).toUpperCase())){
                    service_provider_id = "2";
                }else if(selected_provider.trim().toUpperCase().equals(getString(R.string.mtn).toUpperCase())){
                    service_provider_id = "4";
                }else if(selected_provider.trim().toUpperCase().equals(getString(R.string.sudani).toUpperCase())){
                    service_provider_id = "6";
                }else{
                    service_provider_id = "0";
                }
                rittal.log("service_provider_id",service_provider_id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                service_provider_id = "0";
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
        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String pan                      = et_card_pan.getText().toString().trim().replace(" ","");
                final String expiry_date              = et_card_expiry_date.getText().toString().trim().replace("/","");
                final String pin                      = et_card_pin.getText().toString().trim();
                final String phone_number             = et_phone_number.getText().toString().trim();
                final String amount                   = et_amount.getText().toString().trim();

                if(validation(pan,expiry_date,pin,service_provider_id,phone_number,amount)){
                    if(validation(pan,expiry_date,pin,service_provider_id,phone_number,amount)){
                        new AlertDialog.Builder(activity)
                                .setIcon(R.drawable.ic_attention)
                                .setTitle(R.string.are_u_sure_of_phone_num)
                                .setMessage(phone_number)
                                .setPositiveButton(activity.getResources().getString(R.string.yes),new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        clearForm();
                                        billPayment(pan,expiry_date,pin,service_provider_id,phone_number,amount);                               }
                                })
                                .setNegativeButton(activity.getResources().getString(R.string.no),new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }

                }

                rittal.log(TAG+"_PAN",pan);
                rittal.log(TAG+"_EXPIRY DATA",expiry_date);
                rittal.log(TAG+"_PIN",pin);
            }
        });

        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readContact();
            }
        });
    }

    public void readContact(){
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_RED_CONTACT);
            }else{
                startActivityForResult(intent, PICK_CONTACT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //code
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        try{
            if (reqCode == PICK_CONTACT) {
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            cNumber = phones.getString(phones.getColumnIndex("data1"));

                            cNumber= rittal.modifyPhoneNumber(cNumber);
                            rittal.log("Number", cNumber);
//                      rittal.rittalToast(cNumber,"0","i");
                            System.out.println("number is:" + cNumber);
                        }
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean validation(String pan , String expiry_date , String pin , String service_provider_id , String phone_number,String amount){
        Boolean validate = false;
        // check if transaction from account or card , ACCC mean account transaction
        if(!expiry_date.equals("ACCC")){
            // transaction from card
            if(pan.length() == 0 || expiry_date.length() == 0  || pin.length() == 0 || service_provider_id.length() == 0|| phone_number.length() ==0 || amount.length() == 0){
                if(pan.length() == 0){ rittal.rittalToast(getString(R.string.enter_card_pan),"1","w");}
                if(expiry_date.length() == 0){rittal.rittalToast(getString(R.string.enter_card_expiry_date),"1","w");}
                if(pin.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pin),"1","w");}
                if(amount.length() == 0){rittal.rittalToast(getString(R.string.enter_amount),"1","w");}
                validate = false;
            }else{
                if(pan.length() == 16 || pan.length() == 19){
                    if(expiry_date.length() == 4){
                        if(expiry_date.length() == 4){
                            if(!service_provider_id.equals("0") || !service_provider_id.equals("")){
                                if(phone_number.length() == 10){
                                    validate = true;
                                }else{
                                    rittal.rittalToast(getString(R.string.phone_number_lenght),"1","w");
                                    validate = false;
                                }
                            }else{
                                rittal.rittalToast(getString(R.string.card_service_provider_unselected),"1","w");
                                validate = false;
                            }
                        }else{
                            rittal.rittalToast(getString(R.string.card_ipin_lengh),"1","w");
                            validate = false;
                        }
                    }else{
                        rittal.rittalToast(getString(R.string.card_expiry_date_length),"1","w");
                        validate = false;
                    }
                }else{
                    rittal.rittalToast(getString(R.string.card_pan_length),"1","w");
                    validate = false;
                }
            }
        }else {
            // transaction from account
            if( service_provider_id.length() == 0|| phone_number.length() ==0 || amount.length() == 0){

                if(service_provider_id.equals("0")|| service_provider_id.equals("")){rittal.rittalToast(getString(R.string.select_provider),"1","w");return validate;}
                if(phone_number.length() == 0){rittal.rittalToast(getString(R.string.enter_phone_number),"1","w");return validate;}
                if(amount.length() == 0){rittal.rittalToast(getString(R.string.enter_amount),"1","w");return validate;}
                validate = false;
            }else{

                if(amount.length() != 0){
                    if(!service_provider_id.equals("0") || !service_provider_id.equals("")){
                        if(phone_number.length() == 10){
                            validate = true;
                        }else{
                            rittal.rittalToast(getString(R.string.phone_number_lenght),"1","w");
                            validate = false;
                        }
                    }else{
                        rittal.rittalToast(getString(R.string.card_service_provider_unselected),"1","w");
                        validate = false;
                    }
                }else{
                    rittal.rittalToast(getString(R.string.enter_amount),"1","w");
                    validate = false;
                }
            }
        }



        return validate;
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
    private void billPayment(String pan , final String exdate , String ipin, final String provider_id, String phone, String amount) {

        final String uuid = UUID.randomUUID().toString();

        String ipin_ = "";
        String pk =application.getPublicKey();//"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==";//rittal.getValueOfKey("pk");// "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANx4gKYSMv3CrWWsxdPfxDxFvl+Is/0kc1dvMI1yNWDXI3AgdI4127KMUOv7gmwZ6SnRsHX/KAM0IPRe0+Sa0vMCAwEAAQ==";

        ipin_ = IPINBlockGenerator.getIPINBlock(ipin,pk,uuid);


        String url = application.getBaseUrl()+"/consumer_services.aspx";

        //?service=5&cust_id=1&pan="+Uri.encode(pan)+"&ex_date="
        // +Uri.encode(exdate)+"&ipin="+Uri.decode(ipin_)+"&uuid="
        // +Uri.encode(uuid)+"&opid="+Uri.encode(provider_id)+
        // "&payment_info="+Uri.encode(phone)+
        // "&tranAmount="+Uri.encode(amount);
        rittal.log("pubKeyValue",pk);
        rittal.log("URL",url);
        //rittal.log("ipin",text);
        final String service = "5";
        final String op_id = provider_id;


        showprogres();
        JSONObject js = new JSONObject();

        try {
            js.put("service",service);
            js.put("customerID",rittal.getValueOfKey("user_id"));
            js.put("fromCard",pan);
            js.put("expiryDate",exdate);
            js.put("ipin",ipin_);
            js.put("uuid",uuid);
            js.put("opId",op_id);
            js.put("paymentInfo",phone);
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
                        String transactionSource = "";
                        if(exdate.toUpperCase().equals("ACCC")){
                            transactionSource = "ACCOUNT";
                        }else{
                            transactionSource = "CARD";
                        }
                        rittal.log("TAG Response", response.toString());
                        try {
                            dataStorage.add_transaction(response.toString(),service,rittal.getValueOfKey("user_id"),op_id,transactionSource);
                        }catch (Exception e){

                        }

                        try {


                            ResponseDialog responseDialog = new ResponseDialog(activity,dialog,response,service,provider_id,"","","","","","",null,transactionSource);
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

    public void clearForm(View V){
        clearForm();
    }

    public void clearForm(){
        et_card_pan.setText("");
        et_card_expiry_date.setText("");
        et_card_pin.setText("");
        et_amount.setText("");
        et_phone_number.setText("");

    }


}

package sd.rittal.app.activities;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sd.rittal.app.AppApplication;
import sd.rittal.app.R;
import sd.rittal.app.adapters.CarouselPagerAdapter;
import sd.rittal.app.helper.DataStorage;
import sd.rittal.app.helper.FormSession;
import sd.rittal.app.helper.LoginSession;
import sd.rittal.app.helper.ResponseDialog;
import sd.rittal.app.helper.Rittal;

public class KnownGenerateVoucherCashout extends AppCompatActivity implements FormSession,LoginSession {
    String TAG = CashoutActivity.class.getSimpleName().toUpperCase();
    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;
    Bundle extras;
    AppApplication application;
    ProgressDialog progressDialog;
    Dialog dialog;
    EditText et_voucher_num,et_receiver_id,et_receiver_phone_num,et_voucher_password;
    Button btn_send;
    Switch switch_use_saved_card;
    Toolbar toolbar;
    public CarouselPagerAdapter adapter;
    public ViewPager pager;
    Spinner sp_cards;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_cashout);

        initiateContext();
        initiateLayout();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void knownGenerateVoucherCashout(String voucher_number ,String r_id,String r_phone,String voucher_key ) {

        clearForm();
        //et_otp.setText("");
        // final String uuid = UUID.randomUUID().toString();

        //String ipin_ = "";

        rittal.log("normal pkey",rittal.getValueOfKey("pk"));
        //  rittal.log("normal uuid",uuid);

        // String pk = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANx4gKYSMv3CrWWsxdPfxDxFvl+Is/0kc1dvMI1yNWDXI3AgdI4127KMUOv7gmwZ6SnRsHX/KAM0IPRe0+Sa0vMCAwEAAQ==";

        // ipin_ = IPINBlockGenerator.getIPINBlock(ipin,pk,uuid);

//         ipin_ = IPINBlockGenerator.getIPINBlock(ipin,"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==",uuid);;
        String url = application.getBaseUrl()+"/consumer_services.aspx";
        //?service=2&cust_id=1&pan="+Uri.encode(pan)+"&ex_date="+Uri.encode(exdate)+"&
        // ipin="+Uri.encode(text)+"&uuid="+Uri.encode(uuid)+"&p_key="+pk;
        rittal.log("pubKeyValue",rittal.getValueOfKey("pubKeyValue"));
        rittal.log("URL",url);
        //  rittal.log("ipin",ipin_);

        final String service = "16";
        final String op_id = "";

//        public void generateVoucherKnown()
//        {
//            string cust_id = sData.customerID;
//            string pan = sData.fromCard;
//            string enc_ipin = sData.ipin;
//            string expdate = sData.expiryDate;
//
//            string amount = sData.tranAmount;
//
//            string uuid = sData.uuid;
//
//            string senderName = sData.senderName;
//            string senderIdType = sData.senderIdType;
//            string senderId = sData.senderId;
//            string senderPhone = sData.senderPhone;
//
//            string receiverName = sData.receiverName;
//            string receiverPhoneNo = sData.receiverPhone;
//            string receiverIdType = sData.receiverIdType;
//            string receiverId = sData.receiverId;
//            string voucherKey = sData.voucherNumber;
//            //string result = cust_id + "-" + pan + "-" + amount;
//            Consumer.Service1SoapClient web_service = new Consumer.Service1SoapClient();
//            String result = web_service.generateVoucherKnown(cust_id,pan,enc_ipin,expdate,amount,uuid,
//                    receiverPhoneNo,senderName,senderIdType,senderId, senderPhone,
//                    receiverName,receiverIdType,receiverId,
//                    voucherKey);
//            Response.Write(result);
//            Response.Flush();
//        }
        showprogres();
        JSONObject js = new JSONObject();
        try {
            js.put("service",service);
            js.put("customerID",rittal.getValueOfKey("user_id"));
            js.put("voucherNumber",voucher_number);
//            js.put("fromCard",pan);
//            js.put("expiryDate",exdate);
//            js.put("ipin",ipin_);
//            js.put("uuid",uuid);
//            js.put("tranAmount",amount);
//            js.put("publicKey",pk);
//            put sender information to josn
//            js.put("senderName",s_name);
//            js.put("senderIdType",s_id_type);
//            js.put("senderId",s_id);
//            js.put("senderPhone",s_phone);
////          put receiver information to josn
//            js.put("receiverName",r_name);
              js.put("receiverPhone",r_phone);
//            js.put("receiverIdType",r_id_type);
              js.put("receiverId",r_id);
              js.put("voucherPassword",voucher_key);
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
                        String transactionSource = "ACCOUNT";

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
        et_voucher_num                = (EditText) findViewById(R.id.et_voucher_num);
        et_receiver_phone_num           = (EditText) findViewById(R.id.et_receiver_phone);
        et_receiver_id             = (EditText) findViewById(R.id.et_receiverID);
        et_voucher_password             = (EditText) findViewById(R.id.et_voucherCode);
        btn_send                   = (Button) findViewById(R.id.btn_voucher_cashout);

     //   rittal.myCards(adapter,getSupportFragmentManager(),switch_use_saved_card,sp_cards,pager,et_card_pan,et_card_expiry_date);

        toolbar.setTitle(getString(R.string.known_generate_voucher_cashout));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String voucher_num          = et_voucher_num.getText().toString().trim();
                String r_id              =     et_receiver_id.getText().toString().trim();
                String r_phone           = et_receiver_phone_num.getText().toString().trim();
                String voucher_key       = et_voucher_password.getText().toString().trim();

                if(validation(voucher_num,r_phone,r_id,voucher_key)){
                    clearForm();
                    knownGenerateVoucherCashout(voucher_num,r_id,r_phone,voucher_key);
                }
            }
        });

    }

    public void clearForm(){
        et_voucher_num.setText("");
        et_voucher_password.setText("");
        et_receiver_id.setText("");
        et_receiver_phone_num.setText("");
    }

    public Boolean validation(String voucher_number,String r_id,String r_phone,String voucher_key){
        if(voucher_number.length() == 0 || r_id.length() == 0 || r_phone.length()==0|| voucher_key.length()==0){
            if(voucher_number.length() == 0){ rittal.rittalToast(getString(R.string.enter_voucher_num),"1","w");}
            if(r_id.length() == 0){rittal.rittalToast(getString(R.string.enter_receiver_id),"1","w");}
            if(r_phone.length() == 0){rittal.rittalToast(getString(R.string.enter_receiver_number),"1","w");}
            if(voucher_key.length() == 0){rittal.rittalToast(getString(R.string.enter_voucher_key),"1","w");}
            return false;

        }else{
            if(voucher_number.length() == 6) {
                if (r_id.length() != 0) {
                    if (r_phone.length() != 0) {
                        if (voucher_key.length() != 0) {
                            return true;
                        } else {
                            rittal.rittalToast(getString(R.string.enter_voucher_key), "1", "w");
                            return false;
                        }
                    } else {
                        rittal.rittalToast(getString(R.string.enter_receiver_number), "1", "w");
                        return false;
                    }
                } else {
                    rittal.rittalToast(getString(R.string.enter_receiver_id), "1", "w");
                    return false;
                }
            } else{
                rittal.rittalToast(getString(R.string.enter_voucher_password),"1","w");
                return false;
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

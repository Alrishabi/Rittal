package sd.rittal.app.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sd.rittal.app.AppApplication;
import sd.rittal.app.R;
import sd.rittal.app.helper.DataStorage;
import sd.rittal.app.helper.FormSession;
import sd.rittal.app.helper.LoginSession;
import sd.rittal.app.helper.Rittal;

public class RegistrationActivity extends AppCompatActivity implements FormSession,LoginSession {
    String TAG = LoginActivity.class.getSimpleName().toUpperCase();
    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;
    Bundle extras;
    AppApplication application;
    ProgressDialog progressDialog;
    Dialog dialog;

    Toolbar toolbar;

    List<String> quList;

    EditText et_user_name,et_full_name,et_user_password,et_confirm_user_password,et_phone_number,et_answer,et_invitation_code;
    Spinner sp_security_qu;
    Button btn_register;
    CheckBox cb_terms,cb_invitation;
    RelativeLayout Rl_invitation;
    String is_invited="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        quList = new ArrayList<String>();

        quList.add(getString(R.string.select_question));
        quList.add(getString(R.string.best_friend));

        initiateContext();
        initiateLayout();
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
        et_user_name        = (EditText) findViewById(R.id.et_user_name);
        et_full_name        = (EditText) findViewById(R.id.et_full_name);
        et_phone_number     = (EditText) findViewById(R.id.et_phone_number);
        et_user_password    = (EditText) findViewById(R.id.et_user_password);
        et_invitation_code    = (EditText) findViewById(R.id.et_invitation_code);
        et_confirm_user_password    = (EditText) findViewById(R.id.et_confirm_user_password);
        et_answer            = (EditText) findViewById(R.id.et_answer_security_qu);
        sp_security_qu       = (Spinner) findViewById(R.id.sp_security_qu);
        cb_terms              = (CheckBox) findViewById(R.id.cb_terms);
        cb_invitation         = (CheckBox) findViewById(R.id.cb_invitation);
        Rl_invitation         = (RelativeLayout) findViewById(R.id.Rl_invitation);

        btn_register            = (Button) findViewById(R.id.btn_register);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

     //  toolbar.setTitle(getString(R.string.registration));
        cb_invitation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (cb_invitation.isChecked())
                {
                    //Case 1
                    Rl_invitation.setVisibility(View.VISIBLE);
                    is_invited ="1";
                }

                if(!cb_invitation.isChecked()){
                    //case 2
                    Rl_invitation.setVisibility(View.GONE);
                    is_invited ="0";
                }
            }
        });
//            if(cb_invitation.isChecked()){
//                Rl_invitation.setVisibility(View.VISIBLE);
//            }

     //       setSupportActionBar(toolbar);
     //        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
     //        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, quList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        sp_security_qu.setAdapter(dataAdapter);

        toolbar.setTitle(getString(R.string.registration));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_name        = et_user_name.getText().toString().trim();
                String full_name        = et_full_name.getText().toString().trim();
                String phone            = et_phone_number.getText().toString().trim();
                String password         = et_user_password.getText().toString().trim();
                String confirm_password = et_confirm_user_password.getText().toString().trim();
                String answer           = et_answer.getText().toString().trim();
                String invitation_code  = et_invitation_code.getText().toString().trim();

                if(validation(user_name,full_name,phone,password,confirm_password,answer,invitation_code)){
                    rittal.log("is_invited", "is "+is_invited);
                    rittal.log("invitation_code",invitation_code);
                    registration(user_name,full_name,phone,password,answer,is_invited,invitation_code);
                }
               }
        });
        }
    public void clearForm() {
        et_user_name.setText("");
        et_full_name.setText("");
        et_phone_number.setText("");
        et_user_password.setText("");
        et_confirm_user_password.setText("");
        sp_security_qu.setSelection(0);
        et_answer.setText("");
    }
    public boolean validation(String user_name, String full_name, String phone, String password, String confirm_password,String answer,String invitation_code){
        if(full_name.length() == 0 || user_name.length() == 0||  phone.length() == 0|| password.length() == 0 ||
                confirm_password.length() == 0 || sp_security_qu.getSelectedItemId() == 0 || answer.length() == 0 )
        {
            if(full_name.length() == 0){
                rittal.rittalToast(getString(R.string.please_enter_full_name),"1","w");
                return false;
            }
            if(user_name.length() == 0){
                rittal.rittalToast(getString(R.string.please_enter_user_name),"1","w");
                return false;
            }
            if(phone.length() == 0){
                rittal.rittalToast(getString(R.string.please_enter_phone),"1","w");
                return false;
            }
            if(password.length() == 0){
                rittal.rittalToast(getString(R.string.please_enter_password),"1","w");
                return false;
            }
            if(confirm_password.length() == 0){
                rittal.rittalToast(getString(R.string.please_enter_confirm_password),"1","w");
                return false;
            }
            if(sp_security_qu.getSelectedItemId()==0){
                rittal.rittalToast(getString(R.string.please_select_quesition),"1","w");
                return false;
            }
            if(answer.length() == 0){
                rittal.rittalToast(getString(R.string.please_enter_answer),"1","w");
                return false;
            }
            return false;
        }else{
            String text_full_name [] = full_name.split(" ");

            if(text_full_name.length >= 2){
                if(user_name.length() >= 4){
                    if(phone.length() == 10){
                        if(password.length() >= 8){
                            if(password.equals(confirm_password)){
                                if(cb_invitation.isChecked()){
                                    if(invitation_code.length() >= 6 ){
                                        return true;
                                    }else {
                                        rittal.rittalToast(getString(R.string.fill_invition_code),"1","w");
                                        return false;
                                    }
                                }else{
                                    if(cb_terms.isChecked() ){
                                        return true;
                                    }else {
                                        rittal.rittalToast(getString(R.string.agree_with_condtion),"1","w");
                                        return false;
                                    }
                                }

                            }else {
                                rittal.rittalToast(getString(R.string.password_not_match),"1","w");
                                return false;
                            }

                        }else {
                            rittal.rittalToast(getString(R.string.password_eate_characters),"1","w");
                            return false;
                        }

                    }else {
                        rittal.rittalToast(getString(R.string.phone_number_lenght),"1","w");
                        return false;
                    }
                }else {
                    rittal.rittalToast(getString(R.string.please_enter_user_name),"1","w");
                    return false;
                }
            }else{
                rittal.rittalToast(getString(R.string.full_name_atleast),"1","w");
                return false;
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
    private void registration(final String user_name,final String full_name, final String phone, final String password,final String answer,final String is_invited,final String invitation_code) {
        RequestQueue rq = Volley.newRequestQueue(this);

        String url = application.getBaseUrl()+"/NewReg_Cust.aspx";
        rittal.log("URL",url);
        showprogres();

        JSONObject js = new JSONObject();
        try {
            js.put("version","1");
        } catch (JSONException e) {
            e.printStackTrace();
            rittal.log("Object Ex",e.toString());
        }

        rittal.log("is_invited", is_invited);
        rittal.log("invitation_code",invitation_code);
        StringRequest req=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                rittal.log("Response",response);
                try {
                    String text [] = response.split("#");
                    if(text[0].equals("valid")){
                        rittal.rittalToast(getString(R.string.wellcome)+" "+full_name +" "+getString(R.string.registration_successfully) ,"1","s");
                        Intent i = new Intent(activity,LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    }else {
                        if(text[1].equals("1")){
                            rittal.rittalToast(getString(R.string.phone_number_was_existing_befor),"1","w");
                        }else if(text[1].equals("2")){
                            rittal.rittalToast(getString(R.string.user_name_was_existing_befor),"1","w");
                        }else if(text[1].equals("6")){
                            rittal.rittalToast(getString(R.string.check_the_invitaion_code),"1","w");
                        }else{
                            rittal.rittalToast(getString(R.string.some_thing_error_contact_administrator),"1","w");
                        }
                    }
                }catch (Exception e){
                    rittal.rittalToast(getString(R.string.error_in_data_processing),"1","w");
                }
                dismsprogres();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                rittal.log("Response",error.toString());
                rittal.rittalToast(getString(R.string.error_on_network_connection),"1","w");
                dismsprogres();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("acc_type","MobileApp");
                params.put("acc_name",full_name);
                params.put("full_name",full_name);
                params.put("address","");
                params.put("phone_no",phone);
                params.put("meter_no","");
                params.put("user_name",user_name);
                params.put("pwd",answer);
                params.put("sms_code", "");
                params.put("id_type", "");
                params.put("id_no", "");
                params.put("market_name", "");
                params.put("p_phone_no", "");
                params.put("dealer_id", "9");
                params.put("enc_pwd", password);

                params.put("invitation_code", invitation_code.trim());
                params.put("is_invited", is_invited.trim());

                rittal.log("is_invited","is ->"+ is_invited.trim());
                rittal.log("invitation_code","is ->"+invitation_code);

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

                //rittal.logOut();

            }
        });

    }

    public void goToTermsAndCondition(View view) {
        Intent i = new Intent(activity,TermsAndConditionActivity.class);
        startActivity(i);
    }

}

/*
invalid#4#System.FormatException: Input string was not in a correct format.
        at System.Number.StringToNumber(String str, NumberStyles options, NumberBuffer& number,
        NumberFormatInfo info, Boolean parseDecimal)
        at System.Number.ParseInt32(String s, NumberStyles style, NumberFormatInfo info)
       at MobileServer.Reg_Cust.Page_Load(Object sender, EventArgs e)
       in C:\Users\Administrator\Desktop\Mobile App Middleware\MobileServer\MobileServer\Reg_Cust.aspx.cs:line 37#


 */
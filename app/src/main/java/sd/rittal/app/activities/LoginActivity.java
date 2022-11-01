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
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sd.rittal.app.AppApplication;
import sd.rittal.app.R;
import sd.rittal.app.helper.DataStorage;
import sd.rittal.app.helper.FormSession;
import sd.rittal.app.helper.LoginSession;
import sd.rittal.app.helper.Rittal;

public class LoginActivity extends AppCompatActivity implements FormSession,LoginSession {

    String TAG = LoginActivity.class.getSimpleName().toUpperCase();
    Activity activity;
    Rittal rittal;
//    TextView tv_show,tv_hide;
    ImageView tv_show,tv_hide;
    DataStorage dataStorage;
    Bundle extras;
    AppApplication application;
    ProgressDialog progressDialog;
    Dialog dialog;

    Toolbar toolbar;

    EditText et_user_name,et_user_password;
    Button bt_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        et_user_password    = (EditText) findViewById(R.id.et_user_password);
        tv_hide             =  findViewById(R.id.tv_hide);
        tv_show              =  findViewById(R.id.tv_show);

        bt_login    = (Button) findViewById(R.id.bt_login);
        toolbar         = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(getString(R.string.login));
        et_user_name.setText(rittal.getValueOfKey("login_name"));
        et_user_name.requestFocus();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tv_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_user_password.setTransformationMethod(null);
                tv_show.setVisibility(View.GONE);
                tv_hide.setVisibility(View.VISIBLE);
            }
        });
        tv_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_user_password.setTransformationMethod(new PasswordTransformationMethod());
                tv_show.setVisibility(View.VISIBLE);
                tv_hide.setVisibility(View.GONE);
            }
        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_name = et_user_name.getText().toString().trim();
                String password = et_user_password.getText().toString().trim();
                if(validation(user_name,password)){
                    login(user_name,password,"0");
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
    private void login(final String user_name,final String password, final String type) {
        RequestQueue rq = Volley.newRequestQueue(this);

        String url = application.getBaseUrl()+"/Auth_user.aspx";
        rittal.log("URL",url);
        showprogres();

        JSONObject js = new JSONObject();
        try {
            js.put("version","1");
        } catch (JSONException e) {
            e.printStackTrace();
            rittal.log("Object Ex",e.toString());
        }
        StringRequest req=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                rittal.log("Response",response);
                try {
                    String text [] = response.split("#");
                    if(text[0].equals("valid")){
                        rittal.setValueToKey("is_login","1");
                        rittal.setValueToKey("user_id",text[2]);
                        rittal.setValueToKey("user_name",text[3]);
                        rittal.setValueToKey("user_code",text[4]);
                        dataStorage.add_card("Rittal-C",text[2],"ACCC","1");
                        rittal.setValueToKey("login_name",et_user_name.getText().toString().trim());
                        Intent i = new Intent(activity,ProfileActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    }else {

                        rittal.rittalToast(getString(R.string.user_name_or_password_was_incorect),"1","w");
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
                params.put("uname",user_name);
                params.put("upass",password);
                params.put("utype", type);
                return params;
            }
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
//                return params;
//            }

        };
        req.setRetryPolicy(new DefaultRetryPolicy(
                AppApplication.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        req.setShouldCache(false);
        AppApplication.getInstance().addToRequestQueue(req);
    }
    public boolean validation(String user_name,String password){
        if(user_name.length() == 0 || password.length() == 0){
            if(user_name.length() == 0){
                rittal.rittalToast(getString(R.string.please_enter_user_name),"1","w");
                return false;
            }else if(password.length() == 0){
                rittal.rittalToast(getString(R.string.please_enter_password),"1","w");
                return false;
            }else {
                rittal.rittalToast(getString(R.string.user_name_or_password_was_empty),"1","w");
                return  false;
            }

        }else {
            return true;
        }
    }
    public void goToRegistration(View view) {
        Intent i = new Intent(activity,RegistrationActivity.class);
        startActivity(i);
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

                //clearForm();

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
    public void goToResetPass(View view) {
        Intent i = new Intent(activity,ResetPassowrdActivity.class);
        startActivity(i);
    }

}

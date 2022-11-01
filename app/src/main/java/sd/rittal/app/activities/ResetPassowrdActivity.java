package sd.rittal.app.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

import sd.rittal.app.AppApplication;
import sd.rittal.app.R;
import sd.rittal.app.helper.DataStorage;
import sd.rittal.app.helper.Rittal;

public class ResetPassowrdActivity extends AppCompatActivity {
    String TAG = LoginActivity.class.getSimpleName().toUpperCase();
    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;
    Bundle extras;
    AppApplication application;
    ProgressDialog progressDialog;
    Dialog dialog;
    String    lang = "";
    List<String> quList;

    Toolbar toolbar;

    EditText et_user_password,et_confirm_user_password,et_current_user_pass,et_user_name,et_answer;
    Button btn_reset_pass;
    Spinner sp_security_qu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_passowrd);
        quList = new ArrayList<String>();

        quList.add(getString(R.string.select_question));
        quList.add(getString(R.string.old_password));
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

        dialog          = new Dialog(activity, R.style.DialogTheme);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        lang = Resources.getSystem().getConfiguration().locale.getLanguage();

    }
    private void initiateLayout(){
        et_answer   = (EditText) findViewById(R.id.et_answer_security_qu);
        sp_security_qu   = (Spinner) findViewById(R.id.sp_security_qu);

        et_user_name   = (EditText) findViewById(R.id.et_user_name);
        et_user_password          = (EditText) findViewById(R.id.et_user_password);
        et_confirm_user_password    = (EditText) findViewById(R.id.et_confirm_user_password);
        btn_reset_pass            = (Button) findViewById(R.id.btn_reset);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(getString(R.string.reset_pass));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, quList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        sp_security_qu.setAdapter(dataAdapter);

        btn_reset_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String answer            = et_answer.getText().toString().trim();
                String userName          = et_user_name.getText().toString().trim();
                String password          = et_user_password.getText().toString().trim();
                String confirm_password  = et_confirm_user_password.getText().toString().trim();
                if(validation(userName,password,confirm_password,answer)){
                resetPassword(userName,answer,password,confirm_password);
                }
            }
        });

    }

    private void resetPassword(final String user_name, final String answer,final String password,final String confirmPassword) {

        String url = application.getBaseUrl()+"/consumer_services.aspx";
        showprogres();
        JSONObject js = new JSONObject();
        try {
            js.put("service","17");
            js.put("UserName",user_name);
            js.put("OldPassword",answer);
            js.put("NewPassword", password);
            js.put("NewPasswordConfirm", confirmPassword);
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
                        try {
                            if(response.getString("responseCode").equals("0")){
                                if(lang.equals("ar")) {
//                                    {"responseCode":0,"responseMessage":"Successfull","responseMessageArabic":"تمت العملية بنجاح"}
                                    rittal.rittalToast(response.getString("responseMessageArabic"), "1", "s");
                                }else {
//                                    {"responseCode":0,"responseMessage":"Successfull","responseMessageArabic":"تمت العملية بنجاح"}
                                    rittal.rittalToast(response.getString("responseMessage"), "1", "s");
                                }
                                Intent i = new Intent(activity, LoginActivity.class);
                                startActivity(i);
                                finish();
                            }else{
                                if(lang.equals("ar")) {
                                    rittal.rittalToast(response.getString("responseMessageArabic"),"1","w");
                                }else{
                                    rittal.rittalToast(response.getString("responseMessage"),"1","w");
                                }
                            }

                        }catch (Exception e){
                            rittal.log("TAG Exception", response.toString());
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

    public void clearForm() {
        et_current_user_pass.setText("");
        et_user_password.setText("");
        et_confirm_user_password.setText("");
    }

    public boolean validation(String user_name,
                              String password,
                              String confirm_password,
                              String answer){
        if(user_name.length() == 0 || password.length() == 0 ||password.length()<8|| confirm_password.length() == 0 ||!password.equals(confirm_password)|| sp_security_qu.getSelectedItemId()==0 || answer.length()==0){

            if(user_name.length() == 0){
                rittal.rittalToast(getString(R.string.please_enter_user_name),"1","w");
                return false;
            }
            if(password.length() == 0){
                rittal.rittalToast(getString(R.string.please_enter_password),"1","w");
                return false;
            }
            if(password.length() <8){
                rittal.rittalToast(getString(R.string.password_eate_characters),"1","w");
                return false;
            }
            if(confirm_password.length()== 0){
                rittal.rittalToast(getString(R.string.please_enter_confirm_password),"1","w");
                return false;
            } if(!password.equals(confirm_password)){
                rittal.rittalToast(getString(R.string.please_check_passwords),"1","w");
                return false;
            } if(sp_security_qu.getSelectedItemId()==0){
                rittal.rittalToast(getString(R.string.please_select_quesition),"1","w");
                return false;
            } if(answer.length() == 0){
                rittal.rittalToast(getString(R.string.please_enter_answer),"1","w");
                return false;
            }
            return false;

        }else {
            return true;
        }
    }

}

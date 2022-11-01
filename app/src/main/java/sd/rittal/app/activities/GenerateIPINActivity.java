package sd.rittal.app.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import sd.rittal.app.AppApplication;
import sd.rittal.app.R;
import sd.rittal.app.helper.DataStorage;
import sd.rittal.app.helper.IPINBlockGenerator;
import sd.rittal.app.helper.FormSession;
import sd.rittal.app.helper.LoginSession;
import sd.rittal.app.helper.ResponseDialog;
import sd.rittal.app.helper.Rittal;


public class GenerateIPINActivity extends AppCompatActivity implements FormSession,LoginSession {

    String TAG = GenerateIPINActivity.class.getSimpleName().toUpperCase();

    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;
    AppApplication application;
    Dialog dialog;
    Button btn_generate_otp;

    ProgressDialog progressDialog;
    Toolbar toolbar;


    EditText et_card_pan,et_card_expiry_date,et_ipin,et_confirm_ipin,et_otp;

    String lang = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_ipin);
        initContext();
        initLayout();

        ( (AppApplication)getApplication()).registerSessionListener(this);
        ( (AppApplication)getApplication()).startUserSession();

        rittal.log("pubKeyValue",rittal.getValueOfKey("pubKeyValue"));

        rittal.setValueToKey("pubKeyValue","");
        lang = Resources.getSystem().getConfiguration().locale.getLanguage();
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

    public void initContext(){
        activity = this;
        rittal = new Rittal(activity);
        dataStorage = new DataStorage(activity);
        application = new AppApplication();

        dialog = new Dialog(activity, R.style.DialogTheme);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
    }


    public void initLayout(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        et_card_pan = (EditText) findViewById(R.id.et_card_pan);
        et_card_expiry_date = (EditText) findViewById(R.id.et_card_expiry_date);
        et_ipin = (EditText) findViewById(R.id.et_ipin);
        et_confirm_ipin = (EditText) findViewById(R.id.et_confirm_ipin);
        et_otp = (EditText) findViewById(R.id.et_otp);
        btn_generate_otp = (Button) findViewById(R.id.generate);

        toolbar.setTitle(getString(R.string.generate_ipin));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
        btn_generate_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pan          = et_card_pan.getText().toString().replace(" ","").toString();
                String exdate       = et_card_expiry_date.getText().toString().replace("/","").trim();
                String ipin         = et_ipin.getText().toString().trim();
                String confirm_ipin = et_confirm_ipin.getText().toString().trim();
                String otp          = et_otp.getText().toString().trim();

                if(validation(pan,exdate,ipin,confirm_ipin,otp)){

                    //final String uuid = UUID.randomUUID().toString();

//                    String otp_ = "";
//                    String pk = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjqmUrQ1w1rOH+kmj4dk3C3duIl1trVRr91EJKhphlWjOs2qqba9I2WmRH3CVUuBmkIEojYrnKtXiQBqMOTANzZuly6vZY3AOT/JVDtKy3/z60mUWUi+ILidh5dZz7ZYKsPupXRz5XQXELLpI4XN58N1oiNY95GutJ/hjqE0qqufQ1ie/hU5Dbof9F+Ug3wSJOzba+IVve09Yx76KsFBtc+0qn2/JI7YPca9s8h4ZKaVo7qvFZNScTieoR5f3vaKUsYNTdRK7HMXEU/BMrg8hzh04+ShtDtKtQ02pvObQ+Kha/oV54bdP5e6m9NpXupoL0ydQIDNZ6B76yVEe5WJM2wIDAQAB";
//                    otp_ = IPINBlockGenerator.getIPINBlock(otp,pk,uuid);
//                    String ipin_ = "";
//                    ipin_ = IPINBlockGenerator.getIPINBlock(ipin,pk,uuid);
//                    ipin_ = IPINBlockGenerator.getIPINBlock(ipin,"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==",uuid);;
//                    String pass = IPINBlockGenerator.getIPINBlock("Rittal@123",pk,uuid);
//
//                    JSONObject js = new JSONObject();
//                    try {
//                        js.put("cust_id","1");
//                        js.put("pan",pan);
//                        js.put("expiry_date",exdate);
//                        js.put("ipin",ipin_);
//                        js.put("otp",otp_);
//                        js.put("uuid", uuid);
//                        js.put("version","1");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        rittal.log("Object Ex",e.toString());
//                    }
//                    rittal.log("OBJ",js.toString());
                    generateIpin(pan,exdate,ipin,otp);

                }
            }
        });
    }

    public Boolean validation(String pan, String expiry_date, String ipin ,String confirm_ipin,String otp){
        if(pan.length() == 0 || expiry_date.length() == 0 || ipin.length() == 0){
            if(pan.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pan),"1","w");}
            if(expiry_date.length() == 0){rittal.rittalToast(getString(R.string.enter_card_expiry_date),"1","w");}
            if(ipin.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pin),"1","w");}
            if(confirm_ipin.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pin_confirm),"1","w");}
            if(otp.length() == 0){rittal.rittalToast(getString(R.string.enter_otp),"1","w");}
            return false;

        }else{
            if(pan.length() == 16 || pan.length() == 19){
                if(expiry_date.length() == 4){
                    if(ipin.length() == 4){
                        if(confirm_ipin.length() == 4){

                            if(otp.length() == 4){

                                return true;
                            }else {
                                rittal.rittalToast(getString(R.string.card_otp_lengh),"1","w");
                                return false;
                            }
                        }else {
                            rittal.rittalToast(getString(R.string.card_ipin_confirm_lengh),"1","w");
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

    public void goToGeneratOTP(View v){
        Intent i = new Intent(activity,GenerateOTPActivit.class);
        startActivity(i);
        finish();
    }

    private void generateIpin(final String pan , final String exdate , String ipin, String otp) {

        et_card_pan.setText("");
        et_card_expiry_date.setText("");
        et_ipin.setText("");
        et_confirm_ipin.setText("");
        et_otp.setText("");

        final String uuid = UUID.randomUUID().toString();

        String otp_ = "";
        String pk =application.getPublicKeyForIPIN();// "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjqmUrQ1w1rOH+kmj4dk3C3duIl1trVRr91EJKhphlWjOs2qqba9I2WmRH3CVUuBmkIEojYrnKtXiQBqMOTANzZuly6vZY3AOT/JVDtKy3/z60mUWUi+ILidh5dZz7ZYKsPupXRz5XQXELLpI4XN58N1oiNY95GutJ/hjqE0qqufQ1ie/hU5Dbof9F+Ug3wSJOzba+IVve09Yx76KsFBtc+0qn2/JI7YPca9s8h4ZKaVo7qvFZNScTieoR5f3vaKUsYNTdRK7HMXEU/BMrg8hzh04+ShtDtKtQ02pvObQ+Kha/oV54bdP5e6m9NpXupoL0ydQIDNZ6B76yVEe5WJM2wIDAQAB";//rittal.getValueOfKey("pubKeyValue");// "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjqmUrQ1w1rOH+kmj4dk3C3duIl1trVRr91EJKhphlWjOs2qqba9I2WmRH3CVUuBmkIEojYrnKtXiQBqMOTANzZuly6vZY3AOT/JVDtKy3/z60mUWUi+ILidh5dZz7ZYKsPupXRz5XQXELLpI4XN58N1oiNY95GutJ/hjqE0qqufQ1ie/hU5Dbof9F+Ug3wSJOzba+IVve09Yx76KsFBtc+0qn2/JI7YPca9s8h4ZKaVo7qvFZNScTieoR5f3vaKUsYNTdRK7HMXEU/BMrg8hzh04+ShtDtKtQ02pvObQ+Kha/oV54bdP5e6m9NpXupoL0ydQIDNZ6B76yVEe5WJM2wIDAQAB";
         otp_ = IPINBlockGenerator.getIPINBlock(otp,pk,uuid);
         String ipin_ = "";
         ipin_ = IPINBlockGenerator.getIPINBlock(ipin,pk,uuid);
        String text = ipin_+"#A#A"+otp_+"#A#A"+uuid;
//         ipin_ = IPINBlockGenerator.getIPINBlock(ipin,"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==",uuid);;
        //String pass = IPINBlockGenerator.getIPINBlock("Rittal@123",pk,uuid);
        String url = application.getBaseUrl()+"/consumer_services.aspx";
        //String url = application.getBaseUrl()+"/generateIPinCompletionRequest.aspx?cust_id=1&pan="+pan+"&ex_date="+exdate+"&ipin="+ Uri.encode(text);//"&otp="+otp_+"&uuid="+uuid+"&pkey="+pass;
        rittal.log("pubKeyValue",rittal.getValueOfKey("pubKeyValue"));
        rittal.log("URL",url);
        rittal.log("otp",otp_);
        rittal.log("ipin",ipin_);
        final String step = "2";

        final String service = "1";
        showprogres();
        JSONObject js = new JSONObject();
        try {
            js.put("customerID",rittal.getValueOfKey("user_id"));
            js.put("fromCard",pan);
            js.put("expiryDate",exdate);
            js.put("ipin",ipin_);
            js.put("OTP",otp_);
            js.put("uuid", uuid);
            js.put("step", step);
            js.put("service", service);
            js.put("version","1");
        } catch (JSONException e) {
            e.printStackTrace();
            rittal.log("Object Ex",e.toString());
        }

        rittal.log("OBJ",js.toString());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, js,

                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        rittal.log("TAG Response", response.toString());
                        ResponseDialog responseDialog = new ResponseDialog(activity,dialog,response,service,step,"","","","","","",null,"");
                        responseDialog.show();
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

    public void clearForm(){
        et_card_pan.setText("");
        et_card_expiry_date.setText("");
        et_ipin.setText("");
        et_confirm_ipin.setText("");
        et_otp.setText("");

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
https://mob.rittal-sd.com/generateIPinCompletionRequest.aspx?
cust_id=1&
pan=5818588880475558&
ex_date=2107&
ipin=R9YuhYvdyEWq7Lp955Ig0smOTnON8iDL/8ZjUhM3R9WHT7hp1Zl6qOJdt3iqbtETRbAzj6HoUG+niM4wfsxWzb43WWtZ37a35sh4/2opzd0ZE9N4kJI8kIroqusXf8O7g41VqXaaK3EFCZOA4/EICnC2AQL1n+5I5iI//ssRIZOgIgAbArqaF3R5AnJ12Z90Kzmlinv25qt0kpCGg61az+vtuFWByKT6Ze6Uj75dOc7HDq2RBfG5Ph0UNmsGCm7r2X8fxyj7RRs36xfswyPUGGbqk41Rjv1EkAxx/fMBLF/AhoIQPa8OfaCBrjgUtzHxEzbDj/yp5oblX7iJB44UzQ==&
otp=A9qMdms//bbmEFu053n5lQTUF7FKdrD3ILfcC8/HDL40I0FmFpSVhj+ba2Y0q6PycBwpfm2m4Oh33RCzhSv8BfMBGjXudLCpwtapm0S9mHGiaNyKKrkov8Fd8nQUvx/1lxFFNHB0WZJrgQIa6aLNDppLlrx2OvIfPsEZoW30D4nxPH1rRvz0JpVGGAcPd97r3zu+rzclybogK9L/8iRykwWSAQYH6t7XHJ0ZdzPWTzmvV7A4WuLVK2vUCamxO+yhwU5b7sjFnpjq2Qli7OHN8SIPg1nAIqhOdI9lklZO6acREYuNlHiaGffP6/xdi82Nrq1HPcNMnpVUWcfoJrgMnQ==&
uuid=64ff414a-a6d4-463a-a817-214de55ea849&
pkey=MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjqmUrQ1w1rOH+kmj4dk3C3duIl1trVRr91EJKhphlWjOs2qqba9I2WmRH3CVUuBmkIEojYrnKtXiQBqMOTANzZuly6vZY3AOT/JVDtKy3/z60mUWUi+ILidh5dZz7ZYKsPupXRz5XQXELLpI4XN58N1oiNY95GutJ/hjqE0qqufQ1ie/hU5Dbof9F+Ug3wSJOzba+IVve09Yx76KsFBtc+0qn2/JI7YPca9s8h4ZKaVo7qvFZNScTieoR5f3vaKUsYNTdRK7HMXEU/BMrg8hzh04+ShtDtKtQ02pvObQ+Kha/oV54bdP5e6m9NpXupoL0ydQIDNZ6B76yVEe5WJM2wIDAQAB
pkey=MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjqmUrQ1w1rOH+kmj4dk3C3duIl1trVRr91EJKhphlWjOs2qqba9I2WmRH3CVUuBmkIEojYrnKtXiQBqMOTANzZuly6vZY3AOT/JVDtKy3/z60mUWUi+ILidh5dZz7ZYKsPupXRz5XQXELLpI4XN58N1oiNY95GutJ/hjqE0qqufQ1ie/hU5Dbof9F+Ug3wSJOzba+IVve09Yx76KsFBtc+0qn2/JI7YPca9s8h4ZKaVo7qvFZNScTieoR5f3vaKUsYNTdRK7HMXEU/BMrg8hzh04+ShtDtKtQ02pvObQ+Kha/oV54bdP5e6m9NpXupoL0ydQIDNZ6B76yVEe5WJM2wIDAQAB


https://mob.rittal-sd.com/generateIPinCompletionRequest.aspx?cust_id=1&
pan=5818588880475558&
ex_date=2107&
ipin=ZWnV9FWRfg8NmrhkpK4ju81L1XEMcfk4TJgHcmO2IgzN6RZfZXu40uNrPoejKowK+DV8IXdVd1XbAen4i51i7uJFVEd03qxxcZQJNRjgsL3y6RdvBBW+QbM+qaCC3MQZo8HTAb03TumGEi6oY4AORC6Xg5TzdN1KwRHbdrkY2QT+h4lW966kdfuCMlsPJcxyUV9qhesNzY9i8bLyBlEM3Fu2yWDPLCuvLdxOMmDHrzd9ypaD1SsUkdeNpq8Lqao7B28cVPBhil4actvxy30Md9XpRFBV6laSZshMi/6xW8yrTgNLn8rujvLUw7OPR2d24A7+vZXlLkG9SL1GaycWeQ==&
otp=infTGF5E9jes5i6UTmpYvArNNdBoJOyjOJuzPj4AIo/vILbhKoay8iIDcpYu01o3vnfkUbRiv4e8vkEOpI8S4GSsxWSV94mOIhYwNBb+miuCwN3Lnt0Ux8ueSr9ak808tIXu1VdhLc7Itp63G5ltswydgDd2HHNpTdJ3nnozViTiacwfw+7q+VLV1OLPiWf6RU3TJAUnIb4cM4+utF4lAwdX5/f8o6VUKEdqHDeDwCUviutXcf7B2p8M/eICtAyOX7/2Xd0Go1pdNi+3r+m1j/RA+XL17XFrKMn5jwkCSXt4heppXxt61kvBXe+3wrqNkuVn1+SR32jutYD0gXG2/Q==&

uuid=5fa662a7-3c4f-49f6-9f0b-dde44ceaca4a&pkey=

PSK0hke6SV/01vCQn03x7UvZuuVDc2WyAdNI8s4WZQZdvyZbDB7z7O/hFE4fN6CmRT3XYnPZabhobF9RjHL7o/Ar8gWmmW0SHA7lJ8ph7GGdd9juJr9S1nU7ETTyg+dSgnEuiyhNIZxzncmTKkxETXQMzM3o1mo5xI0qBHD/+E75PgxWSvmLjus+VH6AHC3qREsJNBdC0jqbFldaTg8sgGNeFaSM62domoTrQW0VJRBML2/c1uyjvfbTiEHWSEXxR8yznSlkvh6kJ9cF08BYVG03K3KtBHUMcKfrB+F/zBwSua7rMf0IysRtQyK6EqOX9vnD+jMifnTveu5WG7EnkA==

https://mob.rittal-sd.com/generateIPinCompletionRequest.aspx?cust_id=1&pan=5818588880475558&ex_date=2107&

ipin=UpIxB3qZdq8qPypYZEXQF4APNeSy4hhSdK23GHD4EyONecKbTTINNPpCrX90kjAkE392nGwB/IHMHaVCODMuYc0NnG2al6lZzWZYVlRGrg8m4TLcPrBwTCOGqPBtO0eou8ql5w+9DYmdLTchprREBeJ9KvAfJfPU9pXCPEOpXZAjM0TklxYj6kTluAAeWCjnI25wCj70wJCq/JMzlUmebiV7mQnuy0p1+L1o4cOFuEMBzCGiT3JIpZlDXwvmtxsa0JEQb6jMFqWDjKE98uAAR8DmGP98YXLnYrODmu+dnvq3j33lEoPD0+d5agzZ4UtTXLZISto06ImKSurm7G7big==
otp=czggiSoRnWk9LP4nvgXWuq/harjGUIwpqHbyomBI6WCmMjC2J3HAFrWHKSxzOrk3uhdqz0N3QXOcSeem+u8ARbEcEzQ6h7VO0TaN23e6uNO3Yzu2Em64UWjPfioC54bPZHCtO659ahRs59Wn9gA5hEwlTLogBY+LAoVPH1mHIxFb4vPKd+O8SRHHV+f6Okowe8n5qPgHxHb9IoA986HSAfP7Sm7dLxNjvYlQ36phnfUgZDPCCVuhaNl11GTWYS6lQpghHTV/dnqSNXvETDVwGuk+PNJGLXFmDBkmPgKTQWgJvbasY6wJAXuw85zEKDLiSjzN9db/yAbIiPx+OE6P4Q==
uuid=c2b5b8b9-ea18-4839-a90b-4dc3d406b451
pkey=Nie+GN3xzi1plmwMEoW/toDzGgkLvN41JwJdrHGZqW6ah1PJNAakQlYe1hHDeCpieFVMehXgbBuHgCIzVTq+nYb1zTq8D+DtJYd5wPA+Lij7l/ohxUYs4jhey02HOeoIQk3OkRjC5LeYpw5iv78VxUca17qFwuOQ8RPpR2d7GCpE7Z6Urt9SnrkCCQQXRoDLHsNtQqrU+phg1Ka2bVJ/cdLsJqx0RKOdmPp3soyRbYEtrTZT44nK84slJwtprd5aNROJrxV35kcbUYNx+hnriK3+4THNkiJA7nacnMdCRHBApqdtGzskRimVV8IE1B7RhVbFiPa3ilg6n3z0ROY9pg==



https://mob.rittal-sd.com/generateIPinCompletionRequest.aspx?cust_id=1&pan=5818588880475558&ex_date=2107&

ipin=FDAcAg3xu0p012xMuxfPt/cTkSftQVGGSRd3afZpyL3fd/rv/3uPcjVw0mvb0fGRqoE7F7FhjgNGpjZzii1TNPesLiP4n/X+XkYqK41BrjBSfTYn8B8ToCZoIgvy1Q0qdAgVXSf3FyBVCxqIcxB6mM3rvwjwUZoGnJEge6jSdM9HQy7J8DAFWcMi37ERbBJ1/dpVUSuHZpItc1XLKWXHEoyIMDxrnnrKFIur5EB+lAZkkbUwzKw6BUK58oZVh0K7z9wOdDd6FWzJNzZmnP/U+yka9YPEnIdX37kOnSymNQ9BCyMn2IsYSy6gNjLBAa3R/JBTfcoESrWK20PtZRIrJw==
otp=X+5j86Ww7o/7RAH37U0NEzD9dAAamj0XwpLJJj9upiAX4earaFPp7wFkhk/Hc/hGyqx2+c/K1wFh/sE1L2QE5yXOXIiwy22KZlSjBmm9+pL+NfxImVMxmlnrYxytic+srIa8k6vO+UrDqA/qee5IUHRqWXUjHYOJuoH7EiFJWs2NrgFiTDxm9aNShnSh0pSq/qqrZ0l4h39tVuOUMWN/XMR51zjT1PEWlrP6XwYwwaNl0OLv2w73ZGkbAXtqzNenW/oAVkfl8X9EodM1SiE5bFFlADesbTiuAz3sXHx+SyIzAXYZ1RqrjpFDAQ9RJtcdfVA/yOnLMyIN26Y8zd+56A==
uuid=3b0f68e8-5432-4e7c-9614-392e243137a0

pkey=gZ9zpFlYU0qIEvGoINAHa29FhLXWNdynZ9INyvcceoRaIn0QbE9ZRvoNGR7cZVEzaoutz6gbl+Wy6+aXwo5Xi7VznzDYRHUSTq7eTkmsDA6GeFJ8YLA0N53Km2yI0G9Wxy3RHOTCv1eBZa8bXV3ysHsYfAI5y22WgyuA2yqiDz80yTsq7AaR+aGXI9b79FpdIHuWIp+xP74WY60pc86fsOaKj2MVk+CaLTDAInq7y4Brcxs+grGatGPuQp2gddEMCRG2eWi1IOAZdL/rJ7d6TY08pTzOKxR72ulTsBGdhObPkwbyaELtXuujEX3wkZzf0LsxGA/jnQ/xMB9gU5Deww==

 https://mob.rittal-sd.com/generateIPinCompletionRequest.aspx?cust_id=1&pan=5818588880475558&ex_date=2107&

 ipin=CTax8t2Sf6RhPOzuKsFYzs84bmaMxlIqr3ItvWXElpcgfmc9R1YvNK3H3QiZSoxxg1BRE3hwRMvZswoV1wZB8I5loJE2GZgeIAxZd7ZNtY+DSmV0VedxxNmzCTf6q1spn6I7E1LEVU864ZsiddSlgWZ6rsIg+dZX5CBKb3WhCtvEhgEAut3QHHWjKS7Pa7QykZ2CQ0SvGN7/l6cAA2+rTfQSLXTghvElorbjWLIjdR/zgb7j5jzWX1qBLcBOjzmZKNSoTvRQEvJ44Cjtbh9HYL7Z519QBcCO6uUG99umm6519q2aGS67mfbhxwWUOzHpqelssJZJLZIVD0mnMzMpiA==
 otp=T83DXpBFrJInCD/zHGKd0q5U3pv0WCXfoEoHrABjnvYyQ3uKx1nN4Zbr3+8TASWKExq/FTPqzwyxwYqeJpyTSAkSws9CY9Sj0xT3cnghx0jmpv48SXxsAUQTI3IN+eaW9ThJeusK6k+oCrHy/EVkpN57ISyJeq6q980uBQM0Wr6Ph11TE/DiHp7gY2tOm7bpqhO9t+ujutBFyk71X7RzvHWnqwasi+6jxy3/g4PatckolnpqRSMere/NU1Ncp2l4YezEUS94OZyPpW8mfQ5iU4+lxmeSgGCEq5+IhEEuB9yOUNHa8P+9i8eulCrkXKTVrvKeQAV7QMJPx4gM7DfE7Q==
 uuid=4648804b-3f4f-451a-9bfd-a718e448d67f
 pkey=dQv5THTJKgIDFHo78Ph1BE8FBMb+Oa0bQDBhdOJPkWArzrqcPqxB69e9C1RrW86PuqSepmc2jh1a2Ez2pGJZG9jhZKelax/viVjQqQq2BK+3WM1K3zecuVd9Mk4b6+7iJzDWZnALZvieWjSnt2ZgStukZW1H2PFnJTnNZ/mhPeCr66emyUhFB0qjrON6Z2XrDaPypiHn3VXbWfy+EDw8wUdyAwrtVOqwzT/lGpTHxm8VAXBBZEB7k30R8slKO1mIhttPuss42gybgx+U1UILyYBl/EVfbOaRL1zeUnr3S17PuL8KuQvnNHyMksPD8cS++cbKlOJ5ONLX0y5A523DEg==


https://mob.rittal-sd.com/generateIPinCompletionRequest.aspx?cust_id=1&
pan=5818588880475558&ex_date=2107&
ipin=Ugub5BkvyoHZUnxJ1jnHt/6OKgxOdn1oi4/dr2iD7K31Jk/9NzLl5J1OTEMt9etBkyRnHGcyfKn+Sjyh6ykdg5SupTOJVSSS3dnHZQpf7UHPOlAuJXXrhkS28FtO56mSRdDzzmXnOpX/Tvl9Ng/BAnmUTmQ4Kz0AXfWG2QmWONo2ncwyCgNljldzfqISfwPGTdAxi8SgdF4fH/a/BNuUksLEDQ4FaeMm0KqQT8v9OqJgs7sIVuPAjDvtpEsoN//6vPItbr6yUpqWVND1xYsIPwIqzorP4iS1Ci/0AZFz0pGgQ4nMIHyVstbdWrVFfLJrlwLhkOc8VsUAY8TZ4KSJIQ==#A#AVj5YzLQeai13fxnYVTGObXgWKLmZUCJ7lJIciWZQI4rEXd3YnIKYMVeid9mlwQuO0ko9t34IdP6DdqtKOzBV/OtrCB9dKPFUMyTLZPJ1aCpqorLwUGQvyZYvfrP8z1aZZOuy5at6nWkNYFYXQuj9vzCXj3XAfY4RifudVn+9z4/C6wtiEkRDn20f4Q3vAilD1mHYhOVofj5XCfkLwfId2ETIvefNkY3ZbW0Yiq0Ea5ip7AUMkrTZfsk6aANwyV6T02FVq5U1OoZeX12wDzaMmao5JzkOHOSPHY9cqLCc1kiBxHVTGpKYzc2QXiKD3eMjE2zlYiNmO1wyt2dcI/BKvg==#A#A261c6687-9166-4820-88ea-223fe0ad333b

 */

package sd.rittal.app.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import sd.rittal.app.AppApplication;
import sd.rittal.app.R;
import sd.rittal.app.helper.DataStorage;
import sd.rittal.app.helper.FormSession;
import sd.rittal.app.helper.LoginSession;
import sd.rittal.app.helper.Rittal;


public class HomeActivity extends AppCompatActivity implements FormSession,LoginSession {
    String TAG = HomeActivity.class.getSimpleName().toUpperCase();
    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;
    Bundle extras;
    AppApplication application;
    String lang = "";

    Dialog dialog;
//    ShowcaseView showCaseView;

//    Animation animMoveToTop;
//    LinearLayout layout_icon;
//    ImageView imageIcon;

    Toolbar toolbar;

    String type = "";
    String Service = "";
    String text = "";
    String en_text = "";
    String title = "";
    String en_title = "";
    String url = "";



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initiateContext();
        initLayout();



        if(!rittal.getValueOfKey("is_first_time").equals("1")){
            rittal.setValueToKey("is_first_time","1");
            //showHintBaner("INFO",activity.getString(R.string.generate_ipin_to_use_app));
        }

        try {
            Service = extras.getString("Service");
            text = extras.getString("text");
            en_text = extras.getString("en_text");
            url = extras.getString("url");
            type = extras.getString("type");
        }catch (Exception e){}

        if(!Service.equals("")){
            showAlertDialog();
        }

        JSONObject object = new JSONObject();
//        try {
//            object.put("Service","UPDATE");
//            object.put("text","اختبار");
//            object.put("en_text","text");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        rittal.log("OBJECT",object.toString());
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(getString(R.string.home));

        get_ipin_publicKey();
        get_publicKey();

        lang = Resources.getSystem().getConfiguration().locale.getLanguage();

        checkUpdate(rittal.getValueOfKey("versionCode"),rittal.getValueOfKey("lastVersionCode"));

        check();



        //rittal.rittalToast(lang,"0","s");
    }

    public void test(View v){
        String res = "{\"RRN\":\"3c376bae-97fa-4824-80dc-ac5dac532be9\",\"MeterNO\":\"37158876914\",\"Amount\":\"3\",\"PAmount\":\"1\",\"STS\":null,\"responseMessage\":\"Confirmed\",\"responseCode\":1,\"responseMessageArabic\":null,\"Units\":\"1.2\",\"Token\":null,\"ServiceCharge\":\"0.00\",\"CustomerName\":\"OMAR SHAMS ALDIN ALABEID AHMED\",\"Tarrif\":null,\"units\":null,\"stateId\":null,\"Curent_Balance\":0,\"Invoice\":null,\"calcQty\":null,\"buyTimes\":null,\"VAt\":null,\"verifyCode\":null,\"stampTax\":null,\"code\":null,\"netAMT\":0,\"commAMT\":0,\"refCode\":null,\"regCode\":null,\"Rslt_State\":null,\"vendQty\":null,\"vendAMT\":0,\"supplyAMT\":0,\"transTime\":null,\"transID\":null,\"feesAMT\":\"0\",\"checkCode\":null,\"Chanel\":null,\"FisrtPowerID\":\"1\",\"FisrtPoweramt\":\"1\",\"FisrtPowerKwt\":\"1.2\",\"FisrtPowerPrice\":\"0.8500\",\"SecPowerID\":\"0\",\"SecPoweramt\":\"0\",\"SecPowerKwt\":\"0\",\"SecPowerPrice\":\"0\",\"DetailedArreas\":null,\"ArrearsItems\":\"\",\"MIN_AMT\":null,\"keyId_1\":null,\"keyName_1\":null,\"KeyToken_1\":null,\"keyId_2\":null,\"keyName_2\":null,\"keyToken_2\":null,\"KeyId_2\":null,\"elec_fees\":null,\"ct_fees\":null,\"PAN\":null,\"tranDateTime\":\"271019111433\"}\n";



        if(MainActivity.pl.getState() == 3){
            try {
                MainActivity.receiptPrint(activity,"ar",new JSONObject(res),"11","C","");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else{
            Intent i = new Intent(activity,PrintSettingActivity.class);
            activity.startActivityForResult(i,-1);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        int minimumAllowedVersion =0;
        try {
            minimumAllowedVersion= Integer.valueOf(rittal.getValueOfKey("minimumAllowedVersion"));

        }catch (Exception e){

        }
//        int minimumAllowedVersion =5;
        int versionCode = Integer.valueOf(rittal.getValueOfKey("versionCode"));
//        int versionCode = 4;

        if(minimumAllowedVersion > versionCode ){
            Intent i = new Intent(activity,ForceUpdateActivity.class);
            startActivity(i);
            finish();
        }

        if(rittal.getValueOfKey("app_language").equals("en")){
            rittal.setValueToKey("app_language","en");
            Locale locale = new Locale("en");
            rittal.log("APP_LANGUAGE_IF",rittal.getValueOfKey("app_language"));
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            // Toast.makeText(this, getResources().getString(R.string.lbl_langSelectEnglis), Toast.LENGTH_SHORT).show();
        }
        else{
            rittal.setValueToKey("app_language","ar");
            Locale locale = new Locale("ar");
            rittal.log("APP_LANGUAGE_ELSE",rittal.getValueOfKey("app_language"));
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            //  Toast.makeText(this, getResources().getString(R.string.lbl_langSelecURdu), Toast.LENGTH_SHORT).show();
        }

    }

//    public void showShowCaseView(){
//        showCaseView = new ShowcaseView.Builder(this)
//                .withMaterialShowcase()
//                .setTarget(new ViewTarget(R.id.ipin_generation_liner, this))
//                .setContentTitle(R.string.generate_ipin)
//                .setContentText(R.string.showcaseviewIPINGeneration)
//                .setStyle(R.style.ShowCaseViewStyle)
//                .singleShot(SHOWCASEVIEW_ID)
//                .setShowcaseEventListener(new OnShowcaseEventListener() {
//                    @Override
//                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
//
//                    }
//
//                    @Override
//                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
//
//                    }
//
//                    @Override
//                    public void onShowcaseViewShow(ShowcaseView showcaseView) {
//
//                    }
//                    @Override
//                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
//
//                    }
//                });
//        showCaseView.build();
//    }

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
    }

    public void initLayout(){
//        info_baner = (LinearLayout) findViewById(R.id.info_baner);
//        info_text = (TextView) findViewById(R.id.info_text);
    }
    public void  goToBalanceInquiry(View v){
        Intent i = new Intent(activity,BalanceInquiryActivity.class);
        startActivity(i);
    }
    public void  goToBillInquiry(View v){
        Intent i = new Intent(activity,BillInquiryActivity.class);
        startActivity(i);
    }
    public void  goToBillPayment(View v){
        Intent i = new Intent(activity,BillPaymentActivity.class);
        startActivity(i);
    }
    public void  goToCardTransfer(View v){
        Intent i = new Intent(activity,CardToCardTransferActivity.class);
        startActivity(i);
    }
    public void  goToTopup(View v){
        Intent i = new Intent(activity,TopUpActivity.class);
        startActivity(i);
    }
    public void  goToCards(View v){
        Intent i = new Intent(activity,CardsListActivity.class);
        startActivity(i);
    }
    public void  goToGenerateOTP(View v){
        Intent i = new Intent(activity,GenerateOTPActivit.class);
        startActivity(i);
    }
    public void  goTOHighEducation(View v){
        Intent i = new Intent(activity,EducationActivity.class);
        startActivity(i);

    }
    public void  goToE15(View v){
        Intent i = new Intent(activity,E15Activity.class);
        startActivity(i);
    }
    public void  goToElectricity(View v){
        Intent i = new Intent(activity,Electricity_activity.class);
        startActivity(i);

    }
    public void  goToGenerateVoucher(View v){
        Intent i = new Intent(activity,GenerateVoucherActivity.class);
        startActivity(i);
    }
    public void  goGenerateIPIN(View view) {
        Intent i = new Intent(activity,GenerateOTPActivit.class);
        startActivity(i);
    }
    private void get_ipin_publicKey() {

        String url = application.getBaseUrl()+"/get_ipin_pKey.aspx";
        rittal.log("URL",url);

        JSONObject js = new JSONObject();
        try {
            js.put("version","1");
        } catch (JSONException e) {
            e.printStackTrace();
            rittal.log("Object Ex",e.toString());
        }
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,

                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        rittal.log("TAG Response", response.toString());

                        try {
                            if(response.getString("responseCode").equals("100")){
                                rittal.log("pubKeyValue",response.getString("pubKeyValue"));
                                rittal.setValueToKey("pubKeyValue",response.getString("pubKeyValue"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        rittal.log("TAG Respons ErrorListener", error.toString());
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
    private void get_publicKey() {

        String url = application.getBaseUrl()+"/get_pKey.aspx";
        rittal.log("URL",url);

        JSONObject js = new JSONObject();
        try {
            js.put("version","1");
        } catch (JSONException e) {
            e.printStackTrace();
            rittal.log("Object Ex",e.toString());
        }
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,

                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        rittal.log("TAG Response", response.toString());

                        try {
                            if(response.getString("responseCode").equals("0")){
                                rittal.log("pk",response.getString("pubKeyValue"));
                                rittal.setValueToKey("pk",response.getString("pubKeyValue"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        rittal.log("TAG Respons ErrorListener", error.toString());
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
    public void  goToMyAccount(View view) {

//        rittal.rittalToast(activity.getString(R.string.rittal_account_not_working_now),"0","w");
        if(rittal.getValueOfKey("is_login").equals("1")){
            Intent i = new Intent(activity,ProfileActivity.class);
            startActivity(i);
        }else{
            Intent i = new Intent(activity,LoginActivity.class);
            startActivity(i);
        }

    }
    public void  goToReport(View view) {
//        if(rittal.getValueOfKey("is_login").equals("1")){
            Intent i = new Intent(activity,ReportActivity.class);
            startActivity(i);
//        }else{
//            rittal.rittalToast(activity.getString(R.string.login_first),"1","w");
//        }

    }
    public void  goToSettings(View view) {
        Intent i = new Intent(activity,SettingActivity.class);
        startActivity(i);
        finish();
    }
    public void  checkUpdate(String current,String last){
        int mCurrent = Integer.valueOf(current);
        int mLast = 0;
        int skip_version = 0;
        try {
            mLast = Integer.valueOf(last);
        }catch (Exception e){}
        try {
            skip_version = Integer.valueOf(rittal.getValueOfKey("skip_version"));
        }catch (Exception e){}

        if(mCurrent < mLast){
            if(skip_version < mLast){
                showUpdateDialog();
            }
        }


    }
    public void  showUpdateDialog(){

        dialog.setContentView(R.layout.update_dialog);

        Button update = (Button) dialog.findViewById(R.id.update);
        Button exit = (Button) dialog.findViewById(R.id.exit);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rittal.setValueToKey("skip_version",rittal.getValueOfKey("lastVersionCode"));
                dialog.dismiss();
            }
        });

        dialog.show();


    }
    public void  showAlertDialog(){
//        dialog.setContentView(R.layout.alert_dialog);
//
//        Button update = (Button) dialog.findViewById(R.id.update);
//        Button exit = (Button) dialog.findViewById(R.id.exit);
//
//        update.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                update();
//            }
//        });
//        exit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                rittal.setValueToKey("skip_version",rittal.getValueOfKey("lastVersionCode"));
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
    }
    public void update(){
        try {
            startActivity(new Intent(Intent.ACTION_VIEW ,Uri.parse("market://details?id=sd.rittal.consumer")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=sd.rittal.consumer")));
        }
    }
    private void check() {
        //RequestQueue rq = Volley.newRequestQueue(this);

        String url = application.getBaseUrl()+"/app_version.aspx";
        rittal.log("URL",url);

        JSONObject js = new JSONObject();
        try {
            js.put("version","1");
        }
        catch (JSONException e) {
            e.printStackTrace();
            rittal.log("Object Ex",e.toString());
        }
        StringRequest req=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                rittal.log("Response",response);
                try {
                    //valid#1#
                    String text [] = response.split("#");
                    if(text[0].equals("valid")){
                        rittal.setValueToKey("lastVersionCode",text[1]);
                        rittal.setValueToKey("minimumAllowedVersion",text[2]);
                        checkUpdate(rittal.getValueOfKey("versionCode"),rittal.getValueOfKey("lastVersionCode"));
                    }
                }catch (Exception e){
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                rittal.log("Response",error.toString());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("device","android");
                params.put("version", rittal.getValueOfKey("versionCode"));
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
    void  dialog(View view){
//        AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
//        alertDialog.setTitle(getResources().getString(R.string.congratulations));
//        alertDialog.setMessage(getResources().getString(R.string.your_ipin_generated));
//
//        alertDialog.setButton("Save card", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//        alertDialog.setButton("thanks", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                //here you can add functions
//            } });
//        alertDialog.setIcon(R.drawable.ic_e15);
//        alertDialog.show();

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

    public void goToAbout(View view) {
        Intent i = new Intent(activity,AboutActivity.class);
        startActivity(i);
    }

    public void goToFavourite(View view) {
        Intent i = new Intent(activity, BeneficiaryListActivity.class);
        startActivity(i);
    }

    public void showCaseView(View view) {
//        new MaterialShowcaseView.Builder(this)
//                .setTarget(mButtonShow)
//                .setDismissText("GOT IT")
//                .setContentText("This is some amazing feature you should know about")
//                .setDelay(withDelay) // optional but starting animations immediately in onCreate can make them choppy
//                .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
//                .show();
    }


}
/*
 https://mob.rittal-sd.com/generateIPinCompletionRequest.aspx?
 cust_id=1&
 pan=5818588880475558&

 ex_date=2107&

 ipin=ZaZn2lFjDGX0WHeLszuL6s0+a80UIZc8SlD7wVGXWfwnMmJNGmwREOqDkTwrb/J1JuHvYWS3zWIcUBIza0SvCpMen43jJI/Jog7mkY+9222Fo6zQUHQxQi76I6Dn0ms68wumm6TdFVhnmPzJu4mKWZ05/CTutVyMzH9nHA2oWn+xNSksQqkGQXGmZG8dD9VPhoszy8zjAV619Bilg71SvIqyXFj3IGKzProqpk3vkB/j+8LpWZck1UoRY0B0ba7LW4Bz2LOD5GpP35MYAw2ZsrMAgbv5996Z8PZDgeuM6D8fvyxVhuo1uJ+Z9ltrzVDXeT4jRODvVn03tx+eE3h4OQ==

 otp=YLI5yEMgy0UIm5A84F20twbd1MNh5lMH+fUjJHfk+ugkllohdbieoUrqMmUy+PcT6nVZPeuZiSzz9XpiOMvzTG2/M+Zp+TpF+Cd+yN1EjPqVH43UX+XiZGR1sl+k8xemCg7y6CUIkPX8V8alyRr0rHWcCKrR0tcQ9pe1+u5kUP1lqCkTeO8rKXUDhKSJ3Jd5pSIwi1q2T4kmsjFMxdyhQ7fXURsfxNwKKsYKujmqtp3Twm7rzUuiZoEKTKvro60sGeaDqIYsgXfa8xjxbqHNCY0D0j0XuWPQDw/GhE7AtmNBMZcdgAVDT9nsEhOT0W83f12Mp7sDTwKGOZkmzKPaFw==

 uuid=8c11a141-ede2-49e6-a4e3-83ae2ec3cc2a

 pkey=MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjqmUrQ1w1rOH+kmj4dk3C3duIl1trVRr91EJKhphlWjOs2qqba9I2WmRH3CVUuBmkIEojYrnKtXiQBqMOTANzZuly6vZY3AOT/JVDtKy3/z60mUWUi+ILidh5dZz7ZYKsPupXRz5XQXELLpI4XN58N1oiNY95GutJ/hjqE0qqufQ1ie/hU5Dbof9F+Ug3wSJOzba+IVve09Yx76KsFBtc+0qn2/JI7YPca9s8h4ZKaVo7qvFZNScTieoR5f3vaKUsYNTdRK7HMXEU/BMrg8hzh04+ShtDtKtQ02pvObQ+Kha/oV54bdP5e6m9NpXupoL0ydQIDNZ6B76yVEe5WJM2wIDAQAB

**/
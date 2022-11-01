package sd.rittal.app.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sd.rittal.app.R;
import sd.rittal.app.adapters.CarouselPagerAdapter;
import sd.rittal.app.objects.Card;

/**
 * Created by ahmed on 8/28/2018.
 */

public class Rittal {
    /*
    * this class contain all custom function
    */

    private Activity activity;
    DataStorage dataStorage;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "RittalSession";

    PackageInfo pinfo;


    public Rittal(Context context){
        this.activity = (Activity) context;
        dataStorage = new DataStorage(activity);

        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        try {
            pinfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    // save value in SharedPreferences
    public void setValueToKey(String key,String value){
        editor.putString(key, value).apply();
        editor.commit();
    }
    // get value from SharedPreferences if found else return #
    public String getValueOfKey(String key){
        String value = "";
        value =  String.valueOf(pref.getString(key, ""));
        if(value.equals("")){
            value = "";
        }
        return value;
    }
    /*
    * show toast in debug mode and production mode
    * production mode => 1 || debug mode => 0
    * info => i || success => s || warning => w || danger => d
    */
    public void rittalToast(String msg,String mode,String type){
       if(mode.equals("1") || mode.equals("0")){
           //Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show();

           // Get the custom layout view.
           View toastView = activity.getLayoutInflater().inflate(R.layout.custom_toast, null);
           TextView message = (TextView) toastView.findViewById(R.id.message);
           LinearLayout toast_container = (LinearLayout) toastView.findViewById(R.id.toast_container);
           toast_container.setBackgroundResource(R.drawable.toast_info_shape);
           if(type.toLowerCase().trim().equals("i")){
               toast_container.setBackgroundResource(R.drawable.toast_info_shape);
           }
           if(type.toLowerCase().trim().equals("s")){
               toast_container.setBackgroundResource(R.drawable.toast_success_shape);
           }
           if(type.toLowerCase().trim().equals("w")){
               toast_container.setBackgroundResource(R.drawable.toast_warning_shape);
           }
           if(type.toLowerCase().trim().equals("d")){
               toast_container.setBackgroundResource(R.drawable.toast_denger_shape);
           }

           message.setText(msg);

           // Initiate the Toast instance.
           Toast toast = new Toast(activity.getApplicationContext());
           // Set custom view in toast.
           toast.setView(toastView);
           toast.setDuration(Toast.LENGTH_SHORT);
           toast.setGravity(Gravity.CENTER, 0,0);
           toast.show();
       }
    }

    public String modifyPhoneNumber(String num) {
        num= num.replaceAll("[^a-zA-Z0-9\\s+]", "");
        num= num.replaceAll("\\+", "");
        num= num.replaceAll(" ", "");
        if (num.startsWith("00249")) {
            num = num.replaceFirst("(00249)", "0");
        }else if(num.startsWith("0249")){
            num = num.replaceFirst("(0249)", "0");
        }else if(num.startsWith("249")){
            num = num.replaceFirst("(249)", "0");
        }
        return num;
    }

    public void log(String key,String value){
       Log.d(key,value);
       Log.i(key,value);
    }

    public void logOut(){

       setValueToKey("user_name","");
        setValueToKey("user_code","");
        setValueToKey("is_login","0");
        dataStorage.delete_card_by_expiry_date("ACCC");

    }
    public void logOutFromAccount(){

        setValueToKey("user_id","");
        setValueToKey("user_name","");
        setValueToKey("user_code","");
        setValueToKey("is_login","0");
        dataStorage.delete_card_by_expiry_date("ACCC");
    }


    public ArrayList<Card> getCardList(String source){
        //source => A just account
        //source => C just card
        //source => A&C account and card

        ArrayList<Card> card_list = new ArrayList<Card>();
        Cursor cards_date = dataStorage.get_cards();
        cards_date.moveToFirst();
        if (cards_date.moveToFirst()) {
            do {

                int id              = Integer.valueOf(cards_date.getString(cards_date.getColumnIndex("id")));
                String name         = cards_date.getString(cards_date.getColumnIndex("name"));
                String pan          = "";
                try {
                    pan = Encryption.Decrypt(cards_date.getString(cards_date.getColumnIndex("pan")));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String expiry_date  = cards_date.getString(cards_date.getColumnIndex("expiry_date"));
                String color        = cards_date.getString(cards_date.getColumnIndex("color"));


                if(source.toUpperCase().equals("A")){
                    if(expiry_date.toUpperCase().equals("ACCC")){
                        Card card = new Card(id,name,pan,expiry_date,color);
                        card_list.add(card);
                    }

                }else if(source.toUpperCase().equals("C")){
                    if(!expiry_date.toUpperCase().equals("ACCC")){
                        Card card = new Card(id,name,pan,expiry_date,color);
                        card_list.add(card);
                    }
                }
                else if(source.toUpperCase().equals("A&C")){
                    Card card = new Card(id,name,pan,expiry_date,color);
                    card_list.add(card);
                }else {

                }

            }while (cards_date.moveToNext());
        }

        //Collections.reverse(card_list);
        Log.d("TAg card_list size is ",card_list.size()+"");
        return  card_list;
    }

    public void initCardCarousel(String source, ViewPager pager, CarouselPagerAdapter adapter, EditText et_card_pan, EditText et_card_expiry_date , FragmentManager getSupportFragmentManager, LinearLayout layout_card_data, LinearLayout layout_card_secret){

        //set page margin between pages for viewpager
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int pageMargin = ((metrics.widthPixels / 4) * 2);
        pager.setPageMargin(-pageMargin);

        adapter = new CarouselPagerAdapter(activity,getCardList(source),pager,et_card_pan,et_card_expiry_date, getSupportFragmentManager,layout_card_data,layout_card_secret);
        pager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        pager.addOnPageChangeListener(adapter);

        // Set current item to the middle page so we can fling to both
        // directions left and right
        pager.setCurrentItem(getCardList(source).size()-1);
        pager.setOffscreenPageLimit(3);
    }

    public void initCardSpinner(final ArrayList<Card> card_list, final Spinner sp_cards, final EditText et_card_pan, final EditText et_card_expiry_date, final LinearLayout layout_card_data, final LinearLayout layout_card_secret){
        List<String> cards_name = new ArrayList<String>();
        final List<String> cards_pan = new ArrayList<String>();
        final List<String> cards_expiry_date = new ArrayList<String>();

        cards_name.add(activity.getString(R.string.selec_card));
        cards_pan.add("");
        cards_expiry_date.add("");
        if(card_list!= null && !card_list.isEmpty()){
            for(int i = 0 ; i < card_list.size() ; i++){
                cards_name.add(card_list.get(i).getName());
                cards_pan.add(card_list.get(i).getPan());
                cards_expiry_date.add(card_list.get(i).getExpiry_date());

            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, cards_name);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_cards.setAdapter(dataAdapter);
        sp_cards.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (cards_expiry_date.get(i).toUpperCase().equals("ACCC")) {

                    et_card_pan.setText(cards_pan.get(i));
                    et_card_expiry_date.setText(cards_expiry_date.get(i));

                    if(layout_card_data != null){
                        layout_card_data.setVisibility(View.GONE);
                    }

                    if(layout_card_secret != null){
                        layout_card_secret.setVisibility(View.GONE);
                    }

                } else{
                    if(layout_card_data != null){
                        layout_card_data.setVisibility(View.VISIBLE);
                    }

                    if(layout_card_secret != null){
                        layout_card_secret.setVisibility(View.VISIBLE);
                    }
                    et_card_pan.setText(cards_pan.get(i));
                    et_card_expiry_date.setText(cards_expiry_date.get(i));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                et_card_pan.setText("");
                et_card_expiry_date.setText("");
            }
        });
    }

    public void myCards(final String source,final CarouselPagerAdapter adapter , final FragmentManager getSupportFragmentManager, final Switch switch_use_saved_card, final  Spinner sp_cards, final  ViewPager pager, final  EditText et_card_pan , final  EditText et_card_expiry_date, final LinearLayout layout_card_data, final LinearLayout layout_card_secret){
        //source => A just account
        //source => C just card
        //source => A&C account and card

        switch_use_saved_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(layout_card_data != null){
                    layout_card_data.setVisibility(View.VISIBLE);
                }

                if(layout_card_secret != null){
                    layout_card_secret.setVisibility(View.VISIBLE);
                }
                if(switch_use_saved_card.isChecked()){
                    if(getValueOfKey("CARD_LAYOUT").equals("SPINNER")){
                        sp_cards.setVisibility(View.VISIBLE);
                        initCardSpinner(getCardList(source),sp_cards,et_card_pan,et_card_expiry_date,layout_card_data,layout_card_secret);
                    }else{
                        pager.setVisibility(View.VISIBLE);
                        initCardCarousel(source,pager,adapter,et_card_pan,et_card_expiry_date,getSupportFragmentManager,layout_card_data,layout_card_secret);
                    }
                }else {
                    sp_cards.setVisibility(View.GONE);
                    pager.setVisibility(View.GONE);
                    et_card_pan.setText("");
                    if(et_card_expiry_date != null){
                        et_card_expiry_date.setText("");
                    }

                    //adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public String formatDate(String text){
        char[] dataArray = text.toCharArray();

        try {
            String day = String.valueOf(dataArray[0])+String.valueOf(dataArray[1]);
            String month = String.valueOf(dataArray[2])+String.valueOf(dataArray[3]);
            String year = "20"+String.valueOf(dataArray[4])+String.valueOf(dataArray[5]);
            String h = ""+String.valueOf(dataArray[6])+String.valueOf(dataArray[7]);
            String m = ""+String.valueOf(dataArray[8])+String.valueOf(dataArray[9]);
            String s = ""+String.valueOf(dataArray[10])+String.valueOf(dataArray[11]);

            String date = day+"-"+month+"-"+year+"  "+h+":"+m+":"+s;
            return  date;
        }catch (Exception e){

            String date = text;
            return  date;
        }


    }
    public String hashPan(String pan){

        char[] a = pan.toCharArray();
        for( int j =0 ; j<a.length;j++){
            char[] y = new char[1];
            y = new char[]{'*'};
            if(j>5 && j<a.length-4){
                a[j]=y[0];
            }
        }
        return String.valueOf(a);
    }

    // return string => version name in 1.0.0 format
    public String getVersionName(){
        try {
            setValueToKey("versionName",pinfo.versionName);
        }catch (Exception e){}
        return pinfo.versionName;
    }
    // return int => version code in 1 format
    public int getVersionCode(){
        try {
            setValueToKey("versionCode",String.valueOf(pinfo.versionCode));
        }catch (Exception e){}
        return pinfo.versionCode;

    }

    public String formatDateLastInvoice(String mLastInvoiceDate) {
        char[] dataArray = mLastInvoiceDate.toCharArray();

        try {
            String day = String.valueOf(dataArray[6])+String.valueOf(dataArray[7]);
            String month = String.valueOf(dataArray[4])+String.valueOf(dataArray[5]);
            String year = String.valueOf(dataArray[0])+String.valueOf(dataArray[1])+String.valueOf(dataArray[2])+String.valueOf(dataArray[3]);
            String h = ""+String.valueOf(dataArray[8])+String.valueOf(dataArray[9]);
            String m = ""+String.valueOf(dataArray[10])+String.valueOf(dataArray[11]);
            String s = ""+String.valueOf(dataArray[12])+String.valueOf(dataArray[13]);

            String date = day+"-"+month+"-"+year+"  "+h+":"+m+":"+s;
            return  date;
        }catch (Exception e){

            String date = mLastInvoiceDate;
            return  date;
        }
    }

    public String addTextForSMSEng(int key,String value){
        String text = "";
        String label = getResourseString(activity,key,"en");

        text = label + value ;

        return  text;
    }

    public String takeScreenshotDialog(Dialog dialog) {
        Date now = new Date();
        String filePath="";
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpeg";

            // create bitmap screen capture
            View v1 = dialog.getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            //setting screenshot in imageview
             filePath = imageFile.getPath();
            log("filePath", filePath);

            Bitmap ssbitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            // iv.setImageBitmap(ssbitmap);


        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
        return filePath;

    }
    public String takeScreenshotCard(View view) {
        String filePath="";
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpeg";

            // create bitmap screen capture
//            View v1 = activity.getWindow().getDecorView().getRootView();
//            View v1 = view;
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            //setting screenshot in imageview
            filePath = imageFile.getPath();

            Bitmap ssbitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            // iv.setImageBitmap(ssbitmap);


        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
        return filePath;
    }

    public  void shareImageReceipt(String sharePath){

        Log.d("STARTSHAREIMge",sharePath);
        File file = new File(sharePath);
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent .setType("image/*");
        intent .putExtra(Intent.EXTRA_STREAM, uri);
        activity.startActivity(Intent.createChooser(intent, "Choose sharing method"));
    }

    public static String getResourseString(Activity activity,int key,String lang){
        String text = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            text = getStringByLocalPlus17(activity,key,lang);
        }else{
            text = getStringByLocalBefore17(activity,key,lang);
        }
        return text;

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static String getStringByLocalPlus17(Activity context, int resId, String locale) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(new Locale(locale));
        return context.createConfigurationContext(configuration).getResources().getString(resId);
    }
    private static String getStringByLocalBefore17(Context context, int resId, String language) {
        Resources currentResources = context.getResources();
        AssetManager assets = currentResources.getAssets();
        DisplayMetrics metrics = currentResources.getDisplayMetrics();
        Configuration config = new Configuration(currentResources.getConfiguration());
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        config.locale = locale;

        Resources defaultLocaleResources = new Resources(assets, metrics, config);
        String string = defaultLocaleResources.getString(resId);

        new Resources(assets, metrics, currentResources.getConfiguration());
        return string;
    }

}

//3
//C
//"RRN":"29b5ea43-7824-4535-b92f-98145761279c","MeterNO":"37158876914","Amount":"1",
// "PAmount":"0","STS":null,"responseMessage":"Confirmed","responseCode":1,"Units":"3.9",
// "Token":"37180872749512704149","ServiceCharge":"0.00","CustomerName":"OMAR SHAMS ALDIN ALABEID AHMED",
// "Tarrif":null,"units":null,"stateId":null,"Curent_Balance":0,"Invoice":null,"calcQty":null,"buyTimes":null,
// "VAt":null,"verifyCode":null,"stampTax":null,"code":null,"netAMT":0,"commAMT":0,"refCode":null,"regCode":null,
// "Rslt_State":null,"vendQty":null,"vendAMT":0,"supplyAMT":0,"transTime":null,"transID":null,"feesAMT":"0",
// "checkCode":null,"Chanel":null,"FisrtPowerID":"1","FisrtPoweramt":"1","FisrtPowerKwt":"3.9",
// "FisrtPowerPrice":"0.2600","SecPowerID":"0","SecPoweramt":"0","SecPowerKwt":"0","SecPowerPrice":"0",
// "DetailedArreas":null,"ArrearsItems":"","MIN_AMT":null,"keyId_1":null,"keyName_1":null,"KeyToken_1":null,
// "keyId_2":null,"keyName_2":null,"keyToken_2":null,"KeyId_2":null,"elec_fees":null,"ct_fees":null,"PAN":null}


//G
// {"RRN":"9b85b20e-1100-48a0-aff9-9eb46cf2faf5","MeterNO":"37158876914","Amount":"1","PAmount":null,
// "STS":null,"responseMessage":"Confirmed","responseCode":1,"Units":"3.9","Token":null,"ServiceCharge":"0.00",
// "CustomerName":"OMAR SHAMS ALDIN ALABEID AHMED","Tarrif":null,"units":null,"stateId":null,"Curent_Balance":0,
// "Invoice":null,"calcQty":null,"buyTimes":null,"VAt":null,"verifyCode":null,"stampTax":null,"code":null,"netAMT":0,
// "commAMT":0,"refCode":null,"regCode":null,"Rslt_State":null,"vendQty":null,"vendAMT":0,"supplyAMT":0,
// "transTime":null,"transID":null,"feesAMT":"0","checkCode":null,"Chanel":null,"FisrtPowerID":"1","FisrtPoweramt":"1",
// "FisrtPowerKwt":"3.9","FisrtPowerPrice":"0.2600","SecPowerID":"0","SecPoweramt":"0","SecPowerKwt":"0",
// "SecPowerPrice":"0","DetailedArreas":null,"ArrearsItems":"","MIN_AMT":null,"keyId_1":null,"keyName_1":null,
// "KeyToken_1":null,"keyId_2":null,"keyName_2":null,"keyToken_2":null,"KeyId_2":null,"elec_fees":null,
// "ct_fees":null,"PAN":null}

//4

//5 topup
//{"responseMessage":"Approved","responseMessageArabic":null,"responseCode":0,"responseStatus":null,"pubKeyValue":null,
// "tranDateTime":null,"creationDate":null,"panCategory":null,"balance":"3.12","payeesCount":0,"payeesList":null,
// "billInfo":null,"RespPAN":null,"RespExpDate":null,"RespIPIN":null,"regType":null,"financialInstitutionId":null,
// "respUserName":null,"respUserPassword":null,"RespVoucherNumber":null,"voucherCode":null,"serviceInfo":null,
// "originalTransaction":null,"originalTranType":null,"originalTranUUID":null,"respUUID":null,"resp_acqTranFee":0,
// "resp_issuerTranFee":0,"resp_fromAccount":null,"resp_accountCurrency":null,"resp_tranAmount":0,
// "resp_paymentInfo":null,"toCard":null,"rittalFee":0,"clientFee":0,"voucherNumber":null}
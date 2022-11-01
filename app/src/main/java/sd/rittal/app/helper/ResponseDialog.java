package sd.rittal.app.helper;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import sd.rittal.app.AppApplication;
import sd.rittal.app.R;
import sd.rittal.app.activities.GenerateIPINActivity;
import sd.rittal.app.activities.HomeActivity;
import sd.rittal.app.activities.MainActivity;
import sd.rittal.app.activities.PrintSettingActivity;

import static sd.rittal.app.activities.MainActivity.getResourseString;

/**
 * Created by Ahmed Khatim on 3/9/2019.
 */

public class ResponseDialog {
    Activity activity;
    Rittal rittal;
    JSONObject  response;
    AppApplication application;
    ProgressDialog progressDialog;
    DataStorage dataStorage;

    String service;
    String opid;
    Dialog dialog;

    String mResponseCode = "";
    String mResponseMessage = "";
    String mResponseDate = "";
    String mResponsePan = "";
    String mResponseBalance = "";
    String mResponseFee = "";
    String mContractNumber = "";
    String mSubscriberId = "";
    String mTotalAmount = "";
    String mBilledAmount = "";
    String mUnbilledAmount = "";
    String mLastInvoiceDate = "";
    String mLast4Digits = "";
    String mTranAmount = "";
    String mToCard = "";
    String mVoucherNumber = "";
    String mVoucherCode = "";
    String mUnitName = "";
    String mServiceName = "";
    String mInvoiceExpiry = "";
    String mDueAmount = "";
    String mReferenceId = "";
    String mInvoiceStatus = "";
    String mPayerName = "";
    String mReceiptNo = "";
    String mPhoneNumber = "";
    String mMeterNO = "";
    String mMeterArrear = "";
    String mWaterArrear = "";
    String mPAmount = "";

    String mFisrtPowerKwt = "";
    String mFisrtPoweramt = "";
    String mFisrtPowerPrice = "";
    String mSecPowerKwt = "";
    String mSecPoweramt = "";
    String mSecPowerPrice = "";
    String mAmount = "";
    String mUnits = "";
    String mKWPrice = "";
    String mFisrtPowerID = "";
    String mSecPowerID = "";
    String mSecondKWPrice = "";


    String mToken = "";
    String mCustomerName = "";
    String mRittalFee = "";
    String mSenderName="";
    String mReceiverName="";

    String waterFees       =  "";//= billInfo.getString("waterFees");
    String opertorMessage  = "";//billInfo.getString("opertorMessage");
    String meterFees       = "";//billInfo.getString("meterFees");
    String accountNo       = "";//billInfo.getString("accountNo");

    String englishName    = "";//billInfo.getString("englishName");
    String arabicName    = "";//billInfo.getString("arabicName");
    String studentNo    = "";//billInfo.getString("studentNo");
    String receiptNo    = "";//billInfo.getString("receiptNo");
    String dueAmount    = "";//billInfo.getString("dueAmount");
    String formNo    = "";//billInfo.getString("formNo");

    String mSenderPhone="";
    String mReceiverPhone="";

    String pan ="";
    String expiry_date ="";



    String    lang = "";
    String   ipin = "";
    String counter_num = "";
    String paymentInfo = "";
    String sharePath="no";
    String amount = "";
    String transactionSource;


    String smsText = "";

    ImageView image;
    TextView status;

    TextView res_service,res_pan,res_pan_titel,res_date,res_amount,res_fee,res_fee_title,res_code,res_message,res_balance,res_last_4digit;
    TextView res_phone,res_contract,res_subscriber_id,res_total_bill_amount,res_total_bill_amount_titel,res_bill_amount,res_bill_amount_titel,res_billed_amount,res_unbill_amount,res_last_invoice_date;
    TextView res_to_card,res_to_card_title,res_operator,res_voucher_number,res_voucher_code;
    TextView res_unit_name,res_service_name,res_invoice_expiry_date,res_due_amount,res_reference_id,res_invoice_status,res_payer_name;
    TextView res_receipt_number;
    TextView res_meter_number,res_meter_arrear,res_customer_name,res_units,res_unit_price,res_token,res_token_title,res_purchase_amount,res_rittal_fee,res_s_name,res_s_phone,res_r_name,res_r_phone;
    TextView pay,res_form_no,res_invoice_num;
    TextView res_account_number,res_meter_fees,res_water_fees,res_operater_message,res_net_amount;
    View rootView;


    public ResponseDialog(Activity activity,Dialog dialog, JSONObject response, String service, String opid, String pan,String expiry_date,String ipin,String counter_num,String paymentInfo,String amount,ProgressDialog progressDialog,String transactionSource) {
        this.activity = activity;
        this.rittal = new Rittal(activity);
        this.application = new AppApplication();
        dataStorage = new DataStorage(activity);
        this.dialog = dialog;
        this.progressDialog = progressDialog;
        this.response = response;
        this.service = service;
        this.opid = opid;
        this.pan = pan;
        this.expiry_date = expiry_date;
        this.ipin = ipin;
        this.counter_num = counter_num;
        this.paymentInfo = paymentInfo;
        this.amount = amount;
        this.transactionSource = transactionSource;

        lang = Resources.getSystem().getConfiguration().locale.getLanguage();
    }

    /*
     * service : 2 => balance inquiry
     *
     * */





    public void initLayout(){
        rootView = activity.getWindow().getDecorView().findViewById(R.id.container);
        res_meter_number     = (TextView) dialog.findViewById(R.id.res_meter_number);
        res_meter_arrear     = (TextView) dialog.findViewById(R.id.res_meter_arrear);
        res_customer_name     = (TextView) dialog.findViewById(R.id.res_customer_name);
        res_form_no     = (TextView) dialog.findViewById(R.id.res_form_no);
        res_units              = (TextView) dialog.findViewById(R.id.res_units);
        res_unit_price              = (TextView) dialog.findViewById(R.id.res_unit_price);
        res_token               = (TextView) dialog.findViewById(R.id.res_token);
        res_token_title               = (TextView) dialog.findViewById(R.id.res_token_title);
        res_purchase_amount     = (TextView) dialog.findViewById(R.id.res_purchase_amount);
        res_rittal_fee        = (TextView) dialog.findViewById(R.id.res_rittal_fee);
        res_last_4digit        = (TextView) dialog.findViewById(R.id.res_last4_digits);

        res_service     = (TextView) dialog.findViewById(R.id.response_service);
        res_date        = (TextView) dialog.findViewById(R.id.res_date);
        res_pan         = (TextView) dialog.findViewById(R.id.res_pan);
        res_pan_titel         = (TextView) dialog.findViewById(R.id.res_pan_titel);
        res_amount      = (TextView) dialog.findViewById(R.id.res_amount);
        res_fee         = (TextView) dialog.findViewById(R.id.res_fee);
        res_fee_title         = (TextView) dialog.findViewById(R.id.res_fee_title);
        res_code        = (TextView) dialog.findViewById(R.id.res_code);
        res_message     = (TextView) dialog.findViewById(R.id.res_message);
        res_balance     = (TextView) dialog.findViewById(R.id.res_balance);

        res_phone               = (TextView) dialog.findViewById(R.id.res_phone);
        res_contract            = (TextView) dialog.findViewById(R.id.res_contract);
        res_subscriber_id       = (TextView) dialog.findViewById(R.id.res_subscriber_id);
        res_total_bill_amount   = (TextView) dialog.findViewById(R.id.res_total_bill_amount);
        res_total_bill_amount_titel   = (TextView) dialog.findViewById(R.id.res_total_bill_amount_titel);
        res_billed_amount       = (TextView) dialog.findViewById(R.id.res_billed_amount);
        res_bill_amount         = (TextView) dialog.findViewById(R.id.res_bill_amount);
        res_bill_amount_titel         = (TextView) dialog.findViewById(R.id.res_bill_amount_titel);
        res_unbill_amount       = (TextView) dialog.findViewById(R.id.res_unbill_amount);
        res_last_invoice_date   = (TextView) dialog.findViewById(R.id.res_last_invoice_date);
        res_to_card             = (TextView) dialog.findViewById(R.id.res_to_card);
        res_to_card_title             = (TextView) dialog.findViewById(R.id.res_to_card_title);
        res_operator            = (TextView) dialog.findViewById(R.id.res_operator);
        res_voucher_number      = (TextView) dialog.findViewById(R.id.res_voucher_number);
        res_voucher_code        = (TextView) dialog.findViewById(R.id.res_voucher_code);

        res_unit_name           = (TextView) dialog.findViewById(R.id.res_unit_name);
        res_service_name        = (TextView) dialog.findViewById(R.id.res_service_name);
        res_invoice_expiry_date = (TextView) dialog.findViewById(R.id.res_invoice_expiry_date);
        res_due_amount          = (TextView) dialog.findViewById(R.id.res_due_amount);
        res_reference_id        = (TextView) dialog.findViewById(R.id.res_reference_id);
        res_invoice_status      = (TextView) dialog.findViewById(R.id.res_invoice_status);
        res_payer_name          = (TextView) dialog.findViewById(R.id.res_payer_name);

        res_s_name          = (TextView) dialog.findViewById(R.id.res_s_name);
        res_s_phone         = (TextView) dialog.findViewById(R.id.res_s_phone);
        res_r_name           = (TextView) dialog.findViewById(R.id.res_r_name);
        res_r_phone          = (TextView) dialog.findViewById(R.id.res_r_phone);

        res_receipt_number          = (TextView) dialog.findViewById(R.id.res_receipt_number);
        pay                     = (TextView) dialog.findViewById(R.id.pay);

        res_invoice_num                     = (TextView) dialog.findViewById(R.id.res_invoice_num);
        res_account_number                     = (TextView) dialog.findViewById(R.id.res_account_number);
        res_meter_fees                     = (TextView) dialog.findViewById(R.id.res_meter_fees);
        res_water_fees                     = (TextView) dialog.findViewById(R.id.res_water_fees);
        res_operater_message               = (TextView) dialog.findViewById(R.id.res_operater_message);
        res_net_amount                     = (TextView) dialog.findViewById(R.id.res_net_amount);
    }

    public void show(){
        dialog.setContentView(R.layout.response_dialog);
        Button cancel    = (Button) dialog.findViewById(R.id.cancel);
        Button print     = (Button) dialog.findViewById(R.id.print);
        Button share     = (Button) dialog.findViewById(R.id.share);
        Button save_card = (Button) dialog.findViewById(R.id.save_card);
        Button continues = (Button) dialog.findViewById(R.id.continues);
        Button send = (Button) dialog.findViewById(R.id.send);

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.pl.getState() == 3){
                    try {
                        if(!response.getString("RespPAN").equals("null")){
                            transactionSource="CARD";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MainActivity.receiptPrint(activity,Locale.getDefault().getLanguage(),response,service,opid,transactionSource);
                }
                else{
                    Intent i = new Intent(activity,PrintSettingActivity.class);
                    activity.startActivityForResult(i,-1);
                }
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
                rittal.log("sharePath1",sharePath);
                sharePath=rittal.takeScreenshotDialog(dialog);
                rittal.log("sharePath2",sharePath);
                if (!sharePath.equals("no")) {
                  rittal.shareImageReceipt(sharePath);
                }
               // shareImage();
              //shareReceipt(view);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", "", null));
                    intent.putExtra("sms_body", smsText);
                    activity.startActivity(intent);
                }catch (Exception e){}
            }
        });

        save_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    mResponsePan        = response.getString("RespPAN");
                    expiry_date        = response.getString("RespExpDate");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                rittal.log("PAN_GIPIN",pan);
                rittal.log("expiry_date_PAN_GIPIN",expiry_date);

                if(dataStorage.add_card("ATM card",mResponsePan,expiry_date,"-1672077")){
                    rittal.rittalToast(getResourseString(activity,R.string.card_saved,lang),"1","s");
                }else{
                    rittal.rittalToast(getResourseString(activity,R.string.card_didnot_saved,lang),"1","D");
                }
                Intent i = new Intent(activity, HomeActivity.class);
                activity.startActivity(i);
                activity.finish();

            }
        });

        continues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity,GenerateIPINActivity.class);
                activity.startActivity(i);
                activity.finish();
            }
        });

        initLayout();

        try {
            mResponseCode = response.getString("responseCode");
            if(lang.equals("ar")){
                try {
                    mResponseMessage = response.getString("responseMessageArabic");
                }catch (Exception e){ rittal.log("Read Arabic message",e.toString());}
                if(!mResponseMessage.equals("null")){
                    try {
                        mResponseMessage = response.getString("responseMessageArabic");
                    }catch (Exception e){
                        mResponseMessage = response.getString("responseMessage");
                        rittal.log("Read Arabic message 2",e.toString());
                        rittal.log("mResponseMessage",mResponseMessage);
                    }
                }else{
                    mResponseMessage = response.getString("responseMessage");
                }
            }else{
                mResponseMessage = response.getString("responseMessage");
            }

            addText(res_code,mResponseCode);
            addText(res_message,mResponseMessage);

            rittal.log("dialog service","is =>"+service);

            if(service.equals("1")){
                rittal.log("Generate IPIN service","catched");
                if(opid.equals("1")){
                    addText(res_service,activity.getString(R.string.generate_otp));
                    rittal.log("Generate IPIN","geting otp");
                    // generate ipin => first step generate otp
                    if(mResponseCode.equals("100")){
                        rittal.log("Generate IPIN","success");
                        //successful transaction
                        mResponseDate       = response.getString("tranDateTime");
                        mResponsePan        = response.getString("RespPAN");
                        mResponseFee        = response.getString("resp_issuerTranFee");

                        addText(res_date,formatDate(mResponseDate));

                        addText(res_pan, hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);

                        addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                        res_fee_title.setVisibility(View.VISIBLE);

                        continues.setVisibility(View.VISIBLE);

                    }else{
                        rittal.log("Generate IPIN","faild");
                        mResponseDate       = response.getString("tranDateTime");
                        mResponsePan        = response.getString("RespPAN");
                        mResponseFee        = response.getString("resp_issuerTranFee");

                        addText(res_date,formatDate(mResponseDate));

                        addText(res_pan, hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);

                        addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                        res_fee_title.setVisibility(View.VISIBLE);

                    }
                }
                else if(opid.equals("2")){
                    //generate ipin complition => second step
                    addText(res_service,activity.getString(R.string.generate_ipin));
                    if(mResponseCode.equals("100")){
                        //successful transaction
                        mResponseDate       = response.getString("tranDateTime");
                        mResponsePan        = response.getString("RespPAN");
                        mResponseFee        = response.getString("resp_issuerTranFee");

                        addText(res_date,formatDate(mResponseDate));

                        addText(res_pan, hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);

                        addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                        res_fee_title.setVisibility(View.VISIBLE);
                        save_card.setVisibility(View.VISIBLE);
                    }else{
                        mResponseDate       = response.getString("tranDateTime");
                        mResponsePan        = response.getString("RespPAN");
                        mResponseFee        = response.getString("resp_issuerTranFee");

                        addText(res_date,formatDate(mResponseDate));

                        addText(res_pan, hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);

                        addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                        res_fee_title.setVisibility(View.VISIBLE);

                    }
                }

            }
            else if(service.equals("2")){
                //balance inquiry receipt
                addText(res_service,activity.getString(R.string.balance_inquery));

                if(response.getString("responseCode").equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    mResponseBalance    = response.getString("balance");
                    mResponseFee        = response.getString("resp_issuerTranFee");

                    addText(res_date,formatDate(mResponseDate));
                    if(transactionSource.toUpperCase().equals("CARD")){
                        addText(res_pan, hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);
                    }
                    addText(res_balance,mResponseBalance+" "+ activity.getString(R.string.sdg_hint));
                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                    res_fee_title.setVisibility(View.VISIBLE);

                    print.setVisibility(View.VISIBLE);
                    share.setVisibility(View.VISIBLE);
                }

            }
            else if(service.equals("3")){
                //card transfer
                addText(res_service,activity.getString(R.string.title_card_to_card_transfer));
                if(mResponseCode.equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    mResponseFee        = response.getString("resp_issuerTranFee");
                    mTranAmount        = response.getString("resp_tranAmount");
                    mToCard        = response.getString("toCard");

                    addText(res_date,formatDate(mResponseDate));
                    if(transactionSource.toUpperCase().equals("CARD")){
                        addText(res_pan, hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);
                    }
                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                    res_fee_title.setVisibility(View.VISIBLE);
                    addText(res_to_card,hashPan(mToCard));
                    res_to_card_title.setVisibility(View.VISIBLE);
                    addText(res_amount,mTranAmount+" "+ activity.getString(R.string.sdg_hint));

                    print.setVisibility(View.VISIBLE);
                    share.setVisibility(View.VISIBLE);

                }

            }
            else if(service.equals("4")){
                //bill inquiry receipt
                rittal.log("service name", service + activity.getString(R.string.title_bill_iquery));

                if(mResponseCode.equals("0")){

                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    mResponseFee        = response.getString("resp_issuerTranFee");
                    mPhoneNumber        = response.getString("resp_paymentInfo");
                    String mOperator = "";
                    if(opid.equals("2")){
                        //2 => Zain
                        addText(res_service,activity.getString(R.string.title_bill_iquery));
                        rittal.log("opreter name", opid + activity.getString(R.string.zain));
                        mOperator = activity.getString(R.string.zain);

                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));

                        mContractNumber    = billInfo.getString("contractNumber");
                        mTotalAmount       = billInfo.getString("totalAmount");
                        mBilledAmount      = billInfo.getString("billedAmount");
                        mUnbilledAmount    = billInfo.getString("unbilledAmount");
                        mLastInvoiceDate   = billInfo.getString("lastInvoiceDate");
                        mLast4Digits       = billInfo.getString("last4Digits");

                        addText(res_date,formatDate(mResponseDate));
                        if(transactionSource.toUpperCase().equals("CARD")){
                            addText(res_pan, hashPan(mResponsePan));
                            res_pan_titel.setVisibility(View.VISIBLE);
                        }
                        addText(res_fee,mResponseFee+" "+getResourseString(activity,R.string.sdg_hint,lang));
                        res_fee_title.setVisibility(View.VISIBLE);
                        addText(res_operator,mOperator);
                        addText(res_contract,mContractNumber);

                        addText(res_total_bill_amount,mTotalAmount+" "+ activity.getString(R.string.sdg_hint));
                        res_total_bill_amount_titel.setVisibility(View.VISIBLE);
                        addText(res_billed_amount,mBilledAmount+" "+ activity.getString(R.string.sdg_hint));
                        addText(res_unbill_amount,mUnbilledAmount+" "+ activity.getString(R.string.sdg_hint));

                        addText(res_phone,mPhoneNumber);

                        addText(res_last_invoice_date,mLastInvoiceDate);
                        addText(res_last_4digit,mLast4Digits);


                    }
                    else if(opid.equals("4")){
                        addText(res_service,activity.getString(R.string.title_bill_iquery));
                        rittal.log("opreter name", opid + activity.getString(R.string.mtn));
                        mOperator = activity.getString(R.string.mtn);

                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));
                        rittal.log("billInfo_mtn",billInfo.toString());
//                        {"responseMessage":"Approved","responseMessageArabic":null,"responseCode":0,"responseStatus":"Successful","pubKeyValue":null,"tranDateTime":"240719091100","creationDate":null,"panCategory":null,"balance":null,"payeesCount":0,"payeesList":null,"billInfo":"{total:\"787.22516\",BillAmount:\"592.717\",lastInvoiceDate:\"20190630000000\",contractNumber:\"1037225044\",unbilledAmount:\"194.50816\"}","RespPAN":"9888081119999455925","RespExpDate":null,"RespIPIN":null,"regType":null,"financialInstitutionId":null,"respUserName":null,"respUserPassword":null,"RespVoucherNumber":null,"voucherCode":null,"serviceInfo":null,"originalTransaction":null,"originalTranType":null,"originalTranUUID":null,"respUUID":null,"resp_acqTranFee":0,"resp_issuerTranFee":0,"resp_fromAccount":null,"resp_accountCurrency":null,"resp_tranAmount":0,"resp_paymentInfo":"0990999007","toCard":null,"rittalFee":0,"clientFee":0,"voucherNumber":null}
//                        {total:\"787.22516\",BillAmount:\"592.717\",lastInvoiceDate:\"20190630000000\",contractNumber:\"1037225044\",unbilledAmount:\"194.50816\"}

                        mContractNumber    = billInfo.getString("contractNumber");
                        mTotalAmount       = billInfo.getString("total");
                        mBilledAmount      = billInfo.getString("BillAmount");
                        mUnbilledAmount    = billInfo.getString("unbilledAmount");
                        mLastInvoiceDate   = billInfo.getString("lastInvoiceDate");

                        addText(res_date,formatDate(mResponseDate));
                        if(transactionSource.toUpperCase().equals("CARD")){
                            addText(res_pan, hashPan(mResponsePan));
                            res_pan_titel.setVisibility(View.VISIBLE);
                        }
                        addText(res_phone,mPhoneNumber);

                        addText(res_contract,mContractNumber);
                        addText(res_total_bill_amount,mTotalAmount+getResourseString(activity,R.string.sdg_hint,lang));
                        res_total_bill_amount_titel.setVisibility(View.VISIBLE);
                        addText(res_bill_amount,mBilledAmount+getResourseString(activity,R.string.sdg_hint,lang));
                        res_bill_amount_titel.setVisibility(View.VISIBLE);
                        addText(res_unbill_amount,mUnbilledAmount+getResourseString(activity,R.string.sdg_hint,lang));
                        addText(res_last_invoice_date,rittal.formatDateLastInvoice(mLastInvoiceDate));
                        addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                        res_fee_title.setVisibility(View.VISIBLE);
                        addText(res_operator,mOperator);
                    }
                    else if(opid.equals("6")){
                        addText(res_service,activity.getString(R.string.title_bill_iquery));
                        rittal.log("opreter name", opid + activity.getString(R.string.sudani));
                        mOperator = activity.getString(R.string.sudani);
                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));


                        mSubscriberId    = billInfo.getString("SubscriberID");
                        mBilledAmount      = billInfo.getString("billAmount");

                        addText(res_date,formatDate(mResponseDate));
                        if(transactionSource.toUpperCase().equals("CARD")){
                            addText(res_pan, hashPan(mResponsePan));
                            res_pan_titel.setVisibility(View.VISIBLE);
                        }
                        addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                        res_fee_title.setVisibility(View.VISIBLE);
                        addText(res_phone,mPhoneNumber);
                        addText(res_operator,mOperator);
                        addText(res_subscriber_id,mSubscriberId);
                        addText(res_bill_amount,mBilledAmount+" "+ activity.getString(R.string.sdg_hint));
                        res_bill_amount_titel.setVisibility(View.VISIBLE);

                    }
                    else if(opid.equals("8")){

                        addText(res_service,activity.getString(R.string.edu_form));
                        addText(res_date,formatDate(mResponseDate));
                        if(transactionSource.toUpperCase().equals("CARD")){
                            addText(res_pan, hashPan(mResponsePan));
                            res_pan_titel.setVisibility(View.VISIBLE);
                        }
                        addText(res_fee,mResponseFee);
                        res_fee_title.setVisibility(View.VISIBLE);
                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));

                        englishName    = billInfo.getString("englishName");
                        receiptNo      = billInfo.getString("receiptNo");
                        arabicName     = billInfo.getString("arabicName");
                        dueAmount      = billInfo.getString("dueAmount");
                        formNo         = billInfo.getString("formNo");
                        res_customer_name.setText(activity.getString(R.string.studant_name));

                        addText(res_customer_name,arabicName);
                        addText(res_receipt_number,receiptNo);
                        addText(res_due_amount,dueAmount);
                        addText(res_form_no,formNo);

                        pay.setText(activity.getString(R.string.pay));
                        pay.setVisibility(View.VISIBLE);
                        pay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                pay.setVisibility(View.GONE);
                              doSudaneseCertificatePayment(pan,expiry_date,ipin,paymentInfo,amount);
                            }
                        });

                    }
                    else if(opid.equals("10")){
                        addText(res_service,activity.getString(R.string.edu_form));
                        addText(res_date,formatDate(mResponseDate));
                        if(transactionSource.toUpperCase().equals("CARD")){
                            addText(res_pan, hashPan(mResponsePan));
                            res_pan_titel.setVisibility(View.VISIBLE);
                        }
                        addText(res_fee,mResponseFee);
                        res_fee_title.setVisibility(View.VISIBLE);
                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));


                        englishName    = billInfo.getString("englishName");
                        arabicName     = billInfo.getString("arabicName");
                        studentNo      = billInfo.getString("studentNo");
                        receiptNo      = billInfo.getString("receiptNo");
                        arabicName     = billInfo.getString("arabicName");
                        dueAmount      = billInfo.getString("dueAmount");
                        formNo         = billInfo.getString("formNo");


                        addText(res_customer_name,englishName);
                        addText(res_phone,studentNo);
                        addText(res_receipt_number,receiptNo);
                        addText(res_due_amount,dueAmount);
                        addText(res_form_no,formNo);

                        pay.setText(activity.getString(R.string.pay));
                        pay.setVisibility(View.VISIBLE);
                        pay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                pay.setVisibility(View.GONE);
                                doArabCertificatePayment(pan,expiry_date,ipin,paymentInfo,amount);
                            }
                        });
                    }
                    else {

                    }
                    print.setVisibility(View.VISIBLE);
                    share.setVisibility(View.VISIBLE);
                }

            }
            else if(service.equals("5")){

                if(mResponseCode.equals("0")){
                    if(opid.equals("2") || opid.equals("4") || opid.equals("6")){
                        rittal.log("service name", service + activity.getString(R.string.title_bill_payment));
                        addText(res_service,activity.getString(R.string.title_bill_payment));
                        mResponseDate       = response.getString("tranDateTime");
                        mResponsePan        = response.getString("RespPAN");
                        mResponseFee        = response.getString("resp_issuerTranFee");
                        mTotalAmount        = response.getString("resp_tranAmount");
                        mPhoneNumber        = response.getString("resp_paymentInfo");

                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));


                        String mOperator = "";
                        if(opid.equals("2")){
                            mOperator = activity.getString(R.string.zain);
                            mReceiptNo        = billInfo.getString("receiptNo");
                            addText(res_receipt_number,mReceiptNo);
                        }else if(opid.equals("4")){
                            mOperator = activity.getString(R.string.mtn);
                            try {
                                mReceiptNo        = billInfo.getString("receiptNo");
                                addText(res_receipt_number,mReceiptNo);
                            }catch (Exception e){}
                        }else  if(opid.equals("6")){
                            mOperator = activity.getString(R.string.sudani);
                            try {
                                mSubscriberId    = billInfo.getString("SubscriberID");
                                mBilledAmount      = billInfo.getString("billAmount");
                                addText(res_receipt_number,mReceiptNo);
                                addText(res_subscriber_id,mSubscriberId);
                                addText(res_bill_amount,mBilledAmount+" "+ activity.getString(R.string.sdg_hint));
                                res_bill_amount_titel.setVisibility(View.VISIBLE);
                            }catch (Exception e){}
                        }else{

                        }

                        addText(res_date,formatDate(mResponseDate));
                        if(transactionSource.toUpperCase().equals("CARD")){
                            addText(res_pan, hashPan(mResponsePan));
                            res_pan_titel.setVisibility(View.VISIBLE);
                            share.setVisibility(View.VISIBLE);

                        }
                        addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                        res_fee_title.setVisibility(View.VISIBLE);
                        addText(res_operator,mOperator);

                        addText(res_phone,mPhoneNumber);
                        addText(res_amount,mTotalAmount+" "+ activity.getString(R.string.sdg_hint));

                        print.setVisibility(View.VISIBLE);
                        share.setVisibility(View.VISIBLE);
                    }
                    else if(opid.equals("7")){
                        addText(res_service,activity.getString(R.string.electrivity));
                        try {
                            mResponseDate       = response.getString("tranDateTime");
                        }catch (Exception e){
                        }
                        mResponsePan    = response.getString("RespPAN");
                        mAmount         = response.getString("resp_tranAmount");
                        mResponseFee   = response.getString("resp_issuerTranFee");

                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));


                        mMeterNO        = billInfo.getString("meterNumber");
                        mCustomerName   = billInfo.getString("customerName");
                        mUnits          = billInfo.getString("unitsInKWh");

                        waterFees       = billInfo.getString("waterFees");
                        opertorMessage  = billInfo.getString("opertorMessage");
                        meterFees       = billInfo.getString("meterFees");
                        accountNo       = billInfo.getString("accountNo");
                        mToken          = formatToken(billInfo.getString("token"));



                        addText(res_date,formatDate(mResponseDate));
                        if(transactionSource.toUpperCase().equals("CARD")){
                            addText(res_pan, hashPan(mResponsePan));
                            res_pan_titel.setVisibility(View.VISIBLE);
                        }



                        addText(res_meter_number,mMeterNO);

                        addText(res_meter_fees,meterFees);
                        addText(res_water_fees,waterFees);
                        addText(res_operater_message,opertorMessage);
                        addText(res_account_number,accountNo);

                        addText(res_amount,mAmount +" "+ activity.getString(R.string.sdg_hint));
                        addText(res_units,mUnits+"K/W");
                        addText(res_customer_name,mCustomerName);
                        if(transactionSource.toUpperCase().equals("CARD")){
                            addText(res_fee,mResponseFee +" "+ activity.getString(R.string.sdg_hint));
                            res_fee_title.setVisibility(View.VISIBLE);
                        }
                        addText(res_token,mToken);
                        res_token_title.setVisibility(View.VISIBLE);

                        //{"responseMessage":"Approved","responseMessageArabic":null,"responseCode":0,
                        // "responseStatus":"Successful","pubKeyValue":null,"tranDateTime":"300719092800",
                        // "creationDate":null,"panCategory":null,"balance":"{leger:-174223.09,available:96528887.15}",
                        // "payeesCount":0,"payeesList":null,
                        // "billInfo":"{customerName:\"ALSAFIE BAKHIEYT HEMYDAN\",unitsInKWh:\"66.7\",
                        // waterFees:\"0.00\",opertorMessage:\"Credit Purchase\",meterFees:\"0\",
                        // token:\"07246305192693082213\",accountNo:\"AM042111907231\",
                        // meterNumber:\"04111111111\",netAmount:\"10\"}",
                        // "RespPAN":"9888190130385682","RespExpDate":null,"RespIPIN":null,"regType":null,
                        // "financialInstitutionId":null,"respUserName":null,"respUserPassword":null,
                        // "RespVoucherNumber":null,"voucherCode":null,"serviceInfo":null,"originalTransaction":null,
                        // "originalTranType":null,"originalTranUUID":null,"respUUID":null,"resp_acqTranFee":0,
                        // "resp_issuerTranFee":-1.5,"resp_fromAccount":null,"resp_accountCurrency":null,
                        // "resp_tranAmount":5,"resp_paymentInfo":"04111111111","toCard":null,"rittalFee":0,
                        // "clientFee":0,"voucherNumber":null}
                    }
                    else if(opid.equals("8")){
                        addText(res_service,activity.getString(R.string.edu_form));
                        mResponseDate       = response.getString("tranDateTime");
                        mResponsePan        = response.getString("RespPAN");
                        mResponseFee        = response.getString("resp_issuerTranFee");

                        addText(res_date,formatDate(mResponseDate));
                        if(transactionSource.toUpperCase().equals("CARD")){
                            addText(res_pan, hashPan(mResponsePan));
                            res_pan_titel.setVisibility(View.VISIBLE);
                        }
                        mAmount      = response.getString("resp_tranAmount");
                        addText(res_fee,mResponseFee);
                        res_fee_title.setVisibility(View.VISIBLE);
                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));

                        englishName    = billInfo.getString("englishName");
                        receiptNo      = billInfo.getString("receiptNo");
                        arabicName     = billInfo.getString("arabicName");
                        //dueAmount      = billInfo.getString("dueAmount");
                        formNo         = billInfo.getString("formNo");
                        res_customer_name.setText(activity.getString(R.string.studant_name));

                        addText(res_customer_name,arabicName);
                        addText(res_receipt_number,receiptNo);
                        addText(res_amount,mAmount);
                        addText(res_form_no,formNo);

//                        pay.setText(activity.getString(R.string.pay));
//                        pay.setVisibility(View.VISIBLE);
//                        pay.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                dialog.dismiss();
//                                pay.setVisibility(View.GONE);
//                                doSudaneseCertificatePayment(pan,expiry_date,ipin,paymentInfo,amount);
//                            }
//                        });

                    }
                    else if(opid.equals("10")){
                        addText(res_service,activity.getString(R.string.edu_form));
                        mResponseDate       = response.getString("tranDateTime");
                        mResponsePan        = response.getString("RespPAN");
                        mResponseFee        = response.getString("resp_issuerTranFee");
                        mAmount      = response.getString("resp_tranAmount");
                        addText(res_date,formatDate(mResponseDate));
                        if(transactionSource.toUpperCase().equals("CARD")){
                            addText(res_pan, hashPan(mResponsePan));
                            res_pan_titel.setVisibility(View.VISIBLE);
                        }
                        addText(res_fee,mResponseFee);
                        res_fee_title.setVisibility(View.VISIBLE);
                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));


                        englishName    = billInfo.getString("englishName");
                        arabicName     = billInfo.getString("arabicName");
                        studentNo      = billInfo.getString("studentNo");
                        receiptNo      = billInfo.getString("receiptNo");
                        arabicName     = billInfo.getString("arabicName");
                        //dueAmount      = billInfo.getString("dueAmount");
                        formNo         = billInfo.getString("formNo");


                        res_phone.setText(activity.getString(R.string.set_number));
                        addText(res_customer_name,englishName);
                        addText(res_phone,studentNo);
                        addText(res_receipt_number,receiptNo);
                        addText(res_amount,mAmount);
                        addText(res_form_no,formNo);

//                        pay.setText(activity.getString(R.string.pay));
//                        pay.setVisibility(View.VISIBLE);
//                        pay.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                dialog.dismiss();
//                                pay.setVisibility(View.GONE);
//                                doArabCertificatePayment(pan,expiry_date,ipin,paymentInfo,amount);
//                            }
//                        });
                    }
                    else{}

                }
            }
            else if(service.equals("6")){
                rittal.log("service name", service + activity.getString(R.string.title_top_up));

                if(mResponseCode.equals("0")){
                    addText(res_service,activity.getString(R.string.title_top_up));
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    mResponseFee        = response.getString("resp_issuerTranFee");
                    mTranAmount        = response.getString("resp_tranAmount");
                    mPhoneNumber        = response.getString("resp_paymentInfo");

                    String mOperator = "";
                    if(opid.equals("1")){
                        mOperator = activity.getString(R.string.zain);
                    }else if(opid.equals("3")){
                        mOperator = activity.getString(R.string.mtn);
                    }else  if(opid.equals("5")){
                        mOperator = activity.getString(R.string.sudani);
                    }else{

                    }

                    addText(res_date,formatDate(mResponseDate));
                    if(transactionSource.toUpperCase().equals("CARD")){
                        addText(res_pan, hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);
                        share.setVisibility(View.VISIBLE);

                    }
                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                    res_fee_title.setVisibility(View.VISIBLE);
                    addText(res_operator,mOperator);
                    addText(res_phone,mPhoneNumber);
                    addText(res_amount,mTranAmount+" "+ activity.getString(R.string.sdg_hint));
                    print.setVisibility(View.VISIBLE);
                    share.setVisibility(View.VISIBLE);

                }

            }
            else if(service.equals("7")){ }
            else if(service.equals("8")){
                rittal.log("service name", service + activity.getString(R.string.title_generate_voucher));
                addText(res_service,activity.getString(R.string.title_generate_voucher));
                if(mResponseCode.equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    mResponseFee        = response.getString("resp_issuerTranFee");
                    mTranAmount         = response.getString("resp_tranAmount");
                    //mVoucherNumber      = response.getString("RespVoucherNumber");
                    mVoucherCode     = response.getString("voucherCode");



                    addText(res_date,formatDate(mResponseDate));
                    if(transactionSource.toUpperCase().equals("CARD")){
                        addText(res_pan, hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);
                    }
                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                    res_fee_title.setVisibility(View.VISIBLE);
                    addText(res_amount,mTranAmount);
                    //addText(res_voucher_number,mVoucherNumber);
                    addText(res_voucher_code,mVoucherCode);

                    print.setVisibility(View.VISIBLE);
                    share.setVisibility(View.VISIBLE);
                }

            }
            else if(service.equals("9")){
                rittal.log("service name", service + activity.getString(R.string.title_e15));
                addText(res_service,activity.getString(R.string.title_e15));
                if(mResponseCode.equals("0")){

                    if(opid.equals("2")){
                        mResponseDate       = response.getString("tranDateTime");
                        mResponsePan        = response.getString("RespPAN");
                        mResponseFee        = response.getString("resp_issuerTranFee");
                        mPhoneNumber        = response.getString("resp_paymentInfo");

                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));

                        mUnitName       = billInfo.getString("UnitName");
                        mServiceName    = billInfo.getString("ServiceName");
                        mTotalAmount    = billInfo.getString("TotalAmount");
                        mInvoiceExpiry  = billInfo.getString("InvoiceExpiry");
                        mDueAmount      = billInfo.getString("DueAmount");
                        mReferenceId    = billInfo.getString("ReferenceId");
                        mInvoiceStatus  = billInfo.getString("InvoiceStatus");
                        mPayerName      = billInfo.getString("PayerName");

                        String status_text = "";
                        if(mInvoiceStatus.equals("0")){
                            status_text = activity.getString(R.string.canceled_invoice);
                        }else if(mInvoiceStatus.equals("1")){
                            status_text = activity.getString(R.string.new_invoice);
                        }else if(mInvoiceStatus.equals("2")){
                            status_text = activity.getString(R.string.paid_invoice);
                        }else {
                            status_text = "";
                        }


                        addText(res_date,formatDate(mResponseDate));
                        if(transactionSource.toUpperCase().equals("CARD")){
                            addText(res_pan, hashPan(mResponsePan));
                            res_pan_titel.setVisibility(View.VISIBLE);
                        }
                        addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                        res_fee_title.setVisibility(View.VISIBLE);
                        addText(res_amount,mTotalAmount+" "+ activity.getString(R.string.sdg_hint));
                        addText(res_invoice_num,mPhoneNumber);
                        addText(res_unit_name,mUnitName);
                        addText(res_service_name,mServiceName);
                        addText(res_invoice_expiry_date,mInvoiceExpiry);
                        addText(res_due_amount,mDueAmount+" "+ activity.getString(R.string.sdg_hint));
                        addText(res_reference_id,mReferenceId);
                        addText(res_invoice_status,status_text);
                        addText(res_payer_name,mPayerName);
                    }
                    print.setVisibility(View.VISIBLE);
                    share.setVisibility(View.VISIBLE);
                }
            }
            else if(service.equals("10")){
                rittal.log("service name", service + activity.getString(R.string.title_e15));
                addText(res_service,activity.getString(R.string.title_e15));

                if(mResponseCode.equals("0")){
                    if(opid.equals("6")){
                        try {
                            mResponseDate       = response.getString("tranDateTime");
                        }catch (Exception e){
                                rittal.log("Exception",e.toString());
                        }                        mResponsePan        = response.getString("RespPAN");
                        mResponseFee        = response.getString("resp_issuerTranFee");
                        mPhoneNumber        = response.getString("resp_paymentInfo");

                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));

                        mUnitName       = billInfo.getString("UnitName");
                        mServiceName    = billInfo.getString("ServiceName");
                        mTotalAmount    = billInfo.getString("TotalAmount");
                        mReferenceId    = billInfo.getString("ReferenceId");
                        mPayerName      = billInfo.getString("PayerName");


                        addText(res_date,formatDate(mResponseDate));
                        if(transactionSource.toUpperCase().equals("CARD")){
                            addText(res_pan, hashPan(mResponsePan));
                            res_pan_titel.setVisibility(View.VISIBLE);
                        }
                        addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                        res_fee_title.setVisibility(View.VISIBLE);
                        addText(res_amount,mTotalAmount+" "+ activity.getString(R.string.sdg_hint));
                        addText(res_unit_name,mUnitName);
                        addText(res_invoice_num,mPhoneNumber);
                        addText(res_service_name,mServiceName);
                        addText(res_reference_id,mReferenceId);
                        addText(res_payer_name,mPayerName);
                    }
                    print.setVisibility(View.VISIBLE);
                    share.setVisibility(View.VISIBLE);
                }
            }
            else if(service.equals("11")){
                if(opid.equals("G")){

                    addText(res_service,activity.getString(R.string.inquiry_electricity));
                    if(mResponseCode.equals("1")){
                        try {
                            mResponseDate       = response.getString("tranDateTime");

                        }catch (Exception e){

                        }
                        //mResponseDate       = response.getString("tranDateTime");
                        //mResponsePan        = response.getString("RespPAN");
                        //mResponseFee        = response.getString("resp_acqTranFee");
                        mResponsePan            = response.getString("PAN");
                        mMeterNO                = response.getString("MeterNO");
                        mWaterArrear            = response.getString("ServiceCharge");
                        mMeterArrear            = response.getString("feesAMT");
                        mPAmount                = response.getString("PAmount");

                        mFisrtPowerKwt          = response.getString("FisrtPowerKwt");
                        mFisrtPoweramt          = response.getString("FisrtPoweramt");
                        mFisrtPowerPrice        = response.getString("FisrtPowerPrice");

                        mSecPowerKwt            = response.getString("SecPowerKwt");
                        mSecPoweramt            = response.getString("SecPoweramt");
                        mSecPowerPrice          = response.getString("SecPowerPrice");

                        mAmount                 = response.getString("Amount");
                        mUnits                  = response.getString("Units");
                        mFisrtPowerID           = response.getString("FisrtPowerID");
                        mSecPowerID             = response.getString("SecPowerID");
                        mKWPrice                = response.getString("FisrtPowerPrice");
                        mSecondKWPrice          = response.getString("SecPowerPrice");
                        mToken                  = response.getString("Token");
                        mCustomerName           = response.getString("CustomerName");
                        mResponseFee            = response.getString("ct_fees");
                        mRittalFee              = response.getString("elec_fees");


//                        "FisrtPowerID":"1","FisrtPoweramt":"1.5","FisrtPowerKwt":"10","FisrtPowerPrice":"0.1500",
//                    "SecPowerID":"0","SecPoweramt":"0","SecPowerKwt":"0","SecPowerPrice":"0",

                        addText(res_date,formatDate(mResponseDate));
                        if(transactionSource.toUpperCase().equals("CARD")){
                            addText(res_pan, hashPan(mResponsePan));
                            res_pan_titel.setVisibility(View.VISIBLE);
                        }
                        addText(res_meter_number,mMeterNO);
                        addText(res_meter_arrear,mMeterArrear+" "+ activity.getString(R.string.sdg_hint));

                        addText(res_water_fees,mWaterArrear+" "+ activity.getString(R.string.sdg_hint));


                        addText(res_amount,mAmount +" "+ activity.getString(R.string.sdg_hint));
                        addText(res_purchase_amount, mPAmount+" "+ activity.getString(R.string.sdg_hint));
                        addText(res_units,mUnits+""+ activity.getString(R.string.k_w));

                        String pricing = "";

                        String firstPrice   = mFisrtPowerKwt+ " "+activity.getString(R.string.k_w) +" "+ mFisrtPowerPrice +" "+activity.getString(R.string.sdg_hint).trim()+"/"+activity.getString(R.string.k_w);
                        String secendPrice  = mSecPowerKwt+ " "+activity.getString(R.string.k_w) +" "+ mSecPowerPrice +" "+activity.getString(R.string.sdg_hint).trim()+"/"+activity.getString(R.string.k_w);

                        if(mFisrtPowerID.equals("1")){
                            pricing = " \n "+firstPrice;
                        }else if(mSecPowerID.equals("1")){
                            pricing = " \n "+secendPrice;
                        }
                        addText(res_unit_price,pricing);
                        addText(res_customer_name,mCustomerName);


//                        smsText = activity.getString(R.string.app_name) + "\n";
//                        smsText = res_service.getText().toString() + "\n";
//
//                        smsText = smsText + res_meter_number.getText().toString() + "\n";
//                        smsText = smsText + res_customer_name.getText().toString() + "\n";
//                        smsText = smsText + res_amount.getText().toString() + "\n";
//                        smsText = smsText + res_meter_arrear.getText().toString() + "\n";
//                        smsText = smsText + res_water_fees.getText().toString() + "\n";
//                        smsText = smsText + res_purchase_amount.getText().toString() + "\n";
//                        smsText = smsText + res_units.getText().toString() + "\n";
//                        smsText = smsText + res_unit_price.getText().toString() + "\n";
//                        smsText = smsText + pricing + "\n";


                        //addText(res_fee,mResponseFee);
                        //addText(res_rittal_fee,mRittalFee);
                        //send.setVisibility(View.VISIBLE);
                        pay.setVisibility(View.VISIBLE);
                        pay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                electricity(pan,expiry_date,ipin,counter_num,amount,"C");
                            }
                        });


                    }
//                    Response: {"RRN":"e7044ef9-9fa9-4874-945e-6809baea0eba",
//                    "MeterNO":"04184260000",
//                    "Amount":"63","PAmount":"61",
//                    "STS":null,"responseMessage":"Confirmed","responseCode":1,"responseMessageArabic":null,"Units":"10","Token":null,
//                    "ServiceCharge":"57.00",
//                    "CustomerName":"mhmoud hasan kona mohamed","Tarrif":null,"units":null,"stateId":null,"Curent_Balance":0,"Invoice":null,"calcQty":null,"buyTimes":null,"VAt":null,"verifyCode":null,"stampTax":null,"code":null,"netAMT":0,"commAMT":0,"refCode":null,"regCode":null,"Rslt_State":null,"vendQty":null,"vendAMT":0,"supplyAMT":0,"transTime":null,"transID":null,
//                    "feesAMT":"2.5",
//                    "checkCode":null,"Chanel":null,
//                    "FisrtPowerID":"1","FisrtPoweramt":"1.5","FisrtPowerKwt":"10","FisrtPowerPrice":"0.1500",
//                    "SecPowerID":"0","SecPoweramt":"0","SecPowerKwt":"0","SecPowerPrice":"0",
//                    "DetailedArreas":null,
//                    "ArrearsItems":"WATE : 57.00 *** ",
//                    "MIN_AMT":null,"keyId_1":null,"keyName_1":null,"KeyToken_1":null,"keyId_2":null,"keyName_2":null,"keyToken_2":null,"KeyId_2":null,"elec_fees":null,"ct_fees":null,"PAN":"9222081110072758345","tranDateTime":"031019105600"}
//                    Response: {"RRN":"1d23887b-71a2-4d5d-bfb3-9095fe38fbc9","MeterNO":"04184260000","Amount":"62","PAmount":"60","STS":null,"responseMessage":"Confirmed","responseCode":1,"responseMessageArabic":null,"Units":"3.4","Token":null,"ServiceCharge":"57.00","CustomerName":"mhmoud hasan kona mohamed","Tarrif":null,"units":null,"stateId":null,"Curent_Balance":0,"Invoice":null,"calcQty":null,"buyTimes":null,"VAt":null,"verifyCode":null,"stampTax":null,"code":null,"netAMT":0,"commAMT":0,"refCode":null,"regCode":null,"Rslt_State":null,"vendQty":null,"vendAMT":0,"supplyAMT":0,"transTime":null,"transID":null,"feesAMT":"2.5","checkCode":null,"Chanel":null,"FisrtPowerID":"1","FisrtPoweramt":"0.5","FisrtPowerKwt":"3.4","FisrtPowerPrice":"0.1500","SecPowerID":"0","SecPoweramt":"0","SecPowerKwt":"0","SecPowerPrice":"0","DetailedArreas":null,"ArrearsItems":"WATE : 57.00 *** ","MIN_AMT":null,"keyId_1":null,"keyName_1":null,"KeyToken_1":null,"keyId_2":null,"keyName_2":null,"keyToken_2":null,"KeyId_2":null,"elec_fees":null,"ct_fees":null,"PAN":"9222081110072758345","tranDateTime":"031019113812"}
//                    Response: {"RRN":"1d23887b-71a2-4d5d-bfb3-9095fe38fbc9","MeterNO":"04184260000","Amount":"62","PAmount":"60","STS":null,"responseMessage":"Confirmed","responseCode":1,"responseMessageArabic":null,"Units":"3.4","Token":null,"ServiceCharge":"57.00","CustomerName":"mhmoud hasan kona mohamed","Tarrif":null,"units":null,"stateId":null,"Curent_Balance":0,"Invoice":null,"calcQty":null,"buyTimes":null,"VAt":null,"verifyCode":null,"stampTax":null,"code":null,"netAMT":0,"commAMT":0,"refCode":null,"regCode":null,"Rslt_State":null,"vendQty":null,"vendAMT":0,"supplyAMT":0,"transTime":null,"transID":null,"feesAMT":"2.5","checkCode":null,"Chanel":null,"FisrtPowerID":"1","FisrtPoweramt":"0.5","FisrtPowerKwt":"3.4","FisrtPowerPrice":"0.1500","SecPowerID":"0","SecPoweramt":"0","SecPowerKwt":"0","SecPowerPrice":"0","DetailedArreas":null,"ArrearsItems":"WATE : 57.00 *** ","MIN_AMT":null,"keyId_1":null,"keyName_1":null,"KeyToken_1":null,"keyId_2":null,"keyName_2":null,"keyToken_2":null,"KeyId_2":null,"elec_fees":null,"ct_fees":null,"PAN":"9222081110072758345","tranDateTime":"031019113812"}
                }
                if(opid.equals("C")){
                    addText(res_service,activity.getString(R.string.purchase_electricity));
                    if(mResponseCode.equals("1")){
                        try {
                            mResponseDate       = response.getString("tranDateTime");
                        }catch (Exception e){
                        }
                        mResponsePan            = response.getString("PAN");
                        mMeterNO                = response.getString("MeterNO");
                        mPAmount                = response.getString("PAmount");
                        mAmount                 = response.getString("Amount");
                        mUnits                  = response.getString("Units");
                        mWaterArrear            = response.getString("ServiceCharge");
                        mMeterArrear            = response.getString("feesAMT");
                        mPAmount                = response.getString("PAmount");
                        mFisrtPowerKwt          = response.getString("FisrtPowerKwt");
                        mFisrtPoweramt          = response.getString("FisrtPoweramt");
                        mFisrtPowerPrice        = response.getString("FisrtPowerPrice");

                        mSecPowerKwt            = response.getString("SecPowerKwt");
                        mSecPoweramt            = response.getString("SecPoweramt");
                        mSecPowerPrice          = response.getString("SecPowerPrice");

                        mKWPrice                = response.getString("FisrtPowerPrice");
                        mToken                  = formatToken(response.getString("Token"));
                        mCustomerName           = response.getString("CustomerName");

                        mFisrtPowerID           =  response.getString("FisrtPowerID");
                        mSecPowerID             =  response.getString("SecPowerID");

                        mKWPrice                =  response.getString("FisrtPowerPrice");
                        mSecondKWPrice          =  response.getString("SecPowerPrice");

                        mResponseFee            = response.getString("ct_fees");
                        mRittalFee              = response.getString("elec_fees");



                        addText(res_date,formatDate(mResponseDate));
                        if(transactionSource.toUpperCase().equals("CARD")){
                            addText(res_pan, hashPan(mResponsePan));
                            res_pan_titel.setVisibility(View.VISIBLE);
                            smsText = smsText + res_pan_titel.getText().toString() + "\n";
                            smsText = smsText + res_pan.getText().toString() + "\n";

                            addText(res_fee,mResponseFee +" "+ activity.getString(R.string.sdg_hint));
                            res_fee_title.setVisibility(View.VISIBLE);
                        }
                        addText(res_meter_arrear,mMeterArrear+" "+ activity.getString(R.string.sdg_hint));
                        addText(res_water_fees,mWaterArrear+" "+ activity.getString(R.string.sdg_hint));
                        addText(res_meter_number,mMeterNO);

                        addText(res_amount,mAmount +" "+ activity.getString(R.string.sdg_hint));
                        addText(res_purchase_amount, mPAmount+" "+ activity.getString(R.string.sdg_hint));
                        addText(res_units,mUnits+""+ activity.getString(R.string.k_w));

                        String pricing = "";

                        String firstPrice   = mFisrtPowerKwt+ " "+activity.getString(R.string.k_w) +" "+ mFisrtPowerPrice +" "+activity.getString(R.string.sdg_hint).trim()+"/"+activity.getString(R.string.k_w);
                        String secendPrice  = mSecPowerKwt+ " "+activity.getString(R.string.k_w) +" "+ mSecPowerPrice +" "+activity.getString(R.string.sdg_hint).trim()+"/"+activity.getString(R.string.k_w);

                        if(mFisrtPowerID.equals("1")){
                            pricing = " \n "+firstPrice;
                        }else if(mSecPowerID.equals("1")){
                            pricing = " \n "+secendPrice;
                        }

                        addText(res_unit_price,pricing);
                        addText(res_customer_name,mCustomerName);

                        addText(res_rittal_fee,mRittalFee +" "+ activity.getString(R.string.sdg_hint));
                        addText(res_token,mToken);
                        res_token_title.setVisibility(View.VISIBLE);


                        try {
                            smsText = "Rita-Pay" + "\n";
                            smsText = smsText + rittal.addTextForSMSEng(R.string.purchase_electricity,"") + "\n";

                            smsText = smsText + rittal.addTextForSMSEng(R.string.res_meter_number,mMeterNO) + "\n";
                            //smsText = smsText + res_customer_name.getText().toString() + "\n";
                            //smsText = smsText + res_amount.getText().toString() + "\n";
                            //smsText = smsText + res_meter_arrear.getText().toString() + "\n";
                            //smsText = smsText + res_water_fees.getText().toString() + "\n";
                            //smsText = smsText + res_purchase_amount.getText().toString() + "\n";
                            //smsText = smsText + res_units.getText().toString() + "\n";
                            smsText = smsText + rittal.addTextForSMSEng(R.string.res_units,mUnits) + "\n";
                            //smsText = smsText + res_unit_price.getText().toString() + "\n";
                            //smsText = smsText + pricing + "\n";
                            //smsText = smsText + res_rittal_fee.getText().toString() + "\n";


                            if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                                //smsText = smsText + res_fee_title.getText().toString()+ res_fee.getText().toString() + "\n";

                            }
                            smsText = smsText + rittal.addTextForSMSEng(R.string.res_token,mToken) + "\n";

                        }catch (Exception e){}




                        print.setVisibility(View.VISIBLE);
                        share.setVisibility(View.VISIBLE);
                        send.setVisibility(View.VISIBLE);

//                        Response: {"RRN":"9ae3030d-0af7-4d7c-9cb7-851d98a77b62","MeterNO":"04184260000","Amount":"62","PAmount":"60","STS":null,"responseMessage":"Confirmed","responseCode":1,"responseMessageArabic":null,"Units":"3.4","Token":"71142929723775635382","ServiceCharge":"57.00","CustomerName":"mhmoud hasan kona mohamed","Tarrif":null,"units":null,"stateId":null,"Curent_Balance":0,"Invoice":null,"calcQty":null,"buyTimes":null,"VAt":null,"verifyCode":null,"stampTax":null,"code":null,"netAMT":0,"commAMT":0,"refCode":null,"regCode":null,"Rslt_State":null,"vendQty":null,"vendAMT":0,"supplyAMT":0,"transTime":null,"transID":null,"feesAMT":"2.5","checkCode":null,"Chanel":null,"FisrtPowerID":"1","FisrtPoweramt":"0.5","FisrtPowerKwt":"3.4","FisrtPowerPrice":"0.1500","SecPowerID":"0","SecPoweramt":"0","SecPowerKwt":"0","SecPowerPrice":"0","DetailedArreas":null,"ArrearsItems":"WATE : 57.00 *** ","MIN_AMT":null,"keyId_1":null,"keyName_1":null,"KeyToken_1":null,"keyId_2":null,"keyName_2":null,"keyToken_2":null,"KeyId_2":null,"elec_fees":"2","ct_fees":"1","PAN":"9222081110072758345","tranDateTime":"031019122741"}
                    }
                }
            }
            else if(service.equals("12")){
                addText(res_service,activity.getString(R.string.cash_out));
                if(response.getString("responseCode").equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    // mResponseBalance = response.getString("balance");
                    mAmount        = response.getString("resp_tranAmount");
                    // mResponseFee        = response.getString("resp_acqTranFee");
                    mRittalFee          = response.getString("rittalFee");
                    mResponseFee        = response.getString("resp_issuerTranFee");
                    addText(res_date,formatDate(mResponseDate));
                    if(transactionSource.toUpperCase().equals("CARD")){
                        addText(res_pan, hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);
                    }
                    addText(res_amount,mAmount+" "+ activity.getString(R.string.sdg_hint));
                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                    res_fee_title.setVisibility(View.VISIBLE);
                    addText(res_rittal_fee,mRittalFee+" "+ activity.getString(R.string.sdg_hint));
                    print.setVisibility(View.VISIBLE);
                    share.setVisibility(View.VISIBLE);

                }
            }
            else if(service.equals("13")){
                addText(res_service,activity.getString(R.string.recharge_card_title));
                if(response.getString("responseCode").equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    try {
                        mToCard        = response.getString("toCard");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    // mResponseBalance = response.getString("balance");
                    mAmount        = response.getString("resp_tranAmount");
                    // mResponseFee        = response.getString("resp_acqTranFee");
                    mRittalFee          = response.getString("rittalFee");
                    //   mResponseFee        = response.getString("resp_issuerTranFee");
                    addText(res_date,formatDate(mResponseDate));
                    if(transactionSource.toUpperCase().equals("CARD")){
                        addText(res_pan, hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);
                    }
                    addText(res_amount,mAmount+" "+ activity.getString(R.string.sdg_hint));
                    addText(res_to_card,hashPan(mToCard));
                    res_to_card_title.setVisibility(View.VISIBLE);
                    // addText(res_fee,mResponseFee);
                    addText(res_rittal_fee,mRittalFee+" "+ activity.getString(R.string.sdg_hint));

                    print.setVisibility(View.VISIBLE);
                    share.setVisibility(View.VISIBLE);

                }
            }
            else if(service.equals("14")){
                addText(res_service,activity.getString(R.string.recharge_account_title));
                if(response.getString("responseCode").equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    // mResponseBalance = response.getString("balance");
                    mAmount        = response.getString("resp_tranAmount");
                    mResponseFee        = response.getString("resp_issuerTranFee");
                    mRittalFee          = response.getString("rittalFee");
                    //   mResponseFee        = response.getString("resp_issuerTranFee");
                    addText(res_date,formatDate(mResponseDate));
                    if(transactionSource.toUpperCase().equals("CARD")){
                        addText(res_pan, hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);
                    }
                    addText(res_amount,mAmount+" "+ activity.getString(R.string.sdg_hint));
                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                    res_fee_title.setVisibility(View.VISIBLE);
                    addText(res_rittal_fee,mRittalFee+" "+ activity.getString(R.string.sdg_hint));

                    print.setVisibility(View.VISIBLE);
                    share.setVisibility(View.VISIBLE);

                }
            }
            else if(service.equals("15")){
                addText(res_service,activity.getString(R.string.knownGenerateVoucher));
                if(response.getString("responseCode").equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    mVoucherNumber      = response.getString("voucherNumber");
                    mAmount             = response.getString("resp_tranAmount");
                    mSenderName         = response.getString("respUserName");
                    mReceiverName       = response.getString("respUserPassword");
                    mRittalFee          = response.getString("rittalFee");
                    mResponseFee        = response.getString("resp_issuerTranFee");

//                     OBJ: {"service":"15","customerID":"6f69601a-00ea-4d74-9677-4fc47eccf359","fromCard":"9222081110094611845","expiryDate":"2304","ipin":"03uckQXJTzjFMcxNsC8m+9rcuIUgI33hmwAaKHCoGECFxgOIBSMJ7NtxkT3mA3I3Yv\/M\/KVGMgdtZ7OWmTC6jw==","uuid":"f7c2f699-a223-4b6e-8b7f-f8d5b23204f8","tranAmount":"3",
//                    "senderName":"musab alrishabi","senderIdType":"1","senderId":"1111111111","senderPhone":"+249918883964",
//                    "receiverName":"musab alrishabi","receiverPhone":"+249929995414","receiverIdType":"1","receiverId":"123456789","voucherPassword":"1111111111"}

                    addText(res_date,formatDate(mResponseDate));
                    if(transactionSource.toUpperCase().equals("CARD")){
                        addText(res_pan,hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);
                    }
                    addText(res_voucher_number,mVoucherNumber);
                    addText(res_amount,mAmount+" "+ activity.getString(R.string.sdg_hint));
                    addText(res_s_name,mSenderName);
                    addText(res_r_name,mReceiverName);
                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                    res_fee_title.setVisibility(View.VISIBLE);
                    addText(res_rittal_fee,mRittalFee+" "+ activity.getString(R.string.sdg_hint));

                    print.setVisibility(View.VISIBLE);
                    share.setVisibility(View.VISIBLE);

                }
            }
            else if(service.equals("16")){
                addText(res_service,activity.getString(R.string.known_generate_voucher_cashout));
                if(response.getString("responseCode").equals("0")){
                    mResponseDate         = response.getString("tranDateTime");
//                    mResponsePan        = response.getString("RespPAN");
                    // mResponseBalance   = response.getString("balance");
                    mSenderName            = response.getString("respUserName");
                    mReceiverName          = response.getString("respUserPassword");
                    mAmount                = response.getString("resp_tranAmount");
                    mResponseFee           = response.getString("resp_issuerTranFee");
                    mRittalFee             = response.getString("rittalFee");

                    addText(res_date,formatDate(mResponseDate.replace("\\","")));
//                  addText(res_pan, hashPan(mResponsePan));

                    addText(res_s_name,mSenderName);
                    addText(res_r_name,mReceiverName);

                    addText(res_amount,mAmount+" "+ activity.getString(R.string.sdg_hint));
                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                    res_fee_title.setVisibility(View.VISIBLE);
                    addText(res_rittal_fee,mRittalFee+" "+ activity.getString(R.string.sdg_hint));

                    print.setVisibility(View.VISIBLE);
                    share.setVisibility(View.VISIBLE);
                }
            }
            else if(service.equals("18")){
                //balance inquiry receipt
                addText(res_service,activity.getString(R.string.special_payment));
                if(response.getString("responseCode").equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    mResponseBalance    = response.getString("resp_tranAmount");
                    mResponseFee        = response.getString("resp_issuerTranFee");

                    addText(res_date,formatDate(mResponseDate));
                    if(transactionSource.toUpperCase().equals("CARD")){
                        addText(res_pan, hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);
                    }
                    addText(res_amount,mResponseBalance+" "+ activity.getString(R.string.sdg_hint));
                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                    res_fee_title.setVisibility(View.VISIBLE);

                    print.setVisibility(View.VISIBLE);
                    share.setVisibility(View.VISIBLE);
                }

            }
            else{}
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            rittal.log("Response_dialog_exception",e.toString());
        }
        determinedStatus();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void determinedStatus(){
        if(service.equals("1")){
            if(mResponseCode.equals("100")){
                image =(ImageView) dialog.findViewById(R.id.iv_success);
                status = (TextView) dialog.findViewById(R.id.tv_success);
                image.setVisibility(View.VISIBLE);
                status.setVisibility(View.VISIBLE);
            }else{
                image =(ImageView) dialog.findViewById(R.id.iv_faild);
                status = (TextView) dialog.findViewById(R.id.tv_faild);
                image.setVisibility(View.VISIBLE);
                status.setVisibility(View.VISIBLE);
            }
        }
        else if(service.equals("11")){
            if(mResponseCode.equals("1")){
                image =(ImageView) dialog.findViewById(R.id.iv_success);
                status = (TextView) dialog.findViewById(R.id.tv_success);
                image.setVisibility(View.VISIBLE);
                status.setVisibility(View.VISIBLE);
            }else{
                image =(ImageView) dialog.findViewById(R.id.iv_faild);
                status = (TextView) dialog.findViewById(R.id.tv_faild);
                image.setVisibility(View.VISIBLE);
                status.setVisibility(View.VISIBLE);
            }

        }else {
            if(mResponseCode.equals("0")){
                image =(ImageView) dialog.findViewById(R.id.iv_success);
                status = (TextView) dialog.findViewById(R.id.tv_success);
                image.setVisibility(View.VISIBLE);
                status.setVisibility(View.VISIBLE);
            }else{
                image =(ImageView) dialog.findViewById(R.id.iv_faild);
                status = (TextView) dialog.findViewById(R.id.tv_faild);
                image.setVisibility(View.VISIBLE);
                status.setVisibility(View.VISIBLE);
            }
        }
    }

    private void addText(TextView textView, String text){
        textView.append(" "+text);
        textView.setVisibility(View.VISIBLE);
    }

    private String hashPan(String pan){

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

    private String formatDate(String text){
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

    private String formatToken(String text){
       // String token = text;
      StringBuilder str = new StringBuilder(text);
//        int idx = str.length() - 8;
//
//        while (idx > 0)
//        {
//            str.insert(idx, "-");
//            idx = idx - 8;
//        }


        int idx = str.length() - 4;

        while (idx > 0)
        {
            str.insert(idx, "-");
            idx = idx - 4;
        }
       // token.rep
      // token = text.substring(0,3)+" - "+text.substring(4,7)+" - "+text.substring(8,11)+" - "+text.substring(12,15)+" - "+text.substring(16,19);
        return String.valueOf(str);
    }

    private void electricity(final String pan , final String expiry_date , final String ipin , final String counter_num, final String amount, String flag) {

        final String uuid = UUID.randomUUID().toString();

        String ipin_ = "";

        String pk = application.getPublicKey();//"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANx4gKYSMv3CrWWsxdPfxDxFvl+Is/0kc1dvMI1yNWDXI3AgdI4127KMUOv7gmwZ6SnRsHX/KAM0IPRe0+Sa0vMCAwEAAQ==";

        ipin_ = IPINBlockGenerator.getIPINBlock(ipin,pk,uuid);

        String url = application.getBaseUrl()+"/consumer_services.aspx";

        rittal.log("pubKeyValue",rittal.getValueOfKey("pubKeyValue"));
        rittal.log("URL",url);
        rittal.log("ipin",ipin_);
        final String service = "11";
        final String op_id = flag;


        showprogres();
        JSONObject js = new JSONObject();
        try {
            js.put("service",service);
            js.put("customerID",rittal.getValueOfKey("user_id"));
            js.put("fromCard",pan);
            js.put("expiryDate",expiry_date);
            js.put("ipin",ipin_);
            js.put("uuid",uuid);
            js.put("meterNumber",counter_num);
            js.put("tranAmount",amount);
            js.put("flag",flag);
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
                        String mTransactionSource = "";
                        if(expiry_date.toUpperCase().equals("ACCC")){
                            mTransactionSource = "ACCOUNT";
                        }else{
                            mTransactionSource = "CARD";
                        }
                        try {
                            dataStorage.add_transaction(response.toString(),service,rittal.getValueOfKey("user_id"),op_id,mTransactionSource);
                        }catch (Exception e){

                        }

                        try {


                            ResponseDialog responseDialog = new ResponseDialog(activity,dialog,response,service,op_id,"","","","","","",null,mTransactionSource);
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
                            rittal.rittalToast(activity.getString(R.string.error_in_data_processing),"1","w");
                        }
                        dismsprogres();

                    }


                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        rittal.log("TAG Respons ErrorListener", error.toString());
                        rittal.rittalToast(activity.getString(R.string.error_on_network_connection),"1","w");
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

    private void doSudaneseCertificatePayment(String pan , final String exdate , String ipin,final String paymentInfo, String amount ) {

        final String uuid = UUID.randomUUID().toString();

        String ipin_ = "";
        String pk = application.getPublicKey();//"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==";//rittal.getValueOfKey("pk");// "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANx4gKYSMv3CrWWsxdPfxDxFvl+Is/0kc1dvMI1yNWDXI3AgdI4127KMUOv7gmwZ6SnRsHX/KAM0IPRe0+Sa0vMCAwEAAQ==";

        ipin_ = IPINBlockGenerator.getIPINBlock(ipin,pk,uuid);

        String url = application.getBaseUrl()+"/consumer_services.aspx";//
        // ?service=9&cust_id=1&pan="+Uri.encode(pan)+"&ex_date="+Uri.encode(exdate)+"
        // &ipin="+Uri.decode(ipin_)+"&uuid="+Uri.encode(uuid)+"&serviceId="+Uri.encode("2")+"&paymentInfo="+Uri.encode(phone)+"&invoiceNumber="+Uri.encode(invoice_number);
        rittal.log("pubKeyValue",pk);
        rittal.log("URL",url);
        //rittal.log("ipin",text);
        //String paymentInfo = "SETNUMBER="+seater_number +"/STUDCOURSEID="+ course_id +"/STUDFORMKIND="+ application_type_id;
        final String service = "5";
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
                            dataStorage.add_transaction(response.toString(),service,rittal.getValueOfKey("user_id"),opId,transactionSource);
                        }catch (Exception e){

                        }
                        try {

                            ResponseDialog responseDialog = new ResponseDialog(activity,dialog,response,service,opId,"","","","",paymentInfo,"",null,transactionSource);
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

    private void doArabCertificatePayment(String pan , final String exdate , String ipin, final String paymentInfo, String amount ) {

        final String uuid = UUID.randomUUID().toString();

        String ipin_ = "";
        String pk = application.getPublicKey(); //"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ4HwthfqXiK09AgShnnLqAqMyT5VUV0hvSdG+ySMx+a54Ui5EStkmO8iOdVG9DlWv55eLBoodjSfd0XRxN7an0CAwEAAQ==";//rittal.getValueOfKey("pk");// "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANx4gKYSMv3CrWWsxdPfxDxFvl+Is/0kc1dvMI1yNWDXI3AgdI4127KMUOv7gmwZ6SnRsHX/KAM0IPRe0+Sa0vMCAwEAAQ==";

        ipin_ = IPINBlockGenerator.getIPINBlock(ipin,pk,uuid);

        String url = application.getBaseUrl()+"/consumer_services.aspx";//
        // ?service=9&cust_id=1&pan="+Uri.encode(pan)+"&ex_date="+Uri.encode(exdate)+"
        // &ipin="+Uri.decode(ipin_)+"&uuid="+Uri.encode(uuid)+"&serviceId="+Uri.encode("2")+"&paymentInfo="+Uri.encode(phone)+"&invoiceNumber="+Uri.encode(invoice_number);
        rittal.log("pubKeyValue",pk);
        rittal.log("URL",url);
        //rittal.log("ipin",text);
        //String paymentInfo = "STUCNAME="+student_name +"/STUCPHONE="+ phone_number +"/STUDCOURSEID="+ course_id +"/STUDFORMKIND="+ application_type_id;
        final String service = "5";
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

                            ResponseDialog responseDialog = new ResponseDialog(activity,dialog,response,service,opId,"","","","",paymentInfo,"",progressDialog,transactionSource);
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

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(activity, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
/*
{"RRN":"e716b6a3-ab63-43f6-8563-afc0a1896acf","MeterNO":"37158876914","Amount":"3","PAmount":"0","STS":null,"responseMessage":"Confirmed","responseCode":1,"Units":"3.9","Token":"61173773474917681545","ServiceCharge":"0.00","CustomerName":"OMAR SHAMS ALDIN ALABEID AHMED","Tarrif":null,"units":null,"stateId":null,"Curent_Balance":0,"Invoice":null,"calcQty":null,"buyTimes":null,"VAt":null,"verifyCode":null,"stampTax":null,"code":null,"netAMT":0,"commAMT":0,"refCode":null,"regCode":null,"Rslt_State":null,"vendQty":null,"vendAMT":0,"supplyAMT":0,"transTime":null,"transID":null,"feesAMT":"0","checkCode":null,"Chanel":null,"FisrtPowerID":"1","FisrtPoweramt":"1","FisrtPowerKwt":"3.9","FisrtPowerPrice":"0.2600","SecPowerID":"0","SecPoweramt":"0","SecPowerKwt":"0","SecPowerPrice":"0","DetailedArreas":null,"ArrearsItems":"","MIN_AMT":null,"keyId_1":null,"keyName_1":null,"KeyToken_1":null,"keyId_2":null,"keyName_2":null,"keyToken_2":null,"KeyId_2":null,"elec_fees":"2","ct_fees":null,"PAN":"6391750800438891"}
 */

//090519130116
//20190509125940

//{"RRN":"26ef4957-9435-4d1c-9ad0-e752f048b661","MeterNO":"37158876914","Amount":"3","PAmount":"0","STS":null,
// "responseMessage":"Confirmed","responseCode":1,"Units":"3.9","Token":"01401571618799272648","ServiceCharge":"0.00",
// "CustomerName":"OMAR SHAMS ALDIN ALABEID AHMonsED","Tarrif":null,"units":null,"stateId":null,"Curent_Balance":0,"Invoice":null,"calcQty":null,"buyTimes":null,"VAt":null,"verifyCode":null,"stampTax":null,"code":null,"netAMT":0,"commAMT":0,"refCode":null,"regCode":null,"Rslt_State":null,"vendQty":null,"vendAMT":0,"supplyAMT":0,"transTime":null,"transID":null,"feesAMT":"0","checkCode":null,"Chanel":null,"FisrtPowerID":"1","FisrtPoweramt":"1","FisrtPowerKwt":"3.9","FisrtPowerPrice":"0.2600","SecPowerID":"0","SecPoweramt":"0","SecPowerKwt":"0","SecPowerPrice":"0","DetailedArreas":null,"ArrearsItems":"","MIN_AMT":null,"keyId_1":null,"keyName_1":null,"KeyToken_1":null,"keyId_2":null,"keyName_2":null,"keyToken_2":null,"KeyId_2":null,"elec_fees":"2","ct_fees":null,"PAN":null}
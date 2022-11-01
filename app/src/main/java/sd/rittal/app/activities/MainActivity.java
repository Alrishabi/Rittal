package sd.rittal.app.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wch.wchusbdriver.CH34xAndroidDriver;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import sd.rittal.app.R;
import sd.rittal.app.helper.Rittal;
import sd.rittal.app.printer.Device;
import sd.rittal.app.printer.PrintService;
import sd.rittal.app.printer.PrinterClass;
import sd.rittal.app.printer.bt.BtService;

public class MainActivity extends ListActivity {

    public static Rittal rittal;

    /////////////////// start var for sd.rittal.app.printer use ////////////////////////////////////////////
    public static BtService pl = null;
    public static CH34xAndroidDriver uartInterface;// USB Printer Control
    private static final String ACTION_USB_PERMISSION = "com.wch.wchusbdriver.USB_PERMISSION";

    protected static final String TAG = "MainActivity";
    public static boolean checkState = true;
    private Thread tv_update;
    TextView textView_state;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    Handler mhandler = null;
    Handler handler = null;
    String lang="";
    //
    Activity activity;
    JSONObject response;


    public static void receiptPrint(Activity activity, String lang, JSONObject response, String service, String opid, String transactionSource) {
        rittal = new Rittal(activity);
        String mResponseCode = "";
        String mResponseMessage = "";
        String mResponseDate = "";
        String mResponseExpDate = "";
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

        String mFisrtPowerID = "";
        String mFisrtPowerKwt = "";
        String mFisrtPoweramt = "";
        String mFisrtPowerPrice = "";
        String mSecPowerID = "";
        String mSecPowerKwt = "";
        String mSecPoweramt = "";
        String mSecondKWPrice = "";
        String mSecPowerPrice = "";
        String mMeterArrear = "";
        String mWaterArrear = "";

        String mPAmount = "";
        String mAmount = "";
        String mUnits = "";
        String mKWPrice = "";
        String mToken = "";
        String mCustomerName = "";
        String mRittalFee = "";
        String mSenderName="";
        String mReceiverName="";
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(),R.drawable.logoo);
            MainActivity.pl.printImage(bitmap);

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

            if(service.equals("2")){
                //balance inquiry receipt
                setTitle(activity,R.string.balance_inquery,lang);
                MainActivity.pl.printUnicode("  ");
                if(response.getString("responseCode").equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    mResponseBalance    = response.getString("balance");
                    mResponseFee        = response.getString("resp_acqTranFee");

                    MainActivity.pl.printUnicode("  ");
                    setRow(activity,R.string.res_dae,rittal.formatDate(mResponseDate),lang);
                    rittal.log("PAN in balance inqure",transactionSource.toUpperCase());
                    if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                        setRow(activity,R.string.res_pan,rittal.hashPan(mResponsePan),lang);
                    }

                    setRow(activity,R.string.res_balance,mResponseBalance+" "+"SDG",lang);
                    setRow(activity,R.string.res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint),lang);
                    setRow(activity,R.string.res_code,mResponseCode,lang);
                    setRow(activity,R.string.res_message,mResponseMessage,lang);
                }
            }
            else if(service.equals("3")){
                //card transfer
                setTitle(activity,R.string.title_card_to_card_transfer,lang);
                if(mResponseCode.equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    mResponseFee        = response.getString("resp_issuerTranFee");
                    mTranAmount         = response.getString("resp_tranAmount");
                    mToCard             = response.getString("toCard");


                    MainActivity.pl.printUnicode("  ");
                    setRow(activity,R.string.res_dae,rittal.formatDate(mResponseDate),lang);
                    if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                        setRow(activity,R.string.res_pan,rittal.hashPan(mResponsePan),lang);
                    }
                    setRow(activity,R.string.res_to_card,rittal.hashPan(mToCard),lang);
                    setRow(activity,R.string.res_amount,mTranAmount+" "+"SDG",lang);
                    setRow(activity,R.string.res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint),lang);
//                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                    setRow(activity,R.string.res_code,mResponseCode,lang);
                    setRow(activity,R.string.res_message,mResponseMessage,lang);
                }
            }
            else if(service.equals("4")){
                //bill inquiry receipt
                rittal.log("service name", service + activity.getString(R.string.title_bill_iquery));
                try{
                    setTitle(activity,R.string.title_bill_iquery,lang);
                }catch (Exception e){}
                if(mResponseCode.equals("0")){
                    try {
                        mResponseDate       = response.getString("tranDateTime");
                    }catch (Exception e){}

                    try {
                        mResponsePan        = response.getString("RespPAN");
                        mResponseExpDate     = response.getString("RespExpDate");
                        mResponseFee        = response.getString("resp_acqTranFee");
                        mPhoneNumber        = response.getString("resp_paymentInfo");
                    }catch (Exception e){}

                    String mOperator = "";
                    if(opid.equals("2")){
                        //2 => Zain
//                        {"responseMessage":"Approved","responseMessageArabic":null,"responseCode":0,"responseStatus":"Successful","pubKeyValue":null,"tranDateTime":"011019144459","creationDate":null,"panCategory":null,"balance":null,"payeesCount":0,"payeesList":null,"billInfo":"{totalAmount:\"27.0\",billedAmount:\"27.0\",lastInvoiceDate:\"2019-09-30\",contractNumber:\"106136278\",last4Digits:\"8175\",unbilledAmount:\"0.0\"}","RespPAN":"9222081110094611845","RespExpDate":null,"RespIPIN":null,"regType":null,"financialInstitutionId":null,"respUserName":null,"respUserPassword":null,"RespVoucherNumber":null,"voucherCode":null,"serviceInfo":null,"originalTransaction":null,"originalTranType":null,"originalTranUUID":null,"respUUID":null,"resp_acqTranFee":0,"resp_issuerTranFee":0,"resp_fromAccount":null,"resp_accountCurrency":null,"resp_tranAmount":0,"resp_paymentInfo":"0912536990","toCard":null,"rittalFee":0,"clientFee":0,"voucherNumber":null}
//                        10-01 13:48:09.099 24464-24464/sd.rittal.consumer I/TAG Response: {"responseMessage":"Approved","responseMessageArabic":null,"responseCode":0,"responseStatus":"Successful","pubKeyValue":null,"tranDateTime":"011019144459","creationDate":null,"panCategory":null,"balance":null,"payeesCount":0,"payeesList":null,"billInfo":"{totalAmount:\"27.0\",billedAmount:\"27.0\",lastInvoiceDate:\"2019-09-30\",contractNumber:\"106136278\",last4Digits:\"8175\",unbilledAmount:\"0.0\"}","RespPAN":"9222081110094611845","RespExpDate":null,"RespIPIN":null,"regType":null,"financialInstitutionId":null,"respUserName":null,"respUserPassword":null,"RespVoucherNumber":null,"voucherCode":null,"serviceInfo":null,"originalTransaction":null,"originalTranType":null,"originalTranUUID":null,"respUUID":null,"resp_acqTranFee":0,"resp_issuerTranFee":0,"resp_fromAccount":null,"resp_accountCurrency":null,"resp_tranAmount":0,"resp_paymentInfo":"0912536990","toCard":null,"rittalFee":0,"clientFee":0,"voucherNumber":null}
                        mOperator = getResourseString(activity,R.string.zain,lang);

                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));

                        try {

                            mContractNumber    = billInfo.getString("contractNumber");
                            mTotalAmount       = billInfo.getString("totalAmount");
                            mBilledAmount      = billInfo.getString("billedAmount");
                            mUnbilledAmount    = billInfo.getString("unbilledAmount");
                            mLastInvoiceDate   = billInfo.getString("lastInvoiceDate");
                            mLast4Digits       = billInfo.getString("last4Digits");

                        }catch (Exception e){}

                        setRow(activity,R.string.res_dae,rittal.formatDate(mResponseDate),lang);
//                        if(exdate.equals("ACCC") || exdate.equals("DCCC") || exdate.equals("MCCC")){

                        if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                            setRow(activity,R.string.res_pan,rittal.hashPan(mResponsePan),lang);
                        }
                        setRow(activity,R.string.res_operator,mOperator,lang);
                        setRow(activity,R.string.res_phone,mPhoneNumber,lang);
                        setRow(activity,R.string.res_contract,mContractNumber,lang);

                        setRow(activity,R.string.res_total_bill_amoun,mTotalAmount+" "+ activity.getString(R.string.sdg_hint),lang);
                        setRow(activity,R.string.res_billed_amount,mBilledAmount+" "+ activity.getString(R.string.sdg_hint),lang);
                        setRow(activity,R.string.res_unbill_amount,mUnbilledAmount+" "+ activity.getString(R.string.sdg_hint),lang);
                        setRow(activity,R.string.res_last4_digits,mLast4Digits,lang);

                        setRow(activity,R.string.res_last_invoice_date,mLastInvoiceDate,lang);
                        setRow(activity,R.string.res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint),lang);

                        setRow(activity,R.string.res_code,mResponseCode,lang);
                        setRow(activity,R.string.res_message,mResponseMessage,lang);



                    }
                    else if(opid.equals("4")){
                        rittal.log("opreter name", opid + activity.getString(R.string.mtn));
                        mOperator = getResourseString(activity,R.string.mtn,lang);
                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));
                        rittal.log("billInfo_mtn",billInfo.toString());
//                        {"responseMessage":"Approved","responseMessageArabic":null,"responseCode":0,"responseStatus":"Successful","pubKeyValue":null,"tranDateTime":"240719091100","creationDate":null,"panCategory":null,"balance":null,"payeesCount":0,"payeesList":null,"billInfo":"{total:\"787.22516\",BillAmount:\"592.717\",lastInvoiceDate:\"20190630000000\",contractNumber:\"1037225044\",unbilledAmount:\"194.50816\"}","RespPAN":"9888081119999455925","RespExpDate":null,"RespIPIN":null,"regType":null,"financialInstitutionId":null,"respUserName":null,"respUserPassword":null,"RespVoucherNumber":null,"voucherCode":null,"serviceInfo":null,"originalTransaction":null,"originalTranType":null,"originalTranUUID":null,"respUUID":null,"resp_acqTranFee":0,"resp_issuerTranFee":0,"resp_fromAccount":null,"resp_accountCurrency":null,"resp_tranAmount":0,"resp_paymentInfo":"0990999007","toCard":null,"rittalFee":0,"clientFee":0,"voucherNumber":null}
//                        {total:\"787.22516\",BillAmount:\"592.717\",lastInvoiceDate:\"20190630000000\",contractNumber:\"1037225044\",unbilledAmount:\"194.50816\"}

                        try {
                            mContractNumber    = billInfo.getString("contractNumber");

                            mTotalAmount       = billInfo.getString("total");
                            mBilledAmount      = billInfo.getString("BillAmount");
                            mUnbilledAmount    = billInfo.getString("unbilledAmount");

//                            mTotalAmount       = billInfo.getString("totalAmount");
//                            mBilledAmount      = billInfo.getString("billedAmount");
//                            mUnbilledAmount    = billInfo.getString("unbilledAmount");

                            mLastInvoiceDate   = billInfo.getString("lastInvoiceDate");

                        }catch (Exception e){}
                      //  mLast4Digits       = billInfo.getString("last4Digits");

                        MainActivity.pl.printUnicode("  ");
                        setRow(activity,R.string.res_dae,rittal.formatDate(mResponseDate),lang);
                        if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                            setRow(activity,R.string.res_pan,rittal.hashPan(mResponsePan),lang);
                        }
                        setRow(activity,R.string.res_operator,mOperator,lang);
                        setRow(activity,R.string.res_phone,mPhoneNumber,lang);

                        setRow(activity,R.string.res_contract,mContractNumber,lang);

                        setRow(activity,R.string.res_total_bill_amoun,mTotalAmount+" "+ activity.getString(R.string.sdg_hint),lang);
                        setRow(activity,R.string.res_bill_amount,mBilledAmount+" "+ activity.getString(R.string.sdg_hint),lang);
                        setRow(activity,R.string.res_unbill_amount,mUnbilledAmount+" "+ activity.getString(R.string.sdg_hint),lang);

//                        addText(res_total_bill_amount,mTotalAmount+" "+ activity.getString(R.string.sdg_hint));
//                        res_total_bill_amount_titel.setVisibility(View.VISIBLE);
//                        addText(res_billed_amount,mBilledAmount+" "+ activity.getString(R.string.sdg_hint));
//                        addText(res_unbill_amount,mUnbilledAmount+" "+ activity.getString(R.string.sdg_hint));


                        setRow(activity,R.string.res_last_invoice_date,rittal.formatDateLastInvoice(mLastInvoiceDate),lang);

                        setRow(activity,R.string.res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint),lang);
                        setRow(activity,R.string.res_code,mResponseCode,lang);
                        setRow(activity,R.string.res_message,mResponseMessage,lang);



                    }
                    else if(opid.equals("6")){
                        rittal.log("opreter name", opid + activity.getString(R.string.sudani));
                        mOperator = getResourseString(activity,R.string.sudani,lang);

                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));


                        try {
                            mSubscriberId    = billInfo.getString("SubscriberID");
                            mBilledAmount      = billInfo.getString("billAmount");
                        }catch (Exception e){}


                        setRow(activity,R.string.res_dae,rittal.formatDate(mResponseDate),lang);
                        if(!mResponsePan.equals("null")&&!mResponsePan.equals("9888081119403382541") ){
                            setRow(activity,R.string.res_pan,rittal.hashPan(mResponsePan),lang);
                        }
                        setRow(activity,R.string.res_operator,mOperator,lang);
                        setRow(activity,R.string.res_phone,mPhoneNumber,lang);
                        setRow(activity,R.string.res_subscriber_id,mSubscriberId,lang);
                        setRow(activity,R.string.res_bill_amount,mBilledAmount+" "+ activity.getString(R.string.sdg_hint),lang);
                        setRow(activity,R.string.res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint),lang);
                        setRow(activity,R.string.res_code,mResponseCode,lang);
                        setRow(activity,R.string.res_message,mResponseMessage,lang);

                    }
                    else {
                    }
                }

            }
            else if(service.equals("5")){
                rittal.log("service name", service+ activity.getString(R.string.title_bill_payment));
                setTitle(activity,R.string.title_bill_payment,lang);

                if(mResponseCode.equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    mResponseFee        = response.getString("resp_acqTranFee");
                    mTotalAmount        = response.getString("resp_tranAmount");

                    JSONObject billInfo = new JSONObject(response.getString("billInfo"));
                    mReceiptNo        = billInfo.getString("receiptNo");

                    String mOperator = "";
                    if(opid.equals("2")){
                        mOperator = getResourseString(activity,R.string.zain,lang);
                    }else if(opid.equals("4")){
                        mOperator = getResourseString(activity,R.string.mtn,lang);
                    }else  if(opid.equals("6")){
                        mOperator = getResourseString(activity,R.string.sudani,lang);
                    }else{

                    }

                    setRow(activity,R.string.res_dae,rittal.formatDate(mResponseDate),lang);
                    if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                        setRow(activity,R.string.res_pan,rittal.hashPan(mResponsePan),lang);
                    }
                    setRow(activity,R.string.res_operator,mOperator,lang);
                    setRow(activity,R.string.res_receipt_number,mReceiptNo,lang);
                    setRow(activity,R.string.res_amount,mTotalAmount+" "+ activity.getString(R.string.sdg_hint),lang);
                    setRow(activity,R.string.res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint),lang);

                    setRow(activity,R.string.res_code,mResponseCode,lang);
                    setRow(activity,R.string.res_message,mResponseMessage,lang);



                }

            }
            else if(service.equals("6")){
                rittal.log("service name", service + activity.getString(R.string.title_top_up));
                setTitle(activity,R.string.title_top_up,lang);
                if(mResponseCode.equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    mResponseFee        = response.getString("resp_issuerTranFee");
                    mTranAmount        = response.getString("resp_tranAmount");
                    mPhoneNumber        = response.getString("resp_paymentInfo");

                    String mOperator = "";
                    if(opid.equals("1")){
                        mOperator = getResourseString(activity,R.string.zain,lang);
                    }else if(opid.equals("3")){
                        mOperator = getResourseString(activity,R.string.mtn,lang);
                    }else  if(opid.equals("5")){
                        mOperator = getResourseString(activity,R.string.sudani,lang);
                    }else{

                    }

                    setRow(activity,R.string.res_dae,rittal.formatDate(mResponseDate),lang);
                    if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                        setRow(activity,R.string.res_pan,rittal.hashPan(mResponsePan),lang);
                    }
                    setRow(activity,R.string.res_operator,mOperator,lang);
                    setRow(activity,R.string.res_phone,mPhoneNumber,lang);
                    setRow(activity,R.string.res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint),lang);
                    setRow(activity,R.string.res_amount,mTranAmount+" "+activity.getString(R.string.sdg_hint),lang);

                    setRow(activity,R.string.res_code,mResponseCode,lang);
                    setRow(activity,R.string.res_message,mResponseMessage,lang);




                }

            }
            else if(service.equals("7")){ }
            else if(service.equals("8")){
                rittal.log("service name", service + activity.getString(R.string.title_generate_voucher));
                setTitle(activity,R.string.title_generate_voucher,lang);
                if(mResponseCode.equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    mResponseFee        = response.getString("resp_issuerTranFee");
                    mTranAmount         = response.getString("resp_tranAmount");
                    mVoucherNumber      = response.getString("RespVoucherNumber");
                    mVoucherCode        = response.getString("voucherCode");



                    setRow(activity,R.string.res_dae,rittal.formatDate(mResponseDate),lang);

                    if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                        setRow(activity,R.string.res_pan,rittal.hashPan(mResponsePan),lang);
                    }

                    setRow(activity,R.string.res_amount,mTranAmount+" "+ activity.getString(R.string.sdg_hint),lang);
                    setRow(activity,R.string.res_voucher_code,mVoucherCode,lang);
                    setRow(activity,R.string.res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint),lang);
//                    setRow(activity,R.string.res_voucher_number,mVoucherNumber,lang);
                    setRow(activity,R.string.res_code,mResponseCode,lang);
                    setRow(activity,R.string.res_message,mResponseMessage,lang);


                }

            }
            else if(service.equals("9")){
                rittal.log("service name", service + activity.getString(R.string.title_e15));
                setTitle(activity,R.string.title_e15,lang);
                if(mResponseCode.equals("0")){
                    if(opid.equals("2")){
                        mResponseDate       = response.getString("tranDateTime");
                        mResponsePan        = response.getString("RespPAN");
                        mResponseFee        = response.getString("resp_issuerTranFee");

                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));

                        mUnitName       = billInfo.getString("UnitName");
                        mServiceName    = billInfo.getString("ServiceName");
                        mTotalAmount    = billInfo.getString("resp_tranAmount");
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


                        setRow(activity,R.string.res_dae,rittal.formatDate(mResponseDate),lang);
                        if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                            setRow(activity,R.string.res_pan,rittal.hashPan(mResponsePan),lang);
                        }

                        setRow(activity,R.string.res_unit_name,mUnitName,lang);
                        setRow(activity,R.string.res_service_name,mServiceName,lang);
                        setRow(activity,R.string.res_payer_name,mPayerName,lang);
                        setRow(activity,R.string.res_amount,mTotalAmount+" "+ activity.getString(R.string.sdg_hint),lang);
                        setRow(activity,R.string.res_due_amount,mDueAmount+" "+ activity.getString(R.string.sdg_hint),lang);
                        setRow(activity,R.string.res_invoice_expiry_date,mInvoiceExpiry,lang);
                        setRow(activity,R.string.res_invoice_status,status_text,lang);
                        setRow(activity,R.string.res_reference_id,mReferenceId,lang);
                        setRow(activity,R.string.res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint),lang);

                        setRow(activity,R.string.res_code,mResponseCode,lang);
                        setRow(activity,R.string.res_message,mResponseMessage,lang);

                    }
                }

            }
            else if(service.equals("10")){
                rittal.log("service name", service + activity.getString(R.string.title_e15));
                setTitle(activity,R.string.title_e15,lang);

                if(mResponseCode.equals("0")){
                    if(opid.equals("6")){
                        mResponseDate       = response.getString("tranDateTime");
                        mResponsePan        = response.getString("RespPAN");
                        mResponseFee        = response.getString("resp_issuerTranFee");

                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));

                        mUnitName       = billInfo.getString("UnitName");
                        mServiceName    = billInfo.getString("ServiceName");
                        mTotalAmount    = billInfo.getString("resp_tranAmount");
                        mReferenceId    = billInfo.getString("ReferenceId");
                        mPayerName      = billInfo.getString("PayerName");



                        setRow(activity,R.string.res_dae,rittal.formatDate(mResponseDate),lang);

                        if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                            setRow(activity,R.string.res_pan,rittal.hashPan(mResponsePan),lang);
                        }
                        setRow(activity,R.string.res_unit_name,mUnitName,lang);
                        setRow(activity,R.string.res_service_name,mServiceName,lang);
                        setRow(activity,R.string.res_payer_name,mPayerName,lang);
                        setRow(activity,R.string.res_amount,mTotalAmount+" "+"SDG",lang);
                        setRow(activity,R.string.res_reference_id,mReferenceId,lang);
                        setRow(activity,R.string.res_fee,mResponseFee,lang);

                        setRow(activity,R.string.res_code,mResponseCode,lang);
                        setRow(activity,R.string.res_message,mResponseMessage,lang);
                    }
                }

            }
            else if(service.equals("11")){

                if(opid.equals("G")){
                    if(mResponseCode.equals("1")){
                    }
                }
                if(opid.equals("C")){
                    setTitle(activity,R.string.purchase_electricity,lang);
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



//                        setRow(activity,R.string.res_dae,rittal.formatDate(mResponseDate),lang);
//                        if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
//                            setRow(activity,R.string.res_pan,rittal.hashPan(mResponsePan),lang);
//                            setRow(activity,R.string.res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint),lang);
//                        }
//
//                        setRow(activity,R.string.res_customer_name,mCustomerName,lang);
//                        setRow(activity,R.string.res_meter_number,mMeterNO,lang);
//                        setRow(activity,R.string.res_amount,mAmount+" "+getResourseString(activity,R.string.sdg_hint,lang),lang);
//
//                        setRow(activity,R.string.res_water_fees,mWaterArrear+" "+getResourseString(activity,R.string.sdg_hint,lang),lang);
//                        setRow(activity,R.string.res_meter_arrear,mMeterArrear+" "+getResourseString(activity,R.string.sdg_hint,lang),lang);
//
//
//                        setRow(activity,R.string.res_purchase_amount,mPAmount+" "+getResourseString(activity,R.string.sdg_hint,lang),lang);
//                        setRow(activity,R.string.res_units,mUnits+getResourseString(activity,R.string.k_w,lang),lang);
//
//                        String pricing = "";
//
//                        String firstPrice   = mFisrtPowerKwt+ " "+activity.getString(R.string.k_w) +" "+ mFisrtPowerPrice +" "+activity.getString(R.string.sdg_hint).trim()+"/"+activity.getString(R.string.k_w);
//                        String secendPrice  = mSecPowerKwt+ " "+activity.getString(R.string.k_w) +" "+ mSecPowerPrice +" "+activity.getString(R.string.sdg_hint).trim()+"/"+activity.getString(R.string.k_w);
//
//                        if(mFisrtPowerID.equals("1")){
//                            pricing = firstPrice;
//                        }else if(mSecPowerID.equals("1")){
//                            pricing = " \n "+secendPrice;
//                        }
//                        setRow(activity,R.string.res_unit_price,pricing,lang);
//
//
//                        setRow(activity,R.string.res_rittal_fee,mRittalFee+" "+getResourseString(activity,R.string.sdg_hint,lang),lang);
//                        setRow(activity,R.string.res_code,mResponseCode,lang);
//                        setRow(activity,R.string.res_message,mResponseMessage,lang);
//
//                        setRow(activity,R.string.res_token,mToken,lang);

                        setRowElc(activity,R.string.res_dae,rittal.formatDate(mResponseDate),"",lang);
                        if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                            setRowElc(activity,R.string.res_pan,rittal.hashPan(mResponsePan),"",lang);
                            setRowElc(activity,R.string.res_fee,mResponseFee,activity.getString(R.string.sdg_hint),lang);
                        }

                        setRow(activity,R.string.res_customer_name,mCustomerName,lang);
                        setRowElc(activity,R.string.res_meter_number,mMeterNO,"",lang);
                        setRowElc(activity,R.string.res_amount,mAmount,getResourseString(activity,R.string.sdg_hint,lang),lang);

                        setRowElc(activity,R.string.res_water_fees,mWaterArrear,getResourseString(activity,R.string.sdg_hint,lang),lang);
                        setRowElc(activity,R.string.res_meter_arrear,mMeterArrear,getResourseString(activity,R.string.sdg_hint,lang),lang);


                        setRowElc(activity,R.string.res_purchase_amount,mPAmount,getResourseString(activity,R.string.sdg_hint,lang),lang);
                        setRowElc(activity,R.string.res_units,mUnits,getResourseString(activity,R.string.k_w,lang),lang);

                        String pricing = "";

                        String firstPrice   = mFisrtPowerKwt+ " "+activity.getString(R.string.k_w) +" "+ mFisrtPowerPrice +" "+activity.getString(R.string.sdg_hint).trim()+"/"+activity.getString(R.string.k_w);
                        String secendPrice  = mSecPowerKwt+ " "+activity.getString(R.string.k_w) +" "+ mSecPowerPrice +" "+activity.getString(R.string.sdg_hint).trim()+"/"+activity.getString(R.string.k_w);

                        if(mFisrtPowerID.equals("1")){
                            pricing = firstPrice;
                        }else if(mSecPowerID.equals("1")){
                            pricing = " \n "+secendPrice;
                        }
                        setRow(activity,R.string.res_unit_price,pricing,lang);


                        setRowElc(activity,R.string.res_rittal_fee,mRittalFee,getResourseString(activity,R.string.sdg_hint,lang),lang);
                        setRowElc(activity,R.string.res_code,mResponseCode,"",lang);
                        setRowElc(activity,R.string.res_message,mResponseMessage,"",lang);

                        setRow(activity,R.string.res_token,mToken,lang);
                    }
                }

            }
            else if(service.equals("12")){
                setTitle(activity,R.string.cash_out,lang);
                if(response.getString("responseCode").equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    mAmount        = response.getString("resp_tranAmount");
                    mRittalFee          = response.getString("rittalFee");
                    mResponseFee        = response.getString("resp_issuerTranFee");

                    setRow(activity,R.string.res_dae,rittal.formatDate(mResponseDate),lang);
                    if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                        setRow(activity,R.string.res_pan,rittal.hashPan(mResponsePan),lang);
                    }
                    setRow(activity,R.string.res_amount,mAmount,lang);
                    setRow(activity,R.string.res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint),lang);
                    setRow(activity,R.string.res_rittal_fee,mRittalFee+" "+ activity.getString(R.string.sdg_hint),lang);

                    setRow(activity,R.string.res_code,mResponseCode,lang);
                    setRow(activity,R.string.res_message,mResponseMessage,lang);
                }
            }
            else if(service.equals("13")){
                setTitle(activity,R.string.recharge_card_title,lang);
                if(response.getString("responseCode").equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    try {
                        mToCard        = response.getString("toCard");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    mAmount        = response.getString("resp_tranAmount");
                    mRittalFee          = response.getString("rittalFee");

                    setRow(activity,R.string.res_dae,rittal.formatDate(mResponseDate),lang);
//                    if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                        setRow(activity,R.string.res_to_card,rittal.hashPan(mToCard),lang);
//                    }
//                    setRow(activity,R.string.res_to_card,rittal.hashPan(mToCard),lang);
                    setRow(activity,R.string.res_amount,mAmount+" "+"SDG",lang);
                    setRow(activity,R.string.res_rittal_fee,mRittalFee+" "+ activity.getString(R.string.sdg_hint),lang);

                    setRow(activity,R.string.res_code,mResponseCode,lang);
                    setRow(activity,R.string.res_message,mResponseMessage,lang);
                }
            }
            else if(service.equals("14")){
                setTitle(activity,R.string.recharge_account_title,lang);
                if(response.getString("responseCode").equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    mAmount        = response.getString("resp_tranAmount");
                    mResponseFee        = response.getString("resp_issuerTranFee");
                    mRittalFee          = response.getString("rittalFee");

                    setRow(activity,R.string.res_dae,rittal.formatDate(mResponseDate),lang);
                    if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                        setRow(activity,R.string.res_pan,rittal.hashPan(mResponsePan),lang);
                    }
                    setRow(activity,R.string.res_amount,mAmount+" "+"SDG",lang);

                    setRow(activity,R.string.res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint),lang);
                    setRow(activity,R.string.res_rittal_fee,mRittalFee+" "+ activity.getString(R.string.sdg_hint),lang);

                    setRow(activity,R.string.res_code,mResponseCode,lang);
                    setRow(activity,R.string.res_message,mResponseMessage,lang);
                }
            }
            else if(service.equals("15")){
                setTitle(activity,R.string.knownGenerateVoucher,lang);
                if(response.getString("responseCode").equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    mVoucherNumber      = response.getString("voucherNumber");
                    mAmount             = response.getString("resp_tranAmount");
                    mSenderName         = response.getString("respUserName");
                    mReceiverName       = response.getString("respUserPassword");
                    mRittalFee          = response.getString("rittalFee");
                    mResponseFee        = response.getString("resp_issuerTranFee");

//                    OBJ: {"service":"15","customerID":"6f69601a-00ea-4d74-9677-4fc47eccf359","fromCard":"9222081110094611845","expiryDate":"2304","ipin":"03uckQXJTzjFMcxNsC8m+9rcuIUgI33hmwAaKHCoGECFxgOIBSMJ7NtxkT3mA3I3Yv\/M\/KVGMgdtZ7OWmTC6jw==","uuid":"f7c2f699-a223-4b6e-8b7f-f8d5b23204f8","tranAmount":"3",
//                    "senderName":"musab alrishabi","senderIdType":"1","senderId":"1111111111","senderPhone":"+249918883964",
//                    "receiverName":"musab alrishabi","receiverPhone":"+249929995414","receiverIdType":"1","receiverId":"123456789","voucherPassword":"1111111111"}


                    setRow(activity,R.string.res_dae,rittal.formatDate(mResponseDate),lang);
                    if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                        setRow(activity,R.string.res_pan,rittal.hashPan(mResponsePan),lang);
                    }
                    setRow(activity,R.string.res_sender_name,mSenderName,lang);
                    setRow(activity,R.string.res_rece_name,mReceiverName,lang);

                    setRow(activity,R.string.res_voucher_number,mVoucherNumber,lang);
                    setRow(activity,R.string.res_amount,mAmount+" "+ activity.getString(R.string.sdg_hint),lang);

                    setRow(activity,R.string.res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint),lang);
                    setRow(activity,R.string.res_rittal_fee,mRittalFee+" "+ activity.getString(R.string.sdg_hint),lang);

                    setRow(activity,R.string.res_code,mResponseCode,lang);
                    setRow(activity,R.string.res_message,mResponseMessage,lang);

                }
            }
            else if(service.equals("16")){
                setTitle(activity,R.string.known_generate_voucher_cashout,lang);
                if(response.getString("responseCode").equals("0")){
                    mResponseDate         = response.getString("tranDateTime");
                    mSenderName            = response.getString("respUserName");
                    mReceiverName          = response.getString("respUserPassword");
                    mAmount                = response.getString("resp_tranAmount");
                    mResponseFee           = response.getString("resp_issuerTranFee");
                    mRittalFee             = response.getString("rittalFee");

                    setRow(activity,R.string.res_dae,rittal.formatDate(mResponseDate),lang);
//                    setRow(activity,R.string.res_dae,rittal.formatDate(mResponseDate),lang);
//                    if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
//                        setRow(activity,R.string.res_pan,rittal.hashPan(mResponsePan),lang);
//                    }

                    setRow(activity,R.string.res_sender_name,mSenderName,lang);
                    setRow(activity,R.string.res_rece_name,mReceiverName,lang);
                    setRow(activity,R.string.res_amount,mAmount+" "+ activity.getString(R.string.sdg_hint),lang);
                    setRow(activity,R.string.res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint),lang);
                    setRow(activity,R.string.res_rittal_fee,mRittalFee+" "+ activity.getString(R.string.sdg_hint),lang);

                    setRow(activity,R.string.res_code,mResponseCode,lang);
                    setRow(activity,R.string.res_message,mResponseMessage,lang);
                }
            }
            else{}

            MainActivity.pl.printUnicode("        \r\n");
            MainActivity.pl.printUnicode("        \r\n");
            MainActivity.pl.printUnicode("        \r\n");
            MainActivity.pl.printUnicode("        \r\n");

        }catch (Exception e){}
    }
    public static void setTitle(Activity activity,int key,String lang){

        String title = getResourseString(activity,key,lang);
         rittal.log("INIT TITLE",title);
        if(lang.equals("ar")){
            try {
                MainActivity.pl.printUnicode("     "+title+"     \n");
                MainActivity.pl.printUnicode("===========================\r");

            }catch (Exception e){
                rittal.log("Exception",e.toString());
            }

        }else{
            try {
                MainActivity.pl.printText("     "+title+"     \n");
                MainActivity.pl.printText("===========================\n");

            }catch (Exception e){
                rittal.log("Exception",e.toString());
            }
        }
    }

    public static String formatToken(String text){
        // String token = text;
        StringBuilder str = new StringBuilder(text);

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

    public static void setRow(Activity activity,int key, String value,String lang){
        String label = getResourseString(activity,key,lang);

        if(lang.equals("ar")){
            MainActivity.pl.printUnicode(label +"\n");
            MainActivity.pl.printUnicode(value+"\n");

        }else{
            MainActivity.pl.printText(label +" ");
            MainActivity.pl.printText(value+"\n");
        }
    }

    public static void setRowElc(Activity activity,int key, String value,String unit,String lang){
        String label = getResourseString(activity,key,lang);

        if(lang.equals("ar")){
//            MainActivity.pl.printUnicode(label +"\n");
//            MainActivity.pl.printUnicode(value+"\n");

            MainActivity.pl.printUnicode(label.replace(":","") +"");
            MainActivity.pl.printUnicode(unit+" "+value+": \n");
        }else{
            MainActivity.pl.printText(label +" ");
            MainActivity.pl.printText(value+"\n");
        }
    }
    public static String changeNumberToArabic(String obj){
//        obj = obj.replace("1","١");
//        obj = obj.replace("2","٢");
//        obj = obj.replace("3","٣");
//        obj = obj.replace("4","٤");
//        obj = obj.replace("5","٥");
//        obj = obj.replace("6","٦");
//        obj = obj.replace("7","٧");
//        obj = obj.replace("8","٨");
//        obj = obj.replace("9","٩");
//        obj = obj.replace("0","٠");
        return obj;
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
    private static String getStringByLocalBefore17(Context context,int resId, String language) {
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

    private static byte charToByte(char c) {
        return (byte) "0123456789abcdef".indexOf(c);
    }
    public static byte[] hexStringToBytes(String hexString) {
        hexString = hexString.toLowerCase();
        String[] hexStrings = hexString.split(" ");
        byte[] bytes = new byte[hexStrings.length];
        for (int i = 0; i < hexStrings.length; i++) {
            char[] hexChars = hexStrings[i].toCharArray();
            bytes[i] = (byte) (charToByte(hexChars[0]) << 4 | charToByte(hexChars[1]));
        }
        return bytes;
    }

    public static StringBuffer bytesToString(byte[] bytes)
    {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++)
        {
            String s = Integer.toHexString(bytes[i] & 0xff);
            if (s.length() < 2)
                sBuffer.append('0');
            sBuffer.append(s + " ");
        }
        return sBuffer;
    }

    public void QRprinter(String pan ,String exdate){
        if(exdate.equals("ACCC") || exdate.equals("DCCC") || exdate.equals("MCCC")){

            //String QRText = pan+"#"+exdate+"#";

            MainActivity.pl.printUnicode(" \r\n");
            MainActivity.pl.printUnicode("بيانات الحساب");
//			try {
//				bitmap1 = TextToImageEncode(pan.trim()+"#"+exdate.trim()+"#");
//			} catch (WriterException e) {
//				e.printStackTrace();
//			}
//			MainActivity.pl.printImage(bitmap1);

            // solve start
            String str = pan.trim()+"#"+exdate.trim()+"#";
            byte[] btdata=null;
            try {
                btdata=str.getBytes("ASCII");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String strdata=bytesToString(btdata).toString();

            short datalen=(short) (btdata.length+3);
            //datalen = Short.valueOf((short) (Integer.valueOf(datalen) + 500));
            byte pL=(byte)(datalen&0xff);
            byte pH=(byte)(datalen>>8);

            MainActivity.pl.write(new byte[]{0x1d,0x28,0x6b,0x03,0x00,0x31,0x43,0x33});

            //���
            MainActivity.pl.write(new byte[]{0x1d,0x28,0x6b,0x03,0x00,0x31,0x45,0x05});

            byte[] qrHead=new byte[]{0x1d,0x28,0x6b,pL,pH,0x31,0x50,0x30};
            byte[] qrData=new byte[qrHead.length+datalen];
            System.arraycopy(qrHead, 0, qrData, 0, qrHead.length);
            System.arraycopy(btdata, 0, qrData, qrHead.length, btdata.length);
            MainActivity.pl.write(qrData);
            MainActivity.pl.write(new byte[]{0x1d,0x28,0x6b,0x03,0x00,0x31,0x51,0x30});

            //solve end
            MainActivity.pl.printUnicode("يمكنك استخدام QRcode في العملية القادمة");

        }else{

            MainActivity.pl.printUnicode(" \r\n");
            MainActivity.pl.printUnicode("بيانات البطاقة");
//			try {
//				bitmap1 = TextToImageEncode(pan.trim()+"#"+exdate.trim()+"#");
//			} catch (WriterException e) {
//				e.printStackTrace();
//			}

            // solve start
            String str = pan.trim()+"#"+exdate.trim()+"#";
            byte[] btdata=null;
            try {
                btdata=str.getBytes("ASCII");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String strdata=bytesToString(btdata).toString();

            short datalen=(short) (btdata.length+3);
            //datalen = Short.valueOf((short) (Integer.valueOf(datalen) + 500));
            byte pL=(byte)(datalen&0xff);
            byte pH=(byte)(datalen>>8);

            MainActivity.pl.write(new byte[]{0x1d,0x28,0x6b,0x03,0x00,0x31,0x43,0x33});

            //���
            MainActivity.pl.write(new byte[]{0x1d,0x28,0x6b,0x03,0x00,0x31,0x45,0x05});

            byte[] qrHead=new byte[]{0x1d,0x28,0x6b,pL,pH,0x31,0x50,0x30};
            byte[] qrData=new byte[qrHead.length+datalen];
            System.arraycopy(qrHead, 0, qrData, 0, qrHead.length);
            System.arraycopy(btdata, 0, qrData, qrHead.length, btdata.length);
            MainActivity.pl.write(qrData);
            MainActivity.pl.write(new byte[]{0x1d,0x28,0x6b,0x03,0x00,0x31,0x51,0x30});

            //solve end

            //MainActivity.pl.printImage(bitmap1);
            MainActivity.pl.printUnicode("يمكنك استخدام QRcode في العملية القادمة");


        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_items);
        textView_state = (TextView) findViewById(R.id.textView_state);
        setListAdapter(new SimpleAdapter(this, getData("simple-list-item-2"),
                android.R.layout.simple_list_item_2, new String[] { "title",
                "description" }, new int[] { android.R.id.text1,
                android.R.id.text2 }));
        initContex();

        initHandeler();

        pl =new BtService(this, mhandler, handler);
//        Intent intent = new Intent();
////        intent.putExtra("position", 0);
////        intent.setClass(MainActivity.this, PrintActivity.class);
////        startActivity(intent);

        Intent i = new Intent(activity,HomeActivity.class);
        startActivity(i);
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onListItemClick(ListView listView, View v, int position, long id) {


        MainActivity.checkState = true;
        Intent intent = new Intent();
        intent.putExtra("position", position);
        intent.setClass(MainActivity.this, PrintActivity.class);


        switch (position) {
            case 0:
                startActivity(intent);
                break;
            case 1:
                startActivity(intent);
                break;
            case 2:
                uartInterface = new CH34xAndroidDriver(
                        (UsbManager) getSystemService(Context.USB_SERVICE), this,
                        ACTION_USB_PERMISSION);

                if (!MainActivity.uartInterface.UsbFeatureSupported()) {
                    Log.d(TAG, "Device can not support usb");
                }
                if (2 == MainActivity.uartInterface.ResumeUsbList()) {
                    MainActivity.uartInterface.CloseDevice();
                    Log.d(TAG, "ResumeUsbList Error");
                }

                if (MainActivity.uartInterface.isConnected()) {
                    boolean flags = MainActivity.uartInterface.UartInit();
                    if (flags) {
                        if (MainActivity.pl.open(this)) {
                            startActivity(intent);
                        } else {
                            uartInterface = null;
                        }
                    }
                }

                break;
        }
    }

    private List<Map<String, String>> getData(String title) {
        List<Map<String, String>> listData = new ArrayList<Map<String, String>>();

        Map<String, String> map = new HashMap<String, String>();
        map.put("title", getResources().getString(R.string.mode_bt));
        map.put("description", "");
        listData.add(map);
        return listData;
    }

    private boolean checkData(List<Device> list, Device d) {
        for (Device device : list) {
            if (device.deviceAddress.equals(d.deviceAddress)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        checkState = true;
        super.onRestart();
    }
    public void initContex(){
        activity = this;
        rittal = new Rittal(activity);

    }
    public void initHandeler() {
        mhandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        Log.i(TAG, "readBuf:" + readBuf[0]);
                        if (readBuf[0] == 0x13) {
                            PrintService.isFUll = true;
                            rittal.rittalToast(getResources().getString(R.string.str_printer_state) + ":" + getResources().getString(R.string.str_printer_bufferfull),"1","i");
                        } else if (readBuf[0] == 0x11) {
                            PrintService.isFUll = false;
                            rittal.rittalToast(getResources().getString(R.string.str_printer_state) + ":" + getResources().getString(R.string.str_printer_buffernull),"1","w");
                        } else if (readBuf[0] == 0x08) {
                            rittal.rittalToast(getResources().getString(R.string.str_printer_state) + ":" + getResources().getString(R.string.str_printer_nopaper),"1","w");
                        } else if (readBuf[0] == 0x01) {
                            //ShowMsg(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_printing));
                        } else if (readBuf[0] == 0x04) {
                            rittal.rittalToast(getResources().getString(R.string.str_printer_state) + ":" + getResources().getString(R.string.str_printer_hightemperature),"1","w");
                        } else if (readBuf[0] == 0x02) {
                            rittal.rittalToast(getResources().getString(R.string.str_printer_state) + ":" + getResources().getString(R.string.str_printer_lowpower),"1","w");
                        } else {
                            String readMessage = new String(readBuf, 0, msg.arg1);
                            if (readMessage.contains("800"))// 80mm paper
                            {
                                PrintService.imageWidth = 72;
                                Toast.makeText(getApplicationContext(), "80mm",
                                        Toast.LENGTH_SHORT).show();
                            } else if (readMessage.contains("580"))// 58mm paper
                            {
                                PrintService.imageWidth = 48;
                                Toast.makeText(getApplicationContext(), "58mm",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case MESSAGE_STATE_CHANGE:// 钃濈墮杩炴帴鐘�
                        switch (msg.arg1) {
                            case PrinterClass.STATE_CONNECTED:// 宸茬粡杩炴帴
                                break;
                            case PrinterClass.STATE_CONNECTING:// 姝ｅ湪杩炴帴
                                Toast.makeText(getApplicationContext(),
                                        "STATE_CONNECTING", Toast.LENGTH_SHORT).show();
                                break;
                            case PrinterClass.STATE_LISTEN:
                            case PrinterClass.STATE_NONE:
                                break;
                            case PrinterClass.SUCCESS_CONNECT:
                                pl.write(new byte[]{0x1b, 0x2b});// 妫�祴鎵撳嵃鏈哄瀷鍙�
                                rittal.rittalToast(getResourseString(activity,R.string.connect_success,lang),"1","s");
//                                Toast.makeText(getApplicationContext(),
//                                        "SUCCESS_CONNECT", Toast.LENGTH_SHORT).show();
//                                MainActivity.receiptPrint(activity,Locale.getDefault().getLanguage(),response,service,opid,transactionSource);

                                break;
                            case PrinterClass.FAILED_CONNECT:
                                Toast.makeText(getApplicationContext(),
                                        "FAILED_CONNECT", Toast.LENGTH_SHORT).show();

                                break;
                            case PrinterClass.LOSE_CONNECT:
                                Toast.makeText(getApplicationContext(), "LOSE_CONNECT",
                                        Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case MESSAGE_WRITE:

                        break;
                }
                super.handleMessage(msg);
            }
        };

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        break;
                    case 1:// 鎵弿瀹屾瘯
                        Device d = (Device) msg.obj;
                        if (d != null) {
                            if (PrintSettingActivity.deviceList == null) {
                                PrintSettingActivity.deviceList = new ArrayList<Device>();
                            }

                            if (!checkData(PrintSettingActivity.deviceList, d)) {
                                PrintSettingActivity.deviceList.add(d);
                            }
                        }
                        break;
                    case 2:// 鍋滄鎵弿
                        break;
                }
            }
        };

        tv_update = new Thread() {
            public void run() {
                while (true) {
                    if (checkState) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        textView_state.post(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                if (pl != null) {
                                    if (pl.getState() == PrinterClass.STATE_CONNECTED) {
                                        textView_state
                                                .setText(MainActivity.this
                                                        .getResources()
                                                        .getString(
                                                                R.string.str_connected));
                                    } else if (pl.getState() == PrinterClass.STATE_CONNECTING) {
                                        textView_state
                                                .setText(MainActivity.this
                                                        .getResources()
                                                        .getString(
                                                                R.string.str_connecting));
                                    } else if (pl.getState() == PrinterClass.LOSE_CONNECT
                                            || pl.getState() == PrinterClass.FAILED_CONNECT) {
                                        checkState = false;
                                        textView_state
                                                .setText(MainActivity.this
                                                        .getResources()
                                                        .getString(
                                                                R.string.str_disconnected));
                                        Intent intent = new Intent();
                                        intent.setClass(MainActivity.this,
                                                PrintSettingActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        //startActivity(intent);
                                    } else {
                                        textView_state
                                                .setText(MainActivity.this
                                                        .getResources()
                                                        .getString(
                                                                R.string.str_disconnected));
                                    }
                                }
                            }
                        });
                    }
                }
            }
        };
        tv_update.start();
    }
}

package sd.rittal.app.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sd.rittal.app.R;
import sd.rittal.app.activities.MainActivity;
import sd.rittal.app.activities.PrintSettingActivity;
import sd.rittal.app.helper.Rittal;
import sd.rittal.app.objects.Transactions;

import static sd.rittal.app.activities.MainActivity.getResourseString;

public class TransactionsAdapter extends BaseAdapter{

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
    String mPAmount = "";
    String mAmount = "";
    String mUnits = "";
    String mKWPrice = "";

    String mFisrtPowerKwt = "";
    String mFisrtPoweramt = "";
    String mFisrtPowerPrice = "";
    String mSecPowerKwt = "";
    String mSecPoweramt = "";
    String mSecPowerPrice = "";
    String mFisrtPowerID = "";
    String mSecPowerID = "";
    String mSecondKWPrice = "";

    String mMeterArrear = "";
    String mWaterArrear = "";


    String mToken = "";
    String mCustomerName = "";
    String mRittalFee = "";

    String mSenderName="";
    String mSenderPhone="";

    String mReceiverName="";
    String mReceiverPhone="";

    String pan ="";
    String expiry_date ="";




    String lang = "";
    String ipin = "";
    String counter_num = "";
    String amount = "";
    String transactionSource = "";

    List<Transactions> transactions;
    Rittal rittal;
    String sharePath="no";

    //Used to create view.
    private LayoutInflater mInflater;
    Activity activity  ;
    ProgressBar progressBar ;


    int lastExpandedPosition = -1;

    public TransactionsAdapter(Activity activity, List<Transactions> transactions, ProgressBar progressBar,String transactionSource){
        //Collections.sort(category, Collections.reverseOrder());
        this.transactions =transactions ;
        this.activity =activity ;
        mInflater = LayoutInflater.from(activity);
        this.rittal = new Rittal(activity);
        this.transactionSource = transactionSource;


    }
    /** Get the adapter category (Contacts) count */
    public int getCount() {
        return transactions.size();
    }

    /** Get item (Contact) by position */
    public Object getItem(int position) {
        return transactions.get(position);
    }
    /** Get item (Contact) id by position */

    public long getItemId(int position) {
        return transactions.get(position).getTRANSACTION_id();
    }
    /** Get item (Contact) view by position */
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        final View row =mInflater.inflate(R.layout.transaction_item,parent, false);

        ImageView image;
        TextView status;

        TextView res_meter_number      = (TextView) row.findViewById(R.id.res_meter_number);

        TextView res_meter_arrear      = (TextView) row.findViewById(R.id.res_meter_arrear);
        TextView res_water_fees      = (TextView) row.findViewById(R.id.res_water_fees);

        TextView res_customer_name     = (TextView) row.findViewById(R.id.res_customer_name);
        TextView res_units             = (TextView) row.findViewById(R.id.res_units);
        TextView res_unit_price             = (TextView) row.findViewById(R.id.res_unit_price);
        TextView res_token             = (TextView) row.findViewById(R.id.res_token);
        TextView res_purchase_amount   = (TextView) row.findViewById(R.id.res_purchase_amount);
        TextView res_rittal_fee        = (TextView) row.findViewById(R.id.res_rittal_fee);

        TextView res_service     = (TextView) row.findViewById(R.id.response_service);
        TextView res_date        = (TextView) row.findViewById(R.id.res_date);
        TextView res_pan         = (TextView) row.findViewById(R.id.res_pan);
        TextView res_pan_titel         = (TextView) row.findViewById(R.id.res_pan_titel);
        TextView res_amount      = (TextView) row.findViewById(R.id.res_amount);
        TextView res_fee         = (TextView) row.findViewById(R.id.res_fee);
        TextView res_fee_title         = (TextView) row.findViewById(R.id.res_fee_title);
        TextView res_token_title               = (TextView)row.findViewById(R.id.res_token_title);

//        TextView res_total_bill_amount_titel         = (TextView) row.findViewById(R.id.res_bill_amount_titel);
        TextView res_to_card_title         = (TextView) row.findViewById(R.id.res_to_card_title);
        TextView res_code        = (TextView) row.findViewById(R.id.res_code);
        TextView res_message     = (TextView) row.findViewById(R.id.res_message);

        TextView res_phone               = (TextView) row.findViewById(R.id.res_phone);
        TextView res_contract            = (TextView) row.findViewById(R.id.res_contract);
        TextView res_subscriber_id       = (TextView) row.findViewById(R.id.res_subscriber_id);
        TextView res_total_bill_amount   = (TextView) row.findViewById(R.id.res_total_bill_amount);
        TextView res_billed_amount      = (TextView) row.findViewById(R.id.res_billed_amount);
        TextView res_bill_amount         = (TextView) row.findViewById(R.id.res_bill_amount);
        TextView res_bill_amount_titel         = (TextView) row.findViewById(R.id.res_bill_amount_titel);
        TextView res_unbill_amount       = (TextView) row.findViewById(R.id.res_unbill_amount);
        TextView res_last_invoice_date   = (TextView) row.findViewById(R.id.res_last_invoice_date);
        TextView res_last_4digit   = (TextView) row.findViewById(R.id.res_last4_digits);
        TextView res_to_card             = (TextView) row.findViewById(R.id.res_to_card);
        TextView res_operator            = (TextView) row.findViewById(R.id.res_operator);
        TextView res_voucher_number      = (TextView) row.findViewById(R.id.res_voucher_number);
        TextView res_voucher_code        = (TextView) row.findViewById(R.id.res_voucher_code);
        TextView res_unit_name           = (TextView) row.findViewById(R.id.res_unit_name);
        TextView res_service_name        = (TextView) row.findViewById(R.id.res_service_name);
        TextView res_invoice_expiry_date = (TextView) row.findViewById(R.id.res_invoice_expiry_date);
        TextView res_due_amount          = (TextView) row.findViewById(R.id.res_due_amount);
        TextView res_reference_id        = (TextView) row.findViewById(R.id.res_reference_id);
        TextView res_invoice_status      = (TextView) row.findViewById(R.id.res_invoice_status);
        TextView res_payer_name          = (TextView) row.findViewById(R.id.res_payer_name);
        TextView res_s_name          = (TextView) row.findViewById(R.id.res_s_name);
        TextView res_s_phone         = (TextView) row.findViewById(R.id.res_s_phone);
        TextView res_r_name          = (TextView) row.findViewById(R.id.res_r_name);
        TextView res_r_phone          = (TextView) row.findViewById(R.id.res_r_phone);

        TextView res_receipt_number          = (TextView) row.findViewById(R.id.res_receipt_number);

        Button btn_print = (Button) row.findViewById(R.id.btn_print);
        Button btn_share = (Button) row.findViewById(R.id.btn_share);
        Button btn_send = (Button) row.findViewById(R.id.btn_send);


        try {
            final JSONObject response = new JSONObject(transactions.get(position).getTRANSACTION_RESPONSE());

//            if(!response.getString("RespPAN").equals("9888081119403382541")){
//                transactionSource="CARD";
//            }

            final String service = transactions.get(position).getTRANSACTION_SERVICE_ID();
            final String opid = transactions.get(position).getOP_ID();

            btn_print.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MainActivity.pl.getState() == 3){
//                        try {
////                            if(!response.getString("RespPAN").equals("9888081119403382541")){
////                                rittal.log("transactionSource_befor",transactionSource);
////                                transactionSource="CARD";
////                                rittal.log("transactionSource_after",transactionSource);
////                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                        MainActivity.receiptPrint(activity,Locale.getDefault().getLanguage(),response,service,opid,transactionSource);                    }else{
                        Intent i = new Intent(activity, PrintSettingActivity.class);
                        activity.startActivityForResult(i,-1);
                    }
                }
            });

            btn_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                    sharePath=rittal.takeScreenshotCard(row);
                    if (!sharePath.equals("no")) {
                        rittal.shareImageReceipt(sharePath);
                    }
                    // shareImage();
                    //shareReceipt(view);
                }
            });



            mResponseCode = response.getString("responseCode");
            if(lang.equals("ar")){
                try {
                    mResponseMessage = response.getString("responseMessageArabic");
                }catch (Exception e){
                    rittal.log("Exception_responseMessageArabic",e.toString());
                }
                if(!mResponseMessage.equals("null")){
                    try {
                        mResponseMessage = response.getString("responseMessageArabic");
                    }catch (Exception e){
                        rittal.log("Exception_responseMessageArabic_no_equal_null",e.toString());
                    }
                }else{
                    mResponseMessage = response.getString("responseMessage");
                }
            }else
                {
                mResponseMessage = response.getString("responseMessage");
            }

            addText(res_code,mResponseCode);
            addText(res_message,mResponseMessage);
            if(service.equals("2")){
                //balance inquiry receipt
                addText(res_service,activity.getString(R.string.balance_inquery));
                if(response.getString("responseCode").equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    mResponseBalance    = response.getString("balance");
                    mResponseFee        = response.getString("resp_issuerTranFee");


                    addText(res_date,formatDate(mResponseDate));
                    if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                        addText(res_pan, hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);
                    }
                    addText(res_amount,mResponseBalance+" "+ activity.getString(R.string.sdg_hint));
                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                    res_fee_title.setVisibility(View.VISIBLE);
                    btn_print.setVisibility(View.VISIBLE);
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
                    if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                        addText(res_pan, hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);
                    }
                    addText(res_to_card,hashPan(mToCard));
                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                    res_fee_title.setVisibility(View.VISIBLE);
                    res_to_card_title.setVisibility(View.VISIBLE);
                    addText(res_amount,mTranAmount+" "+ activity.getString(R.string.sdg_hint));

                    btn_print.setVisibility(View.VISIBLE);

//                    addText(res_date,formatDate(mResponseDate));
//                    if(!mResponsePan.equals("null")){
//                        addText(res_pan, hashPan(mResponsePan));
//                        res_pan_titel.setVisibility(View.VISIBLE);
//                    }
//                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
//                    addText(res_to_card,hashPan(mToCard));
//                    addText(res_amount,mTranAmount+" "+ activity.getString(R.string.sdg_hint));
//                    btn_print.setVisibility(View.VISIBLE);
                }

            }
            else if(service.equals("4")){
                //bill inquiry receipt
                rittal.log("service name", service + activity.getString(R.string.title_bill_iquery));
                addText(res_service,activity.getString(R.string.title_bill_iquery));
                if(mResponseCode.equals("0")){

                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    mResponseFee        = response.getString("resp_issuerTranFee");
                    mPhoneNumber        = response.getString("resp_paymentInfo");
                    String mOperator = "";
//                    if(opid.equals("2")){
//                        //2 => Zain
//                        rittal.log("opreter name", opid + activity.getString(R.string.zain));
//                        mOperator = activity.getString(R.string.zain);
//
//                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));
//
//                        mContractNumber    = billInfo.getString("contractNumber");
//                        mTotalAmount       = billInfo.getString("totalAmount");
//                        mBilledAmount      = billInfo.getString("billedAmount");
//                        mUnbilledAmount    = billInfo.getString("unbilledAmount");
//                        mLastInvoiceDate   = billInfo.getString("lastInvoiceDate");
//                        mLast4Digits       = billInfo.getString("last4Digits");
//
//                        addText(res_date,formatDate(mResponseDate));
//
//                        if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
//                            addText(res_pan, hashPan(mResponsePan));
//                            res_pan_titel.setVisibility(View.VISIBLE);
//                        }
//                        addText(res_fee,mResponseFee);
//                        addText(res_operator,mOperator);
//                        addText(res_contract,mContractNumber);
//                        addText(res_total_bill_amount,mTotalAmount+" "+ activity.getString(R.string.sdg_hint));
//                        addText(res_billed_amount,mBilledAmount+" "+ activity.getString(R.string.sdg_hint));
//                        addText(res_phone,mPhoneNumber);
//                        addText(res_last_invoice_date,mLastInvoiceDate);
//                        addText(res_last_4digit,mLast4Digits);
//                        addText(res_unbill_amount,mUnbilledAmount+" "+ activity.getString(R.string.sdg_hint));
//                    }

                    if(opid.equals("2")){
                        //2 => Zain
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

                        if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                            addText(res_pan, hashPan(mResponsePan));
                            res_pan_titel.setVisibility(View.VISIBLE);
                        }
                        addText(res_fee,mResponseFee+" "+getResourseString(activity,R.string.sdg_hint,lang));
                        res_fee_title.setVisibility(View.VISIBLE);
                        addText(res_operator,mOperator);
                        addText(res_contract,mContractNumber);
                        addText(res_total_bill_amount,mTotalAmount+" "+ activity.getString(R.string.sdg_hint));
//                        res_total_bill_amount_titel.setVisibility(View.VISIBLE);
                        addText(res_billed_amount,mBilledAmount+" "+ activity.getString(R.string.sdg_hint));
                        addText(res_phone,mPhoneNumber);

                        addText(res_last_invoice_date,mLastInvoiceDate);
                        addText(res_last_4digit,mLast4Digits);
                        addText(res_unbill_amount,mUnbilledAmount+" "+ activity.getString(R.string.sdg_hint));

                    }
//                    else if(opid.equals("4")){
//                        rittal.log("opreter name", opid + activity.getString(R.string.mtn));
//                        mOperator = activity.getString(R.string.mtn);
//
//                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));
//                        rittal.log("billInfo_mtn",billInfo.toString());
////                        {"responseMessage":"Approved","responseMessageArabic":null,"responseCode":0,"responseStatus":"Successful","pubKeyValue":null,"tranDateTime":"240719091100","creationDate":null,"panCategory":null,"balance":null,"payeesCount":0,"payeesList":null,"billInfo":"{total:\"787.22516\",BillAmount:\"592.717\",lastInvoiceDate:\"20190630000000\",contractNumber:\"1037225044\",unbilledAmount:\"194.50816\"}","RespPAN":"9888081119999455925","RespExpDate":null,"RespIPIN":null,"regType":null,"financialInstitutionId":null,"respUserName":null,"respUserPassword":null,"RespVoucherNumber":null,"voucherCode":null,"serviceInfo":null,"originalTransaction":null,"originalTranType":null,"originalTranUUID":null,"respUUID":null,"resp_acqTranFee":0,"resp_issuerTranFee":0,"resp_fromAccount":null,"resp_accountCurrency":null,"resp_tranAmount":0,"resp_paymentInfo":"0990999007","toCard":null,"rittalFee":0,"clientFee":0,"voucherNumber":null}
////                        {total:\"787.22516\",BillAmount:\"592.717\",lastInvoiceDate:\"20190630000000\",contractNumber:\"1037225044\",unbilledAmount:\"194.50816\"}
//                        mContractNumber    = billInfo.getString("contractNumber");
//                        mTotalAmount       = billInfo.getString("total");
//                        mBilledAmount      = billInfo.getString("BillAmount");
//                        mUnbilledAmount    = billInfo.getString("unbilledAmount");
//                        mLastInvoiceDate   = billInfo.getString("lastInvoiceDate");
//
//                        addText(res_date,formatDate(mResponseDate));
//                        if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
//                            addText(res_pan, hashPan(mResponsePan));
//                            res_pan_titel.setVisibility(View.VISIBLE);
//                        }
//                        addText(res_phone,mPhoneNumber);
//                        addText(res_contract,mContractNumber);
//                        addText(res_total_bill_amount,mTotalAmount+getResourseString(activity,R.string.sdg_hint,lang));
//                        addText(res_bill_amount,mBilledAmount+getResourseString(activity,R.string.sdg_hint,lang));
//                        addText(res_unbill_amount,mUnbilledAmount+getResourseString(activity,R.string.sdg_hint,lang));
//                        addText(res_last_invoice_date,rittal.formatDateLastInvoice(mLastInvoiceDate));
//                        addText(res_fee,mResponseFee+" "+activity.getString(R.string.sdg_hint));
//                        res_fee_title.setVisibility(View.VISIBLE);
//                        addText(res_operator,mOperator);
//                    }
                    else if(opid.equals("4")){
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
                        if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                            addText(res_pan, hashPan(mResponsePan));
                            res_pan_titel.setVisibility(View.VISIBLE);
                        }
                        addText(res_phone,mPhoneNumber);

                        addText(res_contract,mContractNumber);
                        addText(res_total_bill_amount,mTotalAmount+getResourseString(activity,R.string.sdg_hint,lang));
                        res_bill_amount_titel.setVisibility(View.VISIBLE);
                        addText(res_bill_amount,mBilledAmount+getResourseString(activity,R.string.sdg_hint,lang));
                        res_bill_amount_titel.setVisibility(View.VISIBLE);
                        addText(res_unbill_amount,mUnbilledAmount+getResourseString(activity,R.string.sdg_hint,lang));
                        addText(res_last_invoice_date,rittal.formatDateLastInvoice(mLastInvoiceDate));
                        addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                        res_fee_title.setVisibility(View.VISIBLE);
                        addText(res_operator,mOperator);
                    }
                    else if(opid.equals("6")){
//                        rittal.log("opreter name", opid + activity.getString(R.string.sudani));
//                        mOperator = activity.getString(R.string.sudani);
//                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));
//
//                        addText(res_pan, hashPan(mResponsePan));
//                        mSubscriberId    = billInfo.getString("SubscriberID");
//                        mBilledAmount      = billInfo.getString("billAmount");
//
//                        addText(res_date,formatDate(mResponseDate));
//
//                        if(!mResponsePan.equals("null")){
//                            addText(res_pan, hashPan(mResponsePan));
//                            res_pan_titel.setVisibility(View.VISIBLE);
//                        }
//                        addText(res_pan, hashPan(mResponsePan));
//                        addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
//                        addText(res_phone,mPhoneNumber);
//                        addText(res_operator,mOperator);
//                        addText(res_subscriber_id,mSubscriberId);
//                        addText(res_bill_amount,mBilledAmount+" "+ activity.getString(R.string.sdg_hint));

                        rittal.log("opreter name", opid + activity.getString(R.string.sudani));
                        mOperator = activity.getString(R.string.sudani);
                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));

                        mSubscriberId    = billInfo.getString("SubscriberID");
                        mBilledAmount      = billInfo.getString("billAmount");

                        addText(res_date,formatDate(mResponseDate));
                        if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
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
                    else { }

                    btn_print.setVisibility(View.VISIBLE);
                }
            }
            else if(service.equals("5")){
                rittal.log("service name", service + activity.getString(R.string.title_bill_payment));
                addText(res_service,activity.getString(R.string.title_bill_payment));

                if(mResponseCode.equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    mResponseFee        = response.getString("resp_issuerTranFee");
                    mTotalAmount        = response.getString("resp_tranAmount");

                    JSONObject billInfo = new JSONObject(response.getString("billInfo"));
                    mReceiptNo        = billInfo.getString("receiptNo");

                    String mOperator = "";
                    if(opid.equals("2")){
                        mOperator = activity.getString(R.string.zain);
                    }else if(opid.equals("4")){
                        mOperator = activity.getString(R.string.mtn);
                    }else  if(opid.equals("6")){
                        mOperator = activity.getString(R.string.sudani);
                    }else{

                    }

                    addText(res_date,formatDate(mResponseDate));
                    if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                        addText(res_pan, hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);
                    }
                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                    addText(res_operator,mOperator);
                    addText(res_receipt_number,mReceiptNo);
                    addText(res_amount,mTotalAmount+" "+ activity.getString(R.string.sdg_hint));
                    res_bill_amount_titel.setVisibility(View.VISIBLE);
                    btn_print.setVisibility(View.VISIBLE);
                }
            }
            else if(service.equals("6")){
                rittal.log("service name", service + activity.getString(R.string.title_top_up));
                addText(res_service,activity.getString(R.string.title_top_up));
                if(mResponseCode.equals("0")){
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
                    if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                        addText(res_pan, hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);
                    }
                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                    res_fee_title.setVisibility(View.VISIBLE);
                    addText(res_operator,mOperator);
                    addText(res_phone,mPhoneNumber);
                    addText(res_amount,mTranAmount+" "+ activity.getString(R.string.sdg_hint));

                    btn_print.setVisibility(View.VISIBLE);

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
                    mVoucherNumber      = response.getString("RespVoucherNumber");
                    mVoucherCode        = response.getString("voucherCode");


                    addText(res_date,formatDate(mResponseDate));
                    if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                        addText(res_pan, hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);
                    }
                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                    res_fee_title.setVisibility(View.VISIBLE);
                    addText(res_amount,mTranAmount);
                    addText(res_voucher_code,mVoucherCode);
                    //addText(res_voucher_number,mVoucherNumber);
//                    addText(res_date,formatDate(mResponseDate));
//                    if(!mResponsePan.equals("null")){
//                        addText(res_pan, hashPan(mResponsePan));
//                    }
//                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
//                    addText(res_amount,mTranAmount);
//                    addText(res_voucher_number,mVoucherNumber);
//                    addText(res_voucher_code,mVoucherCode);

                    btn_print.setVisibility(View.VISIBLE);
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
                        if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                            addText(res_pan, hashPan(mResponsePan));
                        }
                        addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                        addText(res_amount,mTotalAmount+" "+ activity.getString(R.string.sdg_hint));
                        addText(res_unit_name,mUnitName);
                        addText(res_service_name,mServiceName);
                        addText(res_invoice_expiry_date,mInvoiceExpiry);
                        addText(res_due_amount,mDueAmount+" "+ activity.getString(R.string.sdg_hint));
                        addText(res_reference_id,mReferenceId);
                        addText(res_invoice_status,status_text);
                        addText(res_payer_name,mPayerName);
                    }
                    btn_print.setVisibility(View.VISIBLE);

                }

            }
            else if(service.equals("10")){
                rittal.log("service name", service + activity.getString(R.string.title_e15));
                addText(res_service,activity.getString(R.string.title_e15));

                if(mResponseCode.equals("0")){
                    if(opid.equals("6")){
                        mResponseDate       = response.getString("tranDateTime");
                        mResponsePan        = response.getString("RespPAN");
                        mResponseFee        = response.getString("resp_issuerTranFee");

                        JSONObject billInfo = new JSONObject(response.getString("billInfo"));

                        mUnitName       = billInfo.getString("UnitName");
                        mServiceName    = billInfo.getString("ServiceName");
                        mTotalAmount    = billInfo.getString("TotalAmount");
                        mReferenceId    = billInfo.getString("ReferenceId");
                        mPayerName      = billInfo.getString("PayerName");



                        addText(res_date,formatDate(mResponseDate));
                        if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                            addText(res_pan, hashPan(mResponsePan));
                        }
                        addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                        addText(res_amount,mTotalAmount+" "+ activity.getString(R.string.sdg_hint));
                        addText(res_unit_name,mUnitName);
                        addText(res_service_name,mServiceName);
                        addText(res_reference_id,mReferenceId);
                        addText(res_payer_name,mPayerName);
                    }
                    btn_print.setVisibility(View.VISIBLE);
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
                        mResponsePan        = response.getString("PAN");
                        mMeterNO          = response.getString("MeterNO");

                        mWaterArrear          = response.getString("ServiceCharge");
                        mMeterArrear          = response.getString("feesAMT");
                        mPAmount          = response.getString("PAmount");
                        mFisrtPoweramt          = response.getString("FisrtPoweramt");
                        mSecPoweramt          = response.getString("SecPoweramt");

                        mFisrtPowerID       =  response.getString("FisrtPowerID");
                        mSecPowerID        =  response.getString("SecPowerID");

                        mKWPrice        =  response.getString("FisrtPowerPrice");
                        mSecondKWPrice        =  response.getString("SecPowerPrice");

                        mAmount          = response.getString("Amount");
                        mUnits          = response.getString("Units");
                        mKWPrice        =  response.getString("FisrtPowerPrice");
                        mToken          = response.getString("Token");
                        mCustomerName   = response.getString("CustomerName");

                        mResponseFee   = response.getString("ct_fees");
                        mRittalFee   = response.getString("elec_fees");

                        addText(res_date,formatDate(mResponseDate));
                        if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
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


                        //btn_send.setVisibility(View.VISIBLE);

                        //addText(res_fee,mResponseFee);
                        //addText(res_rittal_fee,mRittalFee);
                    }
                }

                if(opid.equals("C")){
                    addText(res_service,activity.getString(R.string.purchase_electricity));

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
                        if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                            addText(res_pan, hashPan(mResponsePan));
                            res_pan_titel.setVisibility(View.VISIBLE);

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

                        String smsText = "";
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


                            //if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                                //smsText = smsText + res_fee_title.getText().toString()+ res_fee.getText().toString() + "\n";

                            //}
                            smsText = smsText + rittal.addTextForSMSEng(R.string.res_token,mToken) + "\n";

                        }catch (Exception e){}


                        btn_print.setVisibility(View.VISIBLE);
                        btn_send.setVisibility(View.VISIBLE);

                        final String finalSmsText = smsText;
                        btn_send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", "", null));
                                    intent.putExtra("sms_body", finalSmsText);
                                    activity.startActivity(intent);
                                }catch (Exception e){}
                            }
                        });


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
                    if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                        addText(res_pan, hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);
                    }
                    addText(res_amount,mAmount+" "+ activity.getString(R.string.sdg_hint));
                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                    res_fee_title.setVisibility(View.VISIBLE);
                    addText(res_rittal_fee,mRittalFee+" "+ activity.getString(R.string.sdg_hint));
                    btn_print.setVisibility(View.VISIBLE);
                }
            }
//            else if(service.equals("13")){
//                addText(res_service,activity.getString(R.string.recharge_card_title));
//                if(response.getString("responseCode").equals("0")){
//                    mResponseDate       = response.getString("tranDateTime");
//                    mResponsePan        = response.getString("RespPAN");
//                    try {
//                        mToCard        = response.getString("toCard");
//                    }catch (Exception e){}
//                    // mResponseBalance = response.getString("balance");
//                    mAmount        = response.getString("resp_tranAmount");
//                    // mResponseFee        = response.getString("resp_acqTranFee");
//                    mRittalFee          = response.getString("rittalFee");
//                    //   mResponseFee        = response.getString("resp_issuerTranFee");
//                    addText(res_date,formatDate(mResponseDate));
//                    if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
//                        addText(res_pan, hashPan(mResponsePan));
//                        res_pan_titel.setVisibility(View.VISIBLE);
//                    }
//                    addText(res_amount,mAmount+" "+ activity.getString(R.string.sdg_hint));
//                    addText(res_to_card,hashPan(mToCard));
//                    // addText(res_fee,mResponseFee);
//                    addText(res_rittal_fee,mRittalFee+" "+ activity.getString(R.string.sdg_hint));
//                    btn_print.setVisibility(View.VISIBLE);
//                }
//            }
            else if(service.equals("13")){
                addText(res_service,activity.getString(R.string.recharge_card_title));
                if(response.getString("responseCode").equals("0")){
                    mResponseDate       = response.getString("tranDateTime");
                    mResponsePan        = response.getString("RespPAN");
                    try {
                        mToCard        = response.getString("toCard");
                    }catch (Exception e){}
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

                    btn_print.setVisibility(View.VISIBLE);
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
                    if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                        addText(res_pan, hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);
                    }
                    addText(res_amount,mAmount+" "+ activity.getString(R.string.sdg_hint));
                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                    res_fee_title.setVisibility(View.VISIBLE);
                    addText(res_rittal_fee,mRittalFee+" "+ activity.getString(R.string.sdg_hint));
                    btn_print.setVisibility(View.VISIBLE);
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

                    //{"responseMessage":"Approval","responseMessageArabic":"معاملة ناجحة","responseCode":0,
                    // "responseStatus":"Successful","pubKeyValue":null,"tranDateTime":null,
                    // "creationDate":null,"panCategory":null,"balance":null,
                    // "payeesCount":0,"payeesList":null,"billInfo":null,"RespPAN":null,"RespExpDate":null,
                    // "RespIPIN":null,"regType":null,"financialInstitutionId":null,"respUserName":null,
                    // "respUserPassword":null,"RespVoucherNumber":null,"voucherCode":null,"serviceInfo":null,"originalTransaction":null,
                    // "originalTranType":null,"originalTranUUID":null,
                    // "respUUID":null,"resp_acqTranFee":0,"resp_issuerTranFee":0,
                    // "resp_fromAccount":null,"resp_accountCurrency":null,"resp_tranAmount":1,
                    // "resp_paymentInfo":null,"toCard":null,"rittalFee":0,"clientFee":0,"voucherNumber":"775861"}

                    addText(res_date,formatDate(mResponseDate));

                    if(!mResponsePan.equals("null")&& !mResponsePan.equals("9888081119403382541")){
                        addText(res_pan,hashPan(mResponsePan));
                        res_pan_titel.setVisibility(View.VISIBLE);
                        rittal.log("mResponsePan",mResponsePan);
                    }
                    addText(res_voucher_number,mVoucherNumber);
                    addText(res_amount,mAmount+" "+ activity.getString(R.string.sdg_hint));
                    addText(res_s_name,mSenderName);
                    addText(res_r_name,mReceiverName);
                    addText(res_fee,mResponseFee+" "+ activity.getString(R.string.sdg_hint));
                    res_fee_title.setVisibility(View.VISIBLE);
                    addText(res_rittal_fee,mRittalFee+" "+ activity.getString(R.string.sdg_hint));
                    btn_print.setVisibility(View.VISIBLE);
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
                    btn_print.setVisibility(View.VISIBLE);
                }
            }

            else{}
        } catch (JSONException e) {
            e.printStackTrace();
            rittal.log("Response_dialog_exception",e.toString());
        }
        return row;
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
            String date = "";
            return  date;
        }


    }
    public String formatToken(String text){

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



}
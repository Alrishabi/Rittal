package sd.rittal.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import sd.rittal.app.AppApplication;
import sd.rittal.app.R;
import sd.rittal.app.helper.DataStorage;
import sd.rittal.app.helper.FormSession;
import sd.rittal.app.helper.LoginSession;
import sd.rittal.app.helper.Rittal;

public class ProfileActivity extends AppCompatActivity implements FormSession,LoginSession {

    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;
    AppApplication application;

    Toolbar toolbar;

    TextView user_name,user_code;
    Button btn_logout,btn_cashout,ban_accountToCard,btn_cardToAccount,btn_knwonGenerateVoucher,btn_voucherCashOut;

    Thread thread ;
    public final static int QRcodeWidth = 500 ;
    Bitmap bitmap ;

    ImageView iv_qr_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initiateContext();
        initiateLayout();

        user_name.setText(rittal.getValueOfKey("user_name"));
        user_code.setText(rittal.getValueOfKey("user_code"));

        try {
            bitmap = TextToImageEncode(rittal.getValueOfKey("user_id"));
        } catch (WriterException e) {
            e.printStackTrace();
        }

        iv_qr_code.setImageBitmap(bitmap);
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

        ((AppApplication)getApplication()).registerSessionListener(this);
        ((AppApplication)getApplication()).startUserSession();

        ((AppApplication)getApplication()).registerSessionListener2(this);
        ((AppApplication)getApplication()).startLoginSession();


    }

    private void initiateLayout(){

        user_name        = (TextView) findViewById(R.id.user_name);
        user_code        = (TextView) findViewById(R.id.user_code);
        iv_qr_code       = (ImageView) findViewById(R.id.iv_qr_code);

        btn_cashout       = (Button) findViewById(R.id.btn_cashOut);
        ban_accountToCard = (Button) findViewById(R.id.ban_accountToCard);
        btn_cardToAccount       = (Button) findViewById(R.id.btn_cardToAccount);
        btn_knwonGenerateVoucher       = (Button) findViewById(R.id.btn_knwonGenerateVoucher);
        btn_voucherCashOut = (Button) findViewById(R.id.btn_voucherCashOut);
        btn_logout       = (Button) findViewById(R.id.btn_logout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(getString(R.string.title_rittal_account));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rittal.logOutFromAccount();
                finish();
                startActivity( new Intent(activity,LoginActivity.class));
            }
        });
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    public void goToCashout(View view){
        Intent i = new Intent(activity,CashoutActivity.class);
        startActivity(i);
    }

    public void goToChargCard(View view) {
        Intent i = new Intent(activity,CardRechargeFromAccount.class);
        startActivity(i);
    }

    public void goToAccountRecharge(View view) {
        Intent i = new Intent(activity,AccountRechargeFromCard.class);
        startActivity(i);
    }

    public void goToKnownGenerateVoucher(View view) {
        Intent i = new Intent(activity,KnownGenerateVoucher.class);
        startActivity(i);
    }

    public void goToKnownVoucherCashout(View view) {
        Intent i = new Intent(activity,KnownGenerateVoucherCashout.class);
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
//{"RRN":"799581db-1d0b-47f9-8afd-055754e291bb","MeterNO":"04184260000","Amount":"3","PAmount":"1","STS":null,"responseMessage":"Confirmed","responseCode":1,"responseMessageArabic":null,"Units":"3.2","Token":null,"ServiceCharge":"0.00","CustomerName":"mhmoud hasan kona mohamed","Tarrif":null,"units":null,"stateId":null,"Curent_Balance":0,"Invoice":null,"calcQty":null,"buyTimes":null,"VAt":null,"verifyCode":null,"stampTax":null,"code":null,"netAMT":0,"commAMT":0,"refCode":null,"regCode":null,"Rslt_State":null,"vendQty":null,"vendAMT":0,"supplyAMT":0,"transTime":null,"transID":null,"feesAMT":"0","checkCode":null,"Chanel":null,"FisrtPowerID":"1","FisrtPoweramt":"1","FisrtPowerKwt":"3.2","FisrtPowerPrice":"0.3200","SecPowerID":"0","SecPoweramt":"0","SecPowerKwt":"0","SecPowerPrice":"0","DetailedArreas":null,"ArrearsItems":"","MIN_AMT":null,"keyId_1":null,"keyName_1":null,"KeyToken_1":null,"keyId_2":null,"keyName_2":null,"keyToken_2":null,"KeyId_2":null,"elec_fees":null,"ct_fees":null,"PAN":"9222081110094611845","tranDateTime":"180919091340"}

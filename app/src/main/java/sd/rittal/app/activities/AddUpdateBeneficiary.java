package sd.rittal.app.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import sd.rittal.app.AppApplication;
import sd.rittal.app.R;
import sd.rittal.app.helper.DataStorage;
import sd.rittal.app.helper.FormSession;
import sd.rittal.app.helper.LoginSession;
import sd.rittal.app.helper.Rittal;

@SuppressLint("Registered")
public class AddUpdateBeneficiary extends AppCompatActivity implements FormSession,LoginSession {
    Activity activity;
    Rittal rittal;
    DataStorage dataStorage;
    Bundle extras;

    String process="";
    String card_id,card_name,card_pan,card_expiry_date;

    private int mSelectedColor;
    Toolbar toolbar;


    LinearLayout layout_selected_color;
    EditText et_beneficiary_name,et_beneficiary_value;
    Spinner sp_beneficiary_type;
    Button btn_add_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_beneficiary);

        initContext();
        initLayout();

        extras = getIntent().getExtras();
        process="";

        process = extras.getString("process");
        if(process.trim().toLowerCase().equals("update")){
            setCardDataToUpdate();
        }else{
            mSelectedColor = ContextCompat.getColor(this, R.color.tomato);

            layout_selected_color.getBackground().setColorFilter(mSelectedColor, PorterDuff.Mode.SRC_ATOP);
        }
//
        btn_add_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(process.trim().toLowerCase().equals("add")){
                    add();
                }
                if(process.trim().toLowerCase().equals("update")){
                    update();
                }
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    /**
     ** bind layout with activity class
     **/
    public void initLayout(){

        et_beneficiary_name     = (EditText) findViewById(R.id.et_beneficiary_name);
        et_beneficiary_value             = (EditText) findViewById(R.id.et_beneficiary_value);
        sp_beneficiary_type     = (Spinner) findViewById(R.id.sp_beneficiary_type);
        btn_add_update          = (Button) findViewById(R.id.btn_add_update);
//
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(getString(R.string.add_beneficiary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        et_card_expiry_date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////        createDialogWithoutDateField().show();
//                MonthYearPickerDialog pickerDialog = new MonthYearPickerDialog();
//                pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker datePicker, int year, int month, int i2) {
//                        String y=String.valueOf(year);
//                        if(month>9) {
//                            Toast.makeText(activity, y.substring(2) + month, Toast.LENGTH_SHORT).show();
//                            et_card_expiry_date.setText(y.substring(2) + month);
//                        }else {
//                            Toast.makeText(activity, y.substring(2) + "0"+month, Toast.LENGTH_SHORT).show();
//                            et_card_expiry_date.setText(y.substring(2) + "0"+month);
//                        }
//                    }
//                });
//                pickerDialog.show(getSupportFragmentManager(), "Expire date picker ");
//            }
//        });
    }
    /**
     ** initiation objects
     **/
    public void initContext(){
        activity = this;
        rittal = new Rittal(activity);
        dataStorage = new DataStorage(activity);
        ( (AppApplication)getApplication()).registerSessionListener(this);
        ( (AppApplication)getApplication()).startUserSession();

        ( (AppApplication)getApplication()).registerSessionListener2(this);
        ( (AppApplication)getApplication()).startLoginSession();

    }

    public void setCardDataToUpdate(){

        toolbar.setTitle(getString(R.string.update_card));

        card_id             = extras.getString("id");
        card_name           = extras.getString("name");
        card_pan            = extras.getString("pan");
        card_expiry_date    = extras.getString("expiry_date");
        mSelectedColor      = Integer.valueOf(extras.getString("color"));

        et_beneficiary_name.setText(card_name);
        et_beneficiary_value.setText(card_pan);
        sp_beneficiary_type.getSelectedItem();
        layout_selected_color.getBackground().setColorFilter(mSelectedColor, PorterDuff.Mode.SRC_ATOP);
    }

    public void colorPicker(int SelectedColor){

        int[] mColors = getResources().getIntArray(R.array.default_rainbow);

        ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.select_color_title,
                mColors,
                SelectedColor,
                5, // Number of columns
                ColorPickerDialog.SIZE_SMALL,
                true // True or False to enable or disable the serpentine effect
                //0, // stroke width
                //Color.BLACK // stroke color
        );

        dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                mSelectedColor = color;
                layout_selected_color.getBackground().setColorFilter(mSelectedColor, PorterDuff.Mode.SRC_ATOP);

            }

        });

        dialog.show(getFragmentManager(), "color_dialog_test");

    }

    public void showColorPicker(View v){
        colorPicker(mSelectedColor);
    }

    public Boolean isValidate(){
        boolean result = false;
        String name     = et_beneficiary_name.getText().toString().trim();
        String pan      = et_beneficiary_value.getText().toString().trim().replace(" ","");
        String exDate   = sp_beneficiary_type.getSelectedItem().toString().trim();

        if(name.length() == 0 || pan.length() == 0 || exDate.length() == 0){

            if(name.length() == 0){rittal.rittalToast(getString(R.string.enter_card_name),"1","D");}
            if(pan.length() == 0){rittal.rittalToast(getString(R.string.enter_card_pan),"1","D");}
            if(exDate.length() == 0){rittal.rittalToast(getString(R.string.enter_card_expiry_date),"1","D");}
        }else{
            if(name.length() >= 4 && (pan.length() == 16 || pan.length() ==19) && exDate.length() == 4){
                result = true;
            }else{
                if(name.length() >= 4){
                    if(pan.length() == 16 || pan.length() ==19){
                        if(exDate.length() == 4){
                            result = true;
                        }else{
                            rittal.rittalToast(getString(R.string.card_expiry_date_length),"1","D");
                            result = false;
                        }
                    }else{
                        rittal.rittalToast(getString(R.string.card_pan_length),"1","D");
                        result = false;
                    }
                }else{
                    rittal.rittalToast(getString(R.string.enter_card_name_length),"1","D");
                    result = false;
                }
            }
        }

        return  result;
    }

    public void add(){
        //rittal.rittalToast(et_card_pan.getText().toString().trim(),"0","I");
        if(isValidate()){
            String name     = et_beneficiary_name.getText().toString().trim();
            String pan      = et_beneficiary_value.getText().toString().trim().replace(" ","");
            String formatedExDate   = sp_beneficiary_type.getSelectedItem().toString().trim();
            String exDate   = formatedExDate.replace("/","");
            if(dataStorage.add_card(name,pan,exDate,String.valueOf(mSelectedColor))){
                rittal.log("mSelectedColor", String.valueOf(mSelectedColor));
                rittal.rittalToast("Card saved successfully","1","S");
                finish();
            }else{
                rittal.rittalToast("Sorry, Card did't saved successfully","1","D");
            }

        }
    }

    public void update(){
        if(isValidate()){
            String name     = et_beneficiary_name.getText().toString().trim();
            String pan      = et_beneficiary_value.getText().toString().trim();
            String exDate   = sp_beneficiary_type.getSelectedItem().toString().trim();
            if(dataStorage.update_card(card_id,name,pan,exDate,String.valueOf(mSelectedColor))){
                rittal.rittalToast(getResources().getString(R.string.card_update_successfully),"1","S");
                finish();
            }else{
                rittal.rittalToast(getResources().getString(R.string.card_did_not_update_successfully),"1","D");
            }

        }
    }

    @Override
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

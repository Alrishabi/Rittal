package sd.rittal.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import sd.rittal.app.R;

public class ForceUpdateActivity extends AppCompatActivity {

//    Activity activity;
//    Rittal rittal;
//    DataStorage dataStorage;
//    AppApplication application;
    Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_force_update);
//        initiateContext();
        initLayout();
    }

    public void initLayout(){
        updateButton = (Button) findViewById(R.id.update_btn);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });
    }

    public void update(){
        try {
            startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse("market://details?id=sd.rittal.consumer")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=sd.rittal.consumer")));
        }
        finish();
    }
//    private void initiateContext(){
//        activity = this;
//        rittal = new Rittal(activity);
//        dataStorage = new DataStorage(activity);
//        application = new AppApplication();
//    }

}

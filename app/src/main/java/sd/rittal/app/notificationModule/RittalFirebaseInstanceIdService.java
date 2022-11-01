package sd.rittal.app.notificationModule;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


import sd.rittal.app.helper.Rittal;

public class RittalFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String REG_TOKEN = "REG_TOKEN";
    Rittal rittal;

    @Override
    public void onTokenRefresh() {
        //fzCJAyr5OEQ:APA91bH0PXBPEUGi9XitZ-FJXqarAdHx8s7G2krP2MWboea-YKl9fHkM8MNontpep8ScN6k4ghDa7_i2FDGbMwj8kVGUZSatRla64tu_da5YFzljdauEUCTntRbx359-sDDK5JuEYlfq
        String recent_Token = FirebaseInstanceId.getInstance().getToken();
        Log.d(REG_TOKEN,recent_Token);
        //rittal = new Rittal(this);
        //rittal.setValueToKey("firebase_id",recent_Token);

    }
}

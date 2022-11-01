package sd.rittal.app.widget;

/**
 * Created by Ahmed Khatim on 9/30/2018.
 */
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

public class ExpiryDateMask implements TextWatcher {
    private boolean isRunning = false;
    private boolean isDeleting = false;
    private final String mask;

    public ExpiryDateMask(String mask) {
        this.mask = mask;
    }

    public static ExpiryDateMask buildCpf() {
        return new ExpiryDateMask("###.###.###-##");
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        isDeleting = count > after;
        Log.d("beforeTextChanged ","start at -> "+ start);
        Log.d("beforeTextChanged ","after the -> "+ after);
        Log.d("beforeTextChanged ","count is -> "+ count);
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        Log.d("onTextChanged ","start at -> "+ start);
        Log.d("onTextChanged ","before the -> "+ before);
        Log.d("onTextChanged ","count is -> "+ count);
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (isRunning || isDeleting) {
            return;
        }
        isRunning = true;

        int editableLength = editable.length();
        if (editableLength < mask.length()) {
            if (mask.charAt(editableLength) != '#') {
                editable.append(mask.charAt(editableLength));
            } else if (mask.charAt(editableLength-1) != '#') {
                editable.insert(editableLength-1, mask, editableLength-1, editableLength);
            }
        }

        isRunning = false;

        Log.d("afterTextChanged ","editableLength is -> "+ editableLength);
        Log.d("afterTextChanged ","maskLength is -> "+ mask.length());
        if (editableLength < mask.length()) {
            Log.d("afterTextChanged ","maskCharAtEdLength -> "+ mask.charAt(editableLength)+" editableLength is ->"+editableLength);
        }

        Log.d("afterTextChanged ","maskCharAtEdLength-1 -> "+ mask.charAt(editableLength-1));

        for(int i = 0 ;i < mask.length() ; i++ ){

            Log.d("afterTextChanged ","mask at  -> "+ i +" is ->"+mask.charAt(i));

        }
    }
}

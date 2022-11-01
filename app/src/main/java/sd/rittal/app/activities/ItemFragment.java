package sd.rittal.app.activities;

/**
 * Created by Ahmed Khatim on 10/28/2018.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import sd.rittal.app.R;
import sd.rittal.app.utilites.CarouselLinearLayout;

public class ItemFragment extends Fragment {

    public String CARD_NAME,CARD_PAN,CARD_EXPIRY_DATE,CARD_COLOR;

    private static final String POSITON = "position";
    private static final String SCALE = "scale";
    private static final String DRAWABLE_RESOURE = "resource";

    private int screenWidth;
    private int screenHeight;

    private int[] imageArray = new int[]{R.drawable.ic_check_white_24dp, R.drawable.ic_check_white_24dp,
            R.drawable.ic_check_white_24dp, R.drawable.ic_check_white_24dp, R.drawable.ic_check_white_24dp,
            R.drawable.ic_check_white_24dp, R.drawable.ic_check_white_24dp, R.drawable.ic_check_white_24dp,
            R.drawable.ic_check_white_24dp, R.drawable.ic_check_white_24dp};

    public static Fragment newInstance(Activity context, int pos, float scale) {
        Bundle b = new Bundle();
        b.putInt(POSITON, pos);
        b.putFloat(SCALE, scale);

        return Fragment.instantiate(context, ItemFragment.class.getName(), b);
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidthAndHeight();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        final int postion = this.getArguments().getInt(POSITON);
        float scale = this.getArguments().getFloat(SCALE);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth / 2, screenHeight / 2);
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_image, container, false);

        //TextView textView = (TextView) linearLayout.findViewById(R.id.textView1);
        CarouselLinearLayout root = (CarouselLinearLayout) linearLayout.findViewById(R.id.root_container);
        //ImageView imageView = (ImageView) linearLayout.findViewById(R.id.pagerImg);

        //textView.setText("Carousel item: " + postion);
        //imageView.setLayoutParams(layoutParams);
        //imageView.setImageResource(imageArray[postion]);

        //handling click event
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                Intent intent = new Intent(getActivity(), ImageDetailsActivity.class);
////                intent.putExtra(DRAWABLE_RESOURE,imageArray[postion]);
////                startActivity(intent);
//
//            }
//        });

        root.setScaleBoth(scale);

        return linearLayout;
    }

    /**
     * Get device screen width and height
     */
    private void getWidthAndHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;
    }
}

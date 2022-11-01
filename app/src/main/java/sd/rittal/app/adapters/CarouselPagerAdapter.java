package sd.rittal.app.adapters;

/**
 * Created by Ahmed Khatim on 10/28/2018.
 */

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import sd.rittal.app.R;
import sd.rittal.app.activities.ItemFragment;
import sd.rittal.app.objects.Card;
import sd.rittal.app.utilites.CarouselLinearLayout;


public class CarouselPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    public final static float BIG_SCALE = 0.8f;
    public final static float SMALL_SCALE = 0.2f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    private Context context;
    private FragmentManager fragmentManager;
    private float scale;

    public ArrayList<Card> card_list = new ArrayList<Card>();
    ViewPager pager;
    EditText et_card_pan, et_card_expiry_date;

    LinearLayout layout_card_data;
    LinearLayout layout_card_secret;

    public CarouselPagerAdapter(Context context, ArrayList<Card> card_list, ViewPager pager, EditText et_card_pan, EditText et_card_expiry_date, FragmentManager fm, LinearLayout layout_card_data, LinearLayout layout_card_secret) {
        super(fm);
        this.fragmentManager = fm;
        this.context = context;
        this.card_list = card_list;
        this.pager = pager;
        this.et_card_pan = et_card_pan;
        this.et_card_expiry_date = et_card_expiry_date;
        this.layout_card_data = layout_card_data;
        this.layout_card_secret = layout_card_secret;
    }

    @Override
    public Fragment getItem(int position) {
        // make the first pager bigger than others
        try {
            if (position == card_list.size()-1)
                scale = BIG_SCALE;
            else
                scale = SMALL_SCALE;

            position = position % card_list.size();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ItemFragment.newInstance((Activity) context, position, scale);
    }

    @Override
    public int getCount() {
        int count = card_list.size();
//        try {
//            count = EBSBalanceInquery.count * EBSBalanceInquery.LOOPS;
//        } catch (Exception e) {
//            // TODO: handle exception
//            e.printStackTrace();
//        }
        return count;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        try {
            if (positionOffset >= 0f && positionOffset <= 1f) {
                CarouselLinearLayout cur = getRootView(position);
                CarouselLinearLayout next = getRootView(position + 1);

                cur.setScaleBoth(BIG_SCALE - DIFF_SCALE * positionOffset);
                next.setScaleBoth(SMALL_SCALE + DIFF_SCALE * positionOffset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @SuppressWarnings("ConstantConditions")
    private CarouselLinearLayout getRootView(int position) {
        CarouselLinearLayout root_container = (CarouselLinearLayout) fragmentManager.findFragmentByTag(this.getFragmentTag(position))
                .getView().findViewById(R.id.root_container);

        TextView card_name        = (TextView) root_container.findViewById(R.id.card_name);
        TextView card_pan         = (TextView) root_container.findViewById(R.id.card_pan);
        TextView card_expiry_date = (TextView) root_container.findViewById(R.id.card_expiry_date);
        LinearLayout pageImg = (LinearLayout) root_container.findViewById(R.id.pagerImg);

        card_name.setText(card_list.get(position).getName());
        card_pan.setText(hashPan(card_list.get(position).getPan()));
        card_expiry_date.setText(card_list.get(position).getExpiry_date());
        card_list.get(position).getColor();
            if (!card_list.get(position).getExpiry_date().equals("ACCC"))
        pageImg.setBackgroundColor(Integer.parseInt(card_list.get(position).getColor()));

        //ImageView pagerImg = (ImageView) root_container.findViewById(R.id.pagerImg);

        //pagerImg.setImageResource(R.drawable.ripple_icon);

        return root_container;
    }

    private String getFragmentTag(int position) {
        Log.d("android:switcher "," android:switcher:" + pager.getId() + ":" + position);
        try {
            if(et_card_pan != null && et_card_expiry_date != null){
                if (card_list.get(position-1).getExpiry_date().toUpperCase().equals("ACCC")) {
                    Log.d("STAGE", "ACCOUNT");
                    Log.d("PAN", card_list.get(position-1).getPan());
                    et_card_pan.setText(card_list.get(position-1).getPan());
                    et_card_expiry_date.setText(card_list.get(position-1).getExpiry_date());

                    if(layout_card_data != null){
                        layout_card_data.setVisibility(View.GONE);
                    }

                    if(layout_card_secret != null){
                        layout_card_secret.setVisibility(View.GONE);
                    }

                }else {
                    Log.d("STAGE", "CARD");
                    Log.d("PAN", card_list.get(position-1).getPan());
                    if(layout_card_data != null){
                        layout_card_data.setVisibility(View.VISIBLE);
                    }

                    if(layout_card_secret != null){
                        layout_card_secret.setVisibility(View.VISIBLE);
                    }

                    et_card_pan.setText(card_list.get(position-1).getPan());
                    et_card_expiry_date.setText(card_list.get(position-1).getExpiry_date());
                }

            }
        }catch (Exception E){
            Log.d("Carosal Exeption", E.toString());

        }

        return "android:switcher:" + pager.getId() + ":" + position;




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
}
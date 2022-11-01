package sd.rittal.app.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import sd.rittal.app.R;
import sd.rittal.app.activities.AddUpdateCard;
import sd.rittal.app.helper.DataStorage;
import sd.rittal.app.objects.Card;
import sd.rittal.app.objects.Contact;

public class ContactAdapter extends BaseAdapter {


    List<Contact> cards;
    DataStorage dataStorage;

    String process;
    Bundle extras;

    private int mSelectedColor;

    //Used to create view.
    private LayoutInflater mInflater;
    Activity activity  ;
    ProgressBar progressBar ;

    int lastExpandedPosition = -1;

    public ContactAdapter(Activity activity, List<Contact> cards, ProgressBar progressBar){
        //Collections.sort(category, Collections.reverseOrder());
        this.cards =cards ;
        this.activity =activity ;
        dataStorage = new DataStorage(activity);
        mInflater = LayoutInflater.from(activity);

    }
    /** Get the adapter category (Contacts) count */
    public int getCount() {
        return cards.size();
    }

    /** Get item (Contact) by position */
    public Object getItem(int position) {
        return cards.get(position);
    }
    /** Get item (Contact) id by position */

    public long getItemId(int position) {
        return cards.get(position).getId();
    }
    /** Get item (Contact) view by position */
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        final View row =mInflater.inflate(R.layout.item_card,parent, false);

        TextView name = (TextView) row.findViewById(R.id.et_card_name);
        TextView pan = (TextView) row.findViewById(R.id.et_card_pan);
        TextView ex_date = (TextView) row.findViewById(R.id.et_card_expiry_date);
        LinearLayout layout_card_color = (LinearLayout) row.findViewById(R.id.layout_card_color);
        LinearLayout layout_actions = (LinearLayout) row.findViewById(R.id.layout_actions);
        TextView delete               = (TextView) row.findViewById(R.id.delete);
        TextView edit               = (TextView) row.findViewById(R.id.edit);

        name.setText(cards.get(position).getName());
        pan.setText(hashPan(cards.get(position).getPan()));
//        ex_date.setText(cards.get(position).getExpiry_date());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // notifyItemRemoved(position);
                //  notifyItemRangeChanged(position,cards.size());
                //   activity.finish();
                //  activity.startActivity(new Intent(activity,CardsListActivity.class));
                new AlertDialog.Builder(activity)
                        .setIcon(R.drawable.delete)
                        .setTitle(R.string.delete_card)
                        .setMessage(activity.getResources().getString(R.string.are_u_sure_delete))
                        .setPositiveButton(activity.getResources().getString(R.string.delete),new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dataStorage.delete_card(cards.get(position).getPan());
                                cards.remove(position);
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton(R.string.cancel_button,null)
                        .show();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(activity, AddUpdateCard.class);
                i.putExtra("process","update");
                i.putExtra("id", String.valueOf(cards.get(position)));
                i.putExtra("name",cards.get(position).getName());
                i.putExtra("pan",cards.get(position).getPan());
//                i.putExtra("expiry_date",cards.get(position).getExpiry_date());
                i.putExtra("color",cards.get(position).getColor());
                activity.startActivity(i);
            }
        });
        layout_card_color.getBackground().setColorFilter(Integer.valueOf(cards.get(position).getColor()), PorterDuff.Mode.SRC_ATOP);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(lastExpandedPosition != -1 && lastExpandedPosition && position){
//
//                }

            }
        });
        return row;
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

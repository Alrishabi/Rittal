package sd.rittal.app.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by ahmed on 9/16/2018.
 */

public class DataStorage extends SQLiteOpenHelper {
    private static final String DB_NAME = "rittal_db.edb";

    //Tables names
    private static final String TABLE_CARDS = "cards";
    private static final String TABLE_FAVORITE_CONTACT = "contact";
    private static final String TABLE_TRANSACTIONS = "transactions";

    // Cards table fields
    private static final String CARDS_id            = "id";
    private static final String CARDS_NAME          = "name";
    private static final String CARDS_PAN           = "pan";
    private static final String CARDS_EXPIRY_DATE   = "expiry_date";
    private static final String CARDS_COLOR         = "color";
    // Contact table fields
    private static final String CONTACT_id            = "id";
    private static final String CONTACT_NAME          = "name";
    private static final String CONTACT_PAN           = "pan";
    private static final String CONTACT_COLOR         = "color";

    private static final String TRANSACTION_id                  = "id";
    private static final String TRANSACTION_RESPONSE            = "response";
    private static final String TRANSACTION_SERVICE_ID          = "service_id";
    private static final String USER_ID                         = "user_id";
    private static final String OP_ID                           = "op_id";

    public DataStorage(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table "+TABLE_CARDS+" ("+CARDS_id+" integer primary key,"+CARDS_NAME+" text,"+CARDS_PAN+" text,"+CARDS_EXPIRY_DATE+" ,"+CARDS_COLOR+")");
        db.execSQL("create table "+TABLE_FAVORITE_CONTACT+" ("+CONTACT_id+" integer primary key,"+CONTACT_NAME+" text,"+CONTACT_PAN+" text,"+CONTACT_COLOR+")");
        db.execSQL("create table "+TABLE_TRANSACTIONS+" ("+TRANSACTION_id+" integer primary key,"+TRANSACTION_RESPONSE+" text,"+TRANSACTION_SERVICE_ID+" text,"+USER_ID+" text,"+OP_ID+" text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_CARDS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_FAVORITE_CONTACT);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_TRANSACTIONS);
        onCreate(db);
    }

    // cards functions
    public boolean add_card(String name,String pan,String expiry_date,String color) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(CARDS_NAME, name);
            contentValues.put(CARDS_PAN, Encryption.Encrypt(pan));
            contentValues.put(CARDS_EXPIRY_DATE, expiry_date);
            contentValues.put(CARDS_COLOR, color);

            db.insert(TABLE_CARDS, null, contentValues);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean add_contact(String name,String pan,String color) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(CONTACT_NAME, name);
            contentValues.put(CONTACT_PAN, Encryption.Encrypt(pan));
            contentValues.put(CARDS_COLOR, color);

            db.insert(TABLE_CARDS, null, contentValues);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean add_transaction (String transaction_response,String transaction_service_id,String user_id,String op_id,String transactionSource) {


        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TRANSACTION_RESPONSE, transaction_response);
            contentValues.put(TRANSACTION_SERVICE_ID, transaction_service_id);
            contentValues.put(USER_ID, user_id);
            contentValues.put(OP_ID, op_id);

            db.insert(TABLE_TRANSACTIONS, null, contentValues);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean update_card  (String id, String name, String pan, String expiry_date, String color) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(CARDS_NAME, name);
            contentValues.put(CARDS_PAN, Encryption.Encrypt(pan));
            contentValues.put(CARDS_EXPIRY_DATE, expiry_date);
            contentValues.put(CARDS_COLOR, color);
            //db.update(TABLE_CARDS, contentValues, CARDS_id +"= ?" ,new String[] { id });

              db.update(TABLE_CARDS, contentValues,"Where =", new String[] {id});
//            db.update(TABLE_CARDS, contentValues,CARDS_id +" = '"+ id + "'", null);
 //           db.update(TABLE_CARDS, contentValues,CARDS_id +" = '"+ id + "'", null);


            return true;
        }catch (Exception e){
            return false;
        }
    }
    public Cursor get_cards(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_CARDS, null );
        return res;
    }
    public Cursor get_transactions(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_TRANSACTIONS, null );
        return res;
    }
    public Integer delete_card (String pan) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CARDS,CARDS_PAN + " = ?",new String[] {Encryption.Encrypt(pan)} );

    }

    public Boolean delete_card_by_expiry_date (String expiry_date) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_CARDS,CARDS_EXPIRY_DATE + " = ?",new String[] {expiry_date} );
            return  true;
        }catch (Exception e){
            return false;

        }

    }

}

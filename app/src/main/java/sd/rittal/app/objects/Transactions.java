package sd.rittal.app.objects;

public class Transactions {
    private int TRANSACTION_id ;
    private String TRANSACTION_RESPONSE  ;
    private String TRANSACTION_SERVICE_ID ;
    private String USER_ID ;
    private String OP_ID  ;

    public Transactions(int TRANSACTION_id, String TRANSACTION_RESPONSE, String TRANSACTION_SERVICE_ID, String USER_ID, String OP_ID) {
        this.TRANSACTION_id = TRANSACTION_id;
        this.TRANSACTION_RESPONSE = TRANSACTION_RESPONSE;
        this.TRANSACTION_SERVICE_ID = TRANSACTION_SERVICE_ID;
        this.USER_ID = USER_ID;
        this.OP_ID = OP_ID;
    }

    public int getTRANSACTION_id() {
        return TRANSACTION_id;
    }

    public String getTRANSACTION_RESPONSE() {
        return TRANSACTION_RESPONSE;
    }

    public String getTRANSACTION_SERVICE_ID() {
        return TRANSACTION_SERVICE_ID;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public String getOP_ID() {
        return OP_ID;
    }
}

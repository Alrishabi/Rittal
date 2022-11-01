package sd.rittal.app.objects;

/**
 * Created by ahmed on 9/16/2018.
 */

public class Card {
    private int id;
    private String name;
    private String pan;
    private String expiry_date;
    private String color;

    public Card(int id, String name, String pan, String expiry_date, String color) {
        this.id = id;
        this.name = name;
        this.pan = pan;
        this.expiry_date = expiry_date;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPan() {
        return pan;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public String getColor() {
        return color;
    }
}

package sd.rittal.app.objects;

public class Contact {
    private int id;
    private String name;
    private String pan;
    private String color;

    public Contact(int id, String name, String pan, String color) {
        this.id = id;
        this.name = name;
        this.pan = pan;
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

    public String getColor() {
        return color;
    }
}

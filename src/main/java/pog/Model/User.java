package pog.Model;

public class User {
    private int id;
    private String name;
    private String surname;
    private String email;
    private String tip;
    private String fakultetNaziv;

    public String getFakultetNaziv() {
        return fakultetNaziv;
    }

    public void setFakultetNaziv(String fakultetNaziv) {
        this.fakultetNaziv = fakultetNaziv;
    }

    public User(int id, String name, String surname, String email, String tip) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.tip = tip;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getTip() {
        return tip;
    }
}

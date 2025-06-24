package pog.Model;

public class User {
    private int id;
    private String name;
    private String surname;
    private String email;
    private String tip;
    private String fakultetNaziv;
    private String stepen;
    private String centarNaziv;
    private String jmbg;
    private java.sql.Date datumRodjenja;
    private String telefon;

    public User(int id, String name, String surname, String email, String tip, String fakultetNaziv, String stepen,
                String centarNaziv, String jmbg, java.sql.Date datumRodjenja, String telefon) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.tip = tip;
        this.fakultetNaziv = fakultetNaziv;
        this.stepen = stepen;
        this.centarNaziv = centarNaziv;
        this.jmbg = jmbg;
        this.datumRodjenja = datumRodjenja;
        this.telefon = telefon;
    }


    public int getId() { return id; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getEmail() { return email; }
    public String getTip() { return tip; }
    public String getFakultetNaziv() { return fakultetNaziv; }
    public void setFakultetNaziv(String fakultetNaziv) { this.fakultetNaziv = fakultetNaziv; }
    public String getStepen() { return stepen; }
    public void setStepen(String stepen) { this.stepen = stepen; }
    public String getCentarNaziv() { return centarNaziv; }
    public void setCentarNaziv(String centarNaziv) { this.centarNaziv = centarNaziv; }

    // Novi getteri
    public String getJmbg() { return jmbg; }
    public java.sql.Date getDatumRodjenja() { return datumRodjenja; }
    public String getTelefon() { return telefon; }
}

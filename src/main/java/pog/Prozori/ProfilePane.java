package pog.Prozori;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import pog.Model.User;

import java.text.SimpleDateFormat;

public class ProfilePane extends VBox {
    public ProfilePane(User user) {
        setPadding(new Insets(20));
        setSpacing(10);
        getStyleClass().add("content-pane");


        Label nameLabel = new Label("Ime: " + (user.getName() != null ? user.getName() : "Nije dostupno"));
        nameLabel.getStyleClass().add("label");
        Label surnameLabel = new Label("Prezime: " + (user.getSurname() != null ? user.getSurname() : "Nije dostupno"));
        surnameLabel.getStyleClass().add("label");
        Label emailLabel = new Label("Email: " + (user.getEmail() != null ? user.getEmail() : "Nije dostupno"));
        emailLabel.getStyleClass().add("label");
        Label tipLabel = new Label("Tip: " + (user.getTip() != null ? user.getTip() : "Nije dostupno"));
        tipLabel.getStyleClass().add("label");
        Label fakultetLabel = new Label("Fakultet: " + (user.getFakultetNaziv() != null ? user.getFakultetNaziv() : "Nije dostupno"));
        fakultetLabel.getStyleClass().add("label");


        Label jmbgLabel = new Label("JMBG: " + (user.getJmbg() != null ? user.getJmbg() : "Nije dostupno"));
        jmbgLabel.getStyleClass().add("label");
        Label birthDateLabel = new Label("Datum rođenja: " +
                (user.getDatumRodjenja() != null ? new SimpleDateFormat("dd.MM.yyyy").format(user.getDatumRodjenja()) : "Nije dostupno"));
        birthDateLabel.getStyleClass().add("label");
        Label phoneLabel = new Label("Telefon: " + (user.getTelefon() != null ? user.getTelefon() : "Nije dostupno"));
        phoneLabel.getStyleClass().add("label");
        Label stepenLabel = new Label("Stepen: " + (user.getStepen() != null ? user.getStepen() : "Nije dostupno"));
        stepenLabel.getStyleClass().add("label");
        Label centarLabel = new Label("Centar obuke: " + (user.getCentarNaziv() != null ? user.getCentarNaziv() : "Nije dostupno"));
        centarLabel.getStyleClass().add("label");


        getChildren().addAll(nameLabel, surnameLabel, emailLabel, tipLabel, fakultetLabel,
                jmbgLabel, birthDateLabel, phoneLabel, stepenLabel, centarLabel);
    }
}

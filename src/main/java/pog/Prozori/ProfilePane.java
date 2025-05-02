package pog.Prozori;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import pog.Model.User;

public class ProfilePane extends VBox {
    public ProfilePane(User user) {
        setPadding(new Insets(20));
        setSpacing(10);
        getStyleClass().add("content-pane");

        Label nameLabel = new Label("Ime: " + user.getName());
        nameLabel.getStyleClass().add("label");
        Label surnameLabel = new Label("Prezime: " + user.getSurname());
        surnameLabel.getStyleClass().add("label");
        Label emailLabel = new Label("Email: " + user.getEmail());
        emailLabel.getStyleClass().add("label");
        Label tipLabel = new Label("Tip: " + user.getTip());
        tipLabel.getStyleClass().add("label");
        Label fakultetLabel = new Label("Fakultet: " + (user.getFakultetNaziv() != null ? user.getFakultetNaziv() : "Nije dostupno"));
        fakultetLabel.getStyleClass().add("label");

        getChildren().addAll(nameLabel, surnameLabel, emailLabel, tipLabel, fakultetLabel);
    }
}
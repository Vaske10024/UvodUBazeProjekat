package pog.Prozori;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import pog.Model.User;

public class ProfilePane extends VBox {
    public ProfilePane(User user) {
        setPadding(new Insets(20));
        setSpacing(10);

        Label nameLabel = new Label("Ime: " + user.getName());
        Label surnameLabel = new Label("Prezime: " + user.getSurname());
        Label emailLabel = new Label("Email: " + user.getEmail());
        Label tipLabel = new Label("Tip: " + user.getTip());

        getChildren().addAll(nameLabel, surnameLabel, emailLabel, tipLabel);
    }
}

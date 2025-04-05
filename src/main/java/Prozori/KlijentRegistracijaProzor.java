package Prozori;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class KlijentRegistracijaProzor {
    public static void prikazi(Stage primaryStage) {
        primaryStage.setTitle("Klijent - Registracija");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Registracija klijenta");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        // Forma za registraciju
        Label imeLabel = new Label("Ime:");
        grid.add(imeLabel, 0, 1);
        TextField imeField = new TextField();
        grid.add(imeField, 1, 1);

        Label prezimeLabel = new Label("Prezime:");
        grid.add(prezimeLabel, 0, 2);
        TextField prezimeField = new TextField();
        grid.add(prezimeField, 1, 2);

        Label emailLabel = new Label("Email:");
        grid.add(emailLabel, 0, 3);
        TextField emailField = new TextField();
        grid.add(emailField, 1, 3);

        Label pwLabel = new Label("Lozinka:");
        grid.add(pwLabel, 0, 4);
        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 4);

        Label telefonLabel = new Label("Telefon:");
        grid.add(telefonLabel, 0, 5);
        TextField telefonField = new TextField();
        grid.add(telefonField, 1, 5);

        CheckBox ranijeIshodjenje = new CheckBox("Ranije ishodjenje psihoterapije");
        grid.add(ranijeIshodjenje, 1, 6);

        Button btnRegistracija = new Button("Registruj se");
        Button btnNazad = new Button("Nazad");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(btnNazad, btnRegistracija);
        grid.add(hbBtn, 1, 7);

        btnRegistracija.setOnAction(e -> {
            // Logika za registraciju   soon -->
            System.out.println("Registracija klijenta...");
        });

        btnNazad.setOnAction(e -> {
            KlijentLoginProzor.prikazi(new Stage());
            primaryStage.close();
        });

        Scene scene = new Scene(grid, 500, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
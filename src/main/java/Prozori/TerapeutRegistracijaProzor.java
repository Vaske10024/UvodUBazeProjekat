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

public class TerapeutRegistracijaProzor {
    public static void prikazi(Stage primaryStage) {
        primaryStage.setTitle("Terapeut - Registracija");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Registracija terapeuta");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        // Forma za registraciju terapeuta
        Label imeLabel = new Label("Ime:");
        grid.add(imeLabel, 0, 1);
        TextField imeField = new TextField();
        grid.add(imeField, 1, 1);

        Label prezimeLabel = new Label("Prezime:");
        grid.add(prezimeLabel, 0, 2);
        TextField prezimeField = new TextField();
        grid.add(prezimeField, 1, 2);

        Label licenceLabel = new Label("Broj licence:");
        grid.add(licenceLabel, 0, 3);
        TextField licenceField = new TextField();
        grid.add(licenceField, 1, 3);

        Label emailLabel = new Label("Email:");
        grid.add(emailLabel, 0, 4);
        TextField emailField = new TextField();
        grid.add(emailField, 1, 4);

        Label pwLabel = new Label("Lozinka:");
        grid.add(pwLabel, 0, 5);
        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 5);

        Label specijalizacijaLabel = new Label("Specijalizacija:");
        grid.add(specijalizacijaLabel, 0, 6);
        ComboBox<String> specijalizacijaCombo = new ComboBox<>();
        specijalizacijaCombo.getItems().addAll(
                "Kognitivno-bihevioralna terapija",
                "Humanistička terapija",
                "Porodična terapija"
        );
        grid.add(specijalizacijaCombo, 1, 6);

        Button btnRegistracija = new Button("Registruj se");
        Button btnNazad = new Button("Nazad");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(btnNazad, btnRegistracija);
        grid.add(hbBtn, 1, 7);

        btnRegistracija.setOnAction(e -> {
            // Logika za registraciju  UBACICEMOAXFJSDKFG
            System.out.println("Registracija terapeuta...");
        });

        btnNazad.setOnAction(e -> {
            TerapeutLoginProzor.prikazi(new Stage());
            primaryStage.close();
        });

        Scene scene = new Scene(grid, 500, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
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

public class TerapeutLoginProzor {
    public static void prikazi(Stage primaryStage) {
        primaryStage.setTitle("Terapeut - Prijava");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Prijava terapeuta");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label licenceLabel = new Label("Broj licence:");
        grid.add(licenceLabel, 0, 1);
        TextField licenceField = new TextField();
        grid.add(licenceField, 1, 1);

        Label pwLabel = new Label("Lozinka:");
        grid.add(pwLabel, 0, 2);
        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btnPrijava = new Button("Prijavi se");
        Button btnRegistracija = new Button("Registruj se");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(btnRegistracija, btnPrijava);
        grid.add(hbBtn, 1, 4);

        btnPrijava.setOnAction(e -> {
            // Validacija i prijava tek treba se ubaci
            System.out.println("Prijava terapeuta...");
        });

        btnRegistracija.setOnAction(e -> {
            TerapeutRegistracijaProzor.prikazi(new Stage());
            primaryStage.close();
        });

        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
package Prozori;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainProzor extends Application {

    // Declare buttons as instance variables
    private Button btnKlijent;
    private Button btnTerapeut;

    @Override
    public void start(Stage primaryStage) {
        // Glavni kontejner
        VBox mainLayout = new VBox(30);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(50));
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom right, #f0f9ff, #cfe2f3);");

        // Naslov
        Text title = new Text("Dobrodošli u Savetovalište 'Novi Početak'");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setFill(Color.DARKSLATEBLUE);

        // Dugmad - initialize the buttons
        btnKlijent = createStyledButton("Klijent", "#4CAF50");
        btnTerapeut = createStyledButton("Psihoterapeut", "#2196F3");

        // Postavljanje akcija
        btnKlijent.setOnAction(e -> showKlijentLogin(btnKlijent));
        btnTerapeut.setOnAction(e -> showTerapeutLogin(btnTerapeut));

        // Dodavanje elemenata u layout
        mainLayout.getChildren().addAll(title, btnKlijent, btnTerapeut);

        // Scena
        Scene scene = new Scene(mainLayout, 800, 600);

        // CSS za dodatni styling (corrected path)
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (NullPointerException e) {
            System.err.println("CSS fajl nije pronađen, aplikacija će raditi bez dodatnih stilova");
        }

        // Podešavanje stage-a
        primaryStage.setTitle("Novi Početak - Početna");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 18px;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 15 30;"
                + "-fx-background-radius: 25px;"
                + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: derive(" + color + ", -20%);"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + color + ";"));

        return btn;
    }

    private void showKlijentLogin(Button sourceButton) {
        KlijentLoginProzor.prikazi(new Stage());
        ((Stage) sourceButton.getScene().getWindow()).close();
    }

    private void showTerapeutLogin(Button sourceButton) {
        TerapeutLoginProzor.prikazi(new Stage());
        ((Stage) sourceButton.getScene().getWindow()).close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package Prozori;



import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class KlijentLoginProzor {
    public static void prikazi(Stage primaryStage) {
        primaryStage.setTitle("Klijent - Prijava");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Prijava klijenta");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label emailLabel = new Label("Email:");
        grid.add(emailLabel, 0, 1);

        TextField emailField = new TextField();
        grid.add(emailField, 1, 1);

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

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        btnPrijava.setOnAction(e -> {
            // Validacija i prijava
            actiontarget.setText("Prijava u toku...");
        });

        btnRegistracija.setOnAction(e -> {
            KlijentRegistracijaProzor.prikazi(new Stage());
            primaryStage.close();
        });

        Scene scene = new Scene(grid, 400, 350);


        /// OCAJNICKI POKUSAJ UBACIBVANJA CSS ALI NER ADI TKD JBG
        try{
            scene.getStylesheets().add(KlijentLoginProzor.class.getResource("src/main/resources/styles.css").toExternalForm());
        }
        catch (Exception e){
            System.out.println("Pog");
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
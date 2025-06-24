package pog;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pog.Controlleri.LoginController;
import pog.Controlleri.MainController;
import pog.Controlleri.RegisterController;
import pog.Model.User;

public class NoviPocetakApp extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginScene();
    }

    public void showLoginScene() {
        LoginController loginController = new LoginController(this);
        Scene loginScene = new Scene(loginController.getView(), 400, 300);
        loginScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Novi početak - Prijava");
        primaryStage.show();
    }

    public void showRegisterScene() {
        RegisterController registerController = new RegisterController(this);
        Scene registerScene = new Scene(registerController.getView(), 400, 500);
        registerScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(registerScene);
        primaryStage.setTitle("Novi početak - Registracija");
        primaryStage.show();
    }

    public void showMainScene(User user) {
        MainController mainController = new MainController(this, user);
        Scene mainScene = new Scene(mainController.getView(), 800, 600);
        mainScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Novi početak - Glavni prozor");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
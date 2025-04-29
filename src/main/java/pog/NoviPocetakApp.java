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
    private User loggedInUser;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginScene();
    }

    public void showLoginScene() {
        LoginController loginController = new LoginController(this);
        Scene loginScene = new Scene(loginController.getView(), 400, 300);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Login - Novi početak");
        primaryStage.show();
    }

    public void showMainScene(User user) {
        this.loggedInUser = user;
        MainController mainController = new MainController(this, user);
        Scene mainScene = new Scene(mainController.getView(), 800, 600);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Novi početak - Glavni prozor");
    }

    public void showRegisterScene() {
        RegisterController registerController = new RegisterController(this);
        Scene registerScene = new Scene(registerController.getView(), 600, 600);
        primaryStage.setScene(registerScene);
        primaryStage.setTitle("Registracija - Novi početak");
    }

    public static void main(String[] args) {
        launch(args);
    }
}


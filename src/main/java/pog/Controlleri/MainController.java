package pog.Controlleri;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import pog.NoviPocetakApp;
import pog.Prozori.*;
import pog.Model.User;

public class MainController {
    private NoviPocetakApp app;
    private User user;
    private BorderPane view;

    public MainController(NoviPocetakApp app, User user) {
        this.app = app;
        this.user = user;
        view = new BorderPane();
        view.getStyleClass().add("main-form");

        VBox navigation = new VBox(10);
        navigation.setPadding(new Insets(10));
        navigation.getStyleClass().add("navigation");

        Button profileButton = new Button("Profil");
        profileButton.getStyleClass().add("nav-button");
        Button clientsButton = new Button("Klijenti");
        clientsButton.getStyleClass().add("nav-button");
        Button sessionsButton = new Button("Sesije");
        sessionsButton.getStyleClass().add("nav-button");
        Button paymentsButton = new Button("Plaćanja");
        paymentsButton.getStyleClass().add("nav-button");
        Button therapistsButton = new Button("Terapeuti");
        therapistsButton.getStyleClass().add("nav-button");
        Button logoutButton = new Button("Odjavi se");
        logoutButton.getStyleClass().add("nav-button");

        navigation.getChildren().addAll(profileButton, clientsButton, sessionsButton, paymentsButton, therapistsButton, logoutButton);
        view.setLeft(navigation);

        profileButton.setOnAction(e -> showProfile());
        clientsButton.setOnAction(e -> showClients());
        sessionsButton.setOnAction(e -> showSessions());
        paymentsButton.setOnAction(e -> showPayments());
        therapistsButton.setOnAction(e -> showTherapists());
        logoutButton.setOnAction(e -> app.showLoginScene());

        showProfile();
    }

    private void showProfile() {
        ProfilePane profilePane = new ProfilePane(user);
        profilePane.getStyleClass().add("content-pane");
        view.setCenter(profilePane);
    }

    private void showClients() {
        ClientsPane clientsPane = new ClientsPane(user);
        clientsPane.getStyleClass().add("content-pane");
        view.setCenter(clientsPane);
    }

    private void showSessions() {
        SessionsPane sessionsPane = new SessionsPane(app, user);
        sessionsPane.getStyleClass().add("content-pane");
        view.setCenter(sessionsPane);
    }

    private void showPayments() {
        PaymentsPane paymentsPane = new PaymentsPane(user);
        paymentsPane.getStyleClass().add("content-pane");
        view.setCenter(paymentsPane);
    }

    private void showTherapists() {
        TherapistsPane therapistsPane = new TherapistsPane(user);
        therapistsPane.getStyleClass().add("content-pane");
        view.setCenter(therapistsPane);
    }

    public BorderPane getView() {
        return view;
    }
}
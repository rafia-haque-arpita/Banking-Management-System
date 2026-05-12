import java.util.HashMap;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BankApp extends Application {

    Stage window;
    Scene mainScene, loginScene, registerScene, dashboardScene;
    private HashMap<String, User> users = new HashMap<>();
    private User loggedInUser = null;

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("🏛️ ULAB Bank Premium");

        String btnStyle = "-fx-background-color: linear-gradient(to right, #4b6cb7, #182848);"
                + "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;"
                + "-fx-background-radius: 15; -fx-padding: 10 20;";

        String fieldStyle = "-fx-background-color: rgba(255,255,255,0.8);"
                + "-fx-background-radius: 12; -fx-padding: 10; -fx-font-size: 14px; -fx-border-color: #ddd;";

        DropShadow drop = new DropShadow(15, Color.gray(0.3));

        Background gradientBg = new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#e0eafc")),
                        new Stop(1, Color.web("#cfdef3"))),
                CornerRadii.EMPTY, Insets.EMPTY));

        // === MAIN PAGE ===
        Label bankName = new Label("💳 ULAB Bank");
        bankName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        bankName.setTextFill(Color.web("#2c3e50"));

        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Register");
        loginBtn.setStyle(btnStyle);
        registerBtn.setStyle(btnStyle);
        loginBtn.setPrefWidth(150);
        registerBtn.setPrefWidth(150);

        VBox mainCard = new VBox(20, bankName, loginBtn, registerBtn);
        mainCard.setAlignment(Pos.CENTER);
        mainCard.setPadding(new Insets(40));
        mainCard.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.9), new CornerRadii(20), Insets.EMPTY)));
        mainCard.setEffect(drop);

        StackPane mainLayout = new StackPane(mainCard);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setBackground(gradientBg);
        mainScene = new Scene(mainLayout, 500, 400);

        // === LOGIN PAGE ===
        VBox loginCard = createFormCard("🔐 Login", btnStyle, fieldStyle);
        TextField loginUser = new TextField(); loginUser.setPromptText("Username"); loginUser.setStyle(fieldStyle);
        PasswordField loginPass = new PasswordField(); loginPass.setPromptText("Password"); loginPass.setStyle(fieldStyle);
        Button loginSubmit = new Button("Submit"); loginSubmit.setStyle(btnStyle);
        Button loginBack = new Button("⬅ Back"); loginBack.setStyle(btnStyle);

        loginSubmit.setOnAction(e -> {
            String username = loginUser.getText();
            String password = loginPass.getText();

            if (users.containsKey(username)) {
                User user = users.get(username);
                if (user.checkPassword(password)) {
                    loggedInUser = user;
                    showAlert(Alert.AlertType.INFORMATION, "Login successful!");

                    Object[] nodes = (Object[]) dashboardScene.getUserData();
                    Label welcomeLabel = (Label) nodes[0];
                    Label balanceLabel = (Label) nodes[1];
                    welcomeLabel.setText("👋 Welcome, " + loggedInUser.getUsername());
                    balanceLabel.setText("Balance: $" + loggedInUser.getBalance());

                    window.setScene(dashboardScene);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Incorrect password.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "User not found.");
            }
        });

        loginBack.setOnAction(e -> {
            fadeScene(mainCard);
            window.setScene(mainScene);
        });

        loginCard.getChildren().addAll(loginUser, loginPass, loginSubmit, loginBack);
        StackPane loginLayout = new StackPane(loginCard);
        loginLayout.setBackground(gradientBg);
        loginScene = new Scene(loginLayout, 500, 400);

        // === REGISTER PAGE ===
        VBox registerCard = createFormCard("📝 Register", btnStyle, fieldStyle);
        TextField regUser = new TextField(); regUser.setPromptText("Username"); regUser.setStyle(fieldStyle);
        PasswordField regPass = new PasswordField(); regPass.setPromptText("Password"); regPass.setStyle(fieldStyle);
        TextField regEmail = new TextField(); regEmail.setPromptText("Email"); regEmail.setStyle(fieldStyle);
        Button regSubmit = new Button("Register"); regSubmit.setStyle(btnStyle);
        Button regBack = new Button("⬅ Back"); regBack.setStyle(btnStyle);

        regSubmit.setOnAction(e -> {
            String username = regUser.getText();
            String password = regPass.getText();
            String email = regEmail.getText();

            if (users.containsKey(username)) {
                showAlert(Alert.AlertType.ERROR, "Username already exists.");
            } else if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please fill all fields.");
            } else {
                users.put(username, new User(username, password, email));
                showAlert(Alert.AlertType.INFORMATION, "Registration successful!");
                regUser.clear(); regPass.clear(); regEmail.clear();
                window.setScene(loginScene);
            }
        });

        regBack.setOnAction(e -> {
            fadeScene(mainCard);
            window.setScene(mainScene);
        });

        registerCard.getChildren().addAll(regUser, regPass, regEmail, regSubmit, regBack);
        StackPane regLayout = new StackPane(registerCard);
        regLayout.setBackground(gradientBg);
        registerScene = new Scene(regLayout, 500, 450);

        // === DASHBOARD PAGE ===
        dashboardScene = createDashboardScene();

        // === Button Actions ===
        loginBtn.setOnAction(e -> {
            fadeScene(loginCard);
            window.setScene(loginScene);
        });

        registerBtn.setOnAction(e -> {
            fadeScene(registerCard);
            window.setScene(registerScene);
        });

        // === Show
        fadeScene(mainCard);
        window.setScene(mainScene);
        window.show();
    }

    private Scene createDashboardScene() {
        Label welcomeLabel = new Label();
        welcomeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        welcomeLabel.setTextFill(Color.web("#2c3e50"));

        Label balanceLabel = new Label();
        balanceLabel.setFont(Font.font(16));
        balanceLabel.setTextFill(Color.DARKGREEN);

        Button depositBtn = new Button("💸 Deposit");
        Button withdrawBtn = new Button("💳 Withdraw");
        Button logoutBtn = new Button("🚪 Logout");

        depositBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        withdrawBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white;");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        depositBtn.setOnAction(e -> {
            TextInputDialog input = new TextInputDialog();
            input.setHeaderText("Enter amount to deposit:");
            input.showAndWait().ifPresent(amountStr -> {
                try {
                    double amt = Double.parseDouble(amountStr);
                    loggedInUser.deposit(amt);
                    balanceLabel.setText("Balance: $" + loggedInUser.getBalance());
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Invalid amount.");
                }
            });
        });

        withdrawBtn.setOnAction(e -> {
            TextInputDialog input = new TextInputDialog();
            input.setHeaderText("Enter amount to withdraw:");
            input.showAndWait().ifPresent(amountStr -> {
                try {
                    double amt = Double.parseDouble(amountStr);
                    if (loggedInUser.withdraw(amt)) {
                        balanceLabel.setText("Balance: $" + loggedInUser.getBalance());
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Insufficient balance.");
                    }
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Invalid amount.");
                }
            });
        });

        logoutBtn.setOnAction(e -> {
            loggedInUser = null;
            window.setScene(mainScene);
        });

        VBox dashCard = new VBox(15, welcomeLabel, balanceLabel, depositBtn, withdrawBtn, logoutBtn);
        dashCard.setAlignment(Pos.CENTER);
        dashCard.setPadding(new Insets(30));
        dashCard.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.95), new CornerRadii(20), Insets.EMPTY)));
        dashCard.setEffect(new DropShadow(10, Color.gray(0.3)));

        StackPane dashLayout = new StackPane(dashCard);
        dashLayout.setBackground(new Background(new BackgroundFill(Color.web("#f0f4f7"), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(dashLayout, 500, 400);
        scene.setUserData(new Object[]{welcomeLabel, balanceLabel});
        return scene;
    }

    private VBox createFormCard(String title, String btnStyle, String fieldStyle) {
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.95), new CornerRadii(20), Insets.EMPTY)));
        card.setEffect(new DropShadow(10, Color.gray(0.4)));
        card.getChildren().add(titleLabel);

        return card;
    }

    private void fadeScene(Pane pane) {
        FadeTransition ft = new FadeTransition(Duration.millis(500), pane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

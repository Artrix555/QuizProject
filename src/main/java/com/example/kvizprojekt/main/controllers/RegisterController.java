package com.example.kvizprojekt.main.controllers;

import com.example.kvizprojekt.entities.RegisterValidation;
import com.example.kvizprojekt.entities.User;
import com.example.kvizprojekt.enums.TypeOfUser;
import com.example.kvizprojekt.exceptions.AccountCreationException;
import com.example.kvizprojekt.main.QuizApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RegisterController extends RegisterValidation {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField1;

    @FXML
    private PasswordField passwordField2;

    @FXML
    private Button registerButton;

    @FXML
    public void handleRegister() {
        try {
            String username = usernameField.getText();
            String password1 = passwordField1.getText();
            String password2 = passwordField2.getText();

            if (username.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
                throw new AccountCreationException("Jedno ili više polja je prazno!");
            }
            if (!password1.equals(password2)) {
                throw new AccountCreationException("Šifre se ne podudaraju!");
            }
            if (isUsernameTaken(username)) {
                throw new AccountCreationException("Korisničko ime je već zauzeto! Odaberite novo i pokušajte ponovo.");
            }
            String hashedPassword = hashPassword(password1);

            if (hashedPassword == null) {
                throw new AccountCreationException("Greška prilikom unosa šifre. Molimo pokušajte ponovo kasnije.");
            }
            if (!writeCredentialsToFile(new User(username, hashedPassword, TypeOfUser.STANDARD_USER.getDescription(), 0))) {
                throw new AccountCreationException("Greška prilikom stvaranja računa. Molimo pokušajte ponovo kasnije.");
            }

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Uspjeh");
            successAlert.setHeaderText("Račun je stvoren");
            successAlert.setContentText("Vaš račun je uspješno stvoren!");
            successAlert.showAndWait()
                    .ifPresent(response -> {
                        try {
                            QuizApplication.username = username;
                            QuizApplication.logger.info("Novi račun je uspješno stvoren!");
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/kvizprojekt/main/controllers/start_screen.fxml"));
                            Parent root = fxmlLoader.load();
                            Scene currentScene = registerButton.getScene();
                            currentScene.setRoot(root);
                        } catch (IOException e) {
                            QuizApplication.logger.error("Dogodila se greška prilikom otvaranja početnog ekrana: ", e);
                            e.printStackTrace();
                        }
                    });
        } catch (AccountCreationException e) {
            QuizApplication.logger.error("Dogodila se greška prilikom registracije: " + e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška!");
            alert.setHeaderText("Greška tijekom registracije.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void loginHyperlink() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/kvizprojekt/main/controllers/login.fxml"));
            Parent root = fxmlLoader.load();
            Scene currentScene = usernameField.getScene();
            currentScene.setRoot(root);

        } catch (IOException e) {
            QuizApplication.logger.error("Dogodila se greška prilikom otvaranja ekrana za prijavu: ", e);
            e.printStackTrace();
        }

    }
}
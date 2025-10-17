package com.pluralsight.capstones;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;

public class AccountingApplicationGUI extends Application {

    private BorderPane screenContainer;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        AccountingApplication.sortLedger();
        screenContainer = new BorderPane();
        Scene scene = new Scene(screenContainer, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/retro.css").toExternalForm());
        homeScreen();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Accounting Application");
        primaryStage.show();
    }

    private BorderPane createScreen(String headerTitle){
        BorderPane border = new BorderPane();
        border.setPadding(new Insets(20));

        Label title = new Label(headerTitle);
        title.setFont(Font.font("Sevastopol", FontWeight.BOLD, 36));
        title.setTextFill(Color.CYAN);
        title.setEffect(new DropShadow(20, Color.CYAN));

        BorderPane.setAlignment(title, Pos.TOP_CENTER);
        border.setCenter(title);
        return border;
    }

    private TextField createTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.getStyleClass().add("field");
        return tf;
    }

    private Button createButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("button");
        return btn;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void displayLedger(TextArea area, ArrayList<Transactions> transactions) {
        StringBuilder sb = new StringBuilder();
        transactions.stream()
                .sorted(Comparator.comparing(Transactions::getDate)
                        .thenComparing(Transactions::getTime).reversed())
                .forEach(t -> sb.append(String.format("%s | %s | %-15s | %-15s | %.2f\n",
                        t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount())));
        area.setText(sb.toString());
    }

    // Outputs Home Screen menu
    public void homeScreen() {
        BorderPane root = createScreen("HOME");

        Button depositBtn = createButton("Deposit");
        Button paymentBtn = createButton("Payment");
        Button ledgerBtn = createButton("Ledger");
        Button exitBtn = createButton("Exit");

        depositBtn.setOnAction(e -> makeTransactionScreen("D"));
        paymentBtn.setOnAction(e -> makeTransactionScreen("P"));
        ledgerBtn.setOnAction(e -> ledgerScreen());
        exitBtn.setOnAction(e -> System.exit(0));

        VBox vbox = new VBox(15, depositBtn, paymentBtn, ledgerBtn, exitBtn);
        vbox.setAlignment(Pos.CENTER);
        root.setCenter(vbox);
        screenContainer.setCenter(root);
    }

    // Outputs screen to input transaction info
    public void makeTransactionScreen(String option) {
        BorderPane root = createScreen(option.equalsIgnoreCase("D") ? "ADD DEPOSIT" : "MAKE PAYMENT");

        Button backBtn = createButton("Back");
        backBtn.setOnAction(e -> homeScreen());
        HBox topBox = new HBox(backBtn);
        topBox.setAlignment(Pos.TOP_LEFT);
        topBox.setPadding(new Insets(10));
        root.setTop(topBox);

        TextField descField = createTextField("Description");
        TextField vendorField = createTextField("Vendor");
        TextField amountField = createTextField("Amount");

        Button submitBtn = createButton("Submit");
        VBox centerBox = new VBox(15, descField, vendorField, amountField, submitBtn);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));
        root.setCenter(centerBox);

        Label successLabel = new Label();
        successLabel.setTextFill(Color.CYAN);
        successLabel.setFont(Font.font("Sevastopol", FontWeight.BOLD, 20));
        successLabel.setVisible(false);
        HBox bottomBox = new HBox(successLabel);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));
        root.setBottom(bottomBox);

        submitBtn.setOnAction(e -> {
            try {
                LocalDate date = LocalDate.now();
                LocalTime time = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
                double amount = Double.parseDouble(amountField.getText());
                if (option.equalsIgnoreCase("P")) amount *= -1;

                Transactions t = new Transactions(date, time, descField.getText(), vendorField.getText(), amount);
                AccountingApplication.addTransaction(t);

                successLabel.setText("Transaction added.");
                successLabel.setVisible(true);

                descField.clear();
                vendorField.clear();
                amountField.clear();
            } catch (NumberFormatException ex) {
                showAlert("Invalid Amount", "Please enter a valid number for amount.");
            } catch (Exception ex) {
                showAlert("Transaction Error", "Could not add transaction: " + ex.getMessage());
            }
        });

        screenContainer.setCenter(root);
    }

    public void ledgerScreen(){
//  border pane root
//  top HBox of buttons below:
//  Home button top left -> setaction homeScreen()
//  Reports button top right -> setaction reportsScreen()
//  display text area center
//  bottom HBox of buttons below:
//  Display All button -> setaction displayLedger(text area box thing, predicate all)
//  Display Deposits button -> setaction displayLedger(text area box thing, predicate deposits)
//  Display Payments button -> setaction displayLedger(text area box thing, predicate payments)
    }

    public void reportsScreen(){
//  border pane root
//  top HBox of buttons below:
//  Ledger button top left -> setaction ledgerScreen()
//  Search by Vendor button top middle -> setaction vendorScreen()
//  Custom Search button top right -> setaction customSearchScreen()
//  display text area center
//  bottom HBox of buttons below:
//  Display Month to date button -> setaction displayLedger(text area box thing, predicate)
//  Display Previous month button -> setaction displayLedger(text area box thing, predicate)
//  Display Month to year button -> setaction displayLedger(text area box thing, predicate)
//  Display Month to year button -> setaction displayLedger(text area box thing, predicate)
//  Display Previous year button -> setaction displayLedger(text area box thing, predicate)
    }

    public void vendorScreen(){
//  border pane root
//  Reports button top left -> setaction reportsScreen()

//  display text area center

//  bottom Hbox of buttons below:
//  vendor label on bottom left, text field in bottom middle for response
//  submit button bottom right -> setaction try{ getText from field & displayLedger(text area box, predicate)
    }

    public void customSearchScreen(){
//  border pane root
//  top Hbox with stuff below:
//  Reports button left -> setaction reportsScreen()
//  "Custom Search" label middle

//  Center Vbox maybe:
//  setText("Enter only the values you want to filter by") top
//  start date label on left, text field on right for response
//  end date label on left, text field on right for response
//  description label on left, text field on right for response
//  vendor label on left, text field on right for response
//  amount label on left, text field on right for response
//  submit button -> setaction try{ getText from fields & customSearch(text area box, start, end, description,
//  vendor, amount) } catch (Exception e) {showAlert() }

//  bottom text area box
    }

}
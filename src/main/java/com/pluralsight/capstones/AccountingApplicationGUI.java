package com.pluralsight.capstones;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
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

    // Outputs ledger screen to display transactions
    public void ledgerScreen() {
        BorderPane root = createScreen("LEDGER");

        Button backBtn = createButton("Home");
        Button repBtn = createButton("Reports");

        HBox topBox = new HBox(backBtn, new Region(), repBtn);
        HBox.setHgrow(topBox.getChildren().get(1), Priority.ALWAYS);
        topBox.setPadding(new Insets(10));
        root.setTop(topBox);

        TextArea ledgerView = new TextArea();
        ledgerView.setEditable(false);
        ledgerView.getStyleClass().add("textarea");
        root.setCenter(ledgerView);

        Button allBtn = createButton("All");
        Button depBtn = createButton("Deposits");
        Button payBtn = createButton("Payments");

        HBox bottomBox = new HBox(10, allBtn, depBtn, payBtn);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));
        root.setBottom(bottomBox);

        allBtn.setOnAction(e -> displayLedger(ledgerView, AccountingApplication.getLedger()));
        depBtn.setOnAction(e -> displayLedger(ledgerView,
                AccountingApplication.filterLedger(t -> t.getAmount() > 0)));
        payBtn.setOnAction(e -> displayLedger(ledgerView,
                AccountingApplication.filterLedger(t -> t.getAmount() < 0)));

        repBtn.setOnAction(e -> reportsScreen());
        backBtn.setOnAction(e -> homeScreen());

        screenContainer.setCenter(root);
    }

    // Outputs reports screen to display filtered transactions
    public void reportsScreen() {
        BorderPane root = createScreen("REPORTS");

        Button backBtn = createButton("Back");
        Button customSearchBtn = createButton("Custom Search");

        HBox topBox = new HBox(backBtn, new Region(), customSearchBtn);
        HBox.setHgrow(topBox.getChildren().get(1), Priority.ALWAYS);
        topBox.setPadding(new Insets(10));
        root.setTop(topBox);

        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.getStyleClass().add("textarea");
        reportArea.setFont(Font.font("Monospaced", 14));
        root.setCenter(reportArea);

        Button mtdBtn = createButton("Month-to-Date");
        Button prevMonthBtn = createButton("Previous Month");
        Button ytdBtn = createButton("Year-to-Date");
        Button prevYearBtn = createButton("Previous Year");
        Button vendorBtn = createButton("Vendor Search");

        HBox bottomBox = new HBox(10, mtdBtn, prevMonthBtn, ytdBtn, prevYearBtn, vendorBtn);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));
        root.setBottom(bottomBox);

        mtdBtn.setOnAction(e -> displayLedger(reportArea, AccountingApplication.monthToDate()));
        prevMonthBtn.setOnAction(e -> displayLedger(reportArea, AccountingApplication.previousMonth()));
        ytdBtn.setOnAction(e -> displayLedger(reportArea, AccountingApplication.yearToDate()));
        prevYearBtn.setOnAction(e -> displayLedger(reportArea, AccountingApplication.previousYear()));
        vendorBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Search by Vendor");
            dialog.setHeaderText("Enter Vendor Name");
            dialog.setContentText("Vendor:");
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/retro.css").toExternalForm());
            dialog.showAndWait().ifPresent(v -> displayLedger(reportArea, AccountingApplication.filterByVendor(v)));
        });

        backBtn.setOnAction(e -> ledgerScreen());
        customSearchBtn.setOnAction(e -> customSearchScreen());

        screenContainer.setCenter(root);
    }

    // Outputs custom search screen to allow user to be more specific with what they filter from transactions
    public void customSearchScreen() {
        BorderPane root = new BorderPane();

        Button backBtn = createButton("Back");
        backBtn.setOnAction(e -> reportsScreen());
        HBox topBox = new HBox(backBtn);
        topBox.setPadding(new Insets(10));
        topBox.setAlignment(Pos.TOP_LEFT);
        root.setTop(topBox);

        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(20));
        Label instruction = new Label("Enter only the values you want to filter by.");
        instruction.setFont(Font.font("Sevastopol", FontWeight.BOLD, 18));
        instruction.setTextFill(Color.CYAN);
        centerBox.getChildren().add(instruction);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField startField = createTextField("YYYY-MM-DD");
        TextField endField = createTextField("YYYY-MM-DD");
        TextField descField = createTextField("Description");
        TextField vendorField = createTextField("Vendor");
        TextField amountField = createTextField("Amount");

        grid.addRow(0, new Label("Start Date:"), startField);
        grid.addRow(1, new Label("End Date:"), endField);
        grid.addRow(2, new Label("Description:"), descField);
        grid.addRow(3, new Label("Vendor:"), vendorField);
        grid.addRow(4, new Label("Amount:"), amountField);

        centerBox.getChildren().add(grid);

        Button submitBtn = createButton("Submit");
        TextArea ledgerTextArea = new TextArea();
        ledgerTextArea.setEditable(false);
        ledgerTextArea.getStyleClass().add("textarea");
        ledgerTextArea.setPrefHeight(200);

        submitBtn.setOnAction(e -> {
            try {
                LocalDate start = startField.getText().isEmpty() ? null : LocalDate.parse(startField.getText());
                LocalDate end = endField.getText().isEmpty() ? null : LocalDate.parse(endField.getText());
                String description = descField.getText();
                String vendor = vendorField.getText();
                Double amount = amountField.getText().isEmpty() ? null : Double.parseDouble(amountField.getText());

                ArrayList<Transactions> report = AccountingApplication.customSearch(start, end, description, vendor, amount);
                displayLedger(ledgerTextArea, report);
            } catch (Exception ex) {
                showAlert("Search Error", "Invalid input: " + ex.getMessage());
            }
        });

        VBox bottomBox = new VBox(10, submitBtn, ledgerTextArea);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20));

        root.setCenter(centerBox);
        root.setBottom(bottomBox);

        screenContainer.setCenter(root);
    }

}
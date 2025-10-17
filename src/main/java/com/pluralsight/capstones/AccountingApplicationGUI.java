package com.pluralsight.capstones;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;

public class AccountingApplicationGUI extends Application {

    private BorderPane screenContainer;

    public static void main(String[] args) {
        launch();
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

    public void homeScreen(){
//  border plan root
//  deposit button -> setaction makeTransactionScreen("d")
//  payment button -> setaction makeTransactionScreen("p")
//  ledger button -> setaction ledgerScreen()
//  exit button -> setaction system exit
    }

    public void makeTransactionScreen(String option){
//  border pane root
//  "Enter transaction info. " label on top
//  description label on left, text field on right for response
//  vendor label on left, text field on right for response
//  amount label on left, text field on right for response
//  submit button bottom right -> setaction try{ getText from fields & call makeTransaction(option, description,
//  vendor, amount) } catch (Exception e) {showAlert() }
//  if try goes through fine, setText("Transaction added") & then back to homeScreen()
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

    @Override
    public void start(Stage primaryStage) {
//  set screen container and scene to container
//  add css to scene
//  set stage to scene
//  primaryStage.show()
    }
}
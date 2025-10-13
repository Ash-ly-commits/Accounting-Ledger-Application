package com.pluralsight.capstones;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class AccountingApplication {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ArrayList<Transactions> ledger = fillLedger();

    public static void main(String[] args) {
        String homeScreenMenu = """
                \nHome
                D) Add Deposit
                P) Make Payment (Debit)
                L) Ledger
                X) Exit
                Enter command:\t""";
        char command = 'c';
        while(!(command == 'x')) {
            System.out.print(homeScreenMenu);
            command = scanner.next().charAt(0);
            switch (command) {
                case 'D':
                    makeDeposit();
                    break;
                case 'P':
                    makePayment();
                    break;
                case 'L':
                    ledgerScreen();
                    break;
                case 'X':
                    System.out.println("Exiting...");
                    break;
            }
        }
    }

    public static ArrayList<Transactions> fillLedger(){
        ArrayList<Transactions> ledger = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader("transactions.csv"))){
            String input;
            while ((input = reader.readLine()) != null) {
                String[] tokens = input.split("\\|");
                LocalDate localDate = LocalDate.parse(tokens[0]);
                LocalTime localTime = LocalTime.parse(tokens[1]);
                String description = tokens[2];
                String vendor = tokens[3];
                float amount = Float.parseFloat(tokens[4]);
                ledger.add(new Transactions(localDate, localTime, description, vendor, amount));
            }
        } catch (IOException e) {
            System.out.println("failure opening transactions");
        }
        return ledger;
    }
}

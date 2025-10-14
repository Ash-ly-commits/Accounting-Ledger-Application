package com.pluralsight.capstones;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
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

    public static void makeDeposit() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        System.out.print("Enter description for deposit: ");
        String description = scanner.nextLine();
        System.out.print("Enter vendor name: ");
        String vendor = scanner.nextLine();
        System.out.print("Enter amount: ");
        float amount = scanner.nextFloat();
        ledger.add(new Transactions(date, time, description, vendor, amount));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.csv", true))) {
            writer.write(date + "|" + time + "|" + description + "|" + vendor + "|" + amount);
        } catch (IOException e) {
            System.out.println("failure writing to transactions");
        }
    }

    public static void makePayment(){
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        System.out.print("Enter description for payment: ");
        String description = scanner.nextLine();
        System.out.print("Enter vendor name: ");
        String vendor = scanner.nextLine();
        System.out.print("Enter amount: ");
        float amount = -scanner.nextFloat();
        ledger.add(new Transactions(date, time, description, vendor, amount));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.csv", true))) {
            writer.write(date + "|" + time + "|" + description + "|" + vendor + "|" + amount);
        } catch (IOException e) {
            System.out.println("failure writing to transactions");
        }
    }

    public static void ledgerScreen(){
        String ledgerScreenMenu = """
                \nLedger Display
                A) All
                D) Deposits
                P) Payments
                R) Reports
                H) Home
                Enter command:\t""";
        char command = 'c';
        while(!(command == 'H')) {
            System.out.print(ledgerScreenMenu);
            command = scanner.next().charAt(0);
            switch (command) {
                case 'A':
                    ledger.sort(Comparator.comparing(Transactions::getDate).thenComparing(Transactions::getTime));
                    for (Transactions t : ledger) {
                        System.out.print(t.getDate() + "|" + t.getTime()+ "|" + t.getDescription() + "|"
                                + t.getVendor() + "|" + t.getAmount());
                    }
                    break;
                case 'D':
                    ledger.sort(Comparator.comparing(Transactions::getDate).thenComparing(Transactions::getTime));
                    for (Transactions t : ledger) {
                        if(t.getAmount() > 0){
                            System.out.print(t.getDate() + "|" + t.getTime()+ "|" + t.getDescription() + "|"
                                    + t.getVendor() + "|" + t.getAmount());
                        }
                    }
                    break;
                case 'P':
                    ledger.sort(Comparator.comparing(Transactions::getDate).thenComparing(Transactions::getTime));
                    for (Transactions t : ledger) {
                        if(t.getAmount() < 0){
                            System.out.print(t.getDate() + "|" + t.getTime()+ "|" + t.getDescription() + "|"
                                    + t.getVendor() + "|" + t.getAmount());
                        }
                    }
                    break;
                case 'R':
                    reports();
                    break;
                case 'H':
                    System.out.println("Back to Home...");
                    break;
            }
        }
    }
}

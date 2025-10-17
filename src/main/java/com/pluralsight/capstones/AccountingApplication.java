package com.pluralsight.capstones;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AccountingApplication {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ArrayList<Transactions> ledger = fillLedger();

    // Displays home screen menu
    public static void main(String[] args) {
        sortLedger();
        String homeScreenMenu = """
                \nHome
                D) Add Deposit
                P) Make Payment (Debit)
                L) Ledger
                X) Exit
                Enter option:\t""";
        String option;
        while (true) {
            option = screenMenuValidation(homeScreenMenu, "dplx");
            if (option.equalsIgnoreCase("x")) {
                System.out.println("Exiting...");
                break;
            }
            switch (option) {
                case "d", "p" -> makeTransaction(option);
                case "l" -> ledgerScreen();
            }
        }
    }

    // Fills ledger with values from the file
    public static ArrayList<Transactions> fillLedger() {
        ArrayList<Transactions> ledger = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("transactions.csv"))) {
            String input = reader.readLine(); // Eats the header
            while ((input = reader.readLine()) != null) {
                String[] tokens = input.split("\\|");
                LocalDate localDate = LocalDate.parse(tokens[0]);
                LocalTime localTime = LocalTime.parse(tokens[1]);
                String description = tokens[2];
                String vendor = tokens[3];
                double amount = Double.parseDouble(tokens[4]);
                ledger.add(new Transactions(localDate, localTime, description, vendor, amount));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return ledger;
    }

    // Sorts the transactions by date and time with newest entries first
    public static void sortLedger() {
        ledger.sort(Comparator.comparing(Transactions::getDate).thenComparing(Transactions::getTime).reversed());
    }

    // Displays every object in the ledger
    public static void displayLedger(Predicate<Transactions> condition) {
        ledger.stream()
                .filter(condition)
                .forEach(t -> System.out.printf("%s|%s|%s|%s|%.2f%n",
                        t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount()));
    }

    // Displays screen menu and validates choice input
    public static String screenMenuValidation(String prompt, String validOptions) {
        while (true) {
            String input = askUserStr(prompt);
            if (input.isEmpty()) continue;
            String choice = String.valueOf(input.charAt(0));
            if (validOptions.contains(choice)) return choice;
            System.out.println("Invalid option, try again.");
        }
    }

    // Method to output and input strings for user
    public static String askUserStr(String prompt) {
        try {
            System.out.print(prompt);
            return scanner.nextLine().trim();
        } catch (Exception e) {
            System.out.println("Error with scanner.");
            return " ";
        }
    }

    // Method to make deposits or payments and write them to the file
    public static void makeTransaction(String option) {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
        String description = "";
        while (description.isEmpty()) {
            description = askUserStr("Enter description for transaction: ");
            if (description.isEmpty()) System.out.println("Description cannot be blank.");
        }
        String vendor = "";
        while (vendor.isEmpty()) {
            vendor = askUserStr("Enter vendor name: ");
            if (vendor.isEmpty()) System.out.println("Vendor cannot be blank.");
        }
        double amount;
        while (true) {
            String amtStr = askUserStr("Enter amount: ");
            try {
                amount = Double.parseDouble(amtStr);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a valid number.");
            }
        }
        if (option.equalsIgnoreCase("p")) amount = -amount;
        ledger.add(new Transactions(date, time, description, vendor, amount));
        sortLedger();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.csv", true))) {
            writer.newLine();
            writer.write(String.format("%s|%s|%s|%s|%.2f", date, time, description, vendor, amount));
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    // Method to display ledger screen
    public static void ledgerScreen() {
        String ledgerScreenMenu = """
                \nLedger Display
                A) All
                D) Deposits
                P) Payments
                R) Reports
                H) Home
                Enter option:\t""";
        while (true) {
            String option = screenMenuValidation(ledgerScreenMenu, "adprh");
            if (option.equalsIgnoreCase("h")) {
                System.out.println("Back to Home...");
                return;
            }
            switch (option) {
                case "a" -> displayLedger(t -> true);
                case "d" -> displayLedger(t -> t.getAmount() > 0);
                case "p" -> displayLedger(t -> t.getAmount() < 0);
                case "r" -> reportsScreen();
            }
        }
    }

    // Method to display reports screen
    public static void reportsScreen() {
        String reportsScreenMenu = """
                \nReports Display
                1) Month To Date
                2) Previous Month
                3) Year To Date
                4) Previous Year
                5) Search by Vendor
                6) Custom Search
                0) Back
                Enter option:\t""";
        while (true) {
            LocalDate current = LocalDate.now();
            String option = screenMenuValidation(reportsScreenMenu, "0123456");
            if (option.equalsIgnoreCase("0")) {
                System.out.println("Back to Ledger...");
                return;
            } else if (option.equalsIgnoreCase("5")) {
                String vendor = askUserStr("Enter the vendor name: ");
                displayLedger(t -> vendor.equalsIgnoreCase(t.getVendor()));
            }
            switch (option) {
                case "1" -> displayLedger(t -> (t.getDate().getMonthValue() == current.getMonthValue()) &&
                        (t.getDate().getYear() == current.getYear()));
                case "2" -> displayLedger(t -> (t.getDate().getMonthValue() == (current.getMonthValue() - 1)) &&
                        (t.getDate().getYear() == current.getYear()));
                case "3" -> displayLedger(t -> t.getDate().getYear() == current.getYear());
                case "4" -> displayLedger(t -> t.getDate().getYear() == (current.getYear() - 1));
                case "6" -> customSearch();
            }
        }
    }

    // Method to customize search and display it
    public static void customSearch() {
        System.out.println("\nCustom Search\nEnter only the values you want to filter by ->");
        LocalDate start = null;
        LocalDate end = null;
        String description = askUserStr("Description: ");
        String vendor = askUserStr("Vendor: ");
        // Validates start date
        while (true) {
            String startDateStr = askUserStr("Start Date (YYYY-MM-DD): ");
            if (startDateStr.isEmpty()) break; // blank = skip filter
            try {
                start = LocalDate.parse(startDateStr);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid start date format. Please use YYYY-MM-DD or leave blank to skip.");
            }
        }
        // Validates end date
        while (true) {
            String endDateStr = askUserStr("End Date (YYYY-MM-DD): ");
            if (endDateStr.isEmpty()) break;
            try {
                end = LocalDate.parse(endDateStr);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid end date format. Please use YYYY-MM-DD or leave blank to skip.");
            }
        }
        // Validates amount
        Double amount = null;
        while (true) {
            String amountStr = askUserStr("Amount: ");
            if (amountStr.isEmpty()) break;
            try {
                amount = Double.parseDouble(amountStr);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a number or leave blank to skip.");
            }
        }
        // Creates stream of Transaction objects that is filtered to user specifications
        Double finalAmount = amount;
        LocalDate finalStart = start;
        LocalDate finalEnd = end;
        ArrayList<Transactions> report = ledger.stream()
                .filter(e -> (finalStart == null || !e.getDate().isBefore(finalStart)))
                .filter(e -> (finalEnd == null || !e.getDate().isAfter(finalEnd)))
                .filter(e -> (description.isEmpty() || e.getDescription().contains(description)))
                .filter(e -> (vendor.isEmpty() || e.getVendor().contains(vendor)))
                .filter(e -> (finalAmount == null || e.getAmount() == finalAmount))
                .collect(Collectors.toCollection(ArrayList::new));
        // Displays stream
        for (Transactions t : report) {
            System.out.printf("%s|%s|%s|%s|%.2f\n", t.getDate().toString(), t.getTime().toString(),
                    t.getDescription(), t.getVendor(), t.getAmount());
        }
    }
}
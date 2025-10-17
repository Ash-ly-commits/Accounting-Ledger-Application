package com.pluralsight.capstones;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AccountingApplication {
    private static final ArrayList<Transactions> ledger = fillLedger();

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

    // Returns filtered ledger based on predicate
    public static ArrayList<Transactions> filterLedger(Predicate<Transactions> condition) {
        return ledger.stream().filter(condition).collect(Collectors.toCollection(ArrayList::new));
    }

    private static void writeTransactionToFile(Transactions t) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.csv", true))) {
            writer.newLine();
            writer.write(String.format("%s|%s|%s|%s|%.2f",
                    t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount()));
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public static void addTransaction(Transactions t) {
        ledger.add(t);
        sortLedger();
        writeTransactionToFile(t);
    }

    public static ArrayList<Transactions> getLedger() {
        return ledger;
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
    public static void customSearch(java.time.LocalDate start, java.time.LocalDate end,
                                    String description, String vendor, Double amount) {
        ArrayList<Transactions> report = ledger.stream()
                .filter(e -> (finalStart == null || !e.getDate().isBefore(finalStart)))
                .filter(e -> (finalEnd == null || !e.getDate().isAfter(finalEnd)))
                .filter(e -> (description.isEmpty() || e.getDescription().contains(description)))
                .filter(e -> (vendor.isEmpty() || e.getVendor().contains(vendor)))
                .filter(e -> (finalAmount == null || e.getAmount() == finalAmount))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
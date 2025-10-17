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

    // Predefined report filters
    public static ArrayList<Transactions> monthToDate() {
        int month = java.time.LocalDate.now().getMonthValue();
        int year = java.time.LocalDate.now().getYear();
        return filterLedger(t -> t.getDate().getMonthValue() == month && t.getDate().getYear() == year);
    }

    public static ArrayList<Transactions> previousMonth() {
        java.time.LocalDate now = java.time.LocalDate.now();
        int month = now.getMonthValue() == 1 ? 12 : now.getMonthValue() - 1;
        int year = now.getMonthValue() == 1 ? now.getYear() - 1 : now.getYear();
        return filterLedger(t -> t.getDate().getMonthValue() == month && t.getDate().getYear() == year);
    }

    public static ArrayList<Transactions> yearToDate() {
        int year = java.time.LocalDate.now().getYear();
        return filterLedger(t -> t.getDate().getYear() == year);
    }

    public static ArrayList<Transactions> previousYear() {
        int year = java.time.LocalDate.now().getYear() - 1;
        return filterLedger(t -> t.getDate().getYear() == year);
    }

    public static ArrayList<Transactions> filterByVendor(String vendor) {
        return filterLedger(t -> t.getVendor().equalsIgnoreCase(vendor));
    }

    // Method to customize search and display it
    public static ArrayList<Transactions> customSearch(
            java.time.LocalDate start, java.time.LocalDate end,
            String description, String vendor, Double amount) {
        return ledger.stream()
                .filter(t -> start == null || !t.getDate().isBefore(start))
                .filter(t -> end == null || !t.getDate().isAfter(end))
                .filter(t -> description == null || description.isEmpty() || t.getDescription().contains(description))
                .filter(t -> vendor == null || vendor.isEmpty() || t.getVendor().contains(vendor))
                .filter(t -> amount == null || t.getAmount() == amount)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
package com.pluralsight.capstones;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AccountingApplication {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ArrayList<Transactions> ledger = fillLedger();

    public static void main(String[] args) {
        sortLedger();
        String homeScreenMenu = """
                \nHome
                D) Add Deposit
                P) Make Payment (Debit)
                L) Ledger
                X) Exit
                Enter option:\t""";
        char command = 'c';
        while(!(command == 'x')) {
            System.out.print(homeScreenMenu);
            command = scanner.next().charAt(0);
            command = Character.toUpperCase(command);
            scanner.nextLine();
            switch (command) {
                case 'D':
                case 'P':
                    makeTransaction(command);
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
            String input = reader.readLine(); // Eats the header
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
            System.err.println("Error reading file: " + e.getMessage());
        }
        return ledger;
    }

    // Sorts the transactions by date and time with newest entries first
    public static void sortLedger() {
        ledger.sort(Comparator.comparing(Transactions::getDate).thenComparing(Transactions::getTime).reversed());
    }

    public static String askUserStr(String prompt){
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public static void displayLedger(Predicate<Transactions> condition) {
        for (Transactions t : ledger) {
            if (condition.test(t)) {
                System.out.printf("%s|%s|%s|%s|%.2f\n",
                        t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
            }
        }
    }

    public static void makeTransaction(char command){
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
        String description = askUserStr("Enter description for transaction: ");
        String vendor = askUserStr("Enter vendor name: ");
        System.out.print("Enter amount: ");
        float amount = (command == 'P') ? -scanner.nextFloat() : scanner.nextFloat();
        scanner.nextLine();
        ledger.add(new Transactions(date, time, description, vendor, amount));
        sortLedger();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.csv", true))) {
            writer.write(System.lineSeparator());
            writer.write(String.format("%s|%s|%s|%s|%.2f", date, time.toString(), description, vendor, amount));
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
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
                Enter option:\t""";
        char command = 'c';
        while(!(command == 'H')) {
            System.out.print(ledgerScreenMenu);
            command = scanner.next().charAt(0);
            command = Character.toUpperCase(command);
            scanner.nextLine();
            switch (command) {
                case 'A':
                    displayLedger(t -> true);
                    break;
                case 'D':
                    displayLedger(t -> t.getAmount() > 0);
                    break;
                case 'P':
                    displayLedger(t -> t.getAmount() < 0);
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

    public static void reports(){
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
        int command = 6;
        while(!(command == 0)) {
            System.out.print(reportsScreenMenu);
            command = scanner.nextInt();
            scanner.nextLine();
            LocalDate current = LocalDate.now();
            switch (command) {
                case 1:
                    displayLedger(t -> (t.getDate().getMonthValue() == current.getMonthValue()) &&
                            (t.getDate().getYear() == current.getYear()));
                    break;
                case 2:
                    displayLedger(t -> (t.getDate().getMonthValue() == (current.getMonthValue()-1)) &&
                            (t.getDate().getYear() == current.getYear()));
                    break;
                case 3:
                    displayLedger(t -> t.getDate().getYear() == current.getYear());
                    break;
                case 4:
                    displayLedger(t -> t.getDate().getYear() == (current.getYear()-1));
                    break;
                case 5:
                    String vendor = askUserStr("Enter the vendor name: ");
                    displayLedger(t -> vendor.equalsIgnoreCase(t.getVendor()));
                    break;
                case 6:
                    customSearch();
                    break;
                case 0:
                    System.out.println("Back to Ledger...");
                    break;
            }
        }
    }

    public static void customSearch(){
        System.out.println("\nCustom Search\nEnter only the values you want to filter by ->");
        String startDate = askUserStr("Start Date (YYYY-MM-DD): ");
        String endDate = askUserStr("End Date (YYYY-MM-DD): ");
        String description = askUserStr("Description: ");
        String vendor = askUserStr("Vendor: ");
        String amountStr = askUserStr("Amount: ");

        Float amount = amountStr.isEmpty() ? null : Float.parseFloat(amountStr);
        LocalDate start = startDate.isEmpty() ? null : LocalDate.parse(startDate);
        LocalDate end = endDate.isEmpty() ? null : LocalDate.parse(endDate);

        // Creates stream of Transaction objects that is filtered to user specifications
        ArrayList<Transactions> report = ledger.stream()
                .filter(e -> (start == null || !e.getDate().isBefore(start)))
                .filter(e -> (end == null || !e.getDate().isAfter(end)))
                .filter(e -> (description.isEmpty() || e.getDescription().contains(description)))
                .filter(e -> (vendor.isEmpty() || e.getVendor().contains(vendor)))
                .filter(e -> (amount == null || e.getAmount() == amount))
                .collect(Collectors.toCollection(ArrayList::new));

        for (Transactions t : report) {
            System.out.printf("%s|%s|%s|%s|%.2f\n",t.getDate().toString(),t.getTime().toString(),
                    t.getDescription(),t.getVendor(),t.getAmount());
        }
    }
}
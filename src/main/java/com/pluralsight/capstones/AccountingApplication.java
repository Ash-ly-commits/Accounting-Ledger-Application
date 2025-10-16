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
            option = screenMenu(homeScreenMenu, "dplx");
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

    // Displays menu screen and validates choice input
    public static String screenMenu(String prompt, String validOptions) {
        while (true) {
            String input = askUserStr(prompt);
            if (input.isEmpty()) continue;
            String shortenedInput = input.toLowerCase().substring(0,1);
            if (validOptions.contains(shortenedInput)) return shortenedInput;
            System.out.println("Invalid option, try again.");
        }
    }

    public static String askUserStr(String prompt){
        try{
            System.out.print(prompt);
            return scanner.nextLine().trim();
        } catch (Exception e) {
            System.out.println("Error with scanner.");
            return " ";
        }
    }

    public static void displayLedger(Predicate<Transactions> condition) {
        for (Transactions t : ledger) {
            if (condition.test(t)) {
                System.out.printf("%s|%s|%s|%s|%.2f\n",
                        t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
            }
        }
    }

    public static void makeTransaction(String option) {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
        double amount = 0;
        String description;
        do {
            description = askUserStr("Enter description for transaction: ");
            if (description.isEmpty()) System.out.println("Description cannot be blank.");
        } while (description.isEmpty());
        String vendor;
        do {
            vendor = askUserStr("Enter vendor name: ");
            if (vendor.isEmpty()) System.out.println("Vendor cannot be blank.");
        } while (vendor.isEmpty());
        boolean validAmount = false;
        while (!validAmount) {
            String amtStr = askUserStr("Enter amount: ");
            try {
                amount = Double.parseDouble(amtStr);
                validAmount = true;
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

    public static void ledgerScreen(){
        String ledgerScreenMenu = """
                \nLedger Display
                A) All
                D) Deposits
                P) Payments
                R) Reports
                H) Home
                Enter option:\t""";
        while (true) {
            String option = screenMenu(ledgerScreenMenu, "adprh");
            if (option.equalsIgnoreCase("h")) {
                System.out.println("Back to Home...");
                return;
            }
            switch (option) {
                case "a" -> displayLedger(t -> true);
                case "d" -> displayLedger(t -> t.getAmount() > 0);
                case "p" -> displayLedger(t -> t.getAmount() < 0);
                case "r" -> reports();
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
        while (true) {
            LocalDate current = LocalDate.now();
            String option = screenMenu(reportsScreenMenu, "0123456");
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
                case "2" -> displayLedger(t -> (t.getDate().getMonthValue() == (current.getMonthValue()-1)) &&
                        (t.getDate().getYear() == current.getYear()));
                case "3" -> displayLedger(t -> t.getDate().getYear() == current.getYear());
                case "4" -> displayLedger(t -> t.getDate().getYear() == (current.getYear()-1));
                case "6" -> customSearch();
            }
        }
    }

    public static void customSearch(){
        System.out.println("\nCustom Search\nEnter only the values you want to filter by ->");
        LocalDate start = null;
        LocalDate end = null;
        String description = askUserStr("Description: ");
        String vendor = askUserStr("Vendor: ");
        // Start Date Validation
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
        // End Date Validation
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
        // Amount validation
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

        for (Transactions t : report) {
            System.out.printf("%s|%s|%s|%s|%.2f\n",t.getDate().toString(),t.getTime().toString(),
                    t.getDescription(),t.getVendor(),t.getAmount());
        }
    }
}
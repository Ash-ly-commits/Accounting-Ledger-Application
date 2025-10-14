package com.pluralsight.capstones;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.stream.Collectors;

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
            System.out.println("failure opening transactions");
        }
        return ledger;
    }

    public static void makeTransaction(char command){
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
        System.out.print("Enter description for transaction: ");
        String description = scanner.nextLine();
        System.out.print("Enter vendor name: ");
        String vendor = scanner.nextLine();
        System.out.print("Enter amount: ");
        float amount = scanner.nextFloat();
        if (command == 'P') {
            amount = -amount;
        }
        scanner.nextLine();
        ledger.add(new Transactions(date, time, description, vendor, amount));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.csv", true))) {
            writer.write(String.format("\n%s|%s|%s|%s|%.2f", date, time.toString(), description, vendor, amount));
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
            scanner.nextLine();
            switch (command) {
                case 'A':
                    ledger.sort(Comparator.comparing(Transactions::getDate).thenComparing(Transactions::getTime));
                    for (Transactions t : ledger) {
                        System.out.printf("%s|%s|%s|%s|%.2f\n",t.getDate().toString(),t.getTime().toString(),
                                t.getDescription(),t.getVendor(),t.getAmount());
                    }
                    break;
                case 'D':
                    ledger.sort(Comparator.comparing(Transactions::getDate).thenComparing(Transactions::getTime));
                    for (Transactions t : ledger) {
                        if(t.getAmount() > 0){
                            System.out.printf("%s|%s|%s|%s|%.2f\n",t.getDate().toString(),t.getTime().toString(),
                                    t.getDescription(),t.getVendor(),t.getAmount());
                        }
                    }
                    break;
                case 'P':
                    ledger.sort(Comparator.comparing(Transactions::getDate).thenComparing(Transactions::getTime));
                    for (Transactions t : ledger) {
                        if(t.getAmount() < 0){
                            System.out.printf("%s|%s|%s|%s|%.2f\n",t.getDate().toString(),t.getTime().toString(),
                                    t.getDescription(),t.getVendor(),t.getAmount());
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

    public static void reports(){
        String reportsScreenMenu = """
                \nReports Display
                1) Month To Date
                2) Previous Month
                3) Year To Date
                4) Previous Year
                5) Search by Vendor
                0) Back
                Enter command:\t""";
        int command = 6;
        while(!(command == 0)) {
            System.out.print(reportsScreenMenu);
            command = scanner.nextInt();
            scanner.nextLine();
            LocalDate current = LocalDate.now();
            switch (command) {
                case 1:
                    ledger.sort(Comparator.comparing(Transactions::getDate).thenComparing(Transactions::getTime));
                    for (Transactions t : ledger) {
                        if((t.getDate().getMonthValue() == current.getMonthValue()) && (t.getDate().getYear() == current.getYear())){
                            System.out.printf("%s|%s|%s|%s|%.2f\n",t.getDate().toString(),t.getTime().toString(),
                                    t.getDescription(),t.getVendor(),t.getAmount());
                        }
                    }
                    break;
                case 2:
                    ledger.sort(Comparator.comparing(Transactions::getDate).thenComparing(Transactions::getTime));
                    for (Transactions t : ledger) {
                        if((t.getDate().getMonthValue() == (current.getMonthValue()-1)) && (t.getDate().getYear() == current.getYear())){
                            System.out.printf("%s|%s|%s|%s|%.2f\n",t.getDate().toString(),t.getTime().toString(),
                                    t.getDescription(),t.getVendor(),t.getAmount());
                        }
                    }
                    break;
                case 3:
                    ledger.sort(Comparator.comparing(Transactions::getDate).thenComparing(Transactions::getTime));
                    for (Transactions t : ledger) {
                        if(t.getDate().getYear() == current.getYear()){
                            System.out.printf("%s|%s|%s|%s|%.2f\n",t.getDate().toString(),t.getTime().toString(),
                                    t.getDescription(),t.getVendor(),t.getAmount());
                        }
                    }
                    break;
                case 4:
                    ledger.sort(Comparator.comparing(Transactions::getDate).thenComparing(Transactions::getTime));
                    for (Transactions t : ledger) {
                        if(t.getDate().getYear() == (current.getYear()-1)){
                            System.out.printf("%s|%s|%s|%s|%.2f\n",t.getDate().toString(),t.getTime().toString(),
                                    t.getDescription(),t.getVendor(),t.getAmount());
                        }
                    }
                    break;
                case 5:
                    System.out.print("Enter the vendor name: ");
                    String vendor = scanner.nextLine();
                    ledger.sort(Comparator.comparing(Transactions::getDate).thenComparing(Transactions::getTime));
                    for (Transactions t : ledger) {
                        if(vendor.equalsIgnoreCase(t.getVendor())){
                            System.out.printf("%s|%s|%s|%s|%.2f\n",t.getDate().toString(),t.getTime().toString(),
                                    t.getDescription(),t.getVendor(),t.getAmount());
                        }
                    }
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
        System.out.print("Start Date (YYYY-MM-DD): ");
        String startDate = scanner.nextLine();
        System.out.print("End Date (YYYY-MM-DD): ");
        String endDate = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Vendor: ");
        String vendor = scanner.nextLine();
        System.out.print("Amount: ");
        String amountStr = scanner.nextLine();

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
                .sorted(Comparator.comparing(Transactions::getDate)
                        .thenComparing(Transactions::getTime))
                .collect(Collectors.toCollection(ArrayList::new));

        for (Transactions t : report) {
            System.out.printf("%s|%s|%s|%s|%.2f\n",t.getDate().toString(),t.getTime().toString(),
                    t.getDescription(),t.getVendor(),t.getAmount());
        }
    }
}

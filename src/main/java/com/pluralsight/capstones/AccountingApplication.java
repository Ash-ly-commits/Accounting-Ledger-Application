package com.pluralsight.capstones;
import java.util.Scanner;

public class AccountingApplication {
    private static final Scanner scanner = new Scanner(System.in);

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

}

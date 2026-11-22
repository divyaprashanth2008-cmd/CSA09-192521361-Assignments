import java.io.*;
import java.util.Scanner;

public class ATMSimulation {
    static class InsufficientFundsException extends Exception {
        InsufficientFundsException(String message) { super(message); }
    }
    static void withdraw(String account, int amount, int balance)
            throws InsufficientFundsException, FileNotFoundException {
        if (account == null || account.isBlank()) throw new IllegalArgumentException("Account is required");
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        try (FileInputStream card = new FileInputStream("card-reader.dat")) {
            if (amount > balance) throw new InsufficientFundsException("Insufficient balance");
            System.out.println("Cash dispensed: " + amount);
        } catch (IOException e) {
            System.err.println("Card reader/storage failure: " + e.getMessage());
            throw new UncheckedIOException(e);
        } finally {
            System.out.println("ATM session cleanup completed.");
        }
    }
    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            System.out.print("Account number: "); String account=in.nextLine();
            System.out.print("Withdrawal amount: ");
            int amount=Integer.parseInt(in.nextLine().trim());
            withdraw(account,amount,5000);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: enter a whole number.");
        } catch (InsufficientFundsException e) {
            System.out.println("Transaction declined: " + e.getMessage());
        } catch (UncheckedIOException | IllegalArgumentException e) {
            System.out.println("ATM error: " + e.getMessage());
        } finally {
            System.out.println("Thank you for using the ATM.");
        }
    }
}

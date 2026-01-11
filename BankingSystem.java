import java.util.*;
import java.time.*;

/* ================= TRANSACTION CLASS ================= */
class Transaction {
    String type;
    double amount;
    double balance;
    LocalDateTime date;

    Transaction(String type, double amount, double balance) {
        this.type = type;
        this.amount = amount;
        this.balance = balance;
        this.date = LocalDateTime.now();
    }

    public String toString() {
        return date + " | " + type + " | ₹" + amount + " | Balance: ₹" + balance;
    }
}

/* ================= ACCOUNT CLASS ================= */
class Account {
    int accountNo;
    String name;
    int pin;
    double balance;
    boolean active = true;
    List<Transaction> history = new ArrayList<>();

    Account(int accountNo, String name, int pin, double balance) {
        this.accountNo = accountNo;
        this.name = name;
        this.pin = pin;
        this.balance = balance;
        history.add(new Transaction("Account Opened", balance, balance));
    }

    boolean verifyPin(int p) { return pin == p; }

    void deposit(double amt) {
        balance += amt;
        history.add(new Transaction("Deposit", amt, balance));
        System.out.println("Deposit successful!");
    }

    boolean withdraw(double amt) {
        if (amt > balance) return false;
        balance -= amt;
        history.add(new Transaction("Withdraw", amt, balance));
        return true;
    }

    void addTransaction(String type, double amt) {
        history.add(new Transaction(type, amt, balance));
    }

    void showHistory() {
        history.forEach(System.out::println);
    }

    void display() {
        System.out.println("Account No : " + accountNo);
        System.out.println("Name       : " + name);
        System.out.println("Balance    : ₹" + balance);
        System.out.println("Status     : " + (active ? "ACTIVE" : "BLOCKED"));
    }
}

/* ================= MAIN BANKING SYSTEM ================= */
public class BankingSystem {

    static Scanner sc = new Scanner(System.in);
    static Map<Integer, Account> accounts = new HashMap<>();
    static int accNo = 5001;

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== BANKING SYSTEM =====");
            System.out.println("1. Create Account");
            System.out.println("2. Customer Login");
            System.out.println("3. Admin Login");
            System.out.println("4. Exit");
            System.out.print("Choice: ");

            switch (sc.nextInt()) {
                case 1 -> createAccount();
                case 2 -> customerLogin();
                case 3 -> adminMenu();
                case 4 -> System.exit(0);
                default -> System.out.println("Invalid option!");
            }
        }
    }

    /* ================= ACCOUNT CREATION ================= */
    static void createAccount() {
        sc.nextLine();
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Set 4-digit PIN: ");
        int pin = sc.nextInt();
        System.out.print("Initial Deposit: ");
        double bal = sc.nextDouble();

        Account acc = new Account(accNo, name, pin, bal);
        accounts.put(accNo, acc);

        System.out.println("Account created!");
        System.out.println("Your Account Number: " + accNo++);
    }

    /* ================= CUSTOMER LOGIN ================= */
    static void customerLogin() {
        System.out.print("Account No: ");
        int no = sc.nextInt();
        System.out.print("PIN: ");
        int pin = sc.nextInt();

        Account acc = accounts.get(no);
        if (acc == null || !acc.active || !acc.verifyPin(pin)) {
            System.out.println("Invalid login or account blocked!");
            return;
        }
        customerMenu(acc);
    }

    /* ================= CUSTOMER MENU ================= */
    static void customerMenu(Account acc) {
        while (true) {
            System.out.println("\n--- CUSTOMER MENU ---");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Balance Enquiry");
            System.out.println("4. Transfer Money");
            System.out.println("5. Transaction History");
            System.out.println("6. Interest Calculation");
            System.out.println("7. Change PIN");
            System.out.println("8. Logout");
            System.out.print("Choice: ");

            switch (sc.nextInt()) {
                case 1 -> {
                    System.out.print("Amount: ");
                    acc.deposit(sc.nextDouble());
                }
                case 2 -> {
                    System.out.print("Amount: ");
                    if (!acc.withdraw(sc.nextDouble()))
                        System.out.println("Insufficient balance!");
                }
                case 3 -> System.out.println("Balance: ₹" + acc.balance);
                case 4 -> transfer(acc);
                case 5 -> acc.showHistory();
                case 6 -> {
                    double interest = acc.balance * 0.04;
                    System.out.println("Annual Interest: ₹" + interest);
                }
                case 7 -> {
                    System.out.print("New PIN: ");
                    acc.pin = sc.nextInt();
                }
                case 8 -> { return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    /* ================= TRANSFER ================= */
    static void transfer(Account sender) {
        System.out.print("To Account No: ");
        int to = sc.nextInt();
        System.out.print("Amount: ");
        double amt = sc.nextDouble();

        Account rec = accounts.get(to);
        if (rec == null || !rec.active) {
            System.out.println("Invalid receiver!");
            return;
        }

        if (sender.withdraw(amt)) {
            rec.deposit(amt);
            sender.addTransaction("Transfer to " + to, amt);
            rec.addTransaction("Received from " + sender.accountNo, amt);
            System.out.println("Transfer Successful!");
        } else {
            System.out.println("Insufficient balance!");
        }
    }

    /* ================= ADMIN MENU ================= */
    static void adminMenu() {
        System.out.print("Admin Password: ");
        if (!sc.next().equals("admin123")) {
            System.out.println("Access Denied!");
            return;
        }

        while (true) {
            System.out.println("\n--- ADMIN PANEL ---");
            System.out.println("1. View All Accounts");
            System.out.println("2. Search Account");
            System.out.println("3. Block Account");
            System.out.println("4. Unblock Account");
            System.out.println("5. Delete Account");
            System.out.println("6. Exit Admin");
            System.out.print("Choice: ");

            switch (sc.nextInt()) {
                case 1 -> accounts.values().forEach(Account::display);
                case 2 -> {
                    System.out.print("Account No: ");
                    Account a = accounts.get(sc.nextInt());
                    if (a != null) a.display();
                    else System.out.println("Not found!");
                }
                case 3 -> {
                    System.out.print("Account No: ");
                    accounts.get(sc.nextInt()).active = false;
                }
                case 4 -> {
                    System.out.print("Account No: ");
                    accounts.get(sc.nextInt()).active = true;
                }
                case 5 -> {
                    System.out.print("Account No: ");
                    accounts.remove(sc.nextInt());
                    System.out.println("Deleted!");
                }
                case 6 -> { return; }
            }
        }
    }
}

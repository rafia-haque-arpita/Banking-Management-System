public class User {
    private String username;
    private String password;
    private String email;
    private double balance;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.balance = 1000.00; // Starting balance
    }

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public String getEmail() {
        return email;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public boolean withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }
}

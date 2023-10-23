package SumoAutAv;

public class BankAccount extends Thread {

    private double balance;
    private String user;
    private String password;
    private boolean balanceShouldUpdate = false;

    public BankAccount(double balance, String user, String password) {
        this.balance = balance;
        this.user = user;
        this.password = password;
        System.out.println("Account created. User: " + this.user + " with $" + this.balance);
    }

    public double getBalance() { // retorna o saldo da conta
        return balance;
    }

    public void addBalance(double value) { // adiciona saldo à conta
        this.balance += value;
        balanceShouldUpdate = true;
    }

    public void removeBalance(double value) { // remove saldo da conta
        this.balance -= value;
    }

    @Override
    public void run() {
        while (true) { // loop infinito
            if (balanceShouldUpdate) { // se o saldo da conta foi atualizado
                System.out.println("Account " + this.user + " balance changed to $" + this.balance); // avisa que o
                                                                                                     // saldo da conta
                                                                                                     // mudou
                balanceShouldUpdate = false; // avisa que o saldo da conta foi atualizado
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public String getUser() {
        return user;
    }

}

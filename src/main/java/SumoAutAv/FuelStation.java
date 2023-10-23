package SumoAutAv;

import java.util.concurrent.Semaphore;

public class FuelStation extends Thread {
    private BankAccount account;
    private double fuelPrice = 4.87;
    Semaphore semaphore = new Semaphore(2);

    public FuelStation(BankAccount account) {
        this.account = account;
    }

    @Override
    public void run() {
        while (true) {

        }
    }

    public void askForFuel(Driver driver) { // Motorista pede para abastecer nesse metodo
        boolean isWaiting = true;
        while (isWaiting) { // loop enquanto nao tem acesso a uma bomba
            try {
                semaphore.acquire(); // pega uma bomba
                System.out.println("Driver " + driver.getDriversName() + " is refueling");
                Thread.sleep(5000); // para dois minutos mudar para 120000
                driver.getCar().refuelTank(howMuchFuel(driver)); // abastece o carro do motorista com a quantidade de
                                                                 // combustível que ele pode pagar
                driver.askForFuelPayment(howMuchFuel(driver) * fuelPrice); // pede para o motorista pagar o combustível
                semaphore.release(); // libera a bomba
                isWaiting = false; // avisa que o motorista nao esta mais esperando para sair do loop
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    private double howMuchFuel(Driver driver) {
        double fuelAmount = driver.getCar().getTankMax() - driver.getCar().getTank();
        double driverMoney = driver.getAccount().getBalance();
        if (fuelAmount * fuelPrice > driverMoney) {
            fuelAmount = driverMoney / fuelPrice;
        } else {
            fuelAmount = driver.getCar().getTankMax() - driver.getCar().getTank();
        }
        return fuelAmount;
    }

    public BankAccount getAccount() {
        return account;
    }

}

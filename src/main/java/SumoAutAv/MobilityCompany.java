package SumoAutAv;

import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.crypto.SecretKey;

public class MobilityCompany extends Thread {
    private String name = ""; // nome da empresa
    private double balance = 0.0;
    private BankAccount account;// saldo da empresa
    private ArrayList<Route> routesQueue = new ArrayList<Route>(); // fila de rotas
    private ArrayList<Route> routesHistory = new ArrayList<Route>(); // histórico de rotas
    private ArrayList<Route> routesInProgress = new ArrayList<Route>(); // rotas em progresso
    private ArrayList<Driver> drivers = new ArrayList<Driver>();
    private SecretKey encryptionKey;
    private AlphaBank alphaBank;
    private boolean routesCompleted = false;

    public MobilityCompany(String name, double balance, BankAccount account, SecretKey encryptionKey,
            AlphaBank alphaBank) {
        this.name = name;
        this.balance = balance;
        this.account = account;
        this.encryptionKey = encryptionKey;
        this.alphaBank = alphaBank;
    }

    public String getMobilityCompanyName() { // getter do nome da empresa
        return this.name;
    }

    public void addDriver(Driver driver) {
        this.drivers.add(driver);
    }

    public double getBalance() { // getter do saldo da empresa
        return this.balance;
    }

    public void addRouteToQueue(Route route) { // adiciona uma rota à fila
        this.routesQueue.add(route);
        System.out.println(name + ": Route: " + route.getIdItinerary() + " added");
    }

    public void moveRouteToInProgress(Route route) { // move uma rota da fila para em progresso
        this.routesQueue.remove(route);
        this.routesInProgress.add(route);
        System.out.println(name + ": Route: " + route.getIdItinerary() + " moved to in progress");
    }

    public void moveRouteToHistory(Route route) { // move uma rota de em progresso para histórico
        this.routesInProgress.remove(route);
        this.routesHistory.add(route);
        System.out.println(name + ": Route: " + route.getIdItinerary() + " moved to history");
    }

    @Override
    public void run() {
        double numberOfRoutes = routesQueue.size();
        while (!routesCompleted) { // loop infinito até todas as rotas serem completadas
            if (routesQueue.size() > 0 && retrieveFreeDriver() != null) { // se a fila de rotas não estiver vazia e
                                                                          // houver motorista disponível
                Route route = routesQueue.get(0); // pega a primeira rota da fila

                new Thread(() -> { // cria uma thread para o motorista
                    Driver driver = retrieveFreeDriver(); // pega um motorista disponível
                    try {
                        driver.setAvailable(false); // avisa que o motorista não está disponível
                        driver.addRouteToQueue(route); // adiciona a rota à fila do motorista
                        moveRouteToInProgress(route); // move a rota da fila para em progresso
                        driver.start(); // inicia o motorista

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    try {
                        driver.join(); // espera o motorista terminar
                        payDriver(driver); // paga o motorista
                        drivers.remove(driver); // remove o motorista da lista de motoristas para resetar a instancia e
                                                // iniciar a thread novamente
                        Driver refreshedDriver = driver.copyDriver(); // cria uma nova instancia do motorista
                        drivers.add(refreshedDriver); // adiciona o motorista à lista de motoristas
                        moveRouteToHistory(route); // move a rota de em progresso para histórico
                        if (routesQueue.size() == 0) { // se a fila de rotas estiver vazia
                            routesCompleted = true; // avisa que todas as rotas foram completadas
                        }
                        System.out.println("Driver finished route " + route.getIdItinerary());
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }).start();

            }

            if (routesHistory.size() == numberOfRoutes) {
                System.out.println("Mobility Company " + name + " finished all routes");
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    private void payDriver(Driver driver) {
        double finalDistance = driver.getCar().getDistanceTraveled() / 1000;
        double finalPrice = finalDistance * 3.25;
        System.out.println("Driver " + driver.getDriversName() + " drove " + finalDistance + "km and earned $"
                + finalPrice);
        String jsonTransfer = JSONUtil.createTransferJson(name, driver.getDriversName(), finalPrice); // cria um json
                                                                                                      // de
                                                                                                      // transferencia
                                                                                                      // de dinheiro
        try {
            String encryptedJsonTransfer = EncryptionUtil.encrypt(jsonTransfer, encryptionKey); // criptografa o json
            alphaBank.transferMoneyJsonEncrypted(encryptedJsonTransfer); // transfere o dinheiro
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private Driver retrieveFreeDriver() { // pega um motorista disponível
        Driver freeDriver = null;
        for (Driver driver : drivers) {
            if (driver.isAvailable()) {
                freeDriver = driver;
                break;
            }
        }
        return freeDriver;
    }

    public BankAccount getAccount() {
        return account;
    }

    public void generateReportCSV() { // gera um relatório em csv
        CSVReportGenerator csv = new CSVReportGenerator();
        for (int i = 0; i < drivers.size(); i++) {
            csv.generateCSVReport(drivers.get(i).getCar(), drivers.get(i).getDriversName() + "_Report.csv");
        }
    }

}

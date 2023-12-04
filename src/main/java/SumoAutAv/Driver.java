package SumoAutAv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.SecretKey;

import org.jfree.ui.RefineryUtilities;

import de.tudresden.sumo.objects.SumoColor;
import io.sim.DrivingData;
import it.polito.appeal.traci.SumoTraciConnection;

public class Driver extends Thread {
    String name;

    private BankAccount account;
    private Car car;

    private SecretKey key;
    private boolean isAvailable = true;
    private ArrayList<Route> RoutesQueue = new ArrayList<Route>(); // fila de rotas do motorista
    private ArrayList<Route> RoutesHistory = new ArrayList<Route>(); // histórico de rotas do motorista
    private ArrayList<Route> RoutesInProgress = new ArrayList<Route>(); // rotas em progresso
    private SumoTraciConnection sumo;
    private FuelStation fuelStation;
    private AlphaBank alphaBank;

    private ArrayList<double[]> reconciledSpeedsArray = new ArrayList<double[]>();
    private ArrayList<double[]> reconciledTimesArray = new ArrayList<double[]>();

    public ArrayList<double[]> getReconciledTimesArray() {
        return reconciledTimesArray;
    }

    public ArrayList<double[]> getReconciledSpeedsArray() {
        return reconciledSpeedsArray;
    }

    public Driver(String name, BankAccount account, SecretKey key, FuelStation fuelStation, AlphaBank alphaBank) {
        this.fuelStation = fuelStation;
        this.alphaBank = alphaBank;
        this.name = name;
        this.account = account;
        this.key = key;
        createDriverCarThread();
        // Chart chart = new Chart(name, this.car);
        // chart.pack();
        // RefineryUtilities.centerFrameOnScreen(chart);
        // chart.setVisible(true);
    }

    public String getDriversName() {
        return name;
    }

    private void createDriverCarThread() { // cria o carro do motorista
        int fuelType = 2;
        int fuelPreferential = 2;
        double fuelPrice = 4.87;
        int personCapacity = 1;
        int personNumber = 1;
        SumoColor green = new SumoColor(0, 255, 0, 126);

        try {
            this.car = new Car(true, name + " Car", green, "D1", sumo, personNumber, fuelType, fuelPreferential,
                    fuelPrice,
                    personCapacity, personNumber, name);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!isAvailable) { // enquanto o motorista não estiver disponível
            if (RoutesQueue.size() > 0) { // se a fila de rotas não estiver vazia
                startSumoServer(); // inicia o servidor SUMO
                Route route = RoutesQueue.get(0); // pega a primeira rota da fila
                RoutesQueue.remove(0); // remove a rota da fila
                RoutesInProgress.add(route); // adiciona a rota em progresso
                TransportService transportService = new TransportService(true, name, route, car,
                        sumo); // cria um serviço de transporte que irá passar os passos do sumo
                transportService.start(); // inicia o serviço de transporte
                car.setSumo(sumo); // seta o sumo do carro atualizado
                car.setRoute(route);
                car.setCarOwner(this); // seta o dono do carro

                try {
                    Thread.sleep(5000); // espera 5 segundos para o sumo carregar
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                car.start(); // inicia o carro
                car.startCar(); // notifica o carro para sair do wait
                try {
                    car.join(); // espera o carro terminar
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                double[] reconciledTimes = TransportService.doReconciliation(this.car.getTimeMeasurements(),
                        this.car.getEstimateTimes());
                double[] reconciledSpeeds = car.getReconciledSpeeds(reconciledTimes);
                reconciledTimesArray.add(Arrays.copyOfRange(reconciledTimes, 1, reconciledTimes.length - 1));
                reconciledSpeedsArray.add(reconciledSpeeds);

                RoutesInProgress.remove(route); // remove a rota de em progresso
                RoutesHistory.add(route); // adiciona a rota ao histórico
                System.out.println("Driver " + name + " finished route " + route.getIdItinerary()); // avisa que o
                                                                                                    // motorista
                                                                                                    // terminou a rota
                isAvailable = true; // avisa que o motorista está disponível
            }
        }
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public void addRouteToQueue(Route route) { // adiciona uma rota à fila
        this.RoutesQueue.add(route);
        System.out.println(name + ": Route: " + route.getIdItinerary() + " added");
    }

    public void moveRouteToInProgress(Route route) { // move uma rota da fila para em progresso
        this.RoutesQueue.remove(route);
        this.RoutesInProgress.add(route);
        System.out.println(name + ": Route: " + route.getIdItinerary() + " moved to in progress");
    }

    public void moveRouteToHistory(Route route) { // move uma rota de em progresso para histórico
        this.RoutesInProgress.remove(route);
        this.RoutesHistory.add(route);
        System.out.println(name + ": Route: " + route.getIdItinerary() + " moved to history");
    }

    public Car getCar() {
        return car;
    }

    private void startSumoServer() { // como na env simulator
        /* SUMO */
        String sumo_bin = "sumo-gui";
        String config_file = "C:\\Users\\olive\\Desktop\\Importante\\UFLA\\9 período\\Automação Avançada\\AV1\\sim\\sim-main\\map\\map.sumo.cfg";

        // Sumo connection
        this.sumo = new SumoTraciConnection(sumo_bin, config_file);
        sumo.addOption("start", "1"); // auto-run on GUI show
        sumo.addOption("quit-on-end", "1"); // auto-close on end
        try {
            sumo.runServer();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void goToFuelStation() {
        fuelStation.askForFuel(this); // avisa o posto de gasolina que o motorista está indo abastecer
    }

    public void askForFuelPayment(double fuelPrice) { // pede para o banco pagar o posto de gasolina
        String json = JSONUtil.createTransferJson(name, "Fuel Station", fuelPrice); // cria o json
        try {
            String encrypted = EncryptionUtil.encrypt(json, key); // criptografa o json
            System.out.println("Driver " + name + " paying fuel station the amount: " + fuelPrice + "$"); // avisa que
                                                                                                          // está
                                                                                                          // pagando o
                                                                                                          // posto de
                                                                                                          // gasolina
            alphaBank.transferMoneyJsonEncrypted(encrypted); // transfere o dinheiro
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public FuelStation getFuelStation() {
        return fuelStation;
    }

    public AlphaBank getAlphaBank() {
        return alphaBank;
    }

    public Driver copyDriver() {
        Driver copiedDriver = new Driver(this.name, this.account, this.key, this.fuelStation, this.alphaBank);

        copiedDriver.RoutesHistory = this.RoutesHistory;
        copiedDriver.car = this.car.copyCar();

        copiedDriver.reconciledSpeedsArray = this.reconciledSpeedsArray;
        copiedDriver.reconciledTimesArray = this.reconciledTimesArray;

        return copiedDriver;
    }

    public BankAccount getAccount() {
        return account;
    }

}

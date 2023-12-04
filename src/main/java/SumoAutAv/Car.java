package SumoAutAv;

import java.util.ArrayList;

import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.objects.SumoColor;
import io.sim.Auto;
import io.sim.DrivingData;
import it.polito.appeal.traci.SumoTraciConnection;

public class Car extends Auto { // extende a classe Auto para ter acesso aos dados do sumo
    private Driver carOwner;
    private volatile boolean isRunning = false;
    private boolean isRefueling = false;
    private double carTank;

    private Route route;
    private String currentRoadID = "";
    private String newRoute = "";
    private double previousTime = 0.0;
    private ArrayList<Double> timeMeasurements = new ArrayList<Double>();
    private ArrayList<Double> estimateTimes = new ArrayList<Double>();
    private ArrayList<String> currentEdges = new ArrayList<String>();
    double simTime = 0.0;
    private ArrayList<Double> edgesDistances = new ArrayList<Double>();
    private double distanceTraveled = 0.0;

    public Car(boolean _on_off, String _idAuto, SumoColor _colorAuto, String _driverID, SumoTraciConnection _sumo,
            long _acquisitionRate, int _fuelType, int _fuelPreferential, double _fuelPrice, int _personCapacity,
            int _personNumber, String carOwner) throws Exception {
        super(_on_off, _idAuto, _colorAuto, _driverID, _sumo, _acquisitionRate, _fuelType, _fuelPreferential,
                _fuelPrice,
                _personCapacity, _personNumber); // chama o construtor da classe Auto
        this.carTank = 10.0; // tanque do carro começa com 10 litros
    }

    public synchronized void startCar() { // inicia o carro
        isRunning = true;
        notify();
    }

    @Override
    public void run() {

        currentEdges.clear();
        currentEdges = getRouteEdges();

        new Thread(() -> {
            while (!this.getSumo().isClosed()) {
                try {
                    checkRouteId();
                    Thread.sleep(50);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (this.getSumo().isClosed()) {
                timeMeasurements.add(simTime - previousTime);
                getDistanceTraveledAtEdge();
                printDistancesMeasurements();
                printTimesMeasurements();
            }
        }).start();

        while (this.isOn_off()) { // loop infinito
            synchronized (this) {
                while (!isRunning) { // enquanto o carro não estiver rodando
                    try {
                        wait(); // espera, só sai quando o carro é startado nom starCar (notify)
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            if (carTank > 3 && !isRefueling) { // se o tanque do carro for maior que 3 e o carro não estiver
                                               // abastecendo
                super.atualizaSensores(); // atualiza os sensores do carro
                updateTank(); // atualiza o tanque do carro

                ReportGenerator reportGenerator = new ReportGenerator(); // cria um gerador de relatório
                reportGenerator.carReport(this); // gera o relatório do carro
                // Da classe Auto, lê os dados do carro e salva no relatório
            } else {
                stopToRefuel();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void updateTank() {
        try {
            double fuelConsumption = (double) getSumo().do_job_get(Vehicle.getFuelConsumption(this.getIdAuto()));
            fuelConsumption = (fuelConsumption * 0.001) / 0.74; // convert to ml
            fuelConsumption = fuelConsumption / 1000; // convert to liters
            if (fuelConsumption > 0) {
                carTank -= fuelConsumption;
                System.out.println("Car " + this.getIdAuto() + " tank: " + carTank);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void stopToRefuel() {
        isRefueling = true;
        carOwner.goToFuelStation();
    }

    private void refuelComplete() {
        isRefueling = false;
        System.out.println("Car " + this.getIdAuto() + " refueled");
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public boolean isRefueling() {
        return isRefueling;
    }

    public void setRefueling(boolean isRefueling) {
        this.isRefueling = isRefueling;
    }

    public Driver getCarOwner() {
        return carOwner;
    }

    public void setCarOwner(Driver carOwner) {
        this.carOwner = carOwner;
    }

    public void refuelTank(double liters) {
        carTank += liters;
        refuelComplete();
    }

    public double getTankMax() {
        return 50.0;
    }

    public double getTank() {
        return carTank;
    }

    public void setTank(double carTank2) {
        this.carTank = carTank2;
    }

    public Car copyCar() { // copia os atributos do carro para uma nova instância da Thread Car
        Car car = null;
        try {
            car = new Car(true, this.getIdAuto(), this.getColorAuto(), this.getDriverID(), this.getSumo(),
                    this.getAcquisitionRate(), this.getFuelType(), this.getFuelPreferential(), this.getFuelPrice(),
                    this.getPersonCapacity(), this.getPersonNumber(), this.carOwner.getDriversName());
            car.setTank(this.carTank);
            car.drivingRepport = this.drivingRepport;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return car;
    }

    private void printDistancesMeasurements() {
        edgesDistances.remove(0);
        System.out.println("Car " + this.getIdAuto() + " total distance traveled: " + distanceTraveled);
        System.out.println("Car " + this.getIdAuto() + " distances measurements: ");
        for (Double distance : edgesDistances) {
            System.out.println(distance);
        }
    }

// printa o tempo total gasto pelo carro e os tempos gastos em cada aresta
    public void printTimesMeasurements() {
        simTime -= timeMeasurements.get(0);
        timeMeasurements.set(0, simTime);
        System.out.println("Car " + this.getIdAuto() + " total time spent: " + simTime);
        System.out.println("Car " + this.getIdAuto() + " times measurements: ");
        for (Double time : timeMeasurements) {
            System.out.println(time);
        }
    }

    // retorna as arestas da rota passadas ao sumo
    public ArrayList<String> getRouteEdges() {
        ArrayList<String> edges = new ArrayList<String>();
        String[] aux = this.route.getItinerary();

        for (String e : aux[1].split(" ")) {
            edges.add(e);
        }
        return edges;
    }

    // Calcula e retorna as velocidades reconciliadas
    public double[] getReconciledSpeeds(double[] time) {
        double[] speeds = new double[time.length - 2];
        for (int i = 1; i < time.length - 1; i++) {
            speeds[i - 1] = edgesDistances.get(i - 1) / time[i];
        }

        System.out.println("Car " + this.getIdAuto() + " reconciled speeds: ");
        for (Double speed : speeds) {
            System.out.println(speed);
        }
        return speeds;
    }

    // retorna as medições de tempo em cada Edge
    public ArrayList<Double> getTimeMeasurements() {
        return timeMeasurements;
    }

    // verifica se o carro mudou de Edge, para calcular o tempo e distancia de cada
    // segmento
    public void checkRouteId() {
        if (this.getSumo().isClosed()) {
            return;
        }

        try {
            currentRoadID = (String) this.getSumo().do_job_get(Vehicle.getRoadID(this.getIdAuto()));
            simTime = (double) this.getSumo().do_job_get(Simulation.getTime());
            distanceTraveled = (double) this.getSumo().do_job_get(Vehicle.getDistance(this.getIdAuto()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!currentRoadID.equals(newRoute) && currentEdges.contains(currentRoadID)) {
            try {
                double time = simTime - previousTime;
                previousTime = simTime;
                newRoute = currentRoadID;
                timeMeasurements.add(time);
                getDistanceTraveledAtEdge();
                System.out.println("Time: " + time);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // adiciona a distancia percorrida na aresta atual ao vetor de distancias
    public void getDistanceTraveledAtEdge() {
        double distanceSum = 0.0;
        for (Double distance : edgesDistances) {
            distanceSum += distance;
        }
        edgesDistances.add(distanceTraveled - distanceSum);
    }

    public ArrayList<Double> getEdgesDistances() {
        return edgesDistances;
    }

    public ArrayList<Double> getEstimateTimes() {
        return estimateTimes;
    }

    public void setEstimateTimes(ArrayList<Double> estimateTimes) {
        this.estimateTimes = estimateTimes;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

}

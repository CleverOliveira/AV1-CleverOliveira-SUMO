package SumoAutAv;

import java.util.ArrayList;

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
            double fuelConsumption = (double) getSumo().do_job_get(Vehicle.getFuelConsumption(this.getIdAuto())) / 1000; // pega
                                                                                                                         // o
                                                                                                                         // consumo
                                                                                                                         // de
                                                                                                                         // combustível
                                                                                                                         // do
                                                                                                                         // carro
            if (fuelConsumption > 0) {

                carTank -= fuelConsumption / 2;
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

}

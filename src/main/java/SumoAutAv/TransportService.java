package SumoAutAv;

import java.util.ArrayList;

import de.tudresden.sumo.cmd.Route;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.objects.SumoStringList;
import io.sim.Auto;
import io.sim.Itinerary;
import it.polito.appeal.traci.SumoTraciConnection;

public class TransportService extends Thread {

    private String idTransportService;
    private boolean on_off;
    private SumoTraciConnection sumo;
    private Car auto;
    private Itinerary itinerary;

    double totalTravelTime = 0.0;
    ArrayList<Double> edgesTravelTimes = new ArrayList<Double>();

    public TransportService(boolean _on_off, String _idTransportService, Itinerary _itinerary, Car _auto,
            SumoTraciConnection _sumo) {

        this.on_off = _on_off;
        this.idTransportService = _idTransportService;
        this.itinerary = _itinerary;
        this.auto = _auto;
        this.sumo = _sumo;
    }

    @Override
    public void run() {
        try {

            this.initializeRoutes();

            while (this.on_off) {
                try {
                    if (this.auto.isRunning() && !auto.isRefueling()) { // if the car is not stopped
                        this.sumo.do_timestep();
                    }
                } catch (Exception e) {

                }

                Thread.sleep(23);
                if (this.getSumo().isClosed()) {
                    this.on_off = false;
                    this.auto.setOn_off(false); // turn off the car
                    this.auto.setRunning(false); // finish the route
                    System.out.println("SUMO is closed...");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeRoutes() {

        SumoStringList edge = new SumoStringList();
        edge.clear();
        String[] aux = this.itinerary.getItinerary();

        for (String e : aux[1].split(" ")) {
            edge.add(e);
        }

        retrieveEdgesTravelTimes(edge);

        try {
            sumo.do_job_set(Route.add(this.itinerary.getIdItinerary(), edge));
            // sumo.do_job_set(Vehicle.add(this.auto.getIdAuto(), "DEFAULT_VEHTYPE",
            // this.itinerary.getIdItinerary(), 0,
            // 0.0, 0, (byte) 0));

            sumo.do_job_set(Vehicle.addFull(this.auto.getIdAuto(), // vehID
                    this.itinerary.getIdItinerary(), // routeID
                    "DEFAULT_VEHTYPE", // typeID
                    "now", // depart
                    "0", // departLane
                    "0", // departPos
                    "0", // departSpeed
                    "current", // arrivalLane
                    "max", // arrivalPos
                    "current", // arrivalSpeed
                    "", // fromTaz
                    "", // toTaz
                    "", // line
                    this.auto.getPersonCapacity(), // personCapacity
                    this.auto.getPersonNumber()) // personNumber
            );

            sumo.do_job_set(Vehicle.setSpeed(this.auto.getIdAuto(), Math.random() * 100));

            sumo.do_job_set(Vehicle.setColor(this.auto.getIdAuto(), this.auto.getColorAuto()));

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public boolean isOn_off() {
        return on_off;
    }

    public void setOn_off(boolean _on_off) {
        this.on_off = _on_off;
    }

    public String getIdTransportService() {
        return this.idTransportService;
    }

    public SumoTraciConnection getSumo() {
        return this.sumo;
    }

    public Auto getAuto() {
        return this.auto;
    }

    public Itinerary getItinerary() {
        return this.itinerary;
    }

     public void retrieveEdgesTravelTimes(SumoStringList edge) { // recupera os tempos de viagem estimados de cada Edge
        for (String edg : edge) {
            try {
                double t = (double) sumo.do_job_get(de.tudresden.sumo.cmd.Edge.getTraveltime(edg));
                totalTravelTime = totalTravelTime + t;
                edgesTravelTimes.add(t);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        for (int i = 0; i < edgesTravelTimes.size(); i++) {
            System.out.println("Edge estimate times: " + edge.get(i) + " Travel time: " +
                    edgesTravelTimes.get(i));
        }

        this.auto.setEstimateTimes(edgesTravelTimes);

        System.out.println("Travel time: " + totalTravelTime);
    }

    // Prepara os dados para reconciliação dos tempos de viagem medidos e retorna o
    // vetor de tempos de viagem reconciliados
    public static double[] doReconciliation(ArrayList<Double> timeMeasurements, ArrayList<Double> estimates) {
        double[] y = toDoubleArray(timeMeasurements);
        double[] v = varianceMatrix(timeMeasurements.size());
        double[] A = Reconciliation.createIncidenceMatrix(timeMeasurements.size());
        System.out.println("Estimates: " + estimates);
        System.out.println("Y:");
        Reconciliation.printMatrix(y);
        System.out.println("V:");
        Reconciliation.printMatrix(v);
        System.out.println("A:");
        Reconciliation.printMatrix(A);
        Reconciliation rec = new Reconciliation(y, v, A);
        System.out.println("Y_hat:");
        double[] y_hat = rec.getReconciledFlow();
        Reconciliation.printMatrix(y_hat);
        return y_hat;
    }

    // Cria um vetor de variância de acordo com o tamanho do vetor de tempos
    private static double[] varianceMatrix(int size) {
        double[] v = new double[size];

        for (int i = 0; i < size; i++) {
            v[i] = 0.0000000001;
        }
        return v;
    }

    // Converte um ArrayList<Double> para double[]
    private static double[] toDoubleArray(ArrayList<Double> arrayList) {
        // Create a new array with the same size as the ArrayList
        double[] doubleArray = new double[arrayList.size()];

        // Copy each element from the ArrayList to the array
        for (int i = 0; i < arrayList.size(); i++) {
            doubleArray[i] = arrayList.get(i);
        }

        return doubleArray;
    }
}

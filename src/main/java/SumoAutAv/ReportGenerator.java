package SumoAutAv;



public class ReportGenerator {
    public ReportGenerator() {
    }

    public void carReport(SumoAutAv.Car car) {
        System.out.println("Car report: " + car.getDrivingRepport().get(car.getDrivingRepport().size() - 1).getAutoID()
                + " TimeStamp (nanoseconds): " + System.nanoTime() +
                " | ID car: " + car.getDrivingRepport().get(car.getDrivingRepport().size() - 1).getAutoID() +
                " | ID Route: " + car.getDrivingRepport().get(car.getDrivingRepport().size() - 1).getRouteIDSUMO() +
                " | Speed: " + car.getDrivingRepport().get(car.getDrivingRepport().size() - 1).getSpeed() +
                " | Distance: " + car.getDrivingRepport().get(car.getDrivingRepport().size() - 1).getOdometer() +
                " | Fuel Consumption: "
                + car.getDrivingRepport().get(car.getDrivingRepport().size() - 1).getFuelConsumption() +
                " | Fuel Type: " + car.getDrivingRepport().get(car.getDrivingRepport().size() - 1).getFuelType() +
                " | CO2 Emission: " + car.getDrivingRepport().get(car.getDrivingRepport().size() - 1).getCo2Emission() +
                " | Longitude (lon): "
                // + car.getCarLongitude()
                +
                " | Latitude (lat): ");
        // + car.getCarLatitude());
    }

    public void transactionReport(String transaction) {
        System.out.println("Transaction report: " + transaction);
    }
}

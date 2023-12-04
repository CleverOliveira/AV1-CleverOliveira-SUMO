package SumoAutAv;

import javax.crypto.SecretKey;

public class App {
        public static void main(String[] args) throws Exception {

                SecretKey generalKey = EncryptionUtil.generateSecretKey();

                FuelStation fuelStation = new FuelStation(new BankAccount(1000000.0, "Fuel Station", null));
                AlphaBank alphaBank = new AlphaBank(generalKey);

                MobilityCompany mobilityCompany = new MobilityCompany("Transport Enterprise", 1000000.0,
                                new BankAccount(100000, "Transport Enterprise", null), generalKey, alphaBank);
                Driver driver1 = new Driver("Driver 1", new BankAccount(200, "Driver 1", null), generalKey, fuelStation,
                                alphaBank);
                // Driver driver2 = new Driver("Driver 2", new BankAccount(200, "Driver 2",
                // null), generalKey, fuelStation,
                // alphaBank);
                // Driver driver3 = new Driver("Driver 3", new BankAccount(200, "Driver 3",
                // null), generalKey, fuelStation,
                // alphaBank);
                // Driver driver4 = new Driver("Driver 4", new BankAccount(200, "Driver 3",
                // null), generalKey, fuelStation,
                // alphaBank);
                // Driver driver5 = new Driver("Driver 5", new BankAccount(200, "Driver 3",
                // null), generalKey, fuelStation,
                // alphaBank);
                // Driver driver6 = new Driver("Driver 6", new BankAccount(200, "Driver 3",
                // null), generalKey, fuelStation,
                // alphaBank);
                for (int i = 0; i < 3; i++) {
                        String routeId = Integer.toString(0);
                        mobilityCompany.addRouteToQueue(new Route("sim-main\\data\\dados2.xml", routeId));
                        Thread.sleep(100);
                }

                mobilityCompany.addDriver(driver1);
                // mobilityCompany.addDriver(driver2);
                // mobilityCompany.addDriver(driver3);
                // mobilityCompany.addDriver(driver4);
                // mobilityCompany.addDriver(driver5);
                // mobilityCompany.addDriver(driver6);

                alphaBank.addAccount(driver1.getAccount());
                // alphaBank.addAccount(driver2.getAccount());
                // alphaBank.addAccount(driver3.getAccount());
                alphaBank.addAccount(mobilityCompany.getAccount());
                alphaBank.addAccount(fuelStation.getAccount());

                mobilityCompany.start();
                mobilityCompany.join();
                MathUtils.calcularDadosEstatisticos(driver1.getReconciledSpeedsArray(),
                                driver1.getReconciledTimesArray(), 0,
                                driver1.getCar().getEstimateTimes());

        }
}
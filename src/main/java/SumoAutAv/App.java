package SumoAutAv;

import java.io.IOException;

import javax.crypto.SecretKey;

import io.sim.EnvSimulator;
import it.polito.appeal.traci.SumoTraciConnection;

public class App {
        public static void main(String[] args) throws Exception {

                SecretKey generalKey = EncryptionUtil.generateSecretKey(); // gera uma chave para criptografia

                FuelStation fuelStation = new FuelStation(new BankAccount(1000000.0, "Fuel Station", null)); // cria um
                                                                                                             // posto de
                                                                                                             // gasolina
                AlphaBank alphaBank = new AlphaBank(generalKey); // cria um banco

                MobilityCompany mobilityCompany = new MobilityCompany("Transport Enterprise", 1000000.0, // cria uma
                                                                                                         // empresa

                                new BankAccount(100000, "Transport Enterprise", null), generalKey, alphaBank);

                for (int i = 0; i < 100; i++) {
                        Driver driver = new Driver("Driver" + i, new BankAccount(200, "Driver 1", null), generalKey,
                                        fuelStation,
                                        alphaBank);
                        mobilityCompany.addDriver(driver);
                        alphaBank.addAccount(driver.getAccount()); // adiciona 3 motoristas à empresa, para 100 apenas
                        // mudar o numero do for
                }

                for (int i = 0; i < 900; i++) { // aqui vai ser 900, coloquei menos para fins de teste
                        String routeId = Integer.toString(i);
                        mobilityCompany.addRouteToQueue(new Route("sim-main\\data\\dados2.xml", routeId));
                        Thread.sleep(100);
                        // adiciona 3 rotas à fila da empresa
                }

                alphaBank.addAccount(mobilityCompany.getAccount()); // adiciona a conta da empresa ao banco
                alphaBank.addAccount(fuelStation.getAccount()); // adiciona a conta do posto de gasolina ao banco

                mobilityCompany.start(); // inicia a empresa
                mobilityCompany.join(); // espera a empresa terminar
                System.out.println("Mobility company closed"); // avisa que a empresa terminou

                mobilityCompany.generateReportCSV(); // gera o relatório da empresa

                /* SUMO */

                // EnvSimulator env = new EnvSimulator();
                // env.start();

        }
}

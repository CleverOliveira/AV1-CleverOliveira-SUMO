package io.sim;

import java.io.IOException;
import java.util.ArrayList;

import javax.crypto.SecretKey;

import it.polito.appeal.traci.SumoTraciConnection;


/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws Exception {
        new Thread(()->{  
        SumoTraciConnection sumo;
        /* SUMO */
		String sumo_bin = "sumo-gui";
		String config_file = "C:\\Users\\olive\\Desktop\\Importante\\UFLA\\9 período\\Automação Avançada\\AV1\\sim\\sim-main\\map\\map.sumo.cfg";

		// Sumo connection
		sumo = new SumoTraciConnection(sumo_bin, config_file);
		sumo.addOption("start", "1"); // auto-run on GUI show
		sumo.addOption("quit-on-end", "1"); // auto-close on end
		try {
			sumo.runServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }).start();
        //EnvSimulator ev = new EnvSimulator();
        //ev.start();
    }
}

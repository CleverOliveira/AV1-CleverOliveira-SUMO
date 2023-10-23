package SumoAutAv;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.sim.Itinerary;

public class Route extends Itinerary {

    public Route(String _uriRoutesXML, String _idRoute) {
        super(_uriRoutesXML, _idRoute);
        // TODO Auto-generated constructor stub
    } // Copiado da classe Itinerary

}

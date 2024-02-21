package com.example.firmas.util;

import com.example.firmas.model.Persona;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class XMLUtil {

    public static Document crearDocumentoXML(Persona persona) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.newDocument();

            // Crear elemento raíz
            Element personaElement = document.createElement("persona");

            // Crear elementos para los datos de la persona
            Element nombreElement = document.createElement("nombre");
            nombreElement.setTextContent(persona.getNombre());

            Element apellidoElement = document.createElement("apellido");
            apellidoElement.setTextContent(persona.getApellido());

            Element edadElement = document.createElement("edad");
            edadElement.setTextContent(String.valueOf(persona.getEdad()));

            Element emailElement = document.createElement("email");
            emailElement.setTextContent(String.valueOf(persona.getEmail()));

            Element directionElement = document.createElement("direction");
            directionElement.setTextContent(String.valueOf(persona.getDirection()));

            // Agregar elementos al elemento raíz
            personaElement.appendChild(nombreElement);
            personaElement.appendChild(apellidoElement);
            personaElement.appendChild(edadElement);
            personaElement.appendChild(emailElement);
            personaElement.appendChild(directionElement);

            // Agregar el elemento raíz al documento
            document.appendChild(personaElement);

            return document;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Document parsearDocumentoXML(InputStream inputStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

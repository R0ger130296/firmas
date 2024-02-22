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
            agregarElemento(document, personaElement, "nombre", persona.getNombre());
            agregarElemento(document, personaElement, "apellido", persona.getApellido());
            agregarElemento(document, personaElement, "edad", String.valueOf(persona.getEdad()));
            agregarElemento(document, personaElement, "email", persona.getEmail());
            agregarElemento(document, personaElement, "direccion", persona.getDireccion());

            // Agregar el elemento raíz al documento
            document.appendChild(personaElement);

            return document;
        } catch (Exception e) {
            manejarExcepcion(e);
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
            manejarExcepcion(e);
            return null;
        }
    }

    private static void agregarElemento(Document document, Element padre, String nombre, String contenido) {
        Element elemento = document.createElement(nombre);
        elemento.setTextContent(contenido);
        padre.appendChild(elemento);
    }

    private static void manejarExcepcion(Exception e) {
        // Manejar la excepción según las necesidades de tu aplicación
        e.printStackTrace();
    }
}

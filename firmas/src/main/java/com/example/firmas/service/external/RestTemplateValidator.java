package com.example.firmas.service.external;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestTemplateValidator {
    public String validarFirma(String xmlPath) {
        // URL de la solicitud
        String url = "http://127.0.0.1:3000/v1/validar";

        // Configuración de encabezados
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Crear un objeto JSON con la propiedad "xml"
        String jsonRequest = "{\"xml\": \"" + xmlPath + "\"}";

        // Crear la entidad de la solicitud con el objeto JSON y los encabezados
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequest, headers);

        // Crear RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        try {
            // Realizar la solicitud POST y obtener la respuesta
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            // Devolver la respuesta del servidor
            return response.getBody();
        } catch (Exception e) {
            // Manejar excepciones en la solicitud
            e.printStackTrace();
            return "Error al realizar la solicitud";
        }
    }
}

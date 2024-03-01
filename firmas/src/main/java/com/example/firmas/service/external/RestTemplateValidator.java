package com.example.firmas.service.external;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class RestTemplateValidator {
    public String validarFirma(MultipartFile xmlPath) throws IOException {
        // URL de la solicitud
        String url = "http://127.0.0.1:3000/v1/validar";

        // Configuración de encabezados
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Crear un objeto JSON con la propiedad "xml"
        //String jsonRequest = "{\"xml\": \"" + xmlPath + "\"}";
        MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
        // Agregar el contenido del archivo como un ByteArrayResource
        ByteArrayResource xmlResource = new ByteArrayResource(xmlPath.getBytes()) {
            @Override
            public String getFilename() {
                return xmlPath.getOriginalFilename();
            }
        };
        body.add("file", xmlResource);

        // Crear la entidad de la solicitud con el objeto JSON y los encabezados
        HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(body, headers);

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

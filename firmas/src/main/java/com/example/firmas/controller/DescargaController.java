package com.example.firmas.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class DescargaController {

    @GetMapping("/descargarView")
    public String descargarView() {
        return "descargarView";
    }

    @GetMapping("/descargar")
    public ResponseEntity<byte[]> descargarArchivo(HttpSession session) {
        byte[] archivoFirmado = (byte[]) session.getAttribute("archivoFirmado");

        if (archivoFirmado != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename("archivoFirmado2.xml").build());

            // Establece la bandera como verdadera
            return new ResponseEntity<>(archivoFirmado, headers, HttpStatus.OK);
        } else {
            // Manejo de errores si el archivo no está disponible
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

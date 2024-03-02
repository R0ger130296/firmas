package com.example.firmas.controller;

import com.example.firmas.service.external.RestTemplateValidator;
//import com.example.firmas.util.VerificacionUtil;
import jakarta.servlet.http.HttpSession;
//import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

//import java.io.File;
import java.io.IOException;
import java.security.cert.X509Certificate;
//import java.util.Objects;

@Controller
public class VerificarController {
    private final RestTemplateValidator restTemplateValidator;

    @Autowired
    public VerificarController(RestTemplateValidator restTemplateValidator) {
        this.restTemplateValidator = restTemplateValidator;
    }
    @GetMapping("/verificarView")
    public String mostrarFormulario() {
        return "verificarView";
    }

    @PostMapping("/verificar")
    public String verificarFirma(Model model, HttpSession session,
                                 @RequestParam("fileFirm") MultipartFile file) throws IOException {
        // Obtener la ruta del archivo
        //String rutaArchivo = obtenerRutaDesdeMultipartFile(file);

        // Obtener el certificado necesario (ajusta según tu aplicación)
        X509Certificate certificado = obtenerCertificadoDesdeSesionOtroMedio(session);

        // Retornar la página de verificación
        if (certificado != null) {
            // Verificar la firma del archivo
            boolean firmaValida = Boolean.parseBoolean(restTemplateValidator.validarFirma(file));

            // Agregar el resultado de la verificación al modelo
            model.addAttribute("firmaValida", firmaValida);
        } else {
            // Manejo de errores: Archivo o certificado no disponibles
            model.addAttribute("firmaValida", false);
            model.addAttribute("error", "Archivo o certificado no disponibles para la verificación.");
        }
        return "verificarView"; // Retornar la página de verificación
    }

    // Método para obtener la ruta del archivo desde MultipartFile
    /*private String obtenerRutaDesdeMultipartFile(MultipartFile file) {
        // Puedes almacenar el archivo en una ubicación temporal y devolver la ruta
        // O simplemente obtener el nombre del archivo
        return File.separator + "home" + File.separator + "roger" + File.separator + "Descargas" + File.separator
                + Objects.requireNonNull(file.getOriginalFilename());
    }*/


    // Método de ejemplo para obtener el certificado
    private X509Certificate obtenerCertificadoDesdeSesionOtroMedio(HttpSession session) {
        // Supongamos que has almacenado el certificado con el nombre "certificado"
        Object certificadoObj = session.getAttribute("certificado");

        if (certificadoObj instanceof X509Certificate) {
            // El objeto en la sesión es un certificado, devuélvelo
            return (X509Certificate) certificadoObj;
        } else {
            // Manejo en caso de que el certificado no esté disponible en la sesión
            return null;
        }
    }
}

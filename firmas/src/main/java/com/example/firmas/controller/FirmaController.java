package com.example.firmas.controller;

import com.example.firmas.model.Persona;
import com.example.firmas.util.FirmaResultado;
import com.example.firmas.util.FirmaUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class FirmaController {

    @GetMapping("/formularioView")
    public String mostrarFormulario(Model model) {
        // Agregar un objeto Persona al modelo para el formulario
        model.addAttribute("persona", new Persona());
        return "formularioView";
    }

    @PostMapping("/firmar")
    public String firmarArchivo(@ModelAttribute Persona persona,
                                HttpSession session,
                                @RequestParam("file") MultipartFile file) {
        // Lógica para firmar el archivo utilizando el objeto persona y el archivo .p12
        FirmaResultado resultado = FirmaUtil.firmarArchivo(persona, file);

        // Guardar el archivo firmado y el certificado en la sesión
        assert resultado != null;
        session.setAttribute("archivoFirmado", resultado.getArchivoFirmado());
        session.setAttribute("certificado", resultado.getCertificado());

        // Redirigir a la página de descargar_verificar en lugar de la página de descarga
        return "redirect:/descargarView";
    }
}

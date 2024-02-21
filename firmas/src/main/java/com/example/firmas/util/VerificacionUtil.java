package com.example.firmas.util;

import org.apache.xml.security.Init;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;

public class VerificacionUtil {

    public static boolean verificarFirma(byte[] archivoFirmado, X509Certificate certificado) {
        try {
            // Inicializar xml-security
            org.apache.xml.security.Init.init();

            // Crear un documento XML a partir del archivo firmado
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(archivoFirmado)) {
                Document document = XMLUtil.parsearDocumentoXML(inputStream);

                // Configurar Apache Santuario
                ElementProxy.setDefaultPrefix(Constants.SignatureSpecNS, "");

                // Obtener la firma del documento
                Element signatureElement = (Element) document.getElementsByTagNameNS(Constants.SignatureSpecNS, "Signature").item(0);
                XMLSignature signature = new XMLSignature(signatureElement, "");

                // Verificar la firma
                boolean isValid = signature.checkSignatureValue(certificado);

                if (!isValid) {
                    System.out.println("La firma no es válida.");
                } else {
                    System.out.println("La firma es válida.");
                }

                return isValid;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

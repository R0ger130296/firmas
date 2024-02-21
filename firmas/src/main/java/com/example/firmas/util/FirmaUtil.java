package com.example.firmas.util;

import com.example.firmas.model.Persona;
import com.example.firmas.util.FirmaResultado;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

public class FirmaUtil {

    static String claveCertificado = "123";
    static String aliasCertificado = "1";

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public static FirmaResultado firmarArchivo(Persona persona, MultipartFile file) {
        try {
            // Inicializar xml-security
            org.apache.xml.security.Init.init();

            // Validar que el archivo no sea nulo y tenga contenido
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("El archivo .p12 no fue proporcionado o está vacío.");
            }

            // Obtener InputStream desde MultipartFile
            try (InputStream fileInputStream = file.getInputStream()) {
                // Cargar el keystore desde el archivo .p12
                KeyStore keystore = KeyStore.getInstance("PKCS12");
                keystore.load(fileInputStream, claveCertificado.toCharArray());

                // Obtener la clave privada y el certificado del keystore
                PrivateKey privateKey = (PrivateKey) keystore.getKey(aliasCertificado, claveCertificado.toCharArray());
                X509Certificate cert = (X509Certificate) keystore.getCertificate(aliasCertificado);

                // Crear un documento XML con los datos de la persona
                Document document = XMLUtil.crearDocumentoXML(persona);

                // Configurar el elemento para ser firmado
                assert document != null;
                Element elementoAFirmar = document.getDocumentElement();

                // Inicializar Apache Santuario
                ElementProxy.setDefaultPrefix(Constants.SignatureSpecNS, "");

                // Crear el objeto XMLSignature
                XMLSignature signature = new XMLSignature(document, "", XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256);

                // Configurar la transformación para Canonicalizar
                Transforms transforms = new Transforms(document);
                transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
                transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
                signature.addDocument("", transforms, "http://www.w3.org/2001/04/xmlenc#sha256");

                // Configurar la clave privada para firmar
                signature.addKeyInfo(cert);
                signature.addKeyInfo(cert.getPublicKey());

                // Firmar el elemento
                signature.sign(privateKey);

                // Obtener el documento original
                Document originalDocument = elementoAFirmar.getOwnerDocument();

                // Crear un nuevo documento para almacenar la firma
                Document signedDocument = originalDocument.getImplementation().createDocument(null, null, null);

                // Importar el nodo firmado al nuevo documento
                Node importedNode = signedDocument.importNode(signature.getElement(), true);

                // Adjuntar el nodo firmado al nuevo documento
                signedDocument.appendChild(importedNode);

                // Convertir el documento firmado a una representación de bytes
                try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    TransformerFactory.newInstance().newTransformer().transform(new DOMSource(signedDocument), new StreamResult(bos));
                    return new FirmaResultado(bos.toByteArray(), cert);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

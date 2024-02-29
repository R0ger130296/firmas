package com.example.firmas.util;

import com.example.firmas.model.Persona;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class FirmaUtil {

    private static final String CLAVE_CERTIFICADO = "123";
    private static final String ALIAS_CERTIFICADO = "1";

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public static FirmaResultado firmarArchivo(Persona persona, MultipartFile file) {
        try {          org.apache.xml.security.Init.init();

            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("El archivo .p12 no fue proporcionado o está vacío.");
            }

            try (InputStream fileInputStream = file.getInputStream()) {
                KeyStore keystore = KeyStore.getInstance("PKCS12");
                keystore.load(fileInputStream, CLAVE_CERTIFICADO.toCharArray());

                PrivateKey privateKey = (PrivateKey) keystore.getKey(ALIAS_CERTIFICADO, CLAVE_CERTIFICADO.toCharArray());
                X509Certificate cert = (X509Certificate) keystore.getCertificate(ALIAS_CERTIFICADO);

                Document document = XMLUtil.crearDocumentoXML(persona);
                Element elementoAFirmar = document.getDocumentElement();

                ElementProxy.setDefaultPrefix(Constants.SignatureSpecNS, "");

                XMLSignature signature = new XMLSignature(document, "", XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256);

                Transforms transforms = new Transforms(document);
                transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
                transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
                signature.addDocument("", transforms, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);
                signature.addKeyInfo(cert);
                signature.addKeyInfo(cert.getPublicKey());
                // Crear elemento XAdES-BES
                Element xadesBESSignature = signature.getElement();
                Element qualProps = signature.getDocument().createElementNS(Constants.SignatureSpecNS, "QualifyingProperties");
                xadesBESSignature.appendChild(qualProps);
                // Incluir propiedades firmadas para compatibilidad con XAdES
                Element signedProps = signature.getDocument().createElementNS(Constants.SignatureSpecNS, "SignedProperties");
                //qualProps.appendChild(signedProps);
                Element signedSignatureProperties = signature.getDocument().createElementNS(Constants.SignatureSpecNS, "SignedSignatureProperties");
                signedProps.appendChild(signedSignatureProperties);
                Element signingTime = signature.getDocument().createElementNS(Constants.SignatureSpecNS, "SigningTime");
                signingTime.setTextContent(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
                signedSignatureProperties.appendChild(signingTime);
                // **Fin modificaciones para XAdES**
                signature.sign(privateKey);

                // El siguiente código ha sido modificado para agregar la firma al documento original
                Node signatureNode = document.importNode(signature.getElement(), true);
                elementoAFirmar.appendChild(signatureNode);

                try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(bos));
                    return new FirmaResultado(bos.toByteArray(), cert);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

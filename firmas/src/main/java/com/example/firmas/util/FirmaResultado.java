package com.example.firmas.util;

import java.security.cert.X509Certificate;

public class FirmaResultado {
    private byte[] archivoFirmado;
    private X509Certificate certificado;

    public FirmaResultado(byte[] archivoFirmado, X509Certificate certificado) {
        this.archivoFirmado = archivoFirmado;
        this.certificado = certificado;
    }

    public byte[] getArchivoFirmado() {
        return archivoFirmado;
    }

    public X509Certificate getCertificado() {
        return certificado;
    }
}

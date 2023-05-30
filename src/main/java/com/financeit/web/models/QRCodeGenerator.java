package com.financeit.web.models;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QRCodeGenerator {
    public static byte[] generateQRCodeImage(String link, int width, int height) throws IOException {
        ByteArrayOutputStream stream = QRCode.from(link)
                .withSize(width, height)
                .to(ImageType.PNG)
                .stream();

        return stream.toByteArray();
    }
}

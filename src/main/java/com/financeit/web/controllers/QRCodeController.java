package com.financeit.web.controllers;

import com.financeit.web.models.QRCodeGenerator;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class QRCodeController {

    @GetMapping(value = "/generate_qr_code", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateQRCode(@RequestParam("linkQr") String linkQr) throws IOException {
        int width = 250;
        int height = 250;

        return QRCodeGenerator.generateQRCodeImage(linkQr, width, height);
    }
}

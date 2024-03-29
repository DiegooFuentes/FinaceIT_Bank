package com.financeit.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financeit.web.models.PendingTransaction;
import com.financeit.web.models.Transaction;
import com.financeit.web.models.TransactionType;
import com.financeit.web.repositories.PendingTransactionRepository;
import com.financeit.web.repositories.TransactionRepository;
import com.financeit.web.service.EmailNotificationService;
import com.financeit.web.service.PendingTransactionService;
import com.financeit.web.service.TOTPService;
import io.github.flashvayne.chatgpt.dto.chat.MultiChatMessage;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private final PendingTransactionService pendingTransactionService;
    private final EmailNotificationService emailNotificationService;
    private final ChatgptService chatgptService;

    @Autowired
    public QuestionController(PendingTransactionService pendingTransactionService,
                              EmailNotificationService emailNotificationService,
                              ChatgptService chatgptService) {
        this.pendingTransactionService = pendingTransactionService;
        this.emailNotificationService = emailNotificationService;
        this.chatgptService = chatgptService;
    }



    //recibo un mensaje desde el front
    // a ese mensaje le agrego el prompt establecido para generar la consulta al chat
    //la respuesta la convierto en un json
    //los datos del json los utilizo para crear una pendingTransaction
    @Transactional
    @PostMapping("/send")
    public ResponseEntity<?> chatTransaction(@RequestParam String message,
                                             Authentication authentication) {
        String prompt = "Genera solo el código que representa un JSON con las instrucciones que te entregaré a continuación.\n" +
                "El formato que debe representar ese JSON en el Backend es el de la entidad PendingTransaction con el siguiente código:\n" +
                " “  private TransactionType type;\n" +
                "   private double amount;\n" +
                "   private String description;\n" +
                "   private String accountFromNumber;\n" +
                "   private String accountToNumber;\n" +
                "“\n" +
                "Donde TransactionType type siempre será del tipo DEBIT.\n" +
                "Este JSON se debe generar a partir de un texto escrito en lenguaje natural por una persona, el cual, puede tener las siguientes formas a manera de:\n" +
                "Si algo como recibes:\n" +
                "-Transfiere a la cuenta VIN0080 11070 desde mi cuenta VIN01.\n" +
                "Devuelve:\n" +
                "{\n" +
                " \"type\": \"DEBIT\",\n" +
                " \"amount\": 11070,\n" +
                " \"description\": \"Movimiento de VIN01 a VIN0080 \",\n" +
                " \"accountFromNumber\": \"VIN01\",\n" +
                " \"accountToNumber\": \"VIN0080 \"\n" +
                "}\n" +
                "SI recibes algo como:\n" +
                " -Mueve 43244 pesos de mi cuenta VIN01 a mi cuenta VIN002.\n" +
                "Devuelve:\n" +
                "{\n" +
                " \"type\": \"DEBIT\",\n" +
                " \"amount\": 43244,\n" +
                " \"description\": \"Movimiento de VIN01 a VIN002 \",\n" +
                " \"accountFromNumber\": \"VIN01\",\n" +
                " \"accountToNumber\": \"VIN002\"\n" +
                "}\n" +
                "Si recibes algo como:\n" +
                " -Págale desde mi cuenta VIN002 a la cuenta VIN369 10000.\n" +
                " Devuelve:\n" +
                "{\n" +
                " \"type\": \"DEBIT\",\n" +
                " \"amount\": 10000,\n" +
                " \"description\": \"Movimiento de VIN002 a VIN369 \",\n" +
                " \"accountFromNumber\": \"VIN002\",\n" +
                " \"accountToNumber\": \"VIN369\"\n" +
                "}\n" +
                "Si recibes algo como:\n" +
                " -Mueve 25990 de mi cuenta VIN546 a la cuenta VIN05741.\n" +
                " Devuelve:\n" +
                "{\n" +
                " \"type\": \"DEBIT\",\n" +
                " \"amount\": 25990,\n" +
                " \"description\": \"Movimiento de VIN546 a VIN05741\",\n" +
                " \"accountFromNumber\": \"VIN546 \",\n" +
                " \"accountToNumber\": \"VIN05741\"\n" +
                "}\n" +
                "El mensaje escrito por la persona es el siguiente: ";


        String completePrompt = prompt + message + ". Recuerda solo devolver el código JSON, sin ninguna explicación u otro texto que no esté asociado al JSON.";
        String responseMessage = chatgptService.sendMessage(completePrompt);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse;
        try {
            jsonResponse = objectMapper.readTree(responseMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

        String accountFromNumber = jsonResponse.get("accountFromNumber").asText();
        accountFromNumber.replaceAll(" ", "");
        String accountToNumber = jsonResponse.get("accountToNumber").asText();
        accountToNumber.replaceAll(" ", "");
        Double amount = jsonResponse.get("amount").asDouble();
        String description = jsonResponse.get("description").asText();

        System.out.println("Cuenta de origen " + accountFromNumber);
        System.out.println("Cuenta de destino " + accountToNumber);
        System.out.println("amount " + amount);
        System.out.println("description " + description);

        ResponseEntity<?> response = pendingTransactionService.makePendingTransaction(accountFromNumber, accountToNumber,
                amount, description, authentication);
        if(response.getStatusCode().equals(HttpStatus.CREATED)){
            emailNotificationService.sendNotification(authentication.getName());
            return response;
        }else {
            return response;
        }



    }
}

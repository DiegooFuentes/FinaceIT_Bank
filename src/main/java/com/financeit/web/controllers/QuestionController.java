package com.financeit.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financeit.web.models.PendingTransaction;
import com.financeit.web.models.Transaction;
import com.financeit.web.models.TransactionType;
import com.financeit.web.repositories.PendingTransactionRepository;
import com.financeit.web.repositories.TransactionRepository;
import com.financeit.web.service.TOTPService;
import io.github.flashvayne.chatgpt.dto.chat.MultiChatMessage;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    @Autowired
    private ChatgptService chatgptService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private PendingTransactionRepository pendingTransactionRepository;

//    public void test(){
//        String responseMessage = chatgptService.multiChat(Arrays.asList(new MultiChatMessage("user","how are you?")));
//        System.out.print(responseMessage); //\n\nAs an AI language model, I don't have feelings, but I'm functioning well. Thank you for asking. How can I assist you today?
//    }
//
//    public void test2(){
//        String responseMessage = chatgptService.sendMessage("how are you");
//        System.out.print(responseMessage); //I'm doing well, thank you. How about you?
//    }

//    @GetMapping("/send")
//    public String send(@RequestParam String message) {
//        String responseMessage = chatgptService.sendMessage(message);
//        return responseMessage;
//    }

    //recibo un mensaje desde el front
    // a ese mensaje le agrego el prompt establecido para generar la consulta al chat
    //la respuesta la convierto en un json
    //los datos del json los utilizo para crear una pendingTransaction
    @PostMapping("/send")
    public ResponseEntity<Object> chatTransaction(@RequestParam String message, Authentication authentication) {
        String prompt = "Genera solo el código que representa un JSON con las instrucciones que te entregaré a continuación.\n" +
                "El formato que debe representar ese JSON en el Backend es el de la entidad PendingTransaction con el siguiente código:\n" +
                " “   private TransactionType type;\n" +
                "    private double amount;\n" +
                "    private String description;\n" +
                "    private String accountFromNumber;\n" +
                "    private String accountToNumber;\n" +
                "“\n" +
                "Donde TransactionType type siempre será del tipo DEBIT.\n" +
                "Este JSON se debe generar a partir de un texto del siguiente texto escrito en lenguaje natural por una persona, el cual,  puede tener las siguientes formas a manera de:\n" +
                "Si algo como recibes:\n" +
                "-Transfiere a la cuenta VIN20 10000 desde mi cuenta VIN01\n" +
                "Devuelve:\n" +
                "{\n" +
                "  \"type\": \"DEBIT\",\n" +
                "  \"amount\": 10000,\n" +
                "  \"description\": \"Movimiento de VIN01 a VIN20 \",\n" +
                "  \"accountFromNumber\": \" VIN01\",\n" +
                "  \"accountToNumber\": \" VIN20\"\n" +
                "}\n" +
                "SI recibes algo como:\n" +
                "-Mueve 43244 pesos de mi cuenta VIN01 a mi cuenta VIN02\n" +
                "Devuelve:\n" +
                "{\n" +
                "  \"type\": \"DEBIT\",\n" +
                "  \"amount\": 43244,\n" +
                "  \"description\": \"Movimiento de VIN01 a VIN20 \",\n" +
                "  \"accountFromNumber\": \" VIN01\",\n" +
                "  \"accountToNumber\": \" VIN02\"\n" +
                "}\n" +
                "Si recibes algo como:\n" +
                "-Págale desde mi cuenta VIN02 a la cuenta VIN369 10000\n" +
                "Devuelve:\n" +
                "{\n" +
                "  \"type\": \"DEBIT\",\n" +
                "  \"amount\": 10000,\n" +
                "  \"description\": \"Movimiento de VIN01 a VIN20 \",\n" +
                "  \"accountFromNumber\": \" VIN02\",\n" +
                "  \"accountToNumber\": \" VIN369\"\n" +
                "}\n" +
                "El mensaje escrito por la persona es el siguiente: ";


        String completePrompt = prompt + message + "Recuerda solo devolver el código JSON, sin ninguna explicación u otro texto que no esté asociado al JSON.";
        String responseMessage = chatgptService.sendMessage(completePrompt);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse;
        try {
            jsonResponse = objectMapper.readTree(responseMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        LocalDateTime totpDate = TOTPService.generateDateTOTP();
        String totpPassword = TOTPService.generatePasswordTOTP();

        PendingTransaction pendingTransaction = new PendingTransaction(
                authentication.getName(),
                TransactionType.DEBIT,
                jsonResponse.get("amount").asDouble(),
                jsonResponse.get("description").asText(),
                jsonResponse.get("accountFromNumber").asText(),
                jsonResponse.get("accountToNumber").asText(),
                totpPassword,
                totpDate);
        pendingTransactionRepository.save(pendingTransaction);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

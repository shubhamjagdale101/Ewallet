package com.shubham.Wallet.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shubham.Utils.CommonIdentifier;
import com.shubham.Utils.UserIdentifier;
import com.shubham.Wallet.model.Wallet;
import com.shubham.Wallet.repository.WalletRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserCreatedConsumer {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletRepository walletRepository;

    @KafkaListener(topics = CommonIdentifier.USER_CREATED, groupId = "wallet_group")
    public void createWallet(String msg) throws JsonProcessingException {
        System.out.println(msg);
        JSONObject jsonObject = objectMapper.readValue(msg, JSONObject.class);

        String phoneNo = (String) jsonObject.get(CommonIdentifier.USER_CREATED_USER_USER_PhNo);
        String userIdentifier = (String) jsonObject.get(CommonIdentifier.USER_CREATED_USER_USER_IDENTIFIER);
        String userIdentifierValue = (String) jsonObject.get(CommonIdentifier.USER_CREATED_USER_USER_IDENTIFIER_VALUE);

        Wallet wallet = Wallet.builder()
                .contact(phoneNo)
                .userIdentifier(UserIdentifier.valueOf(userIdentifier))
                .userIdentifierValue(userIdentifierValue)
                .balance(50.0)
                .build();

        walletRepository.save(wallet);
    }
}
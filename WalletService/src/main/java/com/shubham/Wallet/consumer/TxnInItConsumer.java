package com.shubham.Wallet.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shubham.Utils.CommonIdentifier;
import com.shubham.Wallet.model.Wallet;
import com.shubham.Wallet.repository.WalletRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TxnInItConsumer {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private WalletRepository walletRepository;

    @KafkaListener(topics = CommonIdentifier.TXN_INIT_TOPIC, groupId = "txn_init")
    public void updateTxn(String msg) throws JsonProcessingException {
        System.out.println(msg);
        JSONObject req = objectMapper.readValue(msg, JSONObject.class);
        String status;

        String sender = req.getString(CommonIdentifier.TXN_INIT_TOPIC_SENDER);
        String receiver = req.getString(CommonIdentifier.TXN_INIT_TOPIC_RECEIVER);
        Double amount = req.getDouble(CommonIdentifier.TXN_INIT_TOPIC_AMOUNT);
        String txnId = req.getString(CommonIdentifier.TXN_INIT_TOPIC_TXN_ID);

        Wallet senderWallet = walletRepository.findByContact(sender);
        Wallet receiverWallet = walletRepository.findByContact(receiver);

        if(senderWallet == null || receiverWallet == null || senderWallet.getBalance() < amount){
            status = CommonIdentifier.TXN_FAILED;
        }

        // success
        walletRepository.updateWallet(amount, receiver);
        walletRepository.updateWallet(-amount, sender);
        status = CommonIdentifier.TXN_SUCCESS;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CommonIdentifier.TXN_STATUS, status);
        jsonObject.put(CommonIdentifier.TXN_INIT_TOPIC_TXN_ID, txnId);

        kafkaTemplate.send(CommonIdentifier.TXN_UPDATE_TOPIC, objectMapper.writeValueAsString(jsonObject.toString()));
    }
}

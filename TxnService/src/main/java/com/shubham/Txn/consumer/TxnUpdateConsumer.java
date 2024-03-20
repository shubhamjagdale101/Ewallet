package com.shubham.Txn.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shubham.Txn.model.Txn;
import com.shubham.Txn.model.TxnStatus;
import com.shubham.Txn.repository.TxnRepository;
import com.shubham.Utils.CommonIdentifier;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TxnUpdateConsumer {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TxnRepository txnRepository;

    @KafkaListener(topics = CommonIdentifier.TXN_UPDATE_TOPIC, groupId = "txn-update")
    public void updateTxn(String msg) throws JsonProcessingException {
        JSONObject jsonObject = objectMapper.readValue(msg, JSONObject.class);

        String txnStatus = jsonObject.getString(CommonIdentifier.TXN_STATUS);
        String txnId = jsonObject.getString(CommonIdentifier.TXN_INIT_TOPIC_TXN_ID);

        txnRepository.updateStatus(TxnStatus.valueOf(txnStatus), txnId);
    }
}
package com.shubham.Txn.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shubham.Txn.model.Txn;
import com.shubham.Txn.model.TxnStatus;
import com.shubham.Txn.repository.TxnRepository;
import com.shubham.Utils.CommonIdentifier;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class TxnService implements UserDetailsService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TxnRepository txnRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("7823065230", "Txn_Server");
        HttpEntity<String> req = new HttpEntity<>(headers);
        System.out.println(req);
        String url = "http://localhost:3001/user/getUser?contact=" + username;

        ResponseEntity<String> response = restTemplate.exchange(url,
                HttpMethod.GET,
                req,
                String.class);

        if (response.getStatusCodeValue() == 200) {
            JSONObject res = new JSONObject(response.getBody());
            String _username = (String) res.get("username");
            String _password = (String) res.get("password");
            List<SimpleGrantedAuthority> authority = List.of(new SimpleGrantedAuthority(res.getString("authority")));

            System.out.println(_username + ", " + _password + ", " + authority.toString());

            User user = new User(_username, _password, authority);
            return user;
        } else {
            throw new UsernameNotFoundException("User not found or server error (status code: " + response.getStatusCodeValue() + ")");
        }
    }

    public String initTxn(String senderId, String receiver, String purpose, double amount) throws JsonProcessingException {
        Txn txn = Txn.builder()
                .txnId(UUID.randomUUID().toString())
                .txnAmount(amount)
                .senderId(senderId)
                .receiverId(receiver)
                .txnStatus(TxnStatus.INITIATED)
                .build();
        txnRepository.save(txn);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CommonIdentifier.TXN_INIT_TOPIC_SENDER, senderId);
        jsonObject.put(CommonIdentifier.TXN_INIT_TOPIC_RECEIVER, receiver);
        jsonObject.put(CommonIdentifier.TXN_INIT_TOPIC_AMOUNT, amount);
        jsonObject.put(CommonIdentifier.TXN_INIT_TOPIC_TXN_ID, txn.getTxnId());

        kafkaTemplate.send(CommonIdentifier.TXN_INIT_TOPIC, objectMapper.writeValueAsString(jsonObject.toString()));
        return txn.getTxnId();
    }

    public List<Txn> getTxnByPages(String senderId, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Txn> pageData = txnRepository.findBySenderId(senderId, pageable);
        return pageData.getContent();
    }
}

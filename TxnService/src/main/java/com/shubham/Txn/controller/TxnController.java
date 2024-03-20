package com.shubham.Txn.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shubham.Txn.model.Txn;
import com.shubham.Txn.service.TxnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/txn")
public class TxnController {
    @Autowired
    private TxnService txnService;

    @PostMapping("/initTxn")
    public String createTxn(
            @RequestParam("amount") String amount,
            @RequestParam("receiver") String receiver,
            @RequestParam("purpose") String purpose
    ) throws JsonProcessingException {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return txnService.initTxn(details.getUsername(), receiver, purpose, Double.parseDouble(amount));
    }

    @GetMapping("/getTxnByPages")
    public List<Txn> getTxnByPages(
            @RequestParam("pageNo") int page,
            @RequestParam("pageSize") int size
    ){
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String senderId = details.getUsername();

        System.out.println(senderId);
        return txnService.getTxnByPages(senderId, page, size);
    }
}

package com.shubham.Txn.repository;

import com.shubham.Txn.model.Txn;
import com.shubham.Txn.model.TxnStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;



@Repository
public interface TxnRepository extends JpaRepository<Txn, Integer> {
    @Transactional
    @Modifying
    @Query("update Txn t set t.txnStatus=:status where t.txnId=:txnId")
    void updateStatus(TxnStatus status, String txnId);

    Page<Txn> findBySenderId(String sender, Pageable pageable);
}

package com.datantt.cuentabancaria.infraestructure.repositories;

import java.util.List;

import com.datantt.cuentabancaria.domain.dtos.TransferDTO;
import org.bson.Document;

import com.datantt.cuentabancaria.domain.dtos.BankAccountDto;
import com.datantt.cuentabancaria.domain.dtos.CustomerDto;
import com.datantt.cuentabancaria.domain.dtos.TransactionDto;

public interface BankAccountRepository {

    List<Document> getList(CustomerDto customerDTO);

    Document getDetail(String id);

    Boolean create(BankAccountDto bankAccountDTO);

    List<Document> getListTransaction(BankAccountDto bankAccountDTO);

    Boolean createTransaction(TransactionDto transactionDTO, Boolean hasCommision);

    Boolean createTransfer(TransferDTO transferDTO);

}

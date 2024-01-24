package com.datantt.cuentabancaria.domain.services;

import java.util.List;

import com.datantt.cuentabancaria.domain.dtos.BankAccountDto;
import com.datantt.cuentabancaria.domain.dtos.CustomerDto;
import com.datantt.cuentabancaria.domain.dtos.TransactionDto;

public interface BankAccountService {

    List<BankAccountDto> getList(CustomerDto customerDTO);

    Boolean create(BankAccountDto bankAccount);

    List<TransactionDto> getListTrasaction(BankAccountDto bankAccount);

    Boolean createTransaction(TransactionDto transactionDTO);

}

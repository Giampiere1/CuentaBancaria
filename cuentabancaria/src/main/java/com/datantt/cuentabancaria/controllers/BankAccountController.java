package com.datantt.cuentabancaria.controllers;

import static java.util.Objects.isNull;

import com.datantt.cuentabancaria.domain.dtos.BankAccountDto;
import com.datantt.cuentabancaria.domain.dtos.CustomerDto;
import com.datantt.cuentabancaria.domain.dtos.TransactionDto;
import com.datantt.cuentabancaria.domain.dtos.TransferDTO;
import com.datantt.cuentabancaria.domain.services.BankAccountService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * BankAccountController.
 */
@RequestMapping("/bankaccount")
@RestController
public class BankAccountController {

    @Autowired
    private BankAccountService bankAccountService;

    /**
     * getList.
     */
    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<?> getList(@RequestParam Map<String, String> params) {
        CustomerDto request = new CustomerDto();
        try {
            System.out.println("INICIO LISTADO");
            request.setId(params.get("CustomerId"));
            List<BankAccountDto> response = bankAccountService.getList(request);
            if (isNull(response)) {
                return ResponseEntity.noContent().build();
            }
            System.out.println("FIN LISTADO");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * create.
     */
    @PostMapping("/")
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody BankAccountDto bankAccountDto) {
        try {
            System.out.println("INICIO CREAR");
            Boolean response = bankAccountService.create(bankAccountDto);
            System.out.println("FIN CREAR");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/transaction")
    @ResponseBody
    public ResponseEntity<?> getListTrasaction(@RequestParam Map<String, String> params) {
        BankAccountDto request = new BankAccountDto();
        try {
            System.out.println("INICIO LISTADO");
            request.setId(params.get("BankAccountId"));
            List<TransactionDto> response = bankAccountService.getListTrasaction(request);
            if (isNull(response)) {
                return ResponseEntity.noContent().build();
            }
            System.out.println("FIN LISTADO");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/transaction")
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody TransactionDto transactionDTO) {
        try {
            System.out.println("INICIO CREAR");
            Boolean response = bankAccountService.createTransaction(transactionDTO);
            System.out.println("FIN CREAR");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/transfer")
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody TransferDTO transferDTO) {
        try {
            System.out.println("INICIO CREAR TRANSFERENCIA");
            Boolean response = bankAccountService.createTransfer(transferDTO);
            System.out.println("FIN CREAR TRANSFERENCIA");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

}

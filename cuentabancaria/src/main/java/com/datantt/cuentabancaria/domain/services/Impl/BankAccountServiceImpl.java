package com.datantt.cuentabancaria.domain.services.Impl;

import java.util.List;
import java.util.function.Predicate;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.datantt.cuentabancaria.domain.dtos.BankAccountDto;
import com.datantt.cuentabancaria.domain.dtos.CustomerDto;
import com.datantt.cuentabancaria.domain.dtos.TransactionDto;
import com.datantt.cuentabancaria.domain.dtos.TransferDTO;
import com.datantt.cuentabancaria.domain.services.BankAccountService;
import com.datantt.cuentabancaria.domain.util.Constantes;
import com.datantt.cuentabancaria.domain.util.Util;
import com.datantt.cuentabancaria.infraestructure.repositories.BankAccountRepository;

@Service
public class BankAccountServiceImpl implements BankAccountService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Value("${maxtransactions}")
    private Integer maxtransactions;

    @Value("${costocomision}")
    private Double costocomision;

    @Value("${minimoahorrovip}")
    private Double minimoahorrovip;

    @Override
    public List<BankAccountDto> getList(CustomerDto customerDTO) {
        List<Document> documents = bankAccountRepository.getList(customerDTO);
        List<BankAccountDto> result = Util.mapToBankAccountDto(documents);
        return result;
    }

    @Override
    public Boolean create(BankAccountDto bankAccount) {
        Boolean result;
        Boolean validate = validateCustomerList.test(bankAccount);
        if (!validate) {
            System.out.println("Error en validacion de cuenta bancaria.");
            return false;
        }
        if (bankAccount.getTypeCode().equalsIgnoreCase(Constantes.TIPOCLIENTE_PERSONAL)) {
            System.out.println("TIPOCLIENTE_PERSONAL");
            List<Document> documents = bankAccountRepository.getList(bankAccount.getCustomerList().get(0));
            List<BankAccountDto> accounts = Util.mapToBankAccountDto(documents);
            System.out.println("NUMERO DE CUENTAS");
            System.out.println(accounts.size());
            long numeroCuentaAhorro = accounts.stream().filter(filterAhorro).count();
            System.out.println("NUMERO DE CUENTAS AHORRO");
            System.out.println(numeroCuentaAhorro);
            long numeroCuentaCorriente = accounts.stream().filter(filterCorriente).count();
            System.out.println("NUMERO DE CUENTAS CORRIENTE");
            System.out.println(numeroCuentaCorriente);
            if (bankAccount.getAccountTypeCode().equalsIgnoreCase(Constantes.TIPOCUENTA_AHORRO)
                    && numeroCuentaAhorro >= 1) {
                System.out.println("Error en validacion de tipo cuenta ahorro.");
                return false;
            }
            if (bankAccount.getAccountTypeCode().equalsIgnoreCase(Constantes.TIPOCUENTA_CUENTACORRIENTE)
                    && numeroCuentaCorriente >= 1) {
                System.out.println("Error en validacion de tipo cuenta corriente.");
                return false;
            }
        } else if (bankAccount.getTypeCode().equalsIgnoreCase(Constantes.TIPOCLIENTE_PERSONALVIP)) {
            System.out.println("TIPOCLIENTE_PERSONALVIP");
            if (bankAccount.getAccountTypeCode().equalsIgnoreCase(Constantes.TIPOCUENTA_AHORRO)
                    && Double.parseDouble(bankAccount.getBalance()) >= minimoahorrovip) {
                System.out.println("Error en validacion de tipo cuenta ahorro.");
                return false;
            }
        } else if (bankAccount.getTypeCode().equalsIgnoreCase(Constantes.TIPOCLIENTE_EMPRESARIAL)) {
            System.out.println("TIPOCLIENTE_EMPRESARIAL");
            if (bankAccount.getAccountTypeCode().equalsIgnoreCase(Constantes.TIPOCUENTA_AHORRO)
                    || bankAccount.getAccountTypeCode().equalsIgnoreCase(Constantes.TIPOCUENTA_PLAZOFIJO)) {
                System.out.println("Error en validacion de tipo cuenta empresarial.");
                return false;
            }
        }
        result = bankAccountRepository.create(bankAccount);
        return result;
    }

    @Override
    public List<TransactionDto> getListTrasaction(BankAccountDto bankAccount) {
        List<Document> documents = bankAccountRepository.getListTransaction(bankAccount);
        List<TransactionDto> result = Util.mapToTransactionDto(documents);
        return result;
    }

    @Override
    public Boolean createTransaction(TransactionDto transactionDTO) {
        Boolean result;
        Boolean hasCommision;
        Document document = bankAccountRepository.getDetail(transactionDTO.getBankaccountId());
        BankAccountDto bankAccount = Util.mapToBankAccountDto(document);
        System.out.println("CUENTA BALANCE");
        System.out.println(bankAccount.getBalance());
        System.out.println("CUENTA NUMERO TRANSACCIONES");
        System.out.println(bankAccount.getNumberTransaction());
        if (Integer.parseInt(bankAccount.getNumberTransaction()) >= maxtransactions) {
            hasCommision = true;
        } else {
            hasCommision = false;
        }
        if (!(transactionDTO.getTypeTransaction().equalsIgnoreCase(Constantes.TIPOTRANSACCION_DEPOSITO)
                && Double.parseDouble(bankAccount.getBalance())
                + Double.parseDouble(transactionDTO.getAmount())
                - (hasCommision ? costocomision : 0) >= (bankAccount.getTypeCode()
                .equalsIgnoreCase(Constantes.TIPOCLIENTE_PERSONALVIP) ? minimoahorrovip : 0))
                && !(transactionDTO.getTypeTransaction().equalsIgnoreCase(Constantes.TIPOTRANSACCION_RETIRO)
                && Double.parseDouble(bankAccount.getBalance()) - Double.parseDouble(transactionDTO.getAmount())
                - (hasCommision ? costocomision : 0) >= (bankAccount.getTypeCode()
                .equalsIgnoreCase(Constantes.TIPOCLIENTE_PERSONALVIP) ? minimoahorrovip : 0))) {
            System.out.println("Error en validacion de transaccion.");
            return false;
        }
        result = bankAccountRepository.createTransaction(transactionDTO, hasCommision);
        return result;
    }

    @Override
    public Boolean createTransfer(TransferDTO transferDTO) {
        Boolean result;
        Document document = bankAccountRepository.getDetail(transferDTO.getBankaccountOriginId());
        BankAccountDto bankAccountOrigin = Util.mapToBankAccountDto(document);
        if (!(Double.parseDouble(bankAccountOrigin.getBalance())
                - Double.parseDouble(transferDTO.getAmount()) >= (bankAccountOrigin.getTypeCode()
                .equalsIgnoreCase(Constantes.TIPOCLIENTE_PERSONALVIP) ? minimoahorrovip : 0))) {
            System.out.println("Error en validacion de transferencia.");
            return false;
        }
        result = bankAccountRepository.createTransfer(transferDTO);
        return result;
    }

    Predicate<CustomerDto> validateCustomerBusiness = object -> object.getCustomerTypeBusinessCode()
            .equalsIgnoreCase(Constantes.TIPOCLIENTEEMPRESARIAL_TITULAR)
            || object.getCustomerTypeBusinessCode().equalsIgnoreCase(Constantes.TIPOCLIENTEEMPRESARIAL_FIRMANTE);

    Predicate<BankAccountDto> validateCustomerList = account -> Double.parseDouble(account.getBalance()) >= 0
            && ((account.getTypeCode()
            .equalsIgnoreCase(Constantes.TIPOCLIENTE_PERSONAL) && account.getCustomerList().size() == 1
            && (account.getAccountTypeCode().equalsIgnoreCase(Constantes.TIPOCUENTA_AHORRO)
            || account.getAccountTypeCode().equalsIgnoreCase(Constantes.TIPOCUENTA_CUENTACORRIENTE)
            || account.getAccountTypeCode().equalsIgnoreCase(Constantes.TIPOCUENTA_PLAZOFIJO)))
            || (account.getTypeCode().equalsIgnoreCase(Constantes.TIPOCLIENTE_EMPRESARIAL)
            && account.getCustomerList().size() >= 1
            && account.getCustomerList().stream().allMatch(validateCustomerBusiness)
            && account.getAccountTypeCode().equalsIgnoreCase(Constantes.TIPOCUENTA_CUENTACORRIENTE))
            || account.getTypeCode().equalsIgnoreCase(Constantes.TIPOCLIENTE_PERSONALVIP)
            || account.getTypeCode().equalsIgnoreCase(Constantes.TIPOCLIENTE_EMPRESARIALPYME));

    Predicate<BankAccountDto> filterAhorro = object -> object.getAccountTypeCode()
            .equalsIgnoreCase(Constantes.TIPOCUENTA_AHORRO);

    Predicate<BankAccountDto> filterCorriente = object -> object.getAccountTypeCode()
            .equalsIgnoreCase(Constantes.TIPOCUENTA_CUENTACORRIENTE);

}

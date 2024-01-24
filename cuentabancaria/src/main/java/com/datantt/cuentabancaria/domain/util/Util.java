package com.datantt.cuentabancaria.domain.util;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.datantt.cuentabancaria.domain.dtos.BankAccountDto;
import com.datantt.cuentabancaria.domain.dtos.TransactionDto;

public class Util {

    public static <T> List<T> mapTo(List<Document> documents, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        for (Document document : documents) {
            T obj = mapTo(document, clazz);
            result.add(obj);
        }
        return result;
    }

    public static <T> T mapTo(Document document, Class<T> clazz) {
        try {
            T obj = clazz.newInstance();
            for (String key : document.keySet()) {
                try {
                    Object value = document.get(key);
                    clazz.getDeclaredField(key).set(obj, value.toString());
                } catch (NoSuchFieldException ignored) {
                }
            }
            return obj;
        } catch (InstantiationException | IllegalAccessException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error al convertir el documento a objeto de clase " + clazz.getSimpleName(), e);
        }
    }

    public static List<BankAccountDto> mapToBankAccountDto(List<Document> documents) {
        List<BankAccountDto> result = new ArrayList<>();
        for (Document document : documents) {
            result.add(mapToBankAccountDto(document));
        }
        return result;
    }

    public static BankAccountDto mapToBankAccountDto(Document document) {
        return new BankAccountDto(
                document.get("_id").toString(),
                null,
                document.getString("TypeCode"),
                document.getString("AccountNumber"),
                document.getString("AccountTypeCode"),
                document.get("Balance").toString());
    }

    public static List<TransactionDto> mapToTransactionDto(List<Document> documents) {
        List<TransactionDto> result = new ArrayList<>();
        for (Document document : documents) {
            result.add(new TransactionDto(
                    document.get("_id").toString(),
                    document.get("BankAccount_Id").toString(),
                    document.getString("TypeTransaction"),
                    document.get("Amount").toString()));
        }
        return result;
    }

}

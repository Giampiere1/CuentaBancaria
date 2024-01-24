package com.datantt.cuentabancaria.infraestructure.repositories.impl;

import com.datantt.cuentabancaria.domain.dtos.BankAccountDto;
import com.datantt.cuentabancaria.domain.dtos.CustomerDto;
import com.datantt.cuentabancaria.domain.dtos.TransactionDto;
import com.datantt.cuentabancaria.domain.util.Constantes;
import com.datantt.cuentabancaria.domain.util.Util;
import com.datantt.cuentabancaria.infraestructure.repositories.BankAccountRepository;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;


@SuppressWarnings("checkstyle:Indentation")
@Repository
public class BankAccountRepositoryImpl implements BankAccountRepository {
  @Value("${connectionstring}")
  private String connectionString;

  @Value("${dbname}")
  private String dbname;

  @Value("${accountcollection}")
  private String accountcollection;

  @Value("${detailaccountcollection}")
  private String detailaccountcollection;

  @Value("${transactioncollection}")
  private String transactioncollection;

  @Override
  public List<Document> getList(CustomerDto customerDto) {
        System.out.println("CustomerDTO Id");
        System.out.println(customerDto.getId());
        List<Document> cuentasFiltradas = new ArrayList<>();
        MongoClient client = MongoClients.create(connectionString);
        MongoDatabase database = client.getDatabase(dbname);
        MongoCollection<Document> detalles = database.getCollection(detailaccountcollection);
        Document matchStage = new Document("$match",
                new Document("Id_Cliente", customerDto.getId()));
        Document lookupStage = new Document("$lookup",
                new Document("from", accountcollection)
                        .append("localField", "Id_Cuenta")
                        .append("foreignField", "_id")
                        .append("as", "cuenta"));
        Document unwindStage = new Document("$unwind", "$cuenta");
        Document finalProjectStage = new Document("$project",
                new Document("_id", 0)
                        .append("_id", "$cuenta._id")
                        .append("AccountNumber", "$cuenta.AccountNumber")
                        .append("AccountTypeCode", "$cuenta.AccountTypeCode")
                        .append("Balance", "$cuenta.Balance"));
        detalles.aggregate(Arrays.asList(
                matchStage, lookupStage, unwindStage, finalProjectStage)).forEach(document -> {
            System.out.println(document.toJson());
            cuentasFiltradas.add(document);
        });
        return cuentasFiltradas;
    }

  @Override
  public Document getDetail(String id) {
        MongoClient client = MongoClients.create(connectionString);
        MongoDatabase database = client.getDatabase(dbname);
        MongoCollection<Document> collection = database.getCollection(accountcollection);
        return collection.find(new Document("_id", new ObjectId(id))).first();
  }

  @Override
  public Boolean create(BankAccountDto bankAccountDto) {
        MongoClient client = MongoClients.create(connectionString);
        MongoDatabase database = client.getDatabase(dbname);
        MongoCollection<Document> collection = database.getCollection(accountcollection);
        Document document = new Document("AccountNumber", bankAccountDto.getAccountNumber())
                .append("AccountTypeCode", bankAccountDto.getAccountTypeCode())
                .append("TypeCode", bankAccountDto.getTypeCode())
                .append("Balance", 0);
        collection.insertOne(document);
        collection = database.getCollection(detailaccountcollection);
        System.out.println("DOCUMENTO CREADO");
        System.out.println(document.get("_id").toString());
        for (CustomerDto customerDto : bankAccountDto.getCustomerList()) {
            Document detail = new Document("Id_Cliente", customerDto.getId())
                    .append("Id_Cuenta", document.get("_id"))
                    .append("TypeBusinessCode", customerDto.getCustomerTypeBusinessCode());
            collection.insertOne(detail);
        }
        return true;
    }

  @Override
  public List<Document> getListTransaction(BankAccountDto bankAccountDto) {
        System.out.println("bankAccountDto Id");
        System.out.println(bankAccountDto.getId());
        List<Document> result = new ArrayList<>();
        MongoClient client = MongoClients.create(connectionString);
        MongoDatabase database = client.getDatabase(dbname);
        MongoCollection<Document> transacciones = database.getCollection(transactioncollection);
        FindIterable<Document> documents = transacciones
                .find(new Document("BankAccount_Id", new ObjectId(bankAccountDto.getId())));
        for (Document document : documents) {
            result.add(document);
        }
        return result;
    }

  @Override
  public Boolean createTransaction(TransactionDto transactionDto) {
        MongoClient client = MongoClients.create(connectionString);
        MongoDatabase database = client.getDatabase(dbname);
        MongoCollection<Document> collection = database.getCollection(transactioncollection);
        Document document =
                new Document("BankAccount_Id", new ObjectId(transactionDto.getBankaccountId()))
                .append("TypeTransaction", transactionDto.getTypeTransaction())
                .append("Amount", transactionDto.getAmount());
        collection.insertOne(document);
        collection = database.getCollection(accountcollection);
        document = getDetail(transactionDto.getBankaccountId());
        BankAccountDto account = Util.mapToBankAccountDto(document);
        System.out.println("balance antes");
        System.out.println(account.getBalance());
        Document replace = new Document("$set",
                new Document("Balance", Double.parseDouble(account.getBalance()) + Double
                        .parseDouble(transactionDto.getAmount())
                        * (transactionDto.getTypeTransaction()
                        .equalsIgnoreCase(Constantes.TIPOTRANSACCION_DEPOSITO) ? 1
                                : -1)));
        collection.updateOne(new Document("_id",
                new ObjectId(transactionDto.getBankaccountId())), replace);
        return true;
   }

}

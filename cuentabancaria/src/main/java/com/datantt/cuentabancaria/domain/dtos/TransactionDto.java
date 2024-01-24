package com.datantt.cuentabancaria.domain.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//codigo
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

  @JsonProperty("_id")
  public String id;

  @JsonProperty("BankAccount_Id")
  public String bankaccountId;

  @JsonProperty("TypeTransaction")
  public String typeTransaction;

  @JsonProperty("Amount")
  public String amount;

}
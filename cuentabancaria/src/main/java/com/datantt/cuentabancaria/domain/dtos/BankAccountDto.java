package com.datantt.cuentabancaria.domain.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountDto {

  @JsonProperty("_id")
  public String id;

  @JsonProperty("CustomerList")
  public List<CustomerDto> customerList;

  @JsonProperty("TypeCode")
  public String typeCode;

  @JsonProperty("AccountNumber")
  public String accountNumber;

  @JsonProperty("AccountTypeCode")
  public String accountTypeCode;

  @JsonProperty("Balance")
  public String balance;

}

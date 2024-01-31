package com.datantt.cuentabancaria.domain.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO {

    @JsonProperty("BankAccountOrigin_Id")
    public String bankaccountOriginId;

    @JsonProperty("BankAccountDestination_Id")
    public String bankaccountDestinationId;

    @JsonProperty("Amount")
    public String amount;

}
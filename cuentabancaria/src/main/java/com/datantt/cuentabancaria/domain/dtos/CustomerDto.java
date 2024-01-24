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
public class CustomerDto {

  @JsonProperty("_id")
  public String id;

  @JsonProperty("CustomerTypeBusinessCode")
  public String customerTypeBusinessCode;

}

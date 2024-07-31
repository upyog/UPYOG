package com.razorpay;

import org.json.JSONObject;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
public class Order extends Entity {

  public Order(JSONObject jsonObject) {
    super(jsonObject);
  }
}

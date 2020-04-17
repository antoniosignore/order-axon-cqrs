package com.signore.demo.order.rest;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class OrderValueBean {
    String orderId;
    String email;
}

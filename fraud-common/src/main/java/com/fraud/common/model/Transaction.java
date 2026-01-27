package com.fraud.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/*
This POJO will be blueprint for every single transaction that flow's through the system.
LifeCyle:
    fraud-producer service (creates a new transaction) ----> send it to Kafka ---> fraud-consumer will then recieve this
    created transaction and know exactly what's inside of it because they all have the same blueprint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction{

    private String transactionID;
    private String userId;
    private BigDecimal amount; //monetary val of transaction
    private String merchantId;
    private LocalDateTime timestamp;
    private String location;
    private String paymentMethod;

}
package com.tiv.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单
 */
@Data
public class Order implements Serializable {

    private Long id;

    private String name;

    private Integer price;
}

package com.lab1.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Duration;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalcRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer value;
    private FuncType funcType;
}

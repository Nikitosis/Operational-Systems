package com.lab1.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalcResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer value;
}

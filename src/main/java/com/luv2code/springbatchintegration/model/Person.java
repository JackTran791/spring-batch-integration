package com.luv2code.springbatchintegration.model;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Person  implements Serializable {
    private static final long serialVersionUID = 5794156324598913167L;

    private String firstName;
    private String lastName;
}

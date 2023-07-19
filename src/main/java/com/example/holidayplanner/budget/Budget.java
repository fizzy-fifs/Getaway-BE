package com.example.holidayplanner.budget;

import com.example.holidayplanner.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;


@Data
@Document(collection = "Budgets")
public class Budget {

    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    @NotBlank
    @NotEmpty
    @DBRef
    private User user;

    @JsonProperty
    @NotBlank
    @NotEmpty
    private double budgetUpperLimit;

    @JsonProperty
    @NotBlank
    @NotEmpty
    private double budgetLowerLimit;

    public Budget(User user, double budgetUpperLimit, double budgetLowerLimit) {
        this.user = user;
        this.budgetUpperLimit = budgetUpperLimit;
        this.budgetLowerLimit = budgetLowerLimit;
    }
}

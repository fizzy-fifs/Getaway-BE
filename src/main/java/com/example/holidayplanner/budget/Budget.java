package com.example.holidayplanner.budget;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;


@Data
@Document(collection = "Budgets")
public class Budget {

    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    private String userId;

    @JsonProperty
    private String holidayId;

    @JsonProperty
    private double budgetUpperLimit;

    @JsonProperty
    private double budgetLowerLimit;


    public Budget(String userId, double budgetUpperLimit, double budgetLowerLimit) {
        this.userId = userId;
        this.budgetUpperLimit = budgetUpperLimit;
        this.budgetLowerLimit = budgetLowerLimit;
    }
}

package com.example.holidayplanner.models.group;

import com.example.holidayplanner.models.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Data
@Document(collection = "Reported Groups")
public class ReportGroup {
    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    @DBRef
    private Group group;

    @JsonProperty
    @DBRef
    private User user;

    @JsonProperty
    private String reason;

    @JsonProperty
    private LocalDateTime dateReported;

    public ReportGroup() {
    }

    public ReportGroup(Group group, User user, String reason, LocalDateTime dateReported) {
        this.group = group;
        this.user = user;
        this.reason = reason;
        this.dateReported = dateReported;
    }
}

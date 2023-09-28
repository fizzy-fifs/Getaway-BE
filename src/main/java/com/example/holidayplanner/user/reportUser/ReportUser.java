package com.example.holidayplanner.user.reportUser;

import com.example.holidayplanner.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Data
@Document(collection = "Reported Users")
public class ReportUser {
    @MongoId(value = FieldType.OBJECT_ID)
    @JsonProperty
    private String id;

    @JsonProperty
    @DBRef
    private User userToReport;

    @JsonProperty
    @DBRef
    private User userReporting;

    @JsonProperty
    private String reason;

    @JsonProperty
    private LocalDateTime dateReported;

    public ReportUser() {
    }

    public ReportUser(User userToReport, User userReporting, String reason, LocalDateTime dateReported) {
        this.userToReport = userToReport;
        this.userReporting = userReporting;
        this.reason = reason;
        this.dateReported = dateReported;
    }
}
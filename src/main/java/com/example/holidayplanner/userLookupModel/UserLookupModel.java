package com.example.holidayplanner.userLookupModel;

import lombok.Data;

import java.util.List;

@Data
public class UserLookupModel {

    public List<String> phoneNumbers;

    public  List<String> emails;

    public UserLookupModel() {}

    public UserLookupModel(List<String> phoneNumbers, List<String> emails) {
        this.phoneNumbers = phoneNumbers;
        this.emails = emails;
    }
}

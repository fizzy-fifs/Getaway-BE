package com.example.holidayplanner.config.mongoConfiguration;

import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MongoShutdownHook implements DisposableBean {

    @Autowired
    private MongoClient mongoClient;

    @Override
    public void destroy() {
        mongoClient.close();
    }
}

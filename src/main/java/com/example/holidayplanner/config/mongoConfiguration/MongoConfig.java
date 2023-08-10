package com.example.holidayplanner.config.mongoConfiguration;

//import com.example.holidayplanner.config.cascadeSaveMongoEventListener.CascadeSaveMongoEventListener;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.util.Objects;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClientFactoryBean mongo( ) throws Exception {
        MongoClientFactoryBean mongo = new MongoClientFactoryBean();

        ConnectionString conn = new ConnectionString(System.getenv("MONGODB_CONNECTION_STRING"));

        mongo.setSingleton(false);
        mongo.setConnectionString(conn);

        MongoClient client = mongo.getObject();
        assert client != null;
        client.listDatabaseNames()
                .forEach(System.out::println)
        ;

        return mongo;
    }

    @Bean
    public MongoClient mongoClient() throws Exception {
        return MongoClients.create(new ConnectionString(System.getenv("MONGODB_CONNECTION_STRING")));
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory( mongoClient(), Objects.requireNonNull(mongoClient().listDatabaseNames().first()));
        return new MongoTemplate(factory);
    }

//    @Bean
//    public CascadeSaveMongoEventListener cascadingMongoEventListener() {
//        return new CascadeSaveMongoEventListener();
//    }

}

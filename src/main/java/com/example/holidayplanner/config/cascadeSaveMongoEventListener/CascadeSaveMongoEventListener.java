package com.example.holidayplanner.config.cascadeSaveMongoEventListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

@Component
class CascadeSaveMongoEventListener extends AbstractMongoEventListener<Object> {
    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object source = event.getSource();

        ReflectionUtils.doWithFields(source.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                ReflectionUtils.makeAccessible(field);
                if (field.isAnnotationPresent(DBRef.class) && field.isAnnotationPresent(CascadeSave.class)) {

                    Object fieldValue = field.get(source);

                    if (fieldValue instanceof List<?>) {
                        for (Object item : (List<?>) fieldValue) {
                            checkAndSave(item);
                        }
                    } else {
                        checkAndSave(fieldValue);
                    }
                }
            }
        });
    }

    private void checkAndSave(Object fieldValue) {
        System.out.println("fieldValue = " + fieldValue);
        try {
            DbRefFieldCallback callback = new DbRefFieldCallback(fieldValue);
            ReflectionUtils.doWithFields(fieldValue.getClass(), callback);
            if (!callback.isIdFound && callback.id == null) {
                mongoTemplate.insert(fieldValue);
            }

            if (callback.id != null) {
                var findById = mongoTemplate.exists(new Query(Criteria.where("_id").is(callback.id)), fieldValue.getClass());
                if (findById) {
                    mongoTemplate.save(fieldValue);
                } else {
                    mongoTemplate.insert(fieldValue);
                }
            }
        } catch (Exception e) {
            System.out.println("fieldValue that threw exception = " + fieldValue);
            e.printStackTrace();
        }
    }

    private static class DbRefFieldCallback implements ReflectionUtils.FieldCallback {

        private final Object fieldValue;
        public boolean isIdFound = false;
        public String id;

        public DbRefFieldCallback(Object fieldValue) {
            this.fieldValue = fieldValue;
        }

        @Override
        public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
            ReflectionUtils.makeAccessible(field);
            if (field.isAnnotationPresent(MongoId.class)) {
                this.isIdFound = true;
                id = Objects.requireNonNull(ReflectionUtils.getField(field, fieldValue)).toString();
            }
        }
    }
}

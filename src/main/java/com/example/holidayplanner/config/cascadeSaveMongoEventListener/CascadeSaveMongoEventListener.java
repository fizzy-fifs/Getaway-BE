package com.example.holidayplanner.config.cascadeSaveMongoEventListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class CascadeSaveMongoEventListener extends AbstractMongoEventListener<Object> {
    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        var source = event.getSource();
        ReflectionUtils.doWithFields(source.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                ReflectionUtils.makeAccessible(field);
                if (field.isAnnotationPresent(CascadeSave.class)) {
                    var fieldValue = field.get(source);

                    if (fieldValue != null) {
                        DbRefFieldCallback callback = new DbRefFieldCallback();
                        ReflectionUtils.doWithFields(fieldValue.getClass(), callback);
                        if (!callback.isIdFound()) {
                            throw new IllegalArgumentException("Cannot perform cascade save on child object without id set");
                        }
                    }
                    mongoOperations.save(fieldValue);
                }
            }
        });
    }

    private static class DbRefFieldCallback implements ReflectionUtils.FieldCallback {
        private boolean idFound;

        @Override
        public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
            ReflectionUtils.makeAccessible(field);
            if (field.isAnnotationPresent(MongoId.class)) {
                idFound = true;
            }
        }

        public boolean isIdFound() {
            return idFound;
        }
    }
}
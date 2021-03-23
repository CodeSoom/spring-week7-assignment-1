package com.codesoom.assignment.dependency;

import com.codesoom.assignment.domain.EncryptionService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Container {
    public static EncryptionService encryptionService() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
            "user-context.xml"
        );
        return context.getBean(EncryptionService.class);
    }
}

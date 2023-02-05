/*******************************************************************************
 * (C) Copyright Gaff Software Team 2019, All Rights Reserved.
 * 
 * File: SpringProvider.java
 ******************************************************************************/
package page.foliage.common.ioc;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import page.foliage.common.util.ResourceUtils;


/**
 * The instance provider what is based on Spring framework.
 * 
 * @author liuzheng@gcsoftware.com
 * @version 1.0.0
 */
public class SpringProvider implements InstanceProvider {

    // -------------------------------------------------------------- ATTRIBUTE

    private final ApplicationContext context;

    // ------------------------------------------------------------ CONSTRUCTOR

    public SpringProvider(ApplicationContext context) {
        this.context = context;
    }

    public static SpringProvider withFileSystem(String path) {
        return new SpringProvider(new FileSystemXmlApplicationContext(path));
    }

    public static SpringProvider withClassPath(String path) {
        return new SpringProvider(new ClassPathXmlApplicationContext(path));
    }

    // ----------------------------------------------------------------- METHOD

    @Override
    public <T> T getInstance(Class<T> clazz) {
        return context.getBean(clazz);
    }

    @Override
    public <T> T getInstance(Class<T> clazz, Object... arguments) {
        return context.getBean(clazz, arguments);
    }

    @Override
    public <T> T getInstance(String bean, Class<T> clazz) {
        return context.getBean(bean, clazz);
    }

    @Override
    public <T> Map<String, T> getInstances(Class<T> clazz) {
        return context.getBeansOfType(clazz);
    }

    @Override
    public Map<String, Object> getInstancesByAnnotation(Class<? extends Annotation> clazz) {
        return context.getBeansWithAnnotation(clazz);
    }

    // ----------------------------------------------------------------- METHOD

    @Override
    public void close() throws Exception {
        ResourceUtils.safeClose(context);
    }

}

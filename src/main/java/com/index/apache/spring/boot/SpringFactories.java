package com.index.apache.spring.boot;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * @ClassName SpringFactories
 * @Description 复制了 {@link SpringApplication} 中获取 META-INF/spring.factories 的代码
 * 直接调用可以获得 spring.factories 中目标接口的实现类
 * @Author xiaoxuezhi
 * @DATE 2020/3/26 17:36
 * @Version 1.0
 * <p>
 * 源码
 * @see SpringApplication
 * @see SpringFactoriesLoader
 * <p>
 * 扩展
 * @see com.index.apache.autumn.boot.AutumnFactories
 * @see com.index.apache.autumn.core.io.support.ResourceLoader
 **/
public final class SpringFactories {


    public static <T> Collection<T> getSpringFactoriesInstances(Class<T> type) {
        return getSpringFactoriesInstances(type, new Class<?>[]{});
    }

    public static <T> Collection<T> getSpringFactoriesInstances(Class<T> type,
                                                                Class<?>[] parameterTypes,
                                                                Object... args) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        // Use names and ensure unique to protect against duplicates
        Set<String> names = new LinkedHashSet<>(
                SpringFactoriesLoader.loadFactoryNames(type, classLoader));
        List<T> instances = createSpringFactoriesInstances(type, parameterTypes,
                classLoader, args, names);
        AnnotationAwareOrderComparator.sort(instances);
        return instances;
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> createSpringFactoriesInstances(Class<T> type,
                                                              Class<?>[] parameterTypes,
                                                              ClassLoader classLoader,
                                                              Object[] args,
                                                              Set<String> names) {
        List<T> instances = new ArrayList<>(names.size());
        for (String name : names) {
            try {
                Class<?> instanceClass = ClassUtils.forName(name, classLoader);
                Assert.isAssignable(type, instanceClass);
                Constructor<?> constructor = instanceClass
                        .getDeclaredConstructor(parameterTypes);
                T instance = (T) BeanUtils.instantiateClass(constructor, args);
                instances.add(instance);
            } catch (Throwable ex) {
                throw new IllegalArgumentException(
                        "Cannot instantiate " + type + " : " + name, ex);
            }
        }
        return instances;
    }

    private SpringFactories() {

    }
}
                                                  
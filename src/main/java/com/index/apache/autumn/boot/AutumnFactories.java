package com.index.apache.autumn.boot;

import com.index.apache.autumn.core.io.support.ResourceLoader;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * @ClassName AutumnFactories
 * @Description 对 {@link SpringApplication} 中获取 META-INF/spring.factories 的代码进行扩展
 * 传入资源文件路径参数，调用可以获得相应文件中目标接口的实现类
 * 实现目标实质上等同于 ServiceLoader 的 SPI 实现
 * @Author xiaoxuezhi
 * @DATE 2020/3/26 17:36
 * @Version 1.0
 * <p>
 * 源码
 * @see SpringApplication
 * @see SpringFactoriesLoader
 **/
public final class AutumnFactories {

    public static <T> Collection<T> getAutumnFactoriesInstances(Class<T> type,
                                                                String resourceLocation) {
        return getAutumnFactoriesInstances(type, new Class<?>[]{}, resourceLocation);
    }

    public static <T> Collection<T> getAutumnFactoriesInstances(Class<T> type,
                                                                Class<?>[] parameterTypes,
                                                                String resourceLocation,
                                                                Object... args) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        // Use names and ensure unique to protect against duplicates
        Set<String> names = new LinkedHashSet<>(
                ResourceLoader.loadFactoryNames(type, classLoader, resourceLocation));
        List<T> instances = createAutumnFactoriesInstances(type, parameterTypes,
                classLoader, args, names);
        AnnotationAwareOrderComparator.sort(instances);
        return instances;
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> createAutumnFactoriesInstances(Class<T> type,
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

    private AutumnFactories() {

    }
}
                                                  
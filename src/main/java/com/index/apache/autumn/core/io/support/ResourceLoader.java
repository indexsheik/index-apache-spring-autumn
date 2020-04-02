package com.index.apache.autumn.core.io.support;

import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @Class: ResourceLoader
 * @Description: 资源加载, copy {@link SpringFactoriesLoader} 代码, 资源路径改为使用传参的方式进行扩展
 * @Author: Xiao Xuezhi
 * @Date: 2020/3/26 21:21
 * @Version： 1.0
 * @see SpringFactoriesLoader
 */
public final class ResourceLoader {

    private static final Map<ClassLoader, MultiValueMap<String, String>> cache = new ConcurrentReferenceHashMap<>();

    public static List<String> loadFactoryNames(Class<?> factoryClass,
                                                @Nullable ClassLoader classLoader,
                                                String resourceLocation) {
        String factoryClassName = factoryClass.getName();
        return loadResource(classLoader, resourceLocation).getOrDefault(factoryClassName, Collections.emptyList());
    }

    public static Map<String, List<String>> loadResource(@Nullable ClassLoader classLoader,
                                                         String resourceLocation) {
        MultiValueMap<String, String> result = cache.get(classLoader);
        if (result != null) {
            return result;
        }

        try {
            Enumeration<URL> urls = (classLoader != null ?
                    classLoader.getResources(resourceLocation) :
                    ClassLoader.getSystemResources(resourceLocation));
            result = new LinkedMultiValueMap<>();
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                UrlResource resource = new UrlResource(url);
                Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                for (Map.Entry<?, ?> entry : properties.entrySet()) {
                    List<String> factoryClassNames = Arrays.asList(
                            StringUtils.commaDelimitedListToStringArray((String) entry.getValue()));
                    result.addAll((String) entry.getKey(), factoryClassNames);
                }
            }
            cache.put(classLoader, result);
            return result;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to load factories from location [" +
                    resourceLocation + "]", ex);
        }
    }

    private ResourceLoader() {

    }
}

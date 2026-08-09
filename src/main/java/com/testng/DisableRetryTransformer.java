package com.testng;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Annotation transformer that explicitly disables retry for every test method,
 * overriding any retry analyzer set by other listeners (e.g., allure-testng).
 */
public class DisableRetryTransformer implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass,
                          Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(NoRetryAnalyzer.class);
    }
}

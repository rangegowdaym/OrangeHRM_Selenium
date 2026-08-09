package com.testng;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/** Retry analyzer that always returns {@code false} — tests never retry. */
public class NoRetryAnalyzer implements IRetryAnalyzer {

    @Override
    public boolean retry(ITestResult result) {
        return false;
    }
}

package functional.tests.core.extensions;

import functional.tests.core.log.LoggerBase;
import org.testng.IConfigurationListener;
import org.testng.ITestResult;

/**
 *
 */
public class IConfigurationListenerCustom implements IConfigurationListener {
    private static LoggerBase loggerBase = LoggerBase.getLogger("TEST");

    @Override
    public void onConfigurationSuccess(ITestResult iTestResult) {

    }

    @Override
    public void onConfigurationFailure(ITestResult iTestResult) {
        if (!iTestResult.getMethod().isTest() && !iTestResult.isSuccess()) {
            loggerBase.debug(iTestResult.getMethod().getMethodName());
            loggerBase.debug(iTestResult.getTestName());
            iTestResult.setStatus(ITestResult.FAILURE);
        }

        System.out.println("on configuration failure");

    }

    @Override
    public void onConfigurationSkip(ITestResult iTestResult) {
        if (!iTestResult.getMethod().isTest() && !iTestResult.isSuccess()) {
            loggerBase.debug(iTestResult.getMethod().getMethodName());
            loggerBase.debug(iTestResult.getTestName());
            iTestResult.setStatus(ITestResult.FAILURE);
        }

        System.out.println("on configuration skip");

    }
}


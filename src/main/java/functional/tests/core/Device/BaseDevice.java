package functional.tests.core.Device;

import functional.tests.core.Device.Android.Adb;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Exceptions.AppiumException;
import functional.tests.core.Exceptions.DeviceException;
import functional.tests.core.Exceptions.UnknownPlatformException;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.AppiumDriver;
import org.testng.Assert;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class BaseDevice {
    private IDevice _device;

    private static List<String> uninstallAppsList() {
        return Arrays.asList("org.nativescript", "com.telerik");
    }

    public BaseDevice(AppiumDriver<?> driver) {
        this._device = DeviceManager.getDevice(driver);
    }

    public IDevice getDevice() {
        return this._device;
    }

    public void initDevice() throws UnknownPlatformException, TimeoutException, InterruptedException, DeviceException {
        this._device.initDevice();
    }

    public void stopDevice() throws UnknownPlatformException {
        this._device.stopDevice();
    }

    public void initTestApp() throws IOException {
        this._device.uninstallApps(uninstallAppsList());
    }

    public void stopTestApp() {
        this._device.stopApps(uninstallAppsList());
    }

    public void pushFile(String deviceId, String localPath, String remotePath) throws Exception {
        this._device.pushFile(deviceId, localPath, remotePath);
    }

    public void pullFile(String deviceId, String remotePath, String destinationFolder) throws Exception {
        this._device.pullFile(deviceId, remotePath, destinationFolder);
    }

    public void pullFile(String deviceId, String remotePath) throws Exception {
        this._device.pullFile(deviceId, remotePath);
    }

    public void cleanConsoleLog() {
        this._device.cleanConsoleLog();
    }

    public void writeConsoleLogToFile(String fileName) throws IOException {
        this._device.writeConsoleLogToFile(fileName);
    }

    public void assertLogContains(String str) throws IOException {
        String logContent = this.getLogContent();

        Assert.assertTrue(logContent.contains(str), "The log does not contain '" + str + "'.");
        Log.info("The log contains '" + str + "'.");
    }

    public void assertLogNotContains(String str) throws IOException {
        String logContent = this.getLogContent();

        Assert.assertFalse(logContent.contains(str), "The log contains '" + str + "'.");
        Log.info("The log does not contains '" + str + "'.");
    }

    public void verifyAppRunning(String deviceId, String appId) throws AppiumException, IOException {
        this._device.verifyAppRunning(deviceId, appId);
    }

    public boolean isAppRunning(String deviceId, String appId) {
        return this._device.isAppRunning(deviceId, appId);
    }

    public String getStartupTime(String appId) throws IOException {
        return this._device.getStartupTime(appId);
    }

    private String getLogContent() throws IOException {
        String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
        this.writeConsoleLogToFile(testName);
        String logContent = this._device.getContent(testName);

        return logContent;
    }
}

## Locators

If you want to locate button with Appium you should do something like this:

Android:
```
    driver.findElement(By.className("android.widget.Button"));
```

iOS 8 and 9:
```
    driver.findElement(By.className("UIAButton")); 
```

iOS 10+:
```
    driver.findElement(By.className("XCUIElementTypeButton"));
```

With this framework and its cross-platform-locators you can do it like this:
```
    this.find.byLocator(this.locators.buttonLocator());
```
...and it will work everywhere.   

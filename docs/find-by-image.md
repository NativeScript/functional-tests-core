## Find by Image

Android and iOS accessibility models have limits and there are things that can not be automated with Appium simple because they are nto accessible.  
Examples: Games, Charts, everything rendered in canvas.

In such cases we have mechanism to locate element via image or OCR via `findImageOnScreen` and `findTextOnScreen` methods:
```
    UIRectangle rectangle1 = this.testContext.sikuliImageProcessing.findImageOnScreen("imageName", 1.0D);
    rectangle1.tap();
    UIRectangle rectangle2 = this.testContext.sikuliImageProcessing.findTextOnScreen("text");
    rectangle2.tap();
```


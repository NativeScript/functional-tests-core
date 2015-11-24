package common.Screenshot;

import java.awt.image.BufferedImage;

public class ImageVerificationResult {
    public final int diffPixels;
    public final double diffPercent;
    public final String actualSuffix = "actual";
    public final String diffSuffix = "diff";
    public final String expectedSuffix = "expected";
    public final BufferedImage actualImage;
    public final BufferedImage diffImage;
    public final BufferedImage expectedImage;

    public ImageVerificationResult(int diffPixels, double diffPercent,
                                   BufferedImage actualImage,
                                   BufferedImage diffImage,
                                   BufferedImage expectedImage) {
        this.diffPixels = diffPixels;
        this.diffPercent = diffPercent;
        this.actualImage = actualImage;
        this.diffImage = diffImage;
        this.expectedImage = expectedImage;
    }


}
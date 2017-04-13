package functional.tests.core.extensions;

import functional.tests.core.mobile.basetest.MobileContext;
import functional.tests.core.mobile.element.UIElement;
import org.openqa.selenium.By;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Provides ability to scroll in list like control from one element to another searching for element by text.
 */
public abstract class ScrollableListObject {

    private MobileContext context;

    private final Map<String, Rectangle> cashedUIElements = new HashMap<>();
    private final java.util.List<UIElement> elements = new ArrayList<>();
    private int retriesCount;
    private int timeOut;
    private Predicate<? super UIElement> excludeElementsFilter;
    private By itemSubElement;

    private Function<String, By> specificElementLocator;

    public ScrollableListObject(MobileContext context) {
        this.context = context;
        this.retriesCount = 10;
        this.timeOut = 2;
    }

    /**
     * The container that contains all  items.
     *
     * @return
     */
    public abstract String getMainContainerLocatorName();

    /**
     * Items that returns text.
     *
     * @return
     */
    public abstract String getMainContainerItemsName();

    public void setRetriesCount(int retriesCount) {
        this.retriesCount = retriesCount;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    /**
     * Set sub element that contains text of list view item.
     *
     * @param itemSubElement
     */
    public void setItemSubElement(By itemSubElement) {
        this.itemSubElement = itemSubElement;
    }

    /**
     * Set filter of items in main container that should be included in main list.
     *
     * @param excludeElements
     */
    public void setExcludeElementsFilter(Predicate<? super UIElement> excludeElements) {
        this.excludeElementsFilter = excludeElements;
    }

    /**
     * Returns locator for all elements.
     *
     * @return
     */
    public By getMainContainerItemsLocator() {
        return By.xpath("//" + this.getMainContainerLocatorName() + "//" + this.getMainContainerItemsName());
    }

    /**
     * Set specific locator for item in list view.
     */
    public void setSpecificElementLocator(Function<String, By> specificElementLocator) {
        this.specificElementLocator = specificElementLocator;
    }

    /**
     * Get all items.
     */
    public void loadItems() {
        if (this.elements != null) {
            this.elements.clear();
        }

        if (this.cashedUIElements != null) {

            this.cashedUIElements.clear();
        }

        java.util.List<UIElement> listOfElements = this.context.wait.forVisibleElements(this.getMainContainerItemsLocator(), this.timeOut, false);
        if (this.excludeElementsFilter != null) {
            listOfElements.removeIf(this.excludeElementsFilter);
        }


        listOfElements.forEach(e -> {
            Rectangle rect = e.getUIRectangle();
            if (this.itemSubElement != null) {
                UIElement el;
                try {
                    el = e.findElement(this.itemSubElement);
                    String text = el.getText();
                    if (!this.cashedUIElements.containsKey(text)) {
                        this.cashedUIElements.put(text, rect);
                    }
                } catch (Exception ex) {

                }

            } else {
                this.cashedUIElements.put(e.getText(), rect);
            }
            this.elements.add(e);
        });
    }

    public Rectangle scrollTo(String example) {
        UIElement element = null;
        if (this.specificElementLocator != null) {
            element = this.context.wait.waitForVisible(this.specificElementLocator.apply(example), 2, false);
        }
        if (element != null) {
            return element.getUIRectangle();
        } else {
            this.loadItems();
            Rectangle el = this.getUiElementFromCache(example);
            int counter = 0;
            while (el == null && counter < this.retriesCount) {
                if (this.elements == null || this.elements.size() == 0) {
                    this.loadItems();
                    el = this.getUiElementFromCache(example);
                    if (el != null) {
                        break;
                    }
                }
                Rectangle firstElement = this.elements.get(0).getUIRectangle();
                this.context.gestures.scrollTo(
                        (int) firstElement.getWidth() / 2,
                        (int) this.elements.get(this.elements.size() - 1).getUIRectangle().getY(),
                        (int) firstElement.getWidth() / 2,
                        (int) firstElement.getY());

                this.loadItems();
                el = this.getUiElementFromCache(example);

                counter++;
            }

            return el;
        }
    }

    private Rectangle getUiElementFromCache(String key) {
        Rectangle rect = this.cashedUIElements.get(key);

        if (rect == null) {
            for (String k :
                    this.cashedUIElements.keySet()) {
                if (k.toLowerCase().compareTo(key.toLowerCase()) == 0) {
                    return this.cashedUIElements.get(k);
                }
            }
        }

        return rect;
    }
}

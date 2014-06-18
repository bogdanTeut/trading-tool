package metatrader;

import indicators.AdxIndicator;
import org.testng.Assert;
import org.testng.annotations.Test;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: bogdan.teut
 * Date: 18/06/14
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
public class MetaTraderServiceTest {

    @Test
    public void testGetPrice(){
        MetaTraderService metaTraderServiceTest = new MetaTraderService();
        double price = metaTraderServiceTest.getPrice();
        assertNotEquals(price, 0);
    }

    @Test
    public void testGetPsar(){
        MetaTraderService metaTraderServiceTest = new MetaTraderService();
        double psar = metaTraderServiceTest.getPsar();
        assertNotEquals(psar, 0);
    }

    @Test
    public void testGetAdx(){
        MetaTraderService metaTraderServiceTest = new MetaTraderService();
        AdxIndicator adxIndicator = metaTraderServiceTest.getAdx();
        assertNotNull(adxIndicator);
    }

    @Test
    public void testGetRsi(){
        MetaTraderService metaTraderServiceTest = new MetaTraderService();
        double rsi = metaTraderServiceTest.getRsi();
        assertNotEquals(rsi, 0);
    }

    @Test
    public void testDoOrder(){
        MetaTraderService metaTraderServiceTest = new MetaTraderService();
        metaTraderServiceTest.doOrder();
    }


}

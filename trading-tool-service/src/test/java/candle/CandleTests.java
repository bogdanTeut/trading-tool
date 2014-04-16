package candle;

import metatrader.MetaTraderService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.BDDMockito.given;

/**
 * Created with IntelliJ IDEA.
 * User: bogdan.teut
 * Date: 16/04/14
 * Time: 16:07
 * To change this template use File | Settings | File Templates.
 */
public class CandleTests {

    final int TIME_UNIT = 60;

    @Mock
    MetaTraderService metaTraderService;

    @InjectMocks
    private Candle candle;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLifeTime (){
        Candle candle = new Candle();
        candle.setStopTime();
        Assert.assertEquals(TIME_UNIT, Math.round((float) candle.lifeTime() / 1000));
    }

    @Test
    public void testStarEndPrice (){

        given(metaTraderService.getPrice()).willReturn(10.1034, 10.1039);

        candle.start();
        candle.stop();

        Assert.assertEquals(candle.startPrice, 10.1034);
        Assert.assertEquals(candle.stopPrice, 10.1039);
    }


//    @Test
//    public void doATest(){
//        long stopTime = 1397337720093L;
//        long startTime = 1397337660094L;
//        Math.round( (float)(stopTime - startTime)/1000);
//    }
}

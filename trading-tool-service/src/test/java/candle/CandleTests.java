package candle;

import metatrader.MetaTraderService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import watch.Watch;

import java.util.ArrayList;

import static org.mockito.BDDMockito.given;

/**
 * Created with IntelliJ IDEA.
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
    public void testStart (){

        given(metaTraderService.getPrice()).willReturn(10.1034);
        candle.start();

        Assert.assertEquals(10.1034, candle.startPrice);
        Assert.assertEquals(System.currentTimeMillis(), candle.startTime);
    }

    @Test
    public void testStartEndPrice (){
        given(metaTraderService.getPrice()).willReturn(10.1034, 10.1039);

        candle.start();
        candle.stop(0);

        Assert.assertEquals(candle.startPrice, 10.1034);
        Assert.assertEquals(candle.stopPrice, 10.1039);
    }

    @Test
    public void testStopWithParameter (){
        long stopTime = candle.getStopTime();

        candle.start();
        candle.stop(stopTime);

        Assert.assertTrue(candle.startTime != 0);
        Assert.assertTrue(candle.stopTime != 0);
        Assert.assertEquals(TIME_UNIT, Math.round( (float)(candle.lifeTime())/1000));
    }

    @Test
    public void testStopWithoutParameter (){
        candle.start();
        candle.stop(0);

        Assert.assertTrue(candle.startTime != 0);
        Assert.assertTrue(candle.stopTime != 0);
        Assert.assertEquals(0, Math.round( (float)(candle.lifeTime())/1000));
    }

    @Test
    public void testBoolishOrBearish (){
        given(metaTraderService.getPrice()).willReturn(10.1034, 10.1039, 10.1036, 10.1034, 10.1035, 10.1035);

        candle.start();
        candle.stop(0);
        Assert.assertEquals(candle.type, CandleEnum.BULLISH);

        candle.start();
        candle.stop(0);
        Assert.assertEquals(candle.type, CandleEnum.BEARISH);

        candle.start();
        candle.stop(0);
        Assert.assertEquals(candle.type, CandleEnum.NEUTRAL);
    }

    @Test
    public void testLifeTime (){
        long startTime = 1397337720093L;
        long stopTime = 1397337720093L + 50000;
        candle.startTime = startTime;
        candle.stopTime = stopTime;

        Assert.assertEquals(50, Math.round((float) candle.lifeTime() / 1000));
    }

    @Test
    public void testGetStopTime (){
        long startTime = 1397337720093L;
        candle.start();
        candle.startTime = startTime;

        Assert.assertEquals(startTime + 1000 * TIME_UNIT, candle.getStopTime());
    }

    @Test
     public void testGetPsar (){
        given(metaTraderService.getPsar()).willReturn(10.1034);
        candle.start();
        candle.stop(0);

        Assert.assertEquals(candle.getPsar(), 10.1034);
    }

    @Test
    public void testGetPsarRevertEvent(){
        given(metaTraderService.getPsar()).willReturn(10.1020);

        Candle prevCandle = new Candle(null);
        prevCandle.setPsar (10.1034);
        candle.getWatch() = new Watch();
        .candles() = new ArrayList<Candle>();
        .add(prevCandle);

        candle.start();
        candle.stop(0);

        Assert.assertEquals(candle.isPsarEventRevert(), true);

    }


//    @Test
//    public void doATest(){
//        long stopTime = 1397337720093L;
//        long startTime = 1397337660094L;
//        Math.round( (float)(stopTime - startTime)/1000);
//    }
}

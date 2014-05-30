package candle;

import indicators.AdxIndicator;
import metatrader.MetaTraderService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import org.testng.collections.Lists;
import watch.Watch;

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

    @Mock
    Watch watch;

    @InjectMocks
    private Candle candle;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testStartTime (){

        given(metaTraderService.getPrice()).willReturn(10.1034);
        candle.start();

        AssertJUnit.assertEquals(System.currentTimeMillis(), candle.startTime);
    }

    @Test
    public void testStartStopPrice (){
        given(metaTraderService.getPrice()).willReturn(10.1034, 10.1039);

        candle.start();
        candle.stop(0);

        AssertJUnit.assertEquals(candle.startPrice, 10.1034);
        AssertJUnit.assertEquals(candle.stopPrice, 10.1039);
    }

    @Test
    public void testStopTimeWithParameter (){
        given(metaTraderService.getPrice()).willReturn(10.1034, 10.1039);
        long stopTime = candle.getStopTime();

        candle.start();
        candle.stop(stopTime);

        AssertJUnit.assertTrue(candle.stopTime != 0);
        AssertJUnit.assertEquals(TIME_UNIT, Math.round( (float)(candle.lifeTime())/1000));
    }

    @Test
    public void testStopTimeWithoutParameter (){
        given(metaTraderService.getPrice()).willReturn(10.1034, 10.1039);
        candle.start();
        candle.stop(0);

        AssertJUnit.assertTrue(candle.stopTime != 0);
        AssertJUnit.assertEquals(0, Math.round( (float)(candle.lifeTime())/1000));
    }

    @Test
    public void testBoolishOrBearish (){
        given(metaTraderService.getPrice()).willReturn(10.1034, 10.1039, 10.1036, 10.1034, 10.1035, 10.1035);

        candle.start();
        candle.stop(0);
        AssertJUnit.assertEquals(candle.type, CandleEnum.BULLISH);

        candle.start();
        candle.stop(0);
        AssertJUnit.assertEquals(candle.type, CandleEnum.BEARISH);

        candle.start();
        candle.stop(0);
        AssertJUnit.assertEquals(candle.type, CandleEnum.NEUTRAL);
    }

    @Test
    public void testLifeTime (){
        long startTime = 1397337720093L;
        long stopTime = 1397337720093L + 50000;
        candle.startTime = startTime;
        candle.stopTime = stopTime;

        AssertJUnit.assertEquals(50, Math.round((float) candle.lifeTime() / 1000));
    }

    @Test
    public void testGetStopTime (){
        long startTime = 1397337720093L;
        candle.start();
        candle.startTime = startTime;

        AssertJUnit.assertEquals(startTime + 1000 * TIME_UNIT, candle.getStopTime());
    }

    @Test
     public void testGetPsar (){
        given(metaTraderService.getPsar()).willReturn(10.1034);
        candle.start();
        candle.stop(0);

        AssertJUnit.assertEquals(candle.getPsar(), 10.1034);
    }

    @Test
    public void testGetPsarReverseEvent(){
//        given(metaTraderService.getPsar()).willReturn(10.1020);
//        given(metaTraderService.getPrice()).willReturn(10.1032, 10.1036);

        candle.setStopPrice(10.1050);
        candle.setType(CandleEnum.BULLISH);

        Candle prevCandle = createCandle(CandleEnum.BEARISH);
        given(watch.candles()).willReturn(Lists.newArrayList(prevCandle));

        AssertJUnit.assertEquals(candle.calculatePsarReverseEvent(), true);



        candle.setStopPrice(10.1020);
        candle.setType(CandleEnum.BEARISH);

        prevCandle = createCandle(CandleEnum.BULLISH);
        given(watch.candles()).willReturn(Lists.newArrayList(prevCandle));

        AssertJUnit.assertEquals(candle.calculatePsarReverseEvent(), true);

    }

    @Test
    public void testGetAdxRevertEvent(){

        Candle beforePrevCandle = new Candle();
        beforePrevCandle.setAdxIndicator(new AdxIndicator(20, 30, 25));

        Candle prevCandle = new Candle();
        prevCandle.setAdxIndicator(new AdxIndicator(30, 20, 25));

        given(watch.candles()).willReturn(Lists.newArrayList(beforePrevCandle, prevCandle));

        AssertJUnit.assertEquals(candle.calculateAdxReverseEvent(), true);


        beforePrevCandle = new Candle();
        beforePrevCandle.setAdxIndicator(new AdxIndicator(31, 21, 25));

        prevCandle = new Candle();
        prevCandle.setAdxIndicator(new AdxIndicator(21, 31, 25));

        given(watch.candles()).willReturn(Lists.newArrayList(beforePrevCandle, prevCandle));

        AssertJUnit.assertEquals(candle.calculateAdxReverseEvent(), true);

    }

    private Candle createCandle(CandleEnum candleType) {
        Candle prevCandle = new Candle();
        prevCandle.setPsar(10.1034);
        prevCandle.setType(candleType);
        return prevCandle;
    }


//    @Test
//    public void doATest(){
//        long stopTime = 1397337720093L;
//        long startTime = 1397337660094L;
//        Math.round( (float)(stopTime - startTime)/1000);
//    }
}

package candle;

import indicators.AdxIndicator;
import metatrader.MetaTraderService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import org.testng.collections.Lists;
import watch.Watch;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
        candle.start();
        candle.stop(0);

        verify(metaTraderService).getPsar();
    }

    @Test
    public void testGetRsi (){
        candle.start();
        candle.stop(0);

        verify(metaTraderService).getRsi();
    }

    @DataProvider(name = "testPsarReverseEventData")
    public Object[][] testPsarReverseEventData (){
        return new Object[][]{
                 //prev candle type     //prev psar     //prev startPrice       //prev stop price       //prev candle type      //actual psar   //actual startPrice     //actual stop price   //result      //comment
                //down trend cases
                {CandleEnum.BEARISH,    10.1048,        10.1044,                10.1034,                CandleEnum.BULLISH,     10.1032,        10.1036,                10.1046,              true,         "Bearish, Bullish psarReverseEvent true"},
                {CandleEnum.BEARISH,    10.1048,        10.1044,                10.1034,                CandleEnum.BEARISH,     10.1032,        10.1036,                10.1031,              false,        "Bearish, Bearish psarReverseEvent false"},
                {CandleEnum.BULLISH,    10.1048,        10.1034,                10.1044,                CandleEnum.BULLISH,     10.1032,        10.1042,                10.1047,              true,         "Bullish, Bearish psarReverseEvent true"},

                //up trend cases
                {CandleEnum.BULLISH,    10.1032,        10.1034,                10.1042,                CandleEnum.BEARISH,     10.1048,        10.1044,                10.1034,              true,         "Bullish, Bearish psarReverseEvent true"},
                {CandleEnum.BULLISH,    10.1032,        10.1034,                10.1042,                CandleEnum.BULLISH,     10.1048,        10.1040,                10.1044,              false,        "Bullish, Bullish psarReverseEvent false"},
                {CandleEnum.BEARISH,    10.1032,        10.1034,                10.1042,                CandleEnum.BEARISH,     10.1048,        10.1040,                10.1046,              true,         "Bearish, Bearish psarReverseEvent true"}
        };
    }

    @Test(dataProvider = "testPsarReverseEventData")
    public void testGetPsarReverseEvent(CandleEnum prevCandleType, double prevPsar, double prevStartPrice, double prevStopPrice,
        CandleEnum actualCandleType, double actualPsar, double actualStartPrice, double actualStopPrice, boolean result, String comment){

        candle.setType(actualCandleType);
        candle.setPsar(actualPsar);
        candle.setStartPrice(actualStartPrice);
        candle.setStopPrice(actualStopPrice);

        Candle prevCandle = createCandle(prevCandleType, prevPsar, prevStartPrice, prevStopPrice);
        given(watch.candles()).willReturn(Arrays.asList(prevCandle));

        AssertJUnit.assertEquals(comment, result, candle.calculatePsarReverseEvent());

    }

    @DataProvider(name = "testAdxReverseEventData")
    public Object[][] testAdxReverseEventData (){
        return new Object[][]{
                //-DI            //+DI                  //ADX            //-DI           //+DI                   //ADX              //result            //comment
                {22,             30,                    25,              30,             20,                     25,                "bearish",          "cross happened"},
                {22,             28,                    25,              28,             22,                     25,                null,               "cross happened"},
                {24,             28,                    25,              28,             24,                     25,                null,               "cross didn't happen"},
                {15,             35,                    25,              24,             26,                     25,                "bearish",          "cross didn't happen"},
                {15,             35,                    25,              22,             26,                     25,                 null,             "cross didn't happen"},

                {30,             20,                    25,              20,             30,                     25,                "bullish",          "cross happened"},
                {28,             22,                    25,              22,             28,                     25,                null,               "cross happened"},
                {28,             24,                    25,              24,             28,                     25,                null,               "cross didn't happen"},
                {35,             15,                    25,              26,             24,                     25,                "bullish",          "cross didn't happen"},
                {35,             17,                    25,              26,             24,                     25,                null,               "cross didn't happen"}
        };
    }

    @Test(dataProvider = "testAdxReverseEventData")
    public void testGetAdxRevertEvent(double secondLastCandleNegativeDi, double secondLastCandlePositiveDi, double secondLastCandleAdx, double currentCandleNegativeDi, double currentCandlePositiveDi, double currentCandleAdx, String result, String comment){

        Candle secondLastCandle = new Candle();
        secondLastCandle.setAdxIndicator(new AdxIndicator(secondLastCandleNegativeDi, secondLastCandlePositiveDi, secondLastCandleAdx));

        Candle lastCandle = new Candle();
        lastCandle.setAdxIndicator(new AdxIndicator(25, 35, 25));

        candle.setAdxIndicator(new AdxIndicator(currentCandleNegativeDi, currentCandlePositiveDi, currentCandleAdx));

        given(watch.candles()).willReturn(Arrays.asList(secondLastCandle, lastCandle));

        AssertJUnit.assertEquals(comment, result, candle.calculateAdxReverseEvent());

    }

    @DataProvider(name = "testRsiEventData")
    public Object[][] testRsiEventData(){
        return new Object[][]{
              //1st RSI        //3rd RSI             //result            //comment
                {60,             50,                 "bearish",          "rsi happened"},
                {60,             55,                 null,               "rsi didn't happen"},
                {50,             60,                 "bullish",          "rsi didn't happen, it's a down bearish trend"},

                {50,             60,                 "bullish",          "rsi happened"},
                {55,             60,                 null,               "rsi didn't happen"},
                {60,             50,                 "bearish",          "rsi didn't happen, it's an up trend"},


        };
    }

    @Test(dataProvider = "testRsiEventData")
    public void testRsiEvent(double secondLastCandleRsi, double currentCandleRsi, String result, String comment){

        Candle secondLastCandle = new Candle();
        secondLastCandle.setRsi(secondLastCandleRsi);

        Candle lastCandle = new Candle();
        lastCandle.setRsi(57);

        candle.setRsi(currentCandleRsi);

        given(watch.candles()).willReturn(Arrays.asList(secondLastCandle, lastCandle));

        AssertJUnit.assertEquals(comment, result, candle.calculateRsiEvent());

    }

    private Candle createCandle(CandleEnum candleType, double psar, double startPrice, double stopPrice) {
        Candle prevCandle = new Candle();
        prevCandle.setPsar(psar);
        prevCandle.setType(candleType);
        prevCandle.setStopPrice(stopPrice);
        prevCandle.setStartPrice(startPrice);
        return prevCandle;
    }


//    @Test
//    public void doATest(){
//        long stopTime = 1397337720093L;
//        long startTime = 1397337660094L;
//        Math.round( (float)(stopTime - startTime)/1000);
//    }
}

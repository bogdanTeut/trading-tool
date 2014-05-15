package watch;

import candle.Candle;
import metatrader.MetaTraderService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: bogdan.teut
 * Date: 11/04/14
 * Time: 10:32
 * To change this template use File | Settings | File Templates.
 */
public class WatchTests {
    final int TIME_UNIT = 60;

    @Mock
    MetaTraderService metaTraderService;

    @InjectMocks
    Watch watch;

    @BeforeClass
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        given(metaTraderService.getPsar()).willReturn(10.1039, 10.1036, 10.1035);
        given(metaTraderService.getPrice()).willReturn(10.1041, 10.1041, 10.1042, 10.1042, 10.1043, 10.1043);

        watch.start();
        try {
            Thread.currentThread().sleep(3 * TIME_UNIT * 1000);
        } catch (InterruptedException e) {
            System.out.println("Something nasty happened");
        }
        watch.stop();
    }

    @Test
    public void checkCandlesCreationEveryMinute(){

        List<Candle> candles =  watch.candles();
        Assert.assertNotNull (candles);
        Assert.assertEquals(candles.size(), 3);

        Candle candle1 = candles.get(0);
        Assert.assertNotNull (candle1);
        Assert.assertEquals(TIME_UNIT, Math.round( (float)(candle1.lifeTime())/1000));
        Assert.assertEquals(candle1.getPsar(), 10.1039);
        Assert.assertEquals(candle1.getStartPrice(), 10.1041);

        Candle candle2 = candles.get(1);
        Assert.assertNotNull (candle2);
        Assert.assertEquals(TIME_UNIT, Math.round( (float)(candle2.lifeTime())/1000));
        Assert.assertEquals(candle2.getPsar(), 10.1036);
        Assert.assertEquals(candle2.getStartPrice(), 10.1042);

        Candle candle3 = candles.get(2);
        Assert.assertNotNull (candle3);
        Assert.assertEquals(TIME_UNIT, Math.round( (float)(candle3.lifeTime())/1000));
        Assert.assertEquals(candle3.getPsar(), 10.1035);
        Assert.assertEquals(candle3.getStartPrice(), 10.1043);

    }
    //TO DO this test duplicates checkDoAlgorithm test. It should be deleted at some point
    @Test
    public void checkDoAlgorithmGettingCalledEveryFewMillSecs(){

        verify(metaTraderService, times((int)timeInTermsOfMilliseconds()/1000)).getAdx();
        verify(metaTraderService, times((int)timeInTermsOfMilliseconds()/1000)).getRsi();
    }

    @Test
    public void checkFirstTimeRunCalendar(){
        Watch watch = new Watch();
        Calendar now =  new GregorianCalendar();
        now.set(Calendar.SECOND, 0);
        now.add(Calendar.MINUTE, 1);

        Assert.assertEquals(watch.firstRunTimeCalendar(), now.getTime());
    }

    @Test
    public void checkDoAlgorithmNoPsarReverseEvent(){
        Watch watch =  new Watch();
        watch.setMetaTraderService(metaTraderService);

        List<Candle> candles = watch.candles();
        Candle candle = new Candle();
        candle.setMetaTraderService(metaTraderService);
        candles.add(candle);

        watch.doAlgorithm();

        Candle prevCandle = candles.get(candles.size()-1);

        verify(metaTraderService, never()).doOrder();
    }

    @Test
    public void checkDoAlgorithmPsarReverseEventNoAdxReverseEvent(){
        Watch watch =  new Watch();
        watch.setMetaTraderService(metaTraderService);

        List<Candle> candles = watch.candles();
        Candle candle = new Candle();
        candle.setMetaTraderService(metaTraderService);
        candle.setPsarReverseEvent(true);
        candle.setAdxReverseEvent(false);
        candles.add(candle);

        watch.doAlgorithm();

        Candle prevCandle = candles.get(candles.size()-1);

        verify(metaTraderService, never()).doOrder();
    }

    @Test
    public void checkDoAlgorithmPsarReverseEventAdxReverseEvent(){
        Watch watch =  new Watch();
        watch.setMetaTraderService(metaTraderService);

        List<Candle> candles = watch.candles();
        Candle candle = new Candle();
        candle.setMetaTraderService(metaTraderService);
        candle.setPsarReverseEvent(true);
        candle.setAdxReverseEvent(true);
        candles.add(candle);

        watch.doAlgorithm();

        Candle prevCandle = candles.get(candles.size()-1);

        verify(metaTraderService, atLeastOnce()).doOrder();
        Mockito.reset(metaTraderService);
    }

    @Test
    public void checkDoAlgorithPsarEventAdxReverseEventNoRsiEvent(){
        Watch watch =  new Watch();
        watch.setMetaTraderService(metaTraderService);

        List<Candle> candles = watch.candles();
        Candle candle = new Candle();
        candle.setMetaTraderService(metaTraderService);
        candle.setPsarReverseEvent(true);
        candle.setAdxReverseEvent(true);
        candle.setRsiEvent(false);
        candles.add(candle);

        watch.doAlgorithm();

        Candle prevCandle = candles.get(candles.size()-1);

        verify(metaTraderService, never()).doOrder();
        //Mockito.reset(metaTraderService);
    }



    private long timeInTermsOfMilliseconds() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.SECOND, 0);
        return 1000 * 2 * TIME_UNIT + System.currentTimeMillis() - calendar.getTimeInMillis() ;
    }

}

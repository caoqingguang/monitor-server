import cqg.monitor.Monitor;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;

/**
 * Created by caoqingguang on 2017/3/29.
 */
public class TestMain {

  @Ignore
  @Test
  public void testStart() throws InterruptedException {
    Monitor.start(4987);
    int i=0;
    while (i<10){
      i++;
      Thread.sleep(10);
      Monitor.incrCountAndTimeBy("myttt",1,12);
    }
    System.out.println(Monitor.dumpStatistics());
  }

  @Ignore
  @Test
  public void testForever() throws InterruptedException {
    Monitor.start(8080);
    Random random = new Random();
    String[] topics={"topic1","topic1","topic2"};
    while (true){
      Thread.sleep(random.nextInt(10));
      int index = random.nextInt(20) % topics.length;
      String topic=topics[index];
      int time=random.nextInt(100);
      if ("topic2".equals(topic)){
        time+=50;
      }
      Monitor.incrCountAndTimeBy(topic,1,time);
    }
  }
}

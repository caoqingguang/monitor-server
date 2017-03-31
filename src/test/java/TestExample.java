import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;

import cqg.monitor.Monitor;

/**
 * Created by caoqingguang on 2017/3/29.
 */
public class TestExample {

  @Ignore
  @Test
  public void testForever() throws InterruptedException {
    //开启服务
    Monitor.start(8080);

    Random random = new Random();
    String[] topics={"topic1","topic1","topic2"};
    while (true){
      //模拟 dosome（）
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

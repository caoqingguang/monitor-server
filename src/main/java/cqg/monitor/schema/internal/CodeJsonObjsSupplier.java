package cqg.monitor.schema.internal;

import cqg.monitor.schema.JsonObj;
import cqg.monitor.schema.JsonObjs;
import cqg.monitor.schema.JsonObjsSupplier;
import cqg.monitor.schema.FormatUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Created by caoqingguang on 2017/3/29.
 * 代码埋点监控
 */
public final class CodeJsonObjsSupplier implements JsonObjsSupplier {

  @Override
  public JsonObjs getJsonObj() {
    Set<TopicGroup> resultTopicGroups=new LinkedHashSet<>();
    TopicGroup topic=currentTopicGroup;
    if (topic!=null){
      resultTopicGroups.add(topic);
    }
    synchronized (topicGroups){
      resultTopicGroups.addAll(topicGroups);
    }
    List<JsonObj> list = resultTopicGroups.stream()
      .filter(tg -> tg != null && tg.topicMap.size() > 0)
      .sorted((tg1, tg2) -> Long.compare(tg2.millis, tg1.millis))
      .flatMap(tg -> getTgJson(tg).stream())
      .filter(json -> json != null)
      .collect(Collectors.toList());
    return new JsonObjs("cqg").addDatas(list);
  }


  //保存几分钟数据
  private final int keepMinutes;
  //当前topicGroup
  private TopicGroup currentTopicGroup;
  //历史topicGroup
  private List<TopicGroup> topicGroups=new LinkedList<>();

  public CodeJsonObjsSupplier(){
    this.keepMinutes = 5;
  }

  private List<JsonObj> getTgJson(TopicGroup tg){
    if(tg==null){
      return Collections.emptyList();
    }
    List<JsonObj> list=new ArrayList<>();
    long time = tg.millis;
    tg.topicMap.forEach((k,v)->{
      long count = v.count.get();
      long usetime = v.time.get();
      float husetime=0;
      if(count>0){
        husetime= FormatUtil.formatFloat(usetime*1.0f/count);
      }
      JsonObj jsonObj = new JsonObj()
        .setTime(time)
        .addFlag("tvm_column", k)
        .addValue("count", count)
        .addValue("usetime", usetime)
        .addValue("husetime",husetime);
      list.add(jsonObj);
    });
    return list;
  }

  //得到当前整分对应的毫秒
  private long getCurrentMillis(){
    long timeMillis = System.currentTimeMillis();
    timeMillis=(timeMillis - timeMillis % 60000);
    return timeMillis;
  }

  public void incrCount(String topic){
    getCurrentTopicGroup().getTopic(topic).incrCount();
  }

  public void incrTime(String topic){
    getCurrentTopicGroup().getTopic(topic).incrTimeBy(1);
  }

  public void incrTimeBy(String topic, int millis){
    getCurrentTopicGroup().getTopic(topic).incrTimeBy(millis);
  }

  public void incrCountBy(String topic,int addCount){
    getCurrentTopicGroup().getTopic(topic).incrCountBy(addCount);
  }

  public void incrCountAndTimeBy(String topic,long millis){
    getCurrentTopicGroup().getTopic(topic).incrCountAndTimeBy(millis);
  }

  public void incrCountAndTimeBy(String topic,int addCount, long millis){
    getCurrentTopicGroup().getTopic(topic).incrCountAndTimeBy(addCount,millis);
  }


  /**
   * 包含一分钟内的数据
   * @return
   */
  private TopicGroup getCurrentTopicGroup(){
    long millis=getCurrentMillis();
    TopicGroup result=currentTopicGroup;
    if(result!=null&&result.millis ==millis){
      return result;
    }
    synchronized (CodeJsonObjsSupplier.class){
      result=currentTopicGroup;
      if(result!=null&&result.millis ==millis){
        return result;
      }
      currentTopicGroup = new TopicGroup(millis);
    }
    //提前释放锁 避免多线线程等待
    if(result!=null){
      //代码能到这里 说明有统计，只是被新的统计给更替
      asyncAdd(result);
    }
    return currentTopicGroup;
  }

  static ScheduledExecutorService executor= Executors.newSingleThreadScheduledExecutor(r -> {
    Thread myThread = new Thread(r, "thread_remove_statistics");
    myThread.setDaemon(true);
    myThread.setPriority(4);
    return myThread;
  });

  private void asyncAdd(TopicGroup topicGroup){
    executor.execute(()->{
      synchronized (topicGroups){
        topicGroups.add(topicGroup);
        while ((topicGroups.size() - keepMinutes) > 0) {
          topicGroups.remove(0);
        }
      }
    });
  }

  //单位（分钟）时间内所有主题统计情况
  static class TopicGroup{
    private final long millis;
    private Map<String,Topic> topicMap=new HashMap<>();

    public TopicGroup(long millis) {
      this.millis = millis;
    }

    private Topic getTopic(String topicName){
      Topic topic = topicMap.get(topicName);
      if(topic!=null){
        return topic;
      }
      synchronized (topicMap){
        return topicMap.computeIfAbsent(topicName,tn->new Topic());
      }
    }
  }

  //一个主题的统计情况
  static class Topic{
    private final AtomicLong count;
    private final AtomicLong time;
    public Topic() {
      this.count = new AtomicLong();
      this.time = new AtomicLong();
    }

    public void incrCount(){
      count.incrementAndGet();
    }

    public void incrCountBy(int addNum){
      count.getAndAdd(addNum);
    }

    public void incrTimeBy(long millis){
      time.getAndAdd(millis);
    }

    public void incrCountAndTimeBy(long millis){
      incrCount();
      incrTimeBy(millis);
    }

    public void incrCountAndTimeBy(int addNum,long millis){
      incrCountBy(addNum);
      incrTimeBy(millis);
    }
  }

}

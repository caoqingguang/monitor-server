# JAVA 项目监控

为**java**程序提供监控信息

## 使用说明

- Monitor.start(port)  开启监控
```
    参数port 代表监控服务对外暴露的端口
```
- Monitor.incrCountAndTime(topic,count,time) 记录程序的埋点数据信息
```
    参数topic 为监控项
    参数count 为发生次数
    参数time  为时间发生用时
```

- 读取监控数据
```
    通过http 访问ip:port/monitor  即可获取所监控的数据
    监控数据总共分为三项
    1.系统开启信息 sys
    2.虚拟机运行信息
         线程信息、内存信息、GC信息
    3.代码埋点信息
```

## 应用实例

```
public class TestExample {
  
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
```

获取数据 get请求 ip:8080/monitor

数据信息如下
```
[
    {
        #项目启动信息
        type: "sys",
        data: [
            {
                time: 1490962380000,
                flags: {
                    
                },
                values: {
                    sys_now: 1490962402879,
                    sys_start: 1490962397299
                },
                desc: {
                    sys_now: "2017-03-31 20:13:22",
                    human_time: "2017-03-31 20:13:00",
                    sys_start: "2017-03-31 20:13:17"
                }
            }
        ]
    },
    {
        #程序埋点信息
        type: "code",
        data: [
            {
                time: 1490962380000,
                flags: {
                    tvm_column: "topic1"
                },
                values: {
                    count: 813,
                    husetime: 47.39,
                    usetime: 38528
                },
                desc: {
                    human_time: "2017-03-31 20:13:00"
                }
            },
            {
                time: 1490962380000,
                flags: {
                    tvm_column: "topic2"
                },
                values: {
                    count: 383,
                    husetime: 100.77,
                    usetime: 38593
                },
                desc: {
                    human_time: "2017-03-31 20:13:00"
                }
            }
        ]
    },
    {
        # jvm 运行信息
        type: "jvm",
        data: [
            {
                #线程信息
                time: 1490962380000,
                flags: {
                    jvm_thread: "jvm_thread"
                },
                values: {
                    thread_num: 7
                },
                desc: {
                    human_time: "2017-03-31 20:13:00"
                }
            },
            {
                # 堆信息
                time: 1490962380000,
                flags: {
                    jvm_heap_info: "jvm_heap_info"
                },
                values: {
                    jvm_heap_ratio: 0.45,
                    jvm_heap_usesize: 8,
                    jvm_heap_maxsize: 1797
                },
                desc: {
                    human_time: "2017-03-31 20:13:00"
                }
            },
            {
                # Young-GC信息
                time: 1490962380000,
                flags: {
                    jvm_gc_type: "YGC",
                    jvm_gc_type2: "PS Scavenge"
                },
                values: {
                    jvm_gc_usetime: 0,
                    jvm_gc_count: 0
                },
                desc: {
                    human_time: "2017-03-31 20:13:00"
                }
            },
            {
                # Full-GC信息
                time: 1490962380000,
                flags: {
                    jvm_gc_type: "FGC",
                    jvm_gc_type2: "PS MarkSweep"
                },
                values: {
                    jvm_gc_usetime: 0,
                    jvm_gc_count: 0
                },
                desc: {
                    human_time: "2017-03-31 20:13:00"
                }
            },
            {
                # 代码区内存占用  jdk1.8
                time: 1490962380000,
                flags: {
                    jvm_mem_type: "Other",
                    jvm_mem_type2: "Code Cache"
                },
                values: {
                    jvm_mem_maxsize: 240,
                    jvm_mem_ratio: 0.83,
                    jvm_mem_usesize: 2
                },
                desc: {
                    human_time: "2017-03-31 20:13:00"
                }
            },
            {
                # 元数据内存占用 jdk1.8
                time: 1490962380000,
                flags: {
                    jvm_mem_type: "Other",
                    jvm_mem_type2: "Metaspace"
                },
                values: {
                    jvm_mem_maxsize: 0,
                    jvm_mem_ratio: 0,
                    jvm_mem_usesize: 7
                },
                desc: {
                    human_time: "2017-03-31 20:13:00"
                }
            },
            {
                time: 1490962380000,
                flags: {
                    jvm_mem_type: "Other",
                    jvm_mem_type2: "Compressed Class Space"
                },
                values: {
                    jvm_mem_maxsize: 1024,
                    jvm_mem_ratio: 0,
                    jvm_mem_usesize: 0
                },
                desc: {
                    human_time: "2017-03-31 20:13:00"
                }
            },
            {
                # eden 区内存占用
                time: 1490962380000,
                flags: {
                    jvm_mem_type: "Eden",
                    jvm_mem_type2: "PS Eden Space"
                },
                values: {
                    jvm_mem_maxsize: 664,
                    jvm_mem_ratio: 1.2,
                    jvm_mem_usesize: 8
                },
                desc: {
                    human_time: "2017-03-31 20:13:00"
                }
            },
            {
                # 幸存代 内存占用
                time: 1490962380000,
                flags: {
                    jvm_mem_type: "Survivor",
                    jvm_mem_type2: "PS Survivor Space"
                },
                values: {
                    jvm_mem_maxsize: 5,
                    jvm_mem_ratio: 0,
                    jvm_mem_usesize: 0
                },
                desc: {
                    human_time: "2017-03-31 20:13:00"
                }
            },
            {
                # 老年代 内存占用
                time: 1490962380000,
                flags: {
                    jvm_mem_type: "OldGen",
                    jvm_mem_type2: "PS Old Gen"
                },
                values: {
                    jvm_mem_maxsize: 1348,
                    jvm_mem_ratio: 0,
                    jvm_mem_usesize: 0
                },
                desc: {
                    human_time: "2017-03-31 20:13:00"
                }
            }
        ]
    }
]

```
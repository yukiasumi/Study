# SparkCore

### sprak分区规则

```scala
    //【1，2】，【3，4】
    //val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4),2)
    //【1】，【2】，【3，4】
    //val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 3)
    //【1】，【2，3】，【4，5】
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4,5), 3)
```

positions方法获取下标，前闭后开区间分区

```scala
    def positions(length: Long, numSlices: Int): Iterator[(Int, Int)] = {
      (0 until numSlices).iterator.map { i =>
        val start = ((i * length) / numSlices).toInt
        val end = (((i + 1) * length) / numSlices).toInt
        (start, end)
      }
    }
```

### **RDD** **转换算子**

==RDD 根据数据处理方式的不同将算子整体上分为 Value 类型、双 Value 类型和 Key-Value类型==

```scala
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc = new SparkContext(sparkConf)
    //*********
    sc.stop()
```

#### ==value类型==

#### 1. map

```scala
def map[U: ClassTag](f: T => U): RDD[U]
```
==将处理的数据逐条进行映射转换，这里的转换可以是类型的转换，也可以是值的转换。==

```scala
 	//TODO 算子 - map
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 5))

    //转换函数
    def mapFunction(num: Int) = {
      num * 2
    }
```

#### 2.**mapPartitions**

```scala
def mapPartitions[U: ClassTag](
 f: Iterator[T] => Iterator[U],
 preservesPartitioning: Boolean = false): RDD[U]
```

==将待处理的数据以分区为单位发送到计算节点进行处理，这里的处理是指可以进行任意的处理，哪怕是过滤数据。==

```scala
    //TODO 算子 - mapPartitions
    //mapPartitions : 可以以分区为单位进行数据转换操作，效率高
    //                但是会将整个分区的数据加载到内存进行引用
    //                如果处理完的数据是不会被释放掉,存在对象的引用。
    //                在内存较小,数据量较大的场合下,容易出现内存溢出。
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4),2)
    val mapRDD: RDD[Int] = rdd.mapPartitions(iter => {
      println(">>>>>>>>")
      iter.map(_ * 2)
    })
```

#### 3. **mapPartitionsWithIndex**

```scala
def mapPartitionsWithIndex[U: ClassTag](
 f: (Int, Iterator[T]) => Iterator[U],
 preservesPartitioning: Boolean = false): RDD[U]
```

==将待处理的数据以分区为单位发送到计算节点进行处理，这里的处理是指可以进行任意的处理，哪怕是过滤数据，在处理时同时可以获取当前分区索引。==

```scala
   //TODO 算子 - mapPartitionsWithIndex

    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4),2)

    //【1，2】，【3，4】
    val mapRDD: RDD[Int] = rdd.mapPartitionsWithIndex(
      (index, iter) => {
        //获取第二个分区的数据
        if (index == 1) {
          iter
        } else {
          Nil.iterator
        }
      }
    )

    mapRDD.collect().foreach(println)
```

#### 4.**flatMap**

```scala
def flatMap[U: ClassTag](f: T => TraversableOnce[U]): RDD[U]
```

==将处理的数据进行扁平化后再进行映射处理，所以算子也称之为扁平映射==

```scala
    //TODO 算子 - flatMap

    val rdd: RDD[List[Int]] = sc.makeRDD(List(List(1, 2), List(3, 4)))
    val flatRDD: RDD[Int] = rdd.flatMap(
      list => {
      list
    })

    flatRDD.collect().foreach(println)
```

#### 5.**glom**

```scala
def glom(): RDD[Array[T]]
```

==将同一个分区的数据直接转换为相同类型的内存数组进行处理，分区不变==

```scala
    //TODO 算子 - glom

    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)
    // List =>Int
    // Int => Array

    val glomRDD: RDD[Array[Int]] = rdd.glom()
    //各分区最大值相加
    val maxRDD: RDD[Int] = glomRDD.map(arr => arr.max)
    println(maxRDD.collect().sum)

```

#### 6.**groupBy**

```scala
def groupBy[K](f: T => K)(implicit kt: ClassTag[K]): RDD[(K, Iterable[T])]
```

==将数据根据指定的规则进行分组, 分区默认不变，但是数据会被打乱重新组合，我们将这样的操作称之为 shuffle。极限情况下，数据可能被分在同一个分区中一个组的数据在一个分区中，但是并不是说一个分区中只有一个组==

```scala
    //TODO 算子 - groupBy

    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)
    //groupBy会将数据源中的每一个数据进行分组判断，
    // 根据返回的分组key进行分组/相同的key值的数据会放置在一个组中

    val groupRDD: RDD[(Int, Iterable[Int])] = rdd.groupBy(_ % 2)

    groupRDD.collect().foreach(println)
```

#### 7.**filter**

```scala
def filter(f: T => Boolean): RDD[T]
```

==将数据根据指定的规则进行筛选过滤，符合规则的数据保留，不符合规则的数据丢弃。当数据进行筛选过滤后，分区不变，但是分区内的数据可能不均衡，生产环境下，可能会出现数据倾斜。==

```scala
    //TODO 算子 - filter
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))

    val filterRDD: RDD[Int] = rdd.filter(_ % 2 != 0)
    filterRDD.collect().foreach(println)
```

#### 8.**sample**

```scala
def sample(
 withReplacement: Boolean,
 fraction: Double,
 seed: Long = Utils.random.nextLong): RDD[T]
```

==根据指定的规则从数据集中抽取数据==

```scala
    //TODO 算子 - sample
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))

    //sample算子需要传递三个参数
    // 1.第一个参数表示，抽取数据后是否将数据返回 true (放回)，false(丢弃)
    // 2.第二个参数表示，数据源中每条数据被抽取的概率
    //                如果抽取不放回的场合:数据源中每条数据被抽取的概率,基准值的概念
    //                如果抽取放回的场合:表示数据源中的每条数据被抽取的次数
    // 3.第三个参数表示,抽取数据时随机算法的种子
    //如果不传递第三个参数，那么使用的是当前系统时间
    println(rdd.sample(true, 2).collect().mkString(","))

```

```scala
val dataRDD = sparkContext.makeRDD(List(
 1,2,3,4
),1)
// 抽取数据不放回（伯努利算法）
// 伯努利算法：又叫 0、1 分布。例如扔硬币，要么正面，要么反面。
// 具体实现：根据种子和随机算法算出一个数和第二个参数设置几率比较，小于第二个参数要，大于不
要
// 第一个参数：抽取的数据是否放回，false：不放回
// 第二个参数：抽取的几率，范围在[0,1]之间,0：全不取；1：全取；
// 第三个参数：随机数种子
val dataRDD1 = dataRDD.sample(false, 0.5)
// 抽取数据放回（泊松算法）
// 第一个参数：抽取的数据是否放回，true：放回；false：不放回
// 第二个参数：重复数据的几率，范围大于等于 0.表示每一个元素被期望抽取到的次数
// 第三个参数：随机数种子
val dataRDD2 = dataRDD.sample(true, 2)
```

#### 9. **distinct**

```scala
def distinct()(implicit ord: Ordering[T] = null): RDD[T]
def distinct(numPartitions: Int)(implicit ord: Ordering[T] = null): RDD[T]
```

==将数据集中重复的数据去重==

```scala
  //TODO 算子 - distinct
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4, 1, 2, 3, 4))

    //map(x => (x, null)).reduceByKey((x, _) => x, numPartitions).map(_._1)
    // (1, null),(2, null),(3, null),(4, null),(1， null),(2, null),(3, null),(4, null)
    // (1, null)(1, null)(1, null)
    // (null, null) => null
    // (1， null) => 1

    val rdd1: RDD[Int] = rdd.distinct()

    rdd1.collect().foreach(println)

    //HashSet
    List(1, 2, 4, 5, 6).distinct
```

#### 10.**coalesce**

```scala
def coalesce(numPartitions: Int, shuffle: Boolean = false,
 partitionCoalescer: Option[PartitionCoalescer] = Option.empty)
 (implicit ord: Ordering[T] = null)
 : RDD[T]
```

==根据数据量缩减分区，用于大数据集过滤后，提高小数据集的执行效率当 spark 程序中，存在过多的小任务的时候，可以通过 coalesce 方法，收缩合并分区，减少分区的个数，减小任务调度成本==

```scala
 //TODO 算子 - coalesce
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4, 5, 6), 3)

    //def coalesce(numPartitions: Int, shuffle: Boolean = false
    // coalesce方法默认情况下不会将分区的数据打乱重新组合
    // 这种情况下的缩减分区可能会导致数据不均衡，
    // 出现数据倾斜如果想要让数据均衡,可以进行shuffle处理

    //val newRDD: RDD[Int] = rdd.coalesce(2)

    val newRDD: RDD[Int] = rdd.coalesce(2, true)
    newRDD.saveAsTextFile("output")

```

#### 11.**repartition**

```scala
def repartition(numPartitions: Int)(implicit ord: Ordering[T] = null): RDD[T]
```

==该操作内部其实执行的是 coalesce 操作，参数 shuffle 的默认值为 true。无论是将分区数多的RDD 转换为分区数少的 RDD，还是将分区数少的 RDD 转换为分区数多的 RDD，repartition操作都可以完成，因为无论如何都会经 shuffle 过程。==

```scala
//TODO 算子 - repartition
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4, 5, 6), 2)

    //coalesce算子可以扩大分区，但是如果不进行shuffle操作，是没有意义，不起作用。
    //所以如果想要实现扩大分区的效果，需要使用shuffle操作
    //val newRDD: RDD[Int] = rdd.coalesce(3,true)

    // repartition扩大分区，底层调用coalesce(numPartitions, shuffle = true)
    val newRDD: RDD[Int] = rdd.repartition(3)

    newRDD.saveAsTextFile("output")
```

#### 12. **sortBy**

```scala
def sortBy[K](
 f: (T) => K,
 ascending: Boolean = true,
 numPartitions: Int = this.partitions.length)
 (implicit ord: Ordering[K], ctag: ClassTag[K]): RDD[T]
```

==该操作用于排序数据。在排序之前，可以将数据通过 f 函数进行处理，之后按照 f 函数处理的结果进行排序，默认为升序排列。排序后新产生的 RDD 的分区数与原 RDD 的分区数一致。中间存在 shuffle 的过程==

```scala
    //TODO 算子 - sortBy
    //val rdd: RDD[Int] = sc.makeRDD(List(6, 2, 1, 4, 3, 5), 2)
    //val newRDD: RDD[Int] = rdd.sortBy(num => num)

    //sortBy方法可以根据指定的规则对数据源中的数据进行排序，默认为升序,第二个参数可以改变排序的方式
    // sortBy认情况下，不会改变分区。但是中问存在shuffle操作
    val rdd: RDD[(String, Int)] = sc.makeRDD(List(("1", 1), ("11", 2), ("2", 3)), 2)
    val newRDD: RDD[(String, Int)] = rdd.sortBy(t => t._1.toInt,false)
    newRDD.collect().foreach(println)
    //newRDD.saveAsTextFile("output")
```

#### ==双Value类型==

#### 13.**intersection（交集）**

```scala
def intersection(other: RDD[T]): RDD[T]
```
#### 14. **union（并集）**

```scala
def union(other: RDD[T]): RDD[T]
```

#### 15. **subtract（差集）**

```scala
def subtract(other: RDD[T]): RDD[T]
```

#### 16.**zip（拉链）**

```scala
def zip[U: ClassTag](other: RDD[U]): RDD[(T, U)]
```

```scala
//TODO 算子 - 双value类型
    val rdd1 = sc.makeRDD(List(1, 2, 3, 4))
    val rdd2 = sc.makeRDD(List(3, 4, 5, 6))
    //    val rdd7 = sc.makeRDD(List("3", "4", "5", "6"))

    //交集,并集和差集要求两个数据源数据类型保持一致

    //交集【3，4】
    val rdd3: RDD[Int] = rdd1.intersection(rdd2)
    //    val rdd8 = rdd1.intersection(rdd7)
    println(rdd3.collect().mkString(","))

    //并集【1，2，3，4，3，4，5，6】
    val rdd4: RDD[Int] = rdd1.union(rdd2)
    println(rdd4.collect().mkString(","))

    //差集【1，2】
    val rdd5: RDD[Int] = rdd1.subtract(rdd2)
    println(rdd5.collect().mkString(","))

    //拉链【1-3，2-4，3-5，4-6】
    val rdd6: RDD[(Int, Int)] = rdd1.zip(rdd2)
    println(rdd6.collect().mkString(","))

```
```scala
//TODO 算子 - 双value类型

    //Can't zip RDDs with unequal numbers of partitions: List(2, 4)
    //两个数据源要求分区数量要保持一致

    //Can only zip RDDs with same number of elements in each partition
    //两个数据源要求分区中数据数量保持一致

    val rdd1 = sc.makeRDD(List(1, 2, 3, 4, 5, 6), 2)
    val rdd2 = sc.makeRDD(List(3, 4, 5, 6), 2)

    val rdd6: RDD[(Int, Int)] = rdd1.zip(rdd2)
    println(rdd6.collect().mkString(","))
```

#### ==Key-Value类型==

#### 17. **partitionBy**

```scala
def partitionBy(partitioner: Partitioner): RDD[(K, V)]
```

==将数据按照指定 Partitioner 重新进行分区。Spark 默认的分区器是 HashPartitioner==

```scala
//TODO 算子 - key-value类型


    val rdd = sc.makeRDD(List(1, 2, 3, 4), 2)

    val mapRDD: RDD[(Int, Int)] = rdd.map((_, 1))
    //RDD => PairRDDFunctions
    //隐式转换(二次编译)

    //partitionBy根据指定的分区规则对数据进行重分区
    val newRDD = mapRDD.partitionBy(new HashPartitioner(2))
    //如果重分区的分区器和当前RDD的分区器一样,不会再次分区
    // if (self.partitioner == Some(partitioner)) self
    newRDD.partitionBy(new HashPartitioner(2))
    newRDD.saveAsTextFile("output")
```

#### 18. **reduceByKey**

```scala
def reduceByKey(func: (V, V) => V): RDD[(K, V)]
def reduceByKey(func: (V, V) => V, numPartitions: Int): RDD[(K, V)]
```

==可以将数据按照相同的 Key 对 Value 进行聚合==

```scala
//TODO 算子 - (key-value类型)


    val rdd = sc.makeRDD(List(("a", 1), ("a", 2), ("a", 3), ("b", 4)))
    //reduceByKey :相同的key的数据进行value数据的聚合操作
    //scala语言中一般的聚合操作都是两两聚合，spark基于scala开发的，所以它的聚合也是两两聚合
    //【1，2，3】
    //【3，3】
    //【6】

    //reduceByKey中如果key的数据只有一个，是不会参与运算的。
    val reduceRDD: RDD[(String, Int)] = rdd.reduceByKey((x, y) => {
      println(s"x=${x},y=${y}")
      x + y
    })

    reduceRDD.collect().foreach(println)
```

#### 19. **groupByKey**

```scala
def groupByKey(): RDD[(K, Iterable[V])]
def groupByKey(numPartitions: Int): RDD[(K, Iterable[V])]
def groupByKey(partitioner: Partitioner): RDD[(K, Iterable[V])]
```

==将数据源的数据根据 key 对 value 进行分组==

```scala
//TODO 算子 - (key-value类型)

    //groupByKey会导致数据打乱重组，存在shuffle操作
    //spark中，shuffle操作必须落盘处理，不能在内存中数据等待，会导致内存溢出。
    //shuffle操作的性能非常低
    //reduceByKey支持分区内预聚合功能，可以有效减少shuffle时落盘的数据量
    val rdd = sc.makeRDD(List(("a", 1), ("a", 2), ("a", 3), ("b", 4)))

    //groupByKey:将数据源中的数据，相同的key的数据分在一个组中,形成一个对偶元组
    //元组中的第一个元素就是key,
    //元组中的第二个元素就是相同key的value的集合
    val groupRDD: RDD[(String, Iterable[Int])] = rdd.groupByKey()

    val groupRDD1: RDD[(String, Iterable[(String, Int)])] = rdd.groupBy(_._1)
    groupRDD.collect().foreach(println)
    groupRDD1.collect().foreach(println)
```

**从** **shuffle** **的角度**：reduceByKey 和 groupByKey 都存在 shuffle 的操作，但是 reduceByKey可以在 shuffle 前对分区内相同 key 的数据进行预聚合（combine）功能，这样会减少落盘的数据量，而 groupByKey 只是进行分组，不存在数据量减少的问题，reduceByKey 性能比较高。

**从功能的角度**：reduceByKey 其实包含分组和聚合的功能。GroupByKey 只能分组，不能聚合，所以在分组聚合的场合下，推荐使用 reduceByKey，如果仅仅是分组而不需要聚合。那么还是只能使用 groupByKey。

#### 20. **aggregateByKey**

```scala
def aggregateByKey[U: ClassTag](zeroValue: U)(seqOp: (U, V) => U,
 combOp: (U, U) => U): RDD[(K, U)]
```

==将数据根据不同的规则进行分区内计算和分区间计算==

```scala
//TODO 算子 - aggregateByKey

    val rdd = sc.makeRDD(List(("a", 1), ("a", 2), ("a", 3), ("a", 4)), 2)
    //(a，【1,2】)，(a，【3，4】)
    // (a，2), (a，4)
    //(a，6)

    // aggregateByKey存在函数柯里化，有两个参数列表
    //第一个参数列表 需要传递一个参数，表示为初始值
    //主要用于当碰见第一个key的时候，和value进行分区内计算

    //第二个参数列表需要传递2个参数
    //   第一个参数表示分区内计算规则
    //   第二个参数表示分区问计算规则

    rdd.aggregateByKey(0)(
      (x,y)=>math.max(x,y),
      (x,y)=>x+y).collect().foreach(println)
```

```scala
//TODO 算子 - aggregateByKey

    val rdd = sc.makeRDD(List(("a", 1), ("a", 2), ("b", 3), ("b", 4), ("b", 5), ("a", 6)), 2)

    // aggregateByKey存在函数柯里化，有两个参数列表
    //第一个参数列表 需要传递一个参数，表示为初始值
    //主要用于当碰见第一个key的时候，和value进行分区内计算

    //第二个参数列表需要传递2个参数
    //   第一个参数表示分区内计算规则
    //   第二个参数表示分区问计算规则

    rdd.aggregateByKey(5)(math.max(_, _), _ + _).collect().foreach(println)

    rdd.aggregateByKey(5)(_ + _, _ + _).collect().foreach(println)

```



#### 21. **foldByKey**

```scala
def foldByKey(zeroValue: V)(func: (V, V) => V): RDD[(K, V)]
```

==当分区内计算规则和分区间计算规则相同时，aggregateByKey 就可以简化为 foldByKey==

```scala
    //TODO 算子 - flodByKey

    val rdd = sc.makeRDD(List(("a", 1), ("a", 2), ("b", 3), ("b", 4), ("b", 5), ("a", 6)), 2)

    rdd.aggregateByKey(5)(_ + _, _ + _).collect().foreach(println)
    //如果聚合计算时，分区内和分区间计算规则相同，spark提供了简化的方法
    rdd.foldByKey(0)(_ + _).collect().foreach(println)
```

```scala
//TODO 算子 - aggregateByKey

    val rdd: RDD[(String, Int)] = sc.makeRDD(List(("a", 1), ("a", 2), ("b", 3), ("b", 4), ("b", 5), ("a", 6)), 2)

    //aggregateByKey最终的返回数据结果应该和初始值的类型保持一致
    //val aggRDD: RDD[(String, String)] = rdd.aggregateByKey("")(_ + _, _ + _)

    //获取相同key数据的平均值=>(a,3) (b,4)
    val aggRDD: RDD[(String, (Int, Int))] = rdd.aggregateByKey((0, 0))(
      (t, v) => (t._1 + v, t._2 + 1),
      (t1, t2) => (t1._1 + t2._1, t1._2 + t2._2)
    )

    aggRDD.mapValues({
      case (sum,count)=>sum/count
    }).collect().foreach(println)
```



#### 22. **combineByKey**

```scala
def combineByKey[C](
 createCombiner: V => C,
 mergeValue: (C, V) => C,
 mergeCombiners: (C, C) => C): RDD[(K, C)]
```

==最通用的对 key-value 型 rdd 进行聚集操作的聚集函数（aggregation function）。类似于aggregate()，combineByKey()允许用户返回值的类型与输入不一致。==

```scala
//TODO 算子 - combineByKey

    val rdd: RDD[(String, Int)] = sc.makeRDD(List(("a", 1), ("a", 2), ("b", 3), ("b", 4), ("b", 5), ("a", 6)), 2)
    //combineByKey :方法需要三个参数
    //第一个参数表示:将相同key的第一个数据进行结构的转换，实现操作
    //第二个参数表示:分区内的计算规则
    //第三个参数表示:分区间的计算规则

    val newRDD: RDD[(String, (Int, Int))] = rdd.combineByKey(v => (
      v, 1),
      (t: (Int, Int), v) => (t._1 + v, t._2 + 1),
      (t1: (Int, Int), t2: (Int, Int)) => (t1._1 + t2._1, t1._2 + t2._2)
    )
    newRDD.mapValues({ case (num, count) => num / count }).collect().foreach(println)

```
==FoldByKey: 相同 key 的第一个数据和初始值进行分区内计算，分区内和分区间计算规则相同==

==AggregateByKey：相同 key 的第一个数据和初始值进行分区内计算，分区内和分区间计算规则可以不相同==

==CombineByKey:当计算时，发现数据结构不满足要求时，可以让第一个数据转换结构。分区内和分区间计算规则不相同。==

```scala
//TODO 算子 - XXXByKey

    val rdd: RDD[(String, Int)] = sc.makeRDD(List(("a", 1), ("a", 2), ("b", 3), ("b", 4), ("b", 5), ("a", 6)), 2)

    /*
      reduceByKey:

           combineByKeyWithClassTag[V](
           (v: V) => v,  第一个值不会参与计算
           func,          分区内计算规则
           func,          分区间计算规则
           )

      aggregateByKey:
          combineByKeyWithClassTag[U](
          (v: V) => cleanedSeqOp(createZero(), v), 初始值和第一个key的value值进行的分区内数据操作
          cleanedSeqOp,   分区内计算规则
          combOp,         分区间计算规则
          )

      foldByKey:
          combineByKeyWithClassTag[V](
          (v: V) => cleanedFunc(createZero(), v), 初始值和第一个key的value值进行的分区内数据操作
          cleanedFunc,  分区内计算规则
          cleanedFunc,  分区间计算规则
          )

      combineByKey:
         combineByKeyWithClassTag(
         createCombiner,  相同key的第一条数据进行的处理函数
         mergeValue,      表示分区内数据的处理函数
         mergeCombiners,  表示分区间数据的处理函数
     )
  }

     */
    rdd.reduceByKey(_ + _) //wordcount
    rdd.aggregateByKey(0)(_ + _, _ + _) //wordcount
    rdd.foldByKey(0)(_ + _) //wordcount
    rdd.combineByKey(v => v, (x: Int, y) => x + y, (x: Int, y: Int) => x + y) //wordcount

```

#### 23. **sortByKey**

```scala
def sortByKey(ascending: Boolean = true, numPartitions: Int = self.partitions.length)
 : RDD[(K, V)]
```

==在一个(K,V)的 RDD 上调用，K 必须实现 Ordered 接口(特质)，返回一个按照 key 进行排序的==

```scala
val dataRDD1 = sparkContext.makeRDD(List(("a",1),("b",2),("c",3)))
val sortRDD1: RDD[(String, Int)] = dataRDD1.sortByKey(true)
val sortRDD1: RDD[(String, Int)] = dataRDD1.sortByKey(false)
```

#### 24. **join**

```scala
def join[W](other: RDD[(K, W)]): RDD[(K, (V, W))]
```

==在类型为(K,V)和(K,W)的 RDD 上调用，返回一个相同 key 对应的所有元素连接在一起的(K,(V,W))的 RDD==

```scala
    //TODO 算子 - join
    //两个不同数据源的数据,相同的key的value会连接在一起，形成元组
    //如果两个数据源中key没有匹配上，那么数据不会出现在结果中
    //如果两个数据源中key有多个相同的，会依次匹配，可能会出现笛卡尔乘积，数据量会几何性增长
    val rdd1 = sc.makeRDD(List(("a", 1), ("b", 2), ("c", 3)))
    val rdd2 = sc.makeRDD(List(("d", 5), ("a", 4), ("a", 6)))

    val joinRDD: RDD[(String, (Int, Int))] = rdd1.join(rdd2)

    joinRDD.collect().foreach(println)
```

#### 25. **leftOuterJoin&rightOuterJoin**

```scala
def leftOuterJoin[W](other: RDD[(K, W)]): RDD[(K, (V, Option[W]))]
def rightOuterJoin[W](other: RDD[(K, W)]): RDD[(K, (V, Option[W]))]
```

==类似于 SQL 语句的左(右)外连接==

```scala
//TODO 算子 - join
     val rdd1 = sc.makeRDD(List(("a", 1), ("b", 2)/*, ("c", 3)*/))
    val rdd2 = sc.makeRDD(List(("a", 5), ("a", 4), ("c", 6)))

    val leftRDD: RDD[(String, (Int, Option[Int]))] = rdd1.leftOuterJoin(rdd2)
    val rightRDD: RDD[(String, (Option[Int],Int))] = rdd1.rightOuterJoin(rdd2)
    leftRDD.collect().foreach(println)
    println("------------")
    rightRDD.collect().foreach(println)
```

#### 26. **cogroup**

```scala
def cogroup[W](other: RDD[(K, W)]): RDD[(K, (Iterable[V], Iterable[W]))]
```

==在类型为(K,V)和(K,W)的 RDD 上调用，返回一个(K,(Iterable<V>,Iterable<W>))类型的 RDD==

```scala
 //TODO 算子 - cogroup
    //connect + group(分组 连接)
     val rdd1 = sc.makeRDD(List(("a", 1), ("b", 2)/*, ("c", 3)*/))
    val rdd2 = sc.makeRDD(List(("a", 5), ("a", 4), ("c", 6),("c",7)))


    val newRDD: RDD[(String, (Iterable[Int], Iterable[Int]))] = rdd1.cogroup(rdd2)

    newRDD.collect().foreach(println)
```



#### 案例实操

![image-20220927200539668](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220927200539668.png)

```scala
//TODO 案例实操

    //1.获取原始数据;时间戳，省份，城市，用户，广告
//1516609143867 6 7 64 16
//1516609143869 9 4 75 18
//1516609143869 1 7 87 12
    //2．将原始数据进行结构的转换。方便统计
    //    时间戳，省份，城市，用户，广告 =>
    //      ((省份，广告)，1 )
    val rdd: RDD[String] = sc.textFile("datas/agent.log")
    val mapRDD: RDD[((String, String), Int)] = rdd.map(lines => {
      val line: Array[String] = lines.split(" ")
      ((line(1), line(4)), 1)
    })


    //3.将转换结构后的数据，进行分组聚合
    //((省份，广告)，1 ) =>((省份，广告)， sum
    val reduceRDD: RDD[((String, String), Int)] = mapRDD.reduceByKey(_ + _)

    // 4.将聚合的结果进行结构的转换
    //((省份，广告 ), sum )=>（省份，(广告， sum ) )
    val newMapRDD: RDD[(String, (String, Int))] = reduceRDD.map({
      case ((prv, ad), sum) => (prv, (ad, sum))
    })

    // 5.将转换结构后的数据根据省份进行分组
    //(省份，【(广告A， sumA )，(广告B, sumB )】)
    val groupRDD: RDD[(String, Iterable[(String, Int)])] = newMapRDD.groupByKey()


    // 6.将分组后的数据组内排序（降序)，取前3名
    val resultRDD: RDD[(String, List[(String, Int)])] = groupRDD.mapValues(
      iter => {
        iter.toList.sortBy(_._2)(Ordering.Int.reverse).take(3)
      }
    )

    //7.采集数据打印在控制合
    resultRDD.collect().foreach(println)

```


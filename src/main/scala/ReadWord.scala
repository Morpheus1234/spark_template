/**
  * Created by root on 17-8-7.
  */
import ReadWord.RawDataRecord
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.feature.{HashingTF, IDF, Tokenizer}

object ReadWord {
  case class RawDataRecord(category: String, text: String)
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
    val conf = new SparkConf().setAppName("ReadWord").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val spark = SparkSession.builder.master("local").appName("Spark SQL basic example").config("spark.some.config.option", "some-value").getOrCreate()
    import spark.implicits._

    //将原始数据映射到DataFrame中，字段category为分类编号，字段text为分好的词，以空格分隔
    var srcDF = sc.textFile("/root/jieguo.txt").map {
      x =>
        var data = x.split(",")
        RawDataRecord(data(0),data(1))
    }.toDF()

    srcDF.select("category", "text").take(2).foreach(println)
    //[0,苹果 官网 苹果 宣布]
    //[1,苹果 梨 香蕉]
    //将分好的词转换为数组
//    var tokenizer = new Tokenizer().setInputCol("text").setOutputCol("words")
//    var wordsData = tokenizer.transform(srcDF)
//
//    wordsData.select($"category",$"text",$"words").take(2).foreach(println)
    //    [0,苹果 官网 苹果 宣布,WrappedArray(苹果, 官网, 苹果, 宣布)]
    //  [1,苹果 梨 香蕉,WrappedArray(苹果, 梨, 香蕉)]

    //将每个词转换成Int型，并计算其在文档中的词频（TF）
//    var hashingTF =new HashingTF().setInputCol("words").setOutputCol("rawFeatures").setNumFeatures(100)
//    var featurizedData = hashingTF.transform(wordsData)
  }

}

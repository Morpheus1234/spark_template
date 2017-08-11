/**
  * Created by root on 17-8-9.
  */

import org.apache.spark.SparkConf
import org.apache.spark.sql.{Row, SQLContext, SparkSession}
import org.apache.spark.SparkContext
import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.ml.feature.HashingTF
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.ml.feature.IDFModel
import org.apache.spark.ml.classification.NaiveBayesModel
import org.apache.log4j.{Level, Logger}
import spark.mllib.NewsClassifyBayes.RawDataRecord

import scala.util.parsing.json.JSON._
import scala.collection.immutable.Map
import scala.io.Source

object BayesModelUsed {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
    val conf = new SparkConf().setMaster("local").setAppName("NewsClassifyTest")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    val spark = SparkSession.builder.master("local").appName("NavieBayesTest").config("spark.some.config.option", "some-value").getOrCreate()

    // For implicit conversions like converting RDDs to DataFrames
    import spark.implicits._

    val url = "jdbc:mysql://192.168.0.202:3306/spider"
    val jdbcDF = spark.read
      .format("jdbc")
      .option("url", url)
      .option("user", "suqi")
      .option("password", "123456")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("dbtable" , "(select newsid,text from _news_data_copy) as textTab")
      .option("dbtable", "_news_data_copy")
      .load()

//    jdbcDF.show()
//
//    val cc=jdbcDF.collect()


    val reg = "<.*?>".r
    val content = jdbcDF.select("newsid","text").toDF()
    content.show(1)
    val regContent = content.map { x_2=> reg.replaceAllIn(x_2.toString, "") }.show()
    //分類数据
    //    val dataPredict = sc.textFile("/root/jieguo").map {
    //      x =>
    //        val data = x.split(",")
    //        RawDataRecord(data(0), data(1))
    //    }.toDF()

    //分词
    val tokenizer = new Tokenizer().setInputCol("text").setOutputCol("words")
    val wordsData = tokenizer.transform(content)
    wordsData.show(false)

    //文档词频
    val hashingTF =
      new HashingTF().setInputCol("words").setOutputCol("rawFeatures").setNumFeatures(500000)
    val featurizedData = hashingTF.transform(wordsData)
    featurizedData.show(false)


    //逆文档词频
    val loadidfModel = IDFModel.load("target/tmp/NewsClassifyidfModelResult")

    val rescaledData = loadidfModel.transform(featurizedData)
    rescaledData.show()


    //    rescaledData.show(false)
    //转换成贝叶斯的输入格式
    val predictDataRdd = rescaledData.select($"newsid", $"features").map {
      case Row(label: String, features: Vector) =>
        LabeledPoint(label.toDouble, Vectors.dense(features.toArray))
    }

    val nbModel = NaiveBayesModel.load("target/tmp/NewsClassifyBayesModelResult")

    val predictions = nbModel.transform(predictDataRdd)

    println("predictln out:");
    predictions.show();
    val predictResult = predictions.select("label", "prediction")
    println("myresult:")
    predictResult.show()
  }
}

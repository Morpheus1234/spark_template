/**
  * Created by root on 17-8-10.
  */

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.ml.classification.NaiveBayesModel
import org.apache.spark.ml.feature.{HashingTF, IDFModel, LabeledPoint, Tokenizer}
import org.apache.spark.ml.linalg.{Vector, Vectors}
import org.apache.spark.sql.Row
import spark.mllib.NewsClassifyBayes.RawDataRecord

import scala.util.parsing.json.JSON._
import scala.collection.immutable.Map
import scala.io.Source

object Readjson {
   def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("DataFram Ops")
    val sqlContext = new SQLContext(new SparkContext(conf))

    val df = sqlContext.read.json("/root/bigdata/json1.txt")

    df.show
//    df.printSchema
//    df.select("name").show
//    df.select("name", "age").show
//    df.select(df("name"),df("age")+10).show
//    df.filter(df("age")>10).show
  }

//  def main(args: Array[String]): Unit = {
//    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
//    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
//    val conf = new SparkConf().setMaster("local").setAppName("NewsClassifyTest")
//    val sqlContext = new SQLContext(new SparkContext(conf))
//    val sc = new SparkContext(conf)
//    val tree = parseFull(Source.fromFile("/root/bigdata/json1.txt").mkString)
//
//    tree.foreach {
//      case ls: List[Any] =>
//        println("List => ")
//        ls.foreach(println)
//
//      case map: Map[String, String] =>
//        println("map => ")
//        map.foreach(println)
//        val dataPredict = sc..map {
//          x =>
//            val data = x.split(",")
//            RawDataRecord(data(0), data(1))
//        }
//
//
//      case _ => println("other")
//    }
//  }
}


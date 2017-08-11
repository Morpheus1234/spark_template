/**
  * Created by root on 17-8-9.
  */

package spark.mllib


import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkContext
import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.ml.feature.HashingTF
import org.apache.spark.sql.Row
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.ml.feature.{IDF, IDFModel}
import org.apache.spark.ml.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.log4j.{Level, Logger}

object NewsClassifyBayes {
  case class RawDataRecord(category: String, text: String)

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
    val conf=new SparkConf().setMaster("local").setAppName("NewsClassifyTest")
    val sc=new SparkContext(conf)

    val spark = SparkSession.builder.master("local").appName("Spark SQL Test").config("spark.some.config.option", "some-value").getOrCreate()
    // For implicit conversions like converting RDDs to DataFrames
    import spark.implicits._


    //分数据
    val Array(srcDF,testDF) = sc.textFile("/root/train_corpus").map {
      x =>
        val data = x.split(",")
        RawDataRecord(data(0),data(1))
    }.toDF().randomSplit(Array(0.9,0.1))

    //分词
    val tokenizer = new Tokenizer().setInputCol("text").setOutputCol("words")
    val wordsData = tokenizer.transform(srcDF)
//    wordsData.show(1)
    val testtokenizer = new Tokenizer().setInputCol("text").setOutputCol("words")
    val testwordsData = testtokenizer.transform(testDF)
//    testwordsData.show(1)

    //文档词频
    val hashingTF =
      new HashingTF().setInputCol("words").setOutputCol("rawFeatures").setNumFeatures(500000)
    val featurizedData = hashingTF.transform(wordsData)
//    featurizedData.show(1)

    val testhashingTF =
      new HashingTF().setInputCol("words").setOutputCol("rawFeatures").setNumFeatures(500000)
    val testfeaturizedData = testhashingTF.transform(testwordsData)
//    testfeaturizedData.show(1)

    //逆文档词频
    val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
    val idfModel = idf.fit(featurizedData)
    val rescaledData = idfModel.transform(featurizedData)
    rescaledData.show(1)

    val testrescaledData = idfModel.transform(testfeaturizedData)
    println("testrescaledData :")
    testrescaledData.show(1)

    //转换成贝叶斯的输入格式
    val trainDataRdd = rescaledData.select($"category",$"features").map {
      case Row(label: String, features:Vector) =>
        LabeledPoint(label.toDouble, Vectors.dense(features.toArray))
    }

    val testtrainDataRdd = testrescaledData.select($"category",$"features").map {
      case Row(label: String, features:Vector) =>
        LabeledPoint(label.toDouble, Vectors.dense(features.toArray))
    }

    val model =new NaiveBayes().fit(trainDataRdd)

    val predictions = model.transform(testtrainDataRdd)
    println("predictln out:");
    predictions.show(1);
    val testResult=predictions.select("label","prediction")
    testResult.show(1)


        //模型评估
    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val accuracy = evaluator.evaluate(predictions)
    println("accuracy out :")
    println("Accuracy:"+accuracy)

    model.save("target/tmp/NewsClassifyBayesModelResult")
    idfModel.save("target/tmp/NewsClassifyidfModelResult")

////    模型再利用////////////////////////////////////////////////////////////////////////////////////////////////////


//    idfModel.save("target/tmp/NewsClassifyidfModelTest")
//    val sameidfModel=IDFModel.load("target/tmp/NewsClassifyidfModelTest")
//
//    val testrescaledData_load = sameidfModel.transform(testfeaturizedData)
//    println("testrescaledData_load :")
//    testrescaledData_load.show(1)
//
//        //转换成贝叶斯的输入格式
//    val testtrainDataRdd_load = testrescaledData_load.select($"category",$"features").map {
//      case Row(label: String, features:Vector) =>
//        LabeledPoint(label.toDouble, Vectors.dense(features.toArray))
//    }
//
//    model.save("target/tmp/NewsClassifyBayesModelTest")
//    val sameModel=NaiveBayesModel.load("target/tmp/NewsClassifyBayesModelTest")
//
//
//
//    val predictions_load = sameModel.transform(testtrainDataRdd_load)
//    println("predictln out_load:");
//    predictions_load.show(1);
//    val testResult_load=predictions_load.select("label","prediction")
//    testResult_load.show(1)
//
//        //模型评估
//    val evaluator_load = new MulticlassClassificationEvaluator()
//      .setLabelCol("label")
//      .setPredictionCol("prediction")
//      .setMetricName("accuracy")
//    val accuracy_load = evaluator_load.evaluate(predictions_load)
//    println("accuracy out_load :")
//    println("Accuracy:"+accuracy_load)
  }
}

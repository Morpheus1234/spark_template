/**
  * Created by suqi on 17-8-7.
  */

import org.apache.spark.ml.fpm.FPGrowth
import org.apache.spark
import org.apache.spark._
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.StreamingContext._

object FPgrowthTest {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("localhost")
      .appName("Spark SQL basic example")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()
    import spark.implicits._
    val data = Seq("1 2 5", "1 2 3 5", "1 2")
    val dataset = spark.createDataset(data).map(t => t.split(" ")).toDF("items")
    val fpgrowth = new FPGrowth().setItemsCol("items").setMinSupport(0.5).setMinConfidence(0.6)
    val model = fpgrowth.fit(dataset)
    model.freqItemsets.show()
    model.associationRules.show()
    model.transform(dataset).show()
  }
}

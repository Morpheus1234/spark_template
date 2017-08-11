/**
  * Created by suqi on 17-8-3.
  */
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.Rating
import org.apache.log4j.{Level,Logger}

object MyMovie {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    val inputfile="file:///root/bigdata/IdeaProjects/MyMllib/data/ml-100k/u.data"
    val sc=new SparkContext("local[2]","Movie Lens App")
    val rawData=sc.textFile(inputfile)
    val rawRatings=rawData.map(_.split("\t").take(3))
//    println(rawRatings.first())
//    rawRatings.first().foreach(println)
    val ratings=rawRatings.map{case Array(user,movie,rating)=>
    Rating(user.toInt,movie.toInt,rating.toDouble)}
//    println(ratings.first())
    val model=ALS.train(ratings,50,10,0.01)

//    println(model.userFeatures.count())
//    println(model.productFeatures.count())

//    给定用户对给定物品的预期评分
//    val predictedRating=model.predict(789,123)
//    println(predictedRating)

//    为某个用户生成前K个推荐物品
    val topKRecs=model.recommendProducts(789,10)
//    topKRecs.foreach(println)
//    println(topKRecs.mkString("\n"))


    val movies=sc.textFile("file:///root/bigdata/IdeaProjects/MyMllib/data/ml-100k/u.item")
    val titles=movies.map(line=>line.split("\\|").take(2)).map(array=>(array(0).toInt,array(1))).collectAsMap()
//    println(titles(123))

//    指定用户接触到的电影
    val moviesForUser=ratings.keyBy(_.user).lookup(789)
//    moviesForUser.foreach(println)

//    指定用户评级最高的前10部电影
    moviesForUser.sortBy(-_.rating).take(10).map(rating=>(titles(rating.product),rating.rating)).foreach(println)
//    推荐的电影
    topKRecs.map(rating=>(titles(rating.product),rating.rating)).foreach(println)

  }

}

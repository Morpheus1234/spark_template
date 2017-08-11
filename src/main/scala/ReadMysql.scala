/**
  * Created by suqi on 17-8-11.
  */

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SQLContext, SparkSession, Row}
import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

import scala.util.matching.Regex

object ReadMysql {
  case class Rest(newsid: String, text: String)
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local").setAppName("NaiveBayesTest")
    val sc = new SparkContext(conf)

    val spark = SparkSession.builder.master("local").appName("Spark SQL basic example").config("spark.some.config.option", "some-value").getOrCreate()
    import spark.implicits._
    val url = "jdbc:mysql://192.168.0.202:3306/spider"
    val jdbcDF = spark.read
      .format("jdbc")
      .option("url", url)
      .option("user", "suqi")
      .option("password", "123456")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("dbtable", "_news_data_copy")
      .load()
    //這是一個dataframe
    //    jdbcDF.collect().take(20).foreach(println) //打印DF中的数据
    val reg = "<.*?>".r
    val content = jdbcDF.sqlContext.sql("select newsid, text from spider._news_data_copy").select("text").map(x => reg.replaceAllIn(x.toString, ""))

    content.show()
//    val content = jdbcDF.select("newsid", "text").map{attributes => Row(attributes(0), reg.replaceAllIn(attributes(1).toString, ""))}
    //    val str = "<p>应东盟轮值主席国老挝总理通伦邀请，国务院总理李克强于当地时间6日傍晚乘专机抵达万象瓦岱机场，出席第十九次中国—东盟（10+1）领导人会议暨中国—东盟建立对话关系25周年纪念峰会、第十九次东盟与中日韩（10+3）领导人会议和第十一届东亚峰会，并对老挝进行正式访问。</p>"
    //    val reg1 = "[\\u4e00-\\u9fa5]+([\\u4e00-\\u9fa5]|[\\（\\）\\《\\》\\——\\；\\，\\。\\“\\”\\<\\>\\！\\/])+([0-9]|[a-z]|[A-Z])+[\\u4e00-\\u9fa5]+".r
    //    val reg = "<.*?>".r
    //
    //    val str_clean = reg.findFirstIn(str)
    //    println(str_clean)
    //    println(reg replaceAllIn(str,""))
  }

}

/**
  * Created by root on 17-8-10.
  */
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import java.sql.{Connection,DriverManager,PreparedStatement,ResultSet}
import scala.util.matching.Regex
object ReadeMysql {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mysql").setMaster("local[4]")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)

    //定义mysql信息
    val jdbcDF = sqlContext.read.format("jdbc").options(
      Map("url"->"jdbc:mysql://192.168.0.202:3306/spider",
    "dbtable"->"(select text from _news_data_copy) as textTab",
    "driver"->"com.mysql.jdbc.Driver",
    "user"-> "spider",
    "password"->"spider-bigdata")).load()

    //這是一個dataframe阿
//    jdbcDF.collect().take(20).foreach(println) //打印DF中的数据
    jdbcDF.show(1)


//    val str = "<p>应东盟轮值主席国老挝总理通伦邀请，国务院总理李克强于当地时间6日傍晚乘专机抵达万象瓦岱机场，出席第十九次中国—东盟（10+1）领导人会议暨中国—东盟建立对话关系25周年纪念峰会、第十九次东盟与中日韩（10+3）领导人会议和第十一届东亚峰会，并对老挝进行正式访问。</p>"
//    val reg1 = "[\\u4e00-\\u9fa5]+([\\u4e00-\\u9fa5]|[\\（\\）\\《\\》\\——\\；\\，\\。\\“\\”\\<\\>\\！\\/])+([0-9]|[a-z]|[A-Z])+[\\u4e00-\\u9fa5]+".r
//    val reg = "<.*?>".r
//
//    val str_clean = reg.findFirstIn(str)
//    println(str_clean)
//    println(reg replaceAllIn(str,""))
  }
}

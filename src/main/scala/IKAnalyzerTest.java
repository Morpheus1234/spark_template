/**
 * Created by root on 17-8-7.
 */

import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class IKAnalyzerTest {
    public static void main(String[] args) {
        String keyWord = "IKAnalyzer的分词效果到底怎么样呢，我们来看一下吧";
        //创建IKAnalyzer中文分词对象
        IKAnalyzer analyzer = new IKAnalyzer();
        // 使用智能分词
        analyzer.setUseSmart(true);
        // 打印分词结果
        try {
            printAnalysisResult(analyzer, keyWord);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印出给定分词器的分词结果
     *
     * @param analyzer 分词器
     * @param keyWord  关键词
     * @throws Exception
     */
    private static void printAnalysisResult(Analyzer analyzer, String keyWord)
            throws Exception {
        System.out.println("[" + keyWord + "]分词效果如下");
        StringReader reader = new StringReader(keyWord);
        TokenStream tokenStream = analyzer.tokenStream("ts.txt",
                reader);
        try {

            tokenStream.reset();
            tokenStream.addAttribute(CharTermAttribute.class);

            while (tokenStream.incrementToken()) {
                CharTermAttribute charTermAttribute = tokenStream
                        .getAttribute(CharTermAttribute.class);
                System.out.println(charTermAttribute.toString());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tokenStream.close();
        }
    }

}

/**
 * Created by root on 17-8-7.
 */

import java.io.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class Test {
    public static void main(String[] args) throws IOException {
        String filePath = "/root/word1.txt";
//        String news = new String();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF8"));
        String str;
        while ((str = in.readLine()) != null) {
            IKAnalyzer analyzer = new IKAnalyzer(true);
            StringReader reader = new StringReader(str);
            TokenStream ts = analyzer.tokenStream("", reader);
            ts.reset();
            CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
            while (ts.incrementToken()) {
                System.out.print(term.toString() + ",");
            }

            System.out.println();
            StringReader re = new StringReader(str);
            IKSegmenter ik = new IKSegmenter(re, true);
            Lexeme lex = null;

            File f = new File("/root/jieguo");
            f.delete();
            String path = "/root/jieguo";  //%%%%%%

            FileWriter fw = new FileWriter(path, true);
            PrintWriter pw = new PrintWriter(fw);
            String line = "";


//            while(in.readLine() != null){
                pw.print("1,");
                try {
                    while ((lex = ik.next()) != null) {

                        System.out.print(lex.getLexemeText() + " ");
                        pw.print(lex.getLexemeText() + " ");
                    }
                    pw.close();
                    fw.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
//            }
            analyzer.close();
            reader.close();
        }
        in.close();


    }
}

package org.elasticsearch.query;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.CustomQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.junit.Test;

public class CustomQueryStringBuilderTest {

    @Test
    public void parseQuery() throws ParseException {
        CustomQueryStringBuilder customQueryStringBuilder = new CustomQueryStringBuilder("test");
        String defaultField = "*";
        String customQueryString = "(中华人民共和国万岁 || \"日德兰海战\") && 法兰西第三帝国";
        SmartChineseAnalyzer smartChineseAnalyzer = new SmartChineseAnalyzer(SmartChineseAnalyzer.getDefaultStopSet());
        CustomQueryParser customQueryParser = customQueryStringBuilder.parseQuery(defaultField, smartChineseAnalyzer);
        String convertQueryString = customQueryStringBuilder.convertQueryString(customQueryString, customQueryParser, defaultField);
        System.out.println(convertQueryString);
        QueryParser queryParser = new QueryParser(defaultField, new StandardAnalyzer());
        Query query = queryParser.parse(convertQueryString);
        assert query != null;
        System.out.println(query.toString());

    }

    // 中华人民共和国万岁    "中华人民共和国" || "万岁"
    // "中华人民共和国万岁"  "中华人民共和国" || "万岁"

    @Test
    public void parseBaseQuery() throws ParseException {
        String defaultField = "*";
        SmartChineseAnalyzer smartChineseAnalyzer = new SmartChineseAnalyzer(SmartChineseAnalyzer.getDefaultStopSet());
        QueryParser queryParser = new QueryParser(defaultField, smartChineseAnalyzer);
        Query query = queryParser.parse("\"日德兰海战\"");
        assert query != null;
        System.out.println(query.toString());
    }


    @Test
    public void parseStandardQuery() throws ParseException {
        String defaultField = "*";
        StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
        QueryParser queryParser = new QueryParser(defaultField, standardAnalyzer);
        Query query = queryParser.parse("\"中华人民共和国\"  \"万岁\"");
        assert query != null;
        System.out.println(query.toString());
    }

    @Test
    public void testQuery() throws ParseException {
        String defaultField = "*";
        String customQueryString = "(中华人民共和国万岁 || 美利坚合众国) && 法兰西第三帝国";
        SmartChineseAnalyzer smartChineseAnalyzer = new SmartChineseAnalyzer(SmartChineseAnalyzer.getDefaultStopSet());
        QueryParser customQueryParser = new QueryParser(defaultField, smartChineseAnalyzer);
        Query customQuery = customQueryParser.parse(customQueryString);
        System.out.println(customQuery.toString());
        if (customQuery instanceof PhraseQuery) {
            StringBuilder buffer = new StringBuilder();
            PhraseQuery phraseQuery = (PhraseQuery) customQuery;
            buffer.append("(");
            for (int i = 0; i < phraseQuery.getTerms().length; i++) {
                if (i > 0) {
                    buffer.append(" ");
                }
                buffer.append("\"");
                buffer.append(phraseQuery.getTerms()[i].text());
                buffer.append("\"");
            }
            buffer.append(")");
            customQueryString = buffer.toString();
            System.out.println(buffer.toString());
        }

        QueryParser queryParser = new QueryParser(defaultField, new StandardAnalyzer());
        Query query = queryParser.parse(customQueryString);
        assert query != null;
        System.out.println(query.toString());
    }

    @Test
    public void testPhraseQuery() {

    }
}
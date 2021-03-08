package org.apache.lucene.queryparser.classic;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public class CustomBooleanQuery extends Query {

    private BooleanQuery booleanQuery;

    public CustomBooleanQuery(BooleanQuery booleanQuery) {
        this.booleanQuery = booleanQuery;
    }

    public String toString(BooleanQuery booleanQuery, String field) {
        StringBuilder buffer = new StringBuilder();
        boolean needParens = booleanQuery.getMinimumNumberShouldMatch() > 0;
        if (needParens) {
            buffer.append("(");
        }

        int i = 0;
        for (BooleanClause c : booleanQuery.clauses()) {
            buffer.append(c.getOccur().toString());

            Query subQuery = c.getQuery();
            if (subQuery instanceof BooleanQuery) {
                BooleanQuery tempBooleanQuery = (BooleanQuery) subQuery;
                buffer.append("(");
                buffer.append(toString(tempBooleanQuery, field));
                buffer.append(")");
            } else if (subQuery instanceof TermQuery) {
                TermQuery termQuery = (TermQuery) subQuery;
                Term term = termQuery.getTerm();
                if (!term.field().equals(field)) {
                    buffer.append(term.field());
                    buffer.append(":");
                }
                buffer.append("\"");
                //TODO 扩展、过滤操作
                buffer.append(term.text());
                buffer.append("\"");
            } else if (subQuery instanceof PhraseQuery) {
                PhraseQuery phraseQuery = (PhraseQuery) subQuery;
                buffer.append("(");
                for (int j = 0; j < phraseQuery.getTerms().length; j++) {
                    if (j > 0) {
                        buffer.append(" ");
                    }
                    buffer.append("\"");
                    buffer.append(phraseQuery.getTerms()[j].text());
                    buffer.append("\"");
                }
                buffer.append(")");
            } else {
                buffer.append(subQuery.toString(field));
            }
            if (i != booleanQuery.clauses().size() - 1) {
                buffer.append(" ");
            }
            i += 1;
        }

        if (needParens) {
            buffer.append(")");
        }

        if (booleanQuery.getMinimumNumberShouldMatch() > 0) {
            buffer.append('~');
            buffer.append(booleanQuery.getMinimumNumberShouldMatch());
        }

        return buffer.toString();
    }

    @Override
    public String toString(String field) {
        return toString(booleanQuery, field);
    }

    @Override
    public boolean equals(Object obj) {
        return booleanQuery.equals(obj);
    }

    @Override
    public int hashCode() {
        return booleanQuery.hashCode();
    }
}

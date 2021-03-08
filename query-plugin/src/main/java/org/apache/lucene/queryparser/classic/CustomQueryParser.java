package org.apache.lucene.queryparser.classic;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CachingTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;

import java.io.IOException;

public class CustomQueryParser extends QueryParser {

    public CustomQueryParser(String f, Analyzer a) {
        super(f, a);
        setSplitOnWhitespace(true);
    }

    @Override
    protected Query createFieldQuery(TokenStream source, BooleanClause.Occur operator, String field, boolean quoted, int phraseSlop) {
        assert operator == BooleanClause.Occur.SHOULD || operator == BooleanClause.Occur.MUST;

        // Build an appropriate query based on the analysis chain.
        try (CachingTokenFilter stream = new CachingTokenFilter(source)) {

            TermToBytesRefAttribute termAtt = stream.getAttribute(TermToBytesRefAttribute.class);
            PositionIncrementAttribute posIncAtt = stream.addAttribute(PositionIncrementAttribute.class);
            PositionLengthAttribute posLenAtt = stream.addAttribute(PositionLengthAttribute.class);

            if (termAtt == null) {
                return null;
            }

            // phase 1: read through the stream and assess the situation:
            // counting the number of tokens/positions and marking if we have any synonyms.

            int numTokens = 0;
            int positionCount = 0;
            boolean hasSynonyms = false;
            boolean isGraph = false;

            stream.reset();
            while (stream.incrementToken()) {
                numTokens++;
                int positionIncrement = posIncAtt.getPositionIncrement();
                if (positionIncrement != 0) {
                    positionCount += positionIncrement;
                } else {
                    hasSynonyms = true;
                }

                int positionLength = posLenAtt.getPositionLength();
                if (enableGraphQueries && positionLength > 1) {
                    isGraph = true;
                }
            }

            // phase 2: based on token count, presence of synonyms, and options
            // formulate a single term, boolean, or phrase.

            if (numTokens == 0) {
                return null;
            } else if (numTokens == 1 && !quoted) {
                // single term
                return analyzeTerm(field, stream);
            } else if (isGraph) {
                // graph
                if (quoted) {
                    return analyzeGraphPhrase(stream, field, phraseSlop);
                } else {
                    return analyzeGraphBoolean(field, stream, operator);
                }
                //positionCount > 1 改为 positionCount>0
            } else if (quoted && positionCount > 0) {
                // phrase
                if (hasSynonyms) {
                    // complex phrase with synonyms
                    return analyzeMultiPhrase(field, stream, phraseSlop);
                } else {
                    // simple phrase
                    return analyzePhrase(field, stream, phraseSlop);
                }
            } else {
                // boolean
                if (positionCount == 1) {
                    // only one position, with synonyms
                    return analyzeBoolean(field, stream);
                } else {
                    // complex case: multiple positions
                    return analyzeMultiBoolean(field, stream, operator);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error analyzing query text", e);
        }
    }

    @Override
    protected String discardEscapeChar(String input) {
        return input;
    }
}

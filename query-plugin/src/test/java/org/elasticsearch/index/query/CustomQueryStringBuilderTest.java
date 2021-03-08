package org.elasticsearch.index.query;

import org.elasticsearch.query.CustomQueryStringBuilder;
import org.junit.Test;

import java.io.IOException;

public class CustomQueryStringBuilderTest {

    @Test
    public void doRewrite() throws IOException {
        CustomQueryStringBuilder queryStringQueryBuilder = new CustomQueryStringBuilder("test");
        queryStringQueryBuilder.doRewrite(null);
    }
}
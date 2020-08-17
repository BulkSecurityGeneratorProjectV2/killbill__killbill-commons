/*
 * Copyright 2004-2014 Brian McCallister
 * Copyright 2010-2014 Ning, Inc.
 * Copyright 2014-2020 Groupon, Inc
 * Copyright 2020-2020 Equinix, Inc
 * Copyright 2014-2020 The Billing Project, LLC
 *
 * The Billing Project licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.skife.jdbi.v2;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 *
 */
@Category(JDBITests.class)
public class TestEscapedCharacters
{
    private final ColonPrefixNamedParamStatementRewriter rewriter = new ColonPrefixNamedParamStatementRewriter();

    private String parseString(final String src)
    {
        return rewriter.parseString(src).getParsedSql();
    }

    @Test
    public void testSimpleString()
    {
        Assert.assertEquals("hello, world", parseString("hello, world"));
    }

    @Test
    public void testSimpleSql()
    {
        Assert.assertEquals("insert into foo (xyz) values (?)", parseString("insert into foo (xyz) values (:bar)"));
    }

    @Test
    public void testEscapedSql()
    {
        Assert.assertEquals("insert into foo (xyz) values (?::some_strange_type)", parseString("insert into foo (xyz) values (:bar\\:\\:some_strange_type)"));
    }
}

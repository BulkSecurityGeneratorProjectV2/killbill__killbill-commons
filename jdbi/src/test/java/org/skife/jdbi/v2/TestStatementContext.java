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

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.skife.jdbi.v2.tweak.StatementLocator;

import static org.junit.Assert.assertEquals;

/**
 *
 */
@Category(JDBITests.class)
public class TestStatementContext extends DBITestCase
{
    @Test
    public void testFoo() throws Exception
    {
        Handle h = openHandle();
        h.setStatementLocator(new StatementLocator() {

            @Override
            public String locate(String name, StatementContext ctx) throws Exception
            {
                return name.replaceAll("<table>", String.valueOf(ctx.getAttribute("table")));
            }
        });
        final int inserted = h.createStatement("insert into <table> (id, name) values (:id, :name)")
                .bind("id", 7)
                .bind("name", "Martin")
                .define("table", "something")
                .execute();
        assertEquals(1, inserted);

    }
}

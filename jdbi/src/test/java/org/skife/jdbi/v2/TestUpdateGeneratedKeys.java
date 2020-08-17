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
import org.skife.jdbi.v2.util.LongMapper;

import java.sql.Connection;
import java.sql.Statement;

@Category(JDBITests.class)
public class TestUpdateGeneratedKeys extends DBITestCase
{
    @Override
    protected void doSetUp() throws Exception
    {
        final Connection conn = DERBY_HELPER.getConnection();

        final Statement create = conn.createStatement();
        try
        {
            create.execute("create table something_else ( id integer not null generated always as identity, name varchar(50) )");
        }
        catch (Exception e)
        {
            // probably still exists because of previous failed test, just delete then
            create.execute("delete from something_else");
        }
        create.close();
        conn.close();
    }

    @Test
    public void testInsert() throws Exception
    {
        Handle h = openHandle();

        Update insert1 = h.createStatement("insert into something_else (name) values (:name)");
        insert1.bind("name", "Brian");
        Long id1 = insert1.executeAndReturnGeneratedKeys(LongMapper.FIRST).first();

        Assert.assertNotNull(id1);

        Update insert2 = h.createStatement("insert into something_else (name) values (:name)");
        insert2.bind("name", "Tom");
        Long id2 = insert2.executeAndReturnGeneratedKeys(LongMapper.FIRST).first();

        Assert.assertNotNull(id2);
        Assert.assertTrue(id2 > id1);
    }

    @Test
    public void testUpdate() throws Exception
    {
        Handle h = openHandle();

        Update insert = h.createStatement("insert into something_else (name) values (:name)");
        insert.bind("name", "Brian");
        Long id1 = insert.executeAndReturnGeneratedKeys(LongMapper.FIRST).first();

        Assert.assertNotNull(id1);

        Update update = h.createStatement("update something_else set name = :name where id = :id");
        update.bind("id", id1);
        update.bind("name", "Tom");
        Long id2 = update.executeAndReturnGeneratedKeys(LongMapper.FIRST).first();

        // https://issues.apache.org/jira/browse/DERBY-6742
        //Assert.assertNull(id2);
        Assert.assertEquals(0, (long) id2);
    }

    @Test
    public void testDelete() throws Exception
    {
        Handle h = openHandle();

        Update insert = h.createStatement("insert into something_else (name) values (:name)");
        insert.bind("name", "Brian");
        Long id1 = insert.executeAndReturnGeneratedKeys(LongMapper.FIRST).first();

        Assert.assertNotNull(id1);

        Update delete = h.createStatement("delete from something_else where id = :id");
        delete.bind("id", id1);
        Long id2 = delete.executeAndReturnGeneratedKeys(LongMapper.FIRST).first();

        Assert.assertNull(id2);
    }
}

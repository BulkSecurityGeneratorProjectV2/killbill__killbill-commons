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
package org.skife.jdbi.v2.sqlobject;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.JDBITests;
import org.skife.jdbi.v2.sqlobject.mixins.CloseMe;
import org.skife.jdbi.v2.tweak.HandleCallback;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@Category(JDBITests.class)
public class TestGetGeneratedKeys
{
    private JdbcConnectionPool ds;
    private DBI                dbi;

    @Before
    public void setUp() throws Exception
    {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:" + UUID.randomUUID(),
                                       "username",
                                       "password");
        dbi = new DBI(ds);
        dbi.withHandle(new HandleCallback<Object>()
        {
            @Override
            public Object withHandle(Handle handle) throws Exception
            {
                handle.execute("create table something (id identity primary key, name varchar(32))");
                return null;
            }
        });
    }

    @After
    public void tearDown() throws Exception
    {
        ds.dispose();
    }

    public static interface DAO extends CloseMe
    {
        @SqlUpdate("insert into something (name) values (:it)")
        @GetGeneratedKeys
        public long insert(@Bind String name);

        @SqlQuery("select name from something where id = :it")
        public String findNameById(@Bind long id);
    }

    @Test
    public void testFoo() throws Exception
    {
        DAO dao = dbi.open(DAO.class);

        long brian_id = dao.insert("Brian");
        long keith_id = dao.insert("Keith");

        assertThat(dao.findNameById(brian_id), equalTo("Brian"));
        assertThat(dao.findNameById(keith_id), equalTo("Keith"));

        dao.close();
    }

}

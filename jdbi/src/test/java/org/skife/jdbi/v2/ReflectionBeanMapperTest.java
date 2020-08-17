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

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.*;


@RunWith(EasyMockRunner.class)
@Category(JDBITests.class)
public class ReflectionBeanMapperTest {

    @Mock
    ResultSet resultSet;

    @Mock
    ResultSetMetaData resultSetMetaData;

    @Mock
    StatementContext ctx;

    ReflectionBeanMapper<SampleBean> mapper = new ReflectionBeanMapper<SampleBean>(SampleBean.class);

    @Test
    public void shouldSetValueOnPrivateField() throws Exception {

        expect(resultSetMetaData.getColumnCount()).andReturn(1).anyTimes();
        expect(resultSetMetaData.getColumnLabel(1)).andReturn("longField");
        replay(resultSetMetaData);

        expect(resultSet.getMetaData()).andReturn(resultSetMetaData);
        Long aLongVal = 100l;
        expect(resultSet.getLong(1)).andReturn(aLongVal);
        expect(resultSet.wasNull()).andReturn(false);
        replay(resultSet);

        SampleBean sampleBean = mapper.map(0, resultSet, ctx);

        assertSame(aLongVal, sampleBean.getLongField());
    }

    @Test
    public void shouldHandleEmptyResult() throws Exception {
        expect(resultSetMetaData.getColumnCount()).andReturn(0);
        replay(resultSetMetaData);
        expect(resultSet.getMetaData()).andReturn(resultSetMetaData);
        replay(resultSet);

        SampleBean sampleBean = mapper.map(0, resultSet, ctx);

        assertNotNull(sampleBean);
    }

    @Test
    public void shouldBeCaseInSensitiveOfColumnAndFieldNames() throws Exception {
        expect(resultSetMetaData.getColumnCount()).andReturn(1).anyTimes();
        expect(resultSetMetaData.getColumnLabel(1)).andReturn("LoNgfielD");
        replay(resultSetMetaData);

        expect(resultSet.getMetaData()).andReturn(resultSetMetaData);
        Long aLongVal = 100l;
        expect(resultSet.getLong(1)).andReturn(aLongVal);
        expect(resultSet.wasNull()).andReturn(false);
        replay(resultSet);
        SampleBean sampleBean = mapper.map(0, resultSet, ctx);

        assertSame(aLongVal, sampleBean.getLongField());

    }

    @Test
    public void shouldHandleNullValue() throws Exception {
        expect(resultSetMetaData.getColumnCount()).andReturn(1).anyTimes();
        expect(resultSetMetaData.getColumnLabel(1)).andReturn("LoNgfielD");
        replay(resultSetMetaData);

        expect(resultSet.getMetaData()).andReturn(resultSetMetaData);
        expect(resultSet.getLong(1)).andReturn(0l);
        expect(resultSet.wasNull()).andReturn(true);
        replay(resultSet);

        SampleBean sampleBean = mapper.map(0, resultSet, ctx);

        assertNull(sampleBean.getLongField());

    }

    @Test
    public void shouldSetValuesOnAllFieldAccessTypes() throws Exception {

        expect(resultSetMetaData.getColumnCount()).andReturn(4).anyTimes();
        expect(resultSetMetaData.getColumnLabel(1)).andReturn("longField");
        expect(resultSetMetaData.getColumnLabel(2)).andReturn("stringField");
        expect(resultSetMetaData.getColumnLabel(3)).andReturn("intField");
        expect(resultSetMetaData.getColumnLabel(4)).andReturn("bigDecimalField");
        replay(resultSetMetaData);

        expect(resultSet.getMetaData()).andReturn(resultSetMetaData);
        Long aLongVal = 100l;
        String aStringVal = "something";
        int aIntVal = 1;
        BigDecimal aBigDecimal = BigDecimal.TEN;

        expect(resultSet.getLong(1)).andReturn(aLongVal);
        expect(resultSet.getString(2)).andReturn(aStringVal);
        expect(resultSet.getInt(3)).andReturn(aIntVal);
        expect(resultSet.getBigDecimal(4)).andReturn(aBigDecimal);
        expect(resultSet.wasNull()).andReturn(false).anyTimes();
        replay(resultSet);

        SampleBean sampleBean = mapper.map(0, resultSet, ctx);

        assertSame(aLongVal, sampleBean.getLongField());
        assertSame(aBigDecimal, sampleBean.getBigDecimalField());
        assertSame(aIntVal, sampleBean.getIntField());
        assertSame(aStringVal, sampleBean.getStringField());
    }

    @Test
    public void shouldSetValuesInSuperClassFields() throws Exception {

        expect(resultSetMetaData.getColumnCount()).andReturn(2).anyTimes();
        expect(resultSetMetaData.getColumnLabel(1)).andReturn("longField");
        expect(resultSetMetaData.getColumnLabel(2)).andReturn("blongField");
        replay(resultSetMetaData);

        expect(resultSet.getMetaData()).andReturn(resultSetMetaData);
        Long aLongVal = 100l;
        Long bLongVal = 200l;

        expect(resultSet.getLong(1)).andReturn(aLongVal);
        expect(resultSet.getLong(2)).andReturn(bLongVal);
        expect(resultSet.wasNull()).andReturn(false).anyTimes();
        replay(resultSet);

        ReflectionBeanMapper<DerivedBean> mapper = new ReflectionBeanMapper<DerivedBean>(DerivedBean.class);

        DerivedBean derivedBean = mapper.map(0, resultSet, ctx);

        assertEquals(aLongVal, derivedBean.getLongField());
        assertEquals(bLongVal, derivedBean.getBlongField());
    }
}


class SampleBean {
    private Long longField;
    protected String stringField;
    public int intField;
    BigDecimal bigDecimalField;

    public Long getLongField() {
        return longField;
    }

    public String getStringField() {
        return stringField;
    }

    public int getIntField() {
        return intField;
    }

    public BigDecimal getBigDecimalField() {
        return bigDecimalField;
    }
}

class DerivedBean extends SampleBean {
    private Long blongField;

    public Long getBlongField() {
        return blongField;
    }
}

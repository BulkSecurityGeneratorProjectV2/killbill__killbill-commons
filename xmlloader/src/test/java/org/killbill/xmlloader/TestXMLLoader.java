/*
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

package org.killbill.xmlloader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import static org.testng.Assert.assertEquals;

public class TestXMLLoader {

    public static final String TEST_XML =
            "<xmlTestClass>" +
            "	<foo>foo</foo>" +
            "	<bar>1.0</bar>" +
            "	<lala>42</lala>" +
            "</xmlTestClass>";

    @Test(groups = "fast")
    public void test() throws SAXException, JAXBException, IOException, TransformerException, ValidationException {
        final InputStream is = new ByteArrayInputStream(TEST_XML.getBytes());
        final XmlTestClass test = XMLLoader.getObjectFromStream(is, XmlTestClass.class);
        assertEquals(test.getFoo(), "foo");
        //noinspection RedundantCast
        assertEquals((double) test.getBar(), 1.0);
        assertEquals((double) test.getLala(), 42);
    }
}

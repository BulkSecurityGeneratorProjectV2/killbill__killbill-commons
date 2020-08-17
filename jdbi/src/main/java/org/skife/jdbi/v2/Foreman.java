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

import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

class Foreman
{
    private final Map<Class<?>, ArgumentFactory> cache = new ConcurrentHashMap<Class<?>, ArgumentFactory>();
    private final List<ArgumentFactory> factories = new CopyOnWriteArrayList<ArgumentFactory>();

    public Foreman()
    {
        factories.add(BUILT_INS);
    }

    public Foreman(List<ArgumentFactory> factories)
    {
        this.factories.addAll(factories);
    }

    Argument waffle(Class expectedType, Object it, StatementContext ctx)
    {
        if (cache.containsKey(expectedType)) {
            return cache.get(expectedType).build(expectedType, it, ctx);
        }

        ArgumentFactory candidate = null;

        for (int i = factories.size() - 1; i >= 0; i--) {
            ArgumentFactory factory = factories.get(i);
            if (factory.accepts(expectedType, it, ctx)) {
                // Note! Cache assumes all ArgumentFactory#accepts implementations don't care about the Object it itself
                cache.put(expectedType, factory);
                return factory.build(expectedType, it, ctx);
            }
            // Fall back to any factory accepting Object if necessary but
            // prefer any more specific factory first.
            if (candidate == null && factory.accepts(Object.class, it, ctx)) {
                candidate = factory;
            }
        }
        if (candidate != null) {
            cache.put(Object.class, candidate);
            return candidate.build(Object.class, it, ctx);
        }

        throw new IllegalStateException("Unbindable argument passed: " + String.valueOf(it));
    }

    private static final ArgumentFactory BUILT_INS = new BuiltInArgumentFactory();

    public void register(ArgumentFactory<?> argumentFactory)
    {
        // [OPTIMIZATION] Only allowed at a global level (DBI)
        factories.add(argumentFactory);
    }

    public Foreman createChild()
    {
        // [OPTIMIZATION] See above
        //return new Foreman(factories);
        return this;
    }


}

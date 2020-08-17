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

package org.killbill.commons.concurrent;

import org.killbill.commons.profiling.Profiling;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


public class WithProfilingThreadPoolExecutor  extends DynamicThreadPoolExecutorWithLoggingOnExceptions {


    public WithProfilingThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final String name, final long keepAliveTime, final TimeUnit unit) {
        super(corePoolSize, maximumPoolSize, name, keepAliveTime, unit);
    }

    public WithProfilingThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadFactory);
    }

    public WithProfilingThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final String name, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, name, keepAliveTime, unit, workQueue);
    }

    public WithProfilingThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, final ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public WithProfilingThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final String name, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, final RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, name, keepAliveTime, unit, workQueue, handler);
    }

    public WithProfilingThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue, final ThreadFactory threadFactory, final RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }


    protected void beforeExecute(final Thread t, final Runnable runnable) {
        // Allocate the thread local data
        Profiling.setPerThreadProfilingData();

        super.beforeExecute(t, runnable);
    }

    protected void afterExecute(final Runnable runnable, final Throwable exception) {
        super.afterExecute(runnable, exception);

        // Clear the thread local data
        Profiling.resetPerThreadProfilingData();
    }
}

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

package org.killbill.bus.api;


import java.util.UUID;

import org.killbill.queue.api.QueueEvent;

/**
 * Base interface for all bus/notiication  events
 */
public interface BusEvent extends QueueEvent {

    /**
     *
     * @return the search key1
     */
    public Long getSearchKey1();

    /**
     *
     * @return the search key2
     */
    public Long getSearchKey2();

    /**
     *
     * @return the user token
     */
    public UUID getUserToken();
}

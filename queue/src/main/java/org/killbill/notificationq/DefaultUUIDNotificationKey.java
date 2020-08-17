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

package org.killbill.notificationq;

import java.util.UUID;

import org.killbill.notificationq.api.NotificationEvent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DefaultUUIDNotificationKey implements NotificationEvent {

    private final UUID uuidKey;

    @JsonCreator
    public DefaultUUIDNotificationKey(@JsonProperty("uuidKey") final UUID uuidKey) {
        this.uuidKey = uuidKey;
    }

    public UUID getUuidKey() {
        return uuidKey;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uuidKey == null) ? 0 : uuidKey.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultUUIDNotificationKey other = (DefaultUUIDNotificationKey) obj;
        if (uuidKey == null) {
            if (other.uuidKey != null) {
                return false;
            }
        } else if (!uuidKey.equals(other.uuidKey)) {
            return false;
        }
        return true;
    }
}

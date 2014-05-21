/*
 * Copyright 2010-2011 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
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

import java.io.PrintStream;

public class ValidationException extends Exception {
    private final ValidationErrors errors;

    ValidationException(final ValidationErrors errors) {
        this.errors = errors;
    }

    public ValidationErrors getErrors() {
        return errors;
    }

    @Override
    public void printStackTrace(final PrintStream arg0) {
        arg0.print(errors.toString());
        super.printStackTrace(arg0);
    }


}


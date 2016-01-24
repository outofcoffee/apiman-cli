/*
 * Copyright 2016 Pete Cornish
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.cli.server.factory;

/**
 * @param <T> the API interface
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public interface ServerApiFactory<T> {
    /**
     * @param username     the management API username
     * @param password     the management API password
     * @param debugLogging whether debug logging should be enabled
     * @return an API client for the given Class
     */
    T build(String endpoint, String username, String password, boolean debugLogging);
}
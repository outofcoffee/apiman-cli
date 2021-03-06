/*
 * Copyright 2017 Pete Cornish
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

package io.apiman.cli.util;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

/**
 * Shared JavaBean utility methods.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public final class BeanUtil {
    private BeanUtil() {
    }

    /**
     * Parse the {@code replacements} into a Map.
     *
     * @param replacements the placeholders and their values in the form <code>key=value</code>
     * @return the replacements Map
     */
    public static Map<String, String> parseReplacements(Collection<String> replacements) {
        return ofNullable(replacements).orElse(emptyList()).stream()
                .map(keyValue -> keyValue.split("="))
                .collect(Collectors.toMap(kv -> kv[0], kv -> (kv.length >= 2 ? kv[1] : "")));
    }

    /**
     * Replace the placeholders in the given input String.
     *
     * @param original     the input String, containing placeholders in the form <code>Example ${placeholder} text.</code>
     * @param replacements the placeholders and their values in the form <code>key=value</code>
     * @return the {@code original} string with {@code replacements}
     */
    public static String resolvePlaceholders(String original, Collection<String> replacements) {
        return resolvePlaceholders(original, parseReplacements(replacements));
    }

    /**
     * Replace the placeholders in the given input String.
     *
     * @param original     the input String, containing placeholders in the form <code>Example ${placeholder} text.</code>
     * @param replacements the Map of placeholders and their values
     * @return the {@code original} string with {@code replacements}
     */
    public static String resolvePlaceholders(String original, Map<String, String> replacements) {
        return StrSubstitutor.replace(original, replacements);
    }

    /**
     * Whether two nullable objects are equal.
     *
     * @param o1 a nullable object
     * @param o2 a nullable object
     * @return <code>true</code> if objects are equal, otherwise <code>false</code>
     */
    public static <T> boolean safeEquals(T o1, T o2) {
        return (null == o1 && null == o2) ||
                ofNullable(o1).filter(o -> o.equals(o2)).isPresent();
    }

    /**
     * Shallow copy of fields from a source object to a target, subject to the following conditions:
     * <ul>
     *     <li>matching name</li>
     *     <li>matching type per {@link BeanUtilsBean}</li>
     *     <li>the target field is null</li>
     * </ul>
     *
     * @param source the source object
     * @param target the target object
     */
    public static void shallowCopyToNonNullFields(Object source, Object target) {
        try {
            final Set<String> nullTargetProperties = PropertyUtils.describe(target).entrySet().stream()
                    .filter(dest -> dest.getValue() == null)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

            final BeanUtilsBean propertyCopier = new BeanUtilsBean() {
                @Override
                public void copyProperty(Object target, String name, Object value) throws IllegalAccessException, InvocationTargetException {
                    // only copy if target of the same name is null
                    if (nullTargetProperties.contains(name)) {
                        super.copyProperty(target, name, value);
                    }
                }
            };
            propertyCopier.copyProperties(target, source);

        } catch (Exception e) {
            throw new RuntimeException(String.format("Error copying fields from %s to %s", source, target), e);
        }
    }
}

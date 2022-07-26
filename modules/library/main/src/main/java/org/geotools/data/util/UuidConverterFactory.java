/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.data.util;

import java.util.UUID;
import org.geotools.util.Converter;
import org.geotools.util.ConverterFactory;
import org.geotools.util.factory.Hints;

/**
 * ConverterFactory for handling uuid (uniqueidentifier) conversions.
 *
 * <p>Supported conversions:
 *
 * <ul>
 *   <li>string -> UUID
 *   <li>byte[] -> UUID
 * </ul>
 *
 * @author Andrea Briganti <kbyte@ciotoni.net>
 */
public class UuidConverterFactory implements ConverterFactory {

    @Override
    public Converter createConverter(Class source, Class target, Hints hints) {
        if (target.equals(UUID.class)) {

            // string to boolean
            if (source.equals(String.class)) {
                return new Converter() {

                    @Override
                    public <T> T convert(Object source, Class<T> target) throws Exception {
                        return target.cast(UUID.fromString((String) source));
                    }
                };
            }

            // integer to boolean
            if (source.equals(byte[].class)) {
                return new Converter() {

                    @Override
                    public <T> T convert(Object source, Class<T> target) throws Exception {
                        return target.cast(UUID.nameUUIDFromBytes((byte[]) source));
                    }
                };
            }
        }

        return null;
    }
}

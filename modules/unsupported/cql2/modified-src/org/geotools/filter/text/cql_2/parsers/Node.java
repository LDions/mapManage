/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.filter.text.cql_2.parsers;

/**
 * This interface extends the functionalities provided by the default 
 * implementation generated by javacc.
 * 
 * @author Mauricio Pazos (Axios Engineering)
 * @since 2.5
 *
 *

 */
public interface Node {

    public abstract void jjtOpen();

    public abstract void jjtClose();

    public abstract void jjtSetParent(Node n);

    public abstract Node jjtGetParent();

    public abstract void jjtAddChild(Node n, int i);

    public abstract Node jjtGetChild(int i);

    public abstract int jjtGetNumChildren();

    public abstract String toString();

    public abstract String toString(String prefix);

    public abstract void dump(String prefix);

    public abstract int getType();

    public abstract void dispose();

    public abstract Token getToken();
}

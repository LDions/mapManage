/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2019, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.filter.visitor;

import org.opengis.filter.And;
import org.opengis.filter.ExcludeFilter;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.Id;
import org.opengis.filter.IncludeFilter;
import org.opengis.filter.NativeFilter;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNil;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.NilExpression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;
import org.opengis.filter.temporal.After;
import org.opengis.filter.temporal.AnyInteracts;
import org.opengis.filter.temporal.Before;
import org.opengis.filter.temporal.Begins;
import org.opengis.filter.temporal.BegunBy;
import org.opengis.filter.temporal.BinaryTemporalOperator;
import org.opengis.filter.temporal.During;
import org.opengis.filter.temporal.EndedBy;
import org.opengis.filter.temporal.Ends;
import org.opengis.filter.temporal.Meets;
import org.opengis.filter.temporal.MetBy;
import org.opengis.filter.temporal.OverlappedBy;
import org.opengis.filter.temporal.TContains;
import org.opengis.filter.temporal.TEquals;
import org.opengis.filter.temporal.TOverlaps;

/**
 * SearchFilterVisitor is a base class used to optimize finding specific information in the filter
 * data structure.
 *
 * <p>This differs slightly form the DefaultFilterVisitor case in that you can abandon the depth
 * first traversal at any point by returning true from found( object ).
 *
 * <p>Most implementations accept the default functionality which simply checks if data is non null.
 * This allows you to simply return an object from any method the moment you have found what you are
 * looking for.
 *
 * @author Jody Garnett
 */
public abstract class AbstractSearchFilterVisitor implements FilterVisitor, ExpressionVisitor {
    /**
     * Check if data is found (ie non null).
     *
     * @return true if the item is found and we can now stop
     */
    protected boolean found(Object data) {
        return data != null; // we found it!
    }

    @Override
    public Object visit(ExcludeFilter filter, Object data) {
        return data;
    }

    @Override
    public Object visit(IncludeFilter filter, Object data) {
        return data;
    }

    @Override
    public Object visit(And filter, Object data) {
        if (found(data)) {
            return data; // short cut
        }
        if (filter.getChildren() != null) {
            for (Filter child : filter.getChildren()) {
                data = child.accept(this, data);
                if (found(data)) {
                    return data;
                }
            }
        }
        return data;
    }

    @Override
    public Object visit(Id filter, Object data) {
        return data;
    }

    @Override
    public Object visit(Not filter, Object data) {
        Filter child = filter.getFilter();
        if (child != null) {
            data = child.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(Or filter, Object data) {
        if (found(data)) {
            return data; // short cut
        }
        if (filter.getChildren() != null) {
            for (Filter child : filter.getChildren()) {
                data = child.accept(this, data);
            }
            if (found(data)) {
                return data;
            }
        }
        return data;
    }

    @Override
    public Object visit(PropertyIsBetween filter, Object data) {
        data = filter.getLowerBoundary().accept(this, data);
        if (found(data)) return data;

        data = filter.getExpression().accept(this, data);
        if (found(data)) return data;

        data = filter.getUpperBoundary().accept(this, data);
        return data;
    }

    @Override
    public Object visit(PropertyIsEqualTo filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        if (found(data)) return data;

        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(PropertyIsNotEqualTo filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        if (found(data)) return data;

        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(PropertyIsGreaterThan filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(PropertyIsGreaterThanOrEqualTo filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(PropertyIsLessThan filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(PropertyIsLessThanOrEqualTo filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(PropertyIsLike filter, Object data) {
        data = filter.getExpression().accept(this, data);

        return data;
    }

    @Override
    public Object visit(PropertyIsNull filter, Object data) {
        data = filter.getExpression().accept(this, data);
        return data;
    }

    @Override
    public Object visit(PropertyIsNil filter, Object data) {
        data = filter.getExpression().accept(this, data);
        return data;
    }

    @Override
    public Object visit(final BBOX filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(Beyond filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(Contains filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(Crosses filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(Disjoint filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(DWithin filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(Equals filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(Intersects filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(Overlaps filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(Touches filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit(Within filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visitNullFilter(Object data) {
        return data;
    }

    @Override
    public Object visit(NilExpression expression, Object data) {
        return data;
    }

    @Override
    public Object visit(Add expression, Object data) {
        data = expression.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = expression.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(Divide expression, Object data) {
        data = expression.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = expression.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(Function expression, Object data) {
        if (found(data)) return data;
        if (expression.getParameters() != null) {
            for (Expression parameter : expression.getParameters()) {
                data = parameter.accept(this, data);
                if (found(data)) return data;
            }
        }
        return data;
    }

    @Override
    public Object visit(Literal expression, Object data) {
        return data;
    }

    @Override
    public Object visit(Multiply expression, Object data) {
        data = expression.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = expression.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(PropertyName expression, Object data) {
        return data;
    }

    @Override
    public Object visit(Subtract expression, Object data) {
        data = expression.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = expression.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(After after, Object extraData) {
        return visit((BinaryTemporalOperator) after, extraData);
    }

    @Override
    public Object visit(AnyInteracts anyInteracts, Object extraData) {
        return visit((BinaryTemporalOperator) anyInteracts, extraData);
    }

    @Override
    public Object visit(Before before, Object extraData) {
        return visit((BinaryTemporalOperator) before, extraData);
    }

    @Override
    public Object visit(Begins begins, Object extraData) {
        return visit((BinaryTemporalOperator) begins, extraData);
    }

    @Override
    public Object visit(BegunBy begunBy, Object extraData) {
        return visit((BinaryTemporalOperator) begunBy, extraData);
    }

    @Override
    public Object visit(During during, Object extraData) {
        return visit((BinaryTemporalOperator) during, extraData);
    }

    @Override
    public Object visit(EndedBy endedBy, Object extraData) {
        return visit((BinaryTemporalOperator) endedBy, extraData);
    }

    @Override
    public Object visit(Ends ends, Object extraData) {
        return visit((BinaryTemporalOperator) ends, extraData);
    }

    @Override
    public Object visit(Meets meets, Object extraData) {
        return visit((BinaryTemporalOperator) meets, extraData);
    }

    @Override
    public Object visit(MetBy metBy, Object extraData) {
        return visit((BinaryTemporalOperator) metBy, extraData);
    }

    @Override
    public Object visit(OverlappedBy overlappedBy, Object extraData) {
        return visit((BinaryTemporalOperator) overlappedBy, extraData);
    }

    @Override
    public Object visit(TContains contains, Object extraData) {
        return visit((BinaryTemporalOperator) contains, extraData);
    }

    @Override
    public Object visit(TEquals equals, Object extraData) {
        return visit((BinaryTemporalOperator) equals, extraData);
    }

    @Override
    public Object visit(TOverlaps contains, Object extraData) {
        return visit((BinaryTemporalOperator) contains, extraData);
    }

    protected Object visit(BinaryTemporalOperator filter, Object data) {
        data = filter.getExpression1().accept(this, data);
        if (found(data)) return data;
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit(NativeFilter filter, Object data) {
        return data;
    }
}

/*
 Copyright 2016 Goldman Sachs.
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */

package com.gs.fw.common.mithra.finder.doubleop;

import com.gs.fw.common.mithra.attribute.DoubleAttribute;
import com.gs.fw.common.mithra.extractor.DoubleExtractor;
import com.gs.fw.common.mithra.extractor.Extractor;
import com.gs.fw.common.mithra.extractor.OperationParameterExtractor;
import com.gs.fw.common.mithra.finder.AtomicEqualityOperation;
import com.gs.fw.common.mithra.finder.SqlParameterSetter;
import com.gs.fw.common.mithra.finder.SqlQuery;
import com.gs.fw.common.mithra.util.HashUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


public class DoubleEqOperation  extends AtomicEqualityOperation implements SqlParameterSetter
{
    private double parameter;

    public DoubleEqOperation(DoubleAttribute attribute, double parameter)
    {
        super(attribute);
        this.parameter = parameter;
    }

    protected Boolean matchesWithoutDeleteCheck(Object o)
    {
        DoubleAttribute doubleAttribute = (DoubleAttribute)this.getAttribute();
        if (doubleAttribute.isAttributeNull(o)) return false;
        return doubleAttribute.doubleValueOf(o) == parameter;
    }

    protected List getByIndex()
    {
        return this.getCache().get(this.getIndexRef(), parameter);
    }

    public int setSqlParameters(PreparedStatement pstmt, int startIndex, SqlQuery query) throws SQLException
    {
        pstmt.setDouble(startIndex, parameter);
        return 1;
    }

    public int hashCode()
    {
        return this.getAttribute().hashCode() ^ HashUtil.hash(this.parameter);
    }

    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj instanceof DoubleEqOperation)
        {
            DoubleEqOperation other = (DoubleEqOperation) obj;
            return this.parameter == other.parameter && this.getAttribute().equals(other.getAttribute());
        }
        return false;
    }

    @Override
    public int getParameterHashCode()
    {
        return HashUtil.hash(this.parameter);
    }

    public Object getParameterAsObject()
    {
        return new Double(this.parameter);
    }

    @Override
    public boolean parameterValueEquals(Object other, Extractor extractor)
    {
        if (extractor.isAttributeNull(other)) return false;
        return ((DoubleExtractor) extractor).doubleValueOf(other) == this.parameter;
    }

    public double getParameter()
    {
        return parameter;
    }

    public Extractor getParameterExtractor()
    {
        return new ParameterExtractor();
    }

    protected class ParameterExtractor extends OperationParameterExtractor implements DoubleExtractor
    {
        public double doubleValueOf(Object o)
        {
            return getParameter();
        }

        public Object valueOf(Object o)
        {
            return new Double(getParameter());
        }

        public int valueHashCode(Object o)
        {
            return HashUtil.hash(getParameter());
        }

        public boolean valueEquals(Object first, Object second)
        {
            return true;
        }

        public boolean valueEquals(Object first, Object second, Extractor secondExtractor)
        {
            return !secondExtractor.isAttributeNull(second) && ((DoubleExtractor) secondExtractor).doubleValueOf(second) == getParameter();
        }
    }
}

/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
package org.egov.infstr.models;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

/*
 * Hibernate 6 Custom UserType Interface Refactoring:
 * Replaced legacy Hibernate 5 org.hibernate.usertype.UserType implementation with Hibernate 6 typed UserType<Object>:
 * 1. Interface parameterization: implements UserType<Object>
 * 2. Method signature change: replaced int[] sqlTypes() with int getSqlType() returning Types.VARCHAR
 * 3. Session contract change: replaced SessionImplementor with SharedSessionContractImplementor across nullSafeGet & nullSafeSet
 */
public class EnumUserType implements UserType<Object>, ParameterizedType {

	private Class clazz = null;

	public void setParameterValues(Properties params) {
		String enumClassName = params.getProperty("enumClassName");
		if (enumClassName == null) {
			throw new MappingException("enumClassName parameter not specified");
		}
		try {
			this.clazz = Class.forName(enumClassName);
		} catch (java.lang.ClassNotFoundException e) {
			throw new MappingException("enumClass " + enumClassName + " not found", e);
		}
	}

	@Override
	public int getSqlType() {
		return Types.VARCHAR;
	}

	@Override
	public Class<Object> returnedClass() {
		return clazz;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		if (x == y) return true;
		if (null == x || null == y) return false;
		return x.equals(y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object nullSafeGet(ResultSet resultSet, int position, SharedSessionContractImplementor session, Object owner)
			throws SQLException {
		String name = resultSet.getString(position);
		if (resultSet.wasNull()) return null;
		return Enum.valueOf(clazz, name);
	}

	@Override
	public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index,
			SharedSessionContractImplementor session) throws SQLException {
		if (null == value) {
			preparedStatement.setNull(index, Types.VARCHAR);
		} else {
			preparedStatement.setString(index, value.toString());
		}
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}
}

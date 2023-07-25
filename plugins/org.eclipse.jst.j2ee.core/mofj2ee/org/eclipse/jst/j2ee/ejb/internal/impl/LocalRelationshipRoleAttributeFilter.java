/*******************************************************************************
 * Copyright (c) 2001, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.j2ee.ejb.internal.impl;

import org.eclipse.jst.j2ee.ejb.ContainerManagedEntity;

/**
 * This filter will return a subset of the persistentAttributes from the 
 * ContainerManagedEntityExtension that is passed as an argument to the filter method.
 * It will remove any attribute that is not pointed to by an EjbRelationshipRole and
 * it will further remove attributes pointed to by inherited EjbRelationshipRoles.
 * Thus, the returned list will contain attributes that only belong to local
 * EjbRelationshipRoles (not including iherited roles).
 * Creation date: (11/28/2000 6:46:08 PM)
 * @author: Administrator
 */
public class LocalRelationshipRoleAttributeFilter extends AbstractRelationshipRoleAttributeFilter {
	private static LocalRelationshipRoleAttributeFilter singleton;
/**
 * Return the proper list of roles from cmpExt.
 */
@Override
protected java.util.List getSourceRoles(ContainerManagedEntity cmp) {
   return getRelationshipRoles(cmp); 
}
/**
 * Insert the method's description here.
 * Creation date: (11/28/2000 5:36:00 PM)
 * @return com.ibm.ejs.models.base.extensions.ejbext.impl.LocalRelationshipRoleAttributeFilter
 */
public static LocalRelationshipRoleAttributeFilter singleton() {
	if (singleton == null)
		singleton = new LocalRelationshipRoleAttributeFilter();
	return singleton;
}
}


















































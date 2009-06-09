/*******************************************************************************
 * Copyright (c) 2008 SAP AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Kaloyan Raev, kaloyan.raev@sap.com - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.j2ee.ejb.internal.operations;

import static org.eclipse.jst.j2ee.ejb.internal.operations.INewEnterpriseBeanClassDataModelProperties.EJB_NAME;
import static org.eclipse.jst.j2ee.ejb.internal.operations.INewEnterpriseBeanClassDataModelProperties.MAPPED_NAME;
import static org.eclipse.jst.j2ee.ejb.internal.operations.INewEnterpriseBeanClassDataModelProperties.TRANSACTION_TYPE;
import static org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties.CLASS_NAME;
import static org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties.JAVA_PACKAGE;
import static org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties.SUPERCLASS;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.j2ee.ejb.internal.plugin.EjbPlugin;
import org.eclipse.jst.j2ee.internal.common.operations.NewJavaClassDataModelProvider;
import org.eclipse.jst.j2ee.internal.ejb.project.operations.EJBCreationResourceHandler;
import org.eclipse.jst.j2ee.model.IModelProvider;
import org.eclipse.jst.j2ee.model.ModelProviderManager;
import org.eclipse.jst.javaee.ejb.EJBJar;
import org.eclipse.jst.javaee.ejb.EnterpriseBeans;
import org.eclipse.jst.javaee.ejb.SessionBean;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

public class NewEnterpriseBeanClassDataModelProvider extends NewJavaClassDataModelProvider {

	/**
	 * Subclasses may extend this method to add their own data model's properties as valid base
	 * properties.
	 * 
	 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider#getPropertyNames()
	 */
	@Override
	public Set<String> getPropertyNames() {
		// Add Bean specific properties defined in this data model
		Set<String> propertyNames = super.getPropertyNames();

		propertyNames.add(EJB_NAME);
		propertyNames.add(MAPPED_NAME);
		propertyNames.add(TRANSACTION_TYPE);

		return propertyNames;
	}

	/**
	 * Subclasses may extend this method to provide their own default values for any of the
	 * properties in the data model hierarchy. This method does not accept a null parameter. It may
	 * return null. 
	 * 
	 * @see NewJavaClassDataModelProvider#getDefaultProperty(String)
	 * @see IDataModelProvider#getDefaultProperty(String)
	 * 
	 * @param propertyName
	 * @return Object default value of property
	 */
	@Override
	public Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(EJB_NAME)) {
			String className = getStringProperty(CLASS_NAME);
			int index = className.lastIndexOf("."); //$NON-NLS-1$
			className = className.substring(index+1);
			return className;
		} else if (propertyName.equals(SUPERCLASS)){
			return "";
		} else if (propertyName.equals(TRANSACTION_TYPE)) {
			return TransactionType.CONTAINER.toString();
		} 

		// Otherwise check super for default value for property
		return super.getDefaultProperty(propertyName);
	}

	@Override
	public IStatus validate(String propertyName) {

		if (propertyName.equals(SUPERCLASS)) {
			String value = getStringProperty(propertyName);
			if (value == null || value.trim().length() == 0)
				return WTPCommonPlugin.OK_STATUS;
		} else if (propertyName.equals(JAVA_PACKAGE)) {
			String value = getStringProperty(propertyName);
			if (value == null || value.trim().length() == 0) {
				String msg = EJBCreationResourceHandler.Bean_Class_Cannot_Be_In_UI_;
				return WTPCommonPlugin.createErrorStatus(msg);
			}
		} else if (EJB_NAME.equals(propertyName)) {
			String value = getStringProperty(propertyName).trim();
			if (value == null || value.trim().length() == 0){
				return WTPCommonPlugin.createWarningStatus(EJBCreationResourceHandler.WRN_BEAN_NAME_IS_EMPTY);
			}
			return validateEjbName();
		}
		return super.validate(propertyName);
	}

	/**
	 * Subclasses may extend this method to add their own specific behavior when a certain property
	 * in the data model hierarchy is set. This method does not accept null for the property name,
	 * but it will for propertyValue. It will not return null. It will return false if the set
	 * fails. This implementation verifies the display name is set to the classname, that the
	 * annotations is disabled/enabled properly, and that the target project name is determined from
	 * the source folder setting.
	 * 
	 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider#propertySet(String,
	 *      Object)
	 * 
	 * @param propertyName
	 * @param propertyValue
	 * @return boolean was property set?
	 */
	@Override
	public boolean propertySet(String propertyName, Object propertyValue) {

		// Call super to set the property on the data model
		boolean result = super.propertySet(propertyName, propertyValue);
		// If class name is changed, update the display name to be the same
		if (propertyName.equals(CLASS_NAME) && !getDataModel().isPropertySet(EJB_NAME)) {
			getDataModel().notifyPropertyChange(EJB_NAME, IDataModel.DEFAULT_CHG);
		}
		return result;
	}
	
	@Override
	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		if (propertyName.equals(TRANSACTION_TYPE)) {
			return DataModelPropertyDescriptor.createDescriptors(
					new String[] { 
							TransactionType.CONTAINER.toString(), 
							TransactionType.BEAN.toString()
					}, 
					new String[] {
							EJBCreationResourceHandler.TRANSACTION_TYPE_CONTAINER, 
							EJBCreationResourceHandler.TRANSACTION_TYPE_BEAN
					});
		} 
		
		return super.getValidPropertyDescriptors(propertyName);
	}

	protected IStatus validateEjbName() {
		// check if an EJB with the same name already exists
		String projectName = getStringProperty(PROJECT_NAME);
		if (projectName != null && projectName.length() > 0) {
			IModelProvider provider = ModelProviderManager.getModelProvider(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName));
			EJBJar modelObject = (EJBJar) provider.getModelObject();
			EnterpriseBeans enterpriseBeans = modelObject.getEnterpriseBeans();
			if (enterpriseBeans != null)
			{
				List sessionBeans = enterpriseBeans.getSessionBeans();
				for (Object object : sessionBeans) {
					SessionBean session = (SessionBean) object;
					if (session.getEjbName().equals(getDataModel().getStringProperty(EJB_NAME).trim())){
						return new Status(IStatus.ERROR, EjbPlugin.PLUGIN_ID, EJBCreationResourceHandler.ERR_BEAN_ALREADY_EXISTS);
					}
				}
			}
		}
		return Status.OK_STATUS;
	}
	
}

/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.jst.jee.project.facet;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.resources.XMLWriter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.common.project.facet.WtpUtils;
import org.eclipse.jst.j2ee.componentcore.util.EARArtifactEdit;
import org.eclipse.jst.j2ee.earcreation.IEarFacetInstallDataModelProperties;
import org.eclipse.jst.j2ee.internal.J2EEConstants;
import org.eclipse.jst.j2ee.internal.common.J2EEVersionUtil;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetInstallDataModelProperties;
import org.eclipse.jst.jee.JEEPlugin;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.FacetDataModelProvider;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @deprecated - This class had duplicated functionality. Please use {@link org.eclipse.jst.j2ee.project.facet.EarFacetInstallDelegate} 
 */
public final class EarFacetInstallDelegate implements IDelegate {

	public void execute(final IProject project, final IProjectFacetVersion fv, final Object cfg, final IProgressMonitor monitor) throws CoreException {
		if (monitor != null) {
			monitor.beginTask("", 1); //$NON-NLS-1$
		}

		try {
			IDataModel model = (IDataModel) cfg;

			if (monitor != null) {
				monitor.worked(1);
			}
			// Add WTP natures.

			WtpUtils.addNaturestoEAR(project);

			final IVirtualComponent c = ComponentCore.createComponent(project, false);
			c.create(0, null);

			final IVirtualFolder earroot = c.getRootFolder();
			earroot.createLink(new Path("/" + model.getStringProperty(IEarFacetInstallDataModelProperties.CONTENT_DIR)), 0, null); //$NON-NLS-1$

			try {
				((IDataModelOperation) model.getProperty(FacetDataModelProvider.NOTIFICATION_OPERATION)).execute(monitor, null);
			} catch (ExecutionException e) {
				JEEPlugin.logError(e);
			}
			
			if( fv == IJ2EEFacetConstants.ENTERPRISE_APPLICATION_80 || fv == IJ2EEFacetConstants.ENTERPRISE_APPLICATION_70 || fv == IJ2EEFacetConstants.ENTERPRISE_APPLICATION_60 || fv == IJ2EEFacetConstants.ENTERPRISE_APPLICATION_50) {
				if(model.getBooleanProperty(IJ2EEFacetInstallDataModelProperties.GENERATE_DD)){
					// Create the deployment descriptor (web.xml) if one doesn't exist
					IFile appXmlFile = earroot.getUnderlyingFolder().getFile(new Path(J2EEConstants.APPLICATION_DD_URI));
					if (!appXmlFile.exists()) {
						try {
							if(!appXmlFile.getParent().exists()
									&& (appXmlFile.getParent().getType() ==  IResource.FOLDER)){
								((IFolder)appXmlFile.getParent()).create(true, true, monitor);
							}
							String appXmlContents = null;
							if(fv == IJ2EEFacetConstants.ENTERPRISE_APPLICATION_80) {
								appXmlContents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<application id=\"Application_ID\" version=\"8\" xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/application_8.xsd\">\n <display-name>" + XMLWriter.getEscaped(project.getName()) +  "</display-name> \n </application> "; //$NON-NLS-1$ //$NON-NLS-2$
							}
							else if(fv == IJ2EEFacetConstants.ENTERPRISE_APPLICATION_70) {
								appXmlContents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<application id=\"Application_ID\" version=\"7\" xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/application_7.xsd\">\n <display-name>" + XMLWriter.getEscaped(project.getName()) +  "</display-name> \n </application> "; //$NON-NLS-1$ //$NON-NLS-2$
							}
							else if(fv == IJ2EEFacetConstants.ENTERPRISE_APPLICATION_60) {
								appXmlContents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<application id=\"Application_ID\" version=\"6\" xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/application_6.xsd\">\n <display-name> \n" + project.getName() +  "</display-name> \n </application> "; //$NON-NLS-1$ //$NON-NLS-2$
							}
							else {
								appXmlContents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<application id=\"Application_ID\" version=\"5\" xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/application_5.xsd\">\n <display-name> \n" + project.getName() +  "</display-name> \n </application> "; //$NON-NLS-1$ //$NON-NLS-2$
							}
							appXmlFile.create(new ByteArrayInputStream(appXmlContents.getBytes("UTF-8")), true, monitor); //$NON-NLS-1$
						} catch (UnsupportedEncodingException e) {
							JEEPlugin.logError(e);
						}
					}
				}
			}
			else {
				if (!earroot.getUnderlyingFolder().getFile(new Path(J2EEConstants.APPLICATION_DD_URI)).exists()) {
	    			String ver = model.getStringProperty(IFacetDataModelProperties.FACET_VERSION_STR);
	    			int nVer = J2EEVersionUtil.convertVersionStringToInt(ver);
	    			EARArtifactEdit.createDeploymentDescriptor(project, nVer);
	    		}
			}
		}

		finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}

}

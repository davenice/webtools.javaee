/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Nov 6, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.jst.j2ee.internal.web.archive.operations;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jst.j2ee.application.operations.FlexibleJ2EEModuleCreationOperation;
import org.eclipse.jst.j2ee.application.operations.FlexibleJ2EEModuleCreationDataModel;
import org.eclipse.jst.j2ee.application.operations.IAnnotationsDataModel;
import org.eclipse.jst.j2ee.application.operations.J2EEModuleCreationDataModel;
import org.eclipse.jst.j2ee.internal.J2EEConstants;
import org.eclipse.jst.j2ee.internal.web.operations.WebPropertiesUtil;
import org.eclipse.jst.j2ee.internal.web.util.WebArtifactEdit;
import org.eclipse.wst.common.modulecore.ModuleCore;
import org.eclipse.wst.common.modulecore.ModuleCoreFactory;
import org.eclipse.wst.common.modulecore.ComponentType;
import org.eclipse.wst.common.modulecore.ProjectComponents;
import org.eclipse.wst.common.modulecore.WorkbenchComponent;
import org.eclipse.wst.common.modulecore.ComponentResource;
import org.eclipse.wst.common.modulecore.internal.operation.ArtifactEditOperation;
import org.eclipse.wst.common.modulecore.internal.operation.ArtifactEditOperationDataModel;
import org.eclipse.wst.common.modulecore.internal.util.IModuleConstants;

import com.ibm.wtp.emf.workbench.ProjectUtilities;

public class FlexibleWebModuleCreationOperation extends FlexibleJ2EEModuleCreationOperation {
	public FlexibleWebModuleCreationOperation(FlexibleWebModuleCreationDataModel dataModel) {
		super(dataModel);
	}

	public FlexibleWebModuleCreationOperation() {
		super();
	}


	protected void createDeploymentDescriptor(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
		
		
		String moduleName = (String)operationDataModel.getProperty(FlexibleWebModuleCreationDataModel.MODULE_NAME);

		
		IFolder moduleFolder = getProject().getFolder( moduleName );

		if (!moduleFolder.exists()) {
			moduleFolder.create(true, true, null);
		}

		IFolder javaSourceFolder = moduleFolder.getFolder( "JavaSource" );
		if (!javaSourceFolder.exists()) {
			javaSourceFolder.create(true, true, null);
		}
		
		IFolder webContentFolder = moduleFolder.getFolder( "WebContent" );
		if (!webContentFolder.exists()) {
			webContentFolder.create(true, true, null); 
		}
		
		IFolder metainf = webContentFolder.getFolder(J2EEConstants.META_INF);
		if (!metainf.exists()) {
			IFolder parent = metainf.getParent().getFolder(null);
			if (!parent.exists()) {
				parent.create(true, true, null);
			}
			metainf.create(true, true, null);
		}
		
		
		IFolder webinf = webContentFolder.getFolder(J2EEConstants.WEB_INF);
		if (!webinf.exists()) {
			webinf.create(true, true, null);
		}
		
		IFolder lib = webinf.getFolder("lib"); //$NON-NLS-1$
		if (!lib.exists()) {
			lib.create(true, true, null);
		}

		
		//should cache wbmodule when created instead of  searching ?
        ModuleCore moduleCore = null;
        WorkbenchComponent wbmodule = null;
        try {
            moduleCore = ModuleCore.getModuleCoreForRead(getProject());
            wbmodule = moduleCore.findWorkbenchModuleByDeployName(operationDataModel.getStringProperty(FlexibleWebModuleCreationDataModel.MODULE_DEPLOY_NAME));
        } finally {
            if (null != moduleCore) {
                moduleCore.dispose();
            }
        }		


        WebArtifactEdit webEdit = null;
       	try{

       		webEdit = WebArtifactEdit.getWebArtifactEditForWrite( wbmodule );
       		IPath path2 = getProject().getLocation();
       		String projPath = getProject().getLocation().toOSString();
       		projPath += IPath.SEPARATOR + moduleName + IPath.SEPARATOR + "WebContent" + IPath.SEPARATOR + J2EEConstants.WEB_INF + IPath.SEPARATOR + "web.xml";
       		IPath webxmlPath = new Path(projPath);
       		boolean b = webxmlPath.isValidPath(webxmlPath.toString());
       		if(webEdit != null) {
       			int moduleVersion = operationDataModel.getIntProperty(FlexibleWebModuleCreationDataModel.J2EE_MODULE_VERSION);
  			
           		webEdit.createModelRoot( getProject(), webinf, webxmlPath, moduleVersion );
       		}
       	}
       	catch(Exception e){
            e.printStackTrace();
       	} finally {
       		if(webEdit != null)
       			webEdit.dispose();
       	}					
	
	
	
	}

	protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
		
		super.execute( IModuleConstants.JST_WEB_MODULE, monitor );

	}
    
	protected  void addResources( WorkbenchComponent component ){
		addResource(component, getModuleRelativeFile(getWebContentSourcePath( getModuleName() ), getProject()), getWebContentDeployPath());
		addResource(component, getModuleRelativeFile(getJavaSourceSourcePath( getModuleName() ), getProject()), getJavaSourceDeployPath());		
	}
	
	/**
	 * @return
	 */
	public String getJavaSourceSourcePath(String moduleName) {
		return "/" + moduleName +"/JavaSource"; //$NON-NLS-1$
	}
	
	/**
	 * @return
	 */
	public String getJavaSourceDeployPath() {
		return "/WEB-INF/classes"; //$NON-NLS-1$
	}
	
	/**
	 * @return
	 */
	public String getWebContentSourcePath(String moduleName) {
		return "/" + moduleName + "/WebContent"; //$NON-NLS-1$
	}
	
	/**
	 * @return
	 */
	public String getWebContentDeployPath() {
		return "/"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.j2ee.application.operations.FlexibleJ2EEModuleCreationOperation#createProjectStructure()
	 */
	protected void createProjectStructure() throws CoreException {
		// TODO Auto-generated method stub
		
	}
}
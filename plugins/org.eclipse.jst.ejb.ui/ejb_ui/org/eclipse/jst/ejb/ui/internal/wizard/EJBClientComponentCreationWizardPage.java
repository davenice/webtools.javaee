/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.jst.ejb.ui.internal.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jst.ejb.ui.internal.util.EJBUIMessages;
import org.eclipse.jst.j2ee.ejb.EJBJar;
import org.eclipse.jst.j2ee.ejb.componentcore.util.EJBArtifactEdit;
import org.eclipse.jst.j2ee.ejb.datamodel.properties.IEJBClientComponentCreationDataModelProperties;
import org.eclipse.jst.j2ee.internal.actions.IJ2EEUIContextIds;
import org.eclipse.jst.j2ee.internal.wizard.NewModuleGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.resources.ComponentHandle;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;

public class EJBClientComponentCreationWizardPage extends DataModelWizardPage implements IEJBClientComponentCreationDataModelProperties{
	public NewModuleGroup newModuleGroup = null;
	protected EJBJar selProject = null;
	private Label selectedProjectLabel;
	private Text selectedProjectName;
	private Label clientJarURILabel;
	private Text clientJarURI;
	private WorkbenchComponent module;
	protected int indent = 0;

	/**
	 * @param model
	 * @param pageName
	 */
	public EJBClientComponentCreationWizardPage(IDataModel model, String pageName) {
		super(model, pageName);
		setTitle(EJBUIMessages.getResourceString(EJBUIMessages.EJB_Client_Title)); //$NON-NLS-1$
		setDescription(EJBUIMessages.getResourceString(EJBUIMessages.EJB_Client_Desc)); //$NON-NLS-1$
	}

	/**
	 * @param model
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public EJBClientComponentCreationWizardPage(IDataModel model, String pageName, String title, ImageDescriptor titleImage) {
		super(model, pageName, title, titleImage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.ui.wizard.WTPWizardPage#createTopLevelComposite(org.eclipse.swt.widgets.Composite)
	 */
	protected Composite createTopLevelComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		setInfopopID(IJ2EEUIContextIds.NEW_EJB_WIZARD_P2);
		createNewJ2EEModuleGroup(composite);
		createClientGroup(composite);
		handleHasClientJar();
		return composite;
	}
	
	private void createClientGroup(Composite parent) {
		Composite newComposite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		newComposite.setLayout(layout);
		createEJBComponentSection(newComposite);
		clientJarURILabel = new Label(newComposite, SWT.NULL);
		clientJarURILabel.setText(EJBUIMessages.getResourceString(EJBUIMessages.Client_JAR_URI) + " "); //$NON-NLS-1$ 

		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 305;
		clientJarURI = new Text(newComposite, SWT.BORDER);
		clientJarURI.setEditable(true);
		clientJarURI.setLayoutData(data);
		synchHelper.synchText(clientJarURI, CLIENT_COMPONENT_URI, new Control[]{clientJarURILabel});
	}

	private void createEJBComponentSection(Composite parent) {

		GridData data = new GridData();
		setSpacer(parent);
		selectedProjectLabel = new Label(parent, SWT.NULL);
		selectedProjectLabel.setText(EJBUIMessages.getResourceString(EJBUIMessages.EJB_Project)); //$NON-NLS-1$ 

		data.widthHint = 305;
		selectedProjectName = new Text(parent, SWT.NULL);
		selectedProjectName.setLayoutData(data);
		selectedProjectName.setEditable(false);
		synchHelper.synchText(selectedProjectName, EJB_COMPONENT_NAME, new Control[]{selectedProjectLabel});
	}

	protected void createNewJ2EEModuleGroup(Composite parent) {
		Composite newComposite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		newComposite.setLayout(layout);
		newModuleGroup = new NewModuleGroup(newComposite, SWT.NULL, model, synchHelper);
	}

	private void setSpacer(Composite composite) {
		Label space = new Label(composite, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		space.setLayoutData(data);

		space = new Label(composite, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		space.setLayoutData(data);
	}

	private void handleHasClientJar() {
		EJBArtifactEdit edit = null;
		try {
			if (module != null) {
				ComponentHandle handle = ComponentHandle.create(StructureEdit.getContainingProject(module),module.getName());
				edit = EJBArtifactEdit.getEJBArtifactEditForRead(handle);
				if (edit != null && edit.hasEJBClientJARProject())
					enableAllSections(false);
				} else
					enableAllSections(true); 
		} finally {
			if(edit != null)
				edit.dispose();
				  
		}
	}
	
	private void enableAllSections(boolean state) {
		selectedProjectLabel.setEnabled(state);
		selectedProjectName.setEnabled(state);
		clientJarURILabel.setEnabled(state);
		clientJarURI.setEnabled(state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.ui.wizard.WTPWizardPage#getValidationPropertyNames()
	 */
	protected String[] getValidationPropertyNames() {
		return new String[]{EJB_COMPONENT_NAME, CLIENT_COMPONENT_URI };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.ui.wizard.WTPWizardPage#showValidationErrorsOnEnter()
	 */
	protected boolean showValidationErrorsOnEnter() {
		return true;
	}


	public void dispose() {
		if (newModuleGroup != null)
			newModuleGroup.dispose();
		super.dispose();
	}

	protected void enter() {
		super.enter();
//		if (newModuleGroup!=null)
//			newModuleGroup.initializeProjectList();
	}
}
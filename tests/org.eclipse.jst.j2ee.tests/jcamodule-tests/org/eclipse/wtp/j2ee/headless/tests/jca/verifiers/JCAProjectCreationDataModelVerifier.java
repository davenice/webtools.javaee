/*
 * Created on Jan 5, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wtp.j2ee.headless.tests.jca.verifiers;

import junit.framework.Assert;

import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jst.j2ee.client.ApplicationClient;
import org.eclipse.jst.j2ee.jca.modulecore.util.ConnectorArtifactEdit;
import org.eclipse.wst.common.componentcore.datamodel.properties.IComponentCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wtp.j2ee.headless.tests.j2ee.verifiers.ModuleProjectCreationDataModelVerifier;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class JCAProjectCreationDataModelVerifier extends ModuleProjectCreationDataModelVerifier {
    /* (non-Javadoc)
     * @see org.eclipse.wtp.j2ee.headless.tests.j2ee.verifiers.ModuleProjectCreationDataModelVerifier#verifyProjectCreationDataModel(com.ibm.etools.application.operations.J2EEProjectCreationDataModel)
     */
	public void verifyProjectCreationDataModel(IDataModel model) {
        Object key = new Object();
		ConnectorArtifactEdit jcaEdit = null;
		
        try {
            Object dd = null;
			jcaEdit = ConnectorArtifactEdit.getConnectorArtifactEditForRead(ProjectUtilities.getProject(model.getStringProperty(IComponentCreationDataModelProperties.PROJECT_NAME)));
       		if(jcaEdit != null) 
       			dd = (ApplicationClient) jcaEdit.getDeploymentDescriptorRoot();
			Assert.assertNotNull("Deployment Descriptor Null", dd);
        } finally {
			if( jcaEdit != null )
				jcaEdit.dispose();
		}
//        if (dataModel.getBooleanProperty(WebComponentCreationDataModel.ADD_TO_EAR)) {
//            IProject earProject = dataModel.getEarComponentCreationDataModel().getTargetProject();
//            EAREditModel ear = null;
//            try {
//                Assert.assertTrue("EAR doesn't exist:", earProject.exists());
//                EARNatureRuntime runtime = EARNatureRuntime.getRuntime(earProject);
//                //EMFWorkbenchContext emfWorkbenchContext = WorkbenchResourceHelper.createEMFContext(earProject, null);
//                ear = (EAREditModel) runtime.getEditModelForRead(IEARNatureConstants.EDIT_MODEL_ID, key);
//                ear.getModuleMapping(dataModel.getTargetProject());
//                //TODO
//            } finally {
//                ear.releaseAccess(key);
//            }
//
//        }
    }

}

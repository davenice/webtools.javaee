package org.eclipse.jst.jee.project.facet;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jst.j2ee.application.internal.operations.AddComponentToEnterpriseApplicationDataModelProvider;
import org.eclipse.jst.j2ee.internal.project.J2EEProjectUtilities;
import org.eclipse.jst.j2ee.model.IModelProvider;
import org.eclipse.jst.j2ee.model.ModelProviderManager;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.properties.ICreateReferenceComponentsDataModelProperties;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class EarCreateDeploymentFilesOperation extends
		CreateDeploymentFilesDataModelOperation {


	public EarCreateDeploymentFilesOperation(IDataModel model) {
		super(model);
	}
	
	protected void createDeploymentFiles(IProject project, IProgressMonitor monitor) {
		final IVirtualComponent component = ComponentCore.createComponent(project);
		final IModelProvider provider = ModelProviderManager.getModelProvider(project);
			provider.modify(new Runnable(){
				public void run() {
				}
			}, IModelProvider.FORCESAVE);
			IVirtualReference[] componentReferences = J2EEProjectUtilities.getJ2EEModuleReferences(component);
			if(componentReferences != null && componentReferences.length > 0){					
				final IDataModel dataModel = DataModelFactory.createDataModel(new AddComponentToEnterpriseApplicationDataModelProvider());
				dataModel.setProperty(ICreateReferenceComponentsDataModelProperties.SOURCE_COMPONENT, component);
				List modList = (List) dataModel.getProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_LIST);
				for(int i = 0; i < componentReferences.length; i++) {
					IVirtualComponent referencedComponent = componentReferences[i].getReferencedComponent();
					modList.add(referencedComponent);
				}
				dataModel.setProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_LIST, modList);
				try {
					dataModel.getDefaultOperation().execute(monitor, null);
				} catch (ExecutionException e) {
					Logger.getLogger().logError(e);
				}
			}	
		
	}

}

package org.eclipse.jst.jee.project.facet;

import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

public class AppClientCreateDeploymentFilesDataModelProvider extends
		CreateDeploymentFilesDataModelProvider implements
		IWebCreateDeploymentFilesDataModelProperties {
	public IDataModelOperation getDefaultOperation() {
        return new AppClientCreateDeploymentFilesOperation(model);
    }

}

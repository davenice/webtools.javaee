/*
 * Created on Jan 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.jst.j2ee.ejb.internal.deployables;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.ILaunchable; 
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.model.ModuleArtifactAdapterDelegate;

public class EJBDeployableArtifactAdapterFactory extends ModuleArtifactAdapterDelegate implements IAdapterFactory {

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		IModuleArtifact moduleArtifact = null;
		if (adapterType == ILaunchable.class ) {
			getModuleArtifact(adaptableObject);
		}
		return moduleArtifact;
	}

	public Class[] getAdapterList() {
		return new Class[]{ILaunchable.class};
	}

	public IModuleArtifact getModuleArtifact(Object obj) {
		return EJBDeployableArtifactAdapterUtil.getModuleObject(obj);
	}

}

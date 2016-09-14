/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.extension.machine.client;

import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.machine.MachineEntity;

/**
 * @author Mihail Kuznyetsov.
 */
public interface RecipeScriptDownloadServiceClient {

    /**
     * Fetch recipe script for machine source location
     *
     * @param machine
     *         machine to fetch script for
     * @return content of the recipe script
     */
    Promise<String> getRecipeScript(MachineEntity machine);
}

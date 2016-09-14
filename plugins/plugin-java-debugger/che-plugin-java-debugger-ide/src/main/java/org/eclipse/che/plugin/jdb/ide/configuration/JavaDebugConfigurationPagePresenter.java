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
package org.eclipse.che.plugin.jdb.ide.configuration;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.debug.DebugConfiguration;
import org.eclipse.che.ide.api.debug.DebugConfigurationPage;
import org.eclipse.che.ide.api.machine.MachineEntity;
import org.eclipse.che.ide.api.machine.MachineManager;
import org.eclipse.che.ide.util.Pair;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Page allows to edit Java debug configuration.
 *
 * @author Artem Zatsarynnyi
 */
@Singleton
public class JavaDebugConfigurationPagePresenter implements JavaDebugConfigurationPageView.ActionDelegate,
                                                            DebugConfigurationPage<DebugConfiguration> {

    private final JavaDebugConfigurationPageView view;
    private final MachineManager                 machineManager;
    private final AppContext                     appContext;

    private DebugConfiguration editedConfiguration;
    private String             originHost;
    private int                originPort;
    private DirtyStateListener listener;

    @Inject
    public JavaDebugConfigurationPagePresenter(JavaDebugConfigurationPageView view,
                                               MachineManager machineManager,
                                               AppContext appContext) {
        this.view = view;
        this.machineManager = machineManager;
        this.appContext = appContext;

        view.setDelegate(this);
    }

    @Override
    public void resetFrom(DebugConfiguration configuration) {
        editedConfiguration = configuration;

        originHost = configuration.getHost();
        originPort = configuration.getPort();
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);

        final String host = editedConfiguration.getHost();

        view.setHost(host);
        view.setPort(editedConfiguration.getPort());
        view.setDevHost("localhost".equals(host));

        setPortsList();
    }

    private void setPortsList() {
        machineManager.getMachine(appContext.getWorkspaceId(), appContext.getDevMachine().getId()).then(new Operation<MachineEntity>() {
            @Override
            public void apply(MachineEntity machine) throws OperationException {
                List<Pair<String, String>> ports = extractPortsList(machine);
                view.setPortsList(ports);
            }
        });
    }

    /** Extracts list of ports available for connecting to the remote debugger. */
    private List<Pair<String, String>> extractPortsList(MachineEntity machine) {
        List<Pair<String, String>> ports = new ArrayList<>();
//        for (Server server : machine.getServersList()) {
//            if (server.getPort().endsWith("/tcp")) {
//                String portWithoutTcp = server.getPort().substring(0, server.getPort().length() - 4);
//                String description = portWithoutTcp + " (" + server.getRef() + ")";
//                Pair<String, String> pair = new Pair<>(description, portWithoutTcp);
//
//                ports.add(pair);
//            }
//        }

        return ports;
    }

    @Override
    public boolean isDirty() {
        return !originHost.equals(editedConfiguration.getHost()) || originPort != editedConfiguration.getPort();
    }

    @Override
    public void setDirtyStateListener(@NotNull DirtyStateListener listener) {
        this.listener = listener;
    }

    @Override
    public void onHostChanged() {
        editedConfiguration.setHost(view.getHost());
        listener.onDirtyStateChanged();
    }

    @Override
    public void onPortChanged() {
        editedConfiguration.setPort(view.getPort());
        listener.onDirtyStateChanged();
    }

    @Override
    public void onDevHostChanged(boolean value) {
        view.setHostEnableState(!value);
        if (value) {
            editedConfiguration.setHost("localhost");
            view.setHost(editedConfiguration.getHost());
            listener.onDirtyStateChanged();
        }
    }
}

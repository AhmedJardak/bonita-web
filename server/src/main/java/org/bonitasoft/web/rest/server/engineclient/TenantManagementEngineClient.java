/*******************************************************************************
 * Copyright (C) 2009, 2014 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package org.bonitasoft.web.rest.server.engineclient;

import org.bonitasoft.engine.api.TenantManagementAPI;
import org.bonitasoft.engine.business.data.BusinessDataRepositoryDeploymentException;
import org.bonitasoft.engine.business.data.InvalidBusinessDataModelException;
import org.bonitasoft.engine.exception.UpdateException;
import org.bonitasoft.web.toolkit.client.common.exception.api.APIException;
import org.bonitasoft.web.toolkit.client.common.i18n._;

/**
 * @author Colin PUY
 */
public class TenantManagementEngineClient {

    private final TenantManagementAPI tenantManagementAPI;

    public TenantManagementEngineClient(final TenantManagementAPI tenantManagementAPI) {
        this.tenantManagementAPI = tenantManagementAPI;
    }

    public void installBusinessDataModel(final byte[] bussinessDataModelContent) {
        try {
            tenantManagementAPI.installBusinessDataModel(bussinessDataModelContent);
        } catch (final InvalidBusinessDataModelException e) {
            throw new APIException(new _("Invalid Business Data Model content"), e);
        } catch (final BusinessDataRepositoryDeploymentException e) {
            throw new APIException(new _("An error has occurred when deploying Business Data Model."), e);
        }
    }

    public void uninstallBusinessDataModel() {
        if (!isTenantPaused()) {
            throw new APIException(new _("Unable to uninstall the Business Data Model. Please pause the BPM Services first. Go to Configuration > BPM Services."));
        }
        try {
            tenantManagementAPI.uninstallBusinessDataModel();
        } catch (final BusinessDataRepositoryDeploymentException e) {
            throw new APIException(new _("An error has occurred when deploying Business Data Model."), e);
        }
    }

    public boolean isTenantPaused() {
        return tenantManagementAPI.isPaused();
    }

    public void pauseTenant() {
        if (!isTenantPaused()) {
            pause();
        }
    }

    private void pause() {
        try {
            tenantManagementAPI.pause();
        } catch (final UpdateException e) {
            throw new APIException(new _("Error when pausing BPM services"), e);
        }
    }

    public void resumeTenant() {
        if (isTenantPaused()) {
            resume();
        }
    }

    private void resume() {
        try {
            tenantManagementAPI.resume();
        } catch (final UpdateException e) {
            throw new APIException(new _("Error when resuming BPM services"), e);
        }
    }
}

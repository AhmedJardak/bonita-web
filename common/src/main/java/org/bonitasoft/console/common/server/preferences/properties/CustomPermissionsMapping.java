/**
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.console.common.server.preferences.properties;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Anthony Birembaut
 */
public class CustomPermissionsMapping extends SimpleProperties {

    /**
     * Default name of the preferences file
     */
    public static final String PROPERTIES_FILENAME = "custom-permissions-mapping.properties";

    /**
     * Instances attribute
     */
    private static Map<Long, CustomPermissionsMapping> INSTANCES = new ConcurrentHashMap<Long, CustomPermissionsMapping>();

    /**
     * @return the {@link CustomPermissionsMapping} instance
     */
    protected static CustomPermissionsMapping getInstance(final long tenantId) {
        CustomPermissionsMapping tenancyProperties = INSTANCES.get(tenantId);
        if (tenancyProperties == null || SecurityProperties.getInstance(tenantId).isAPIAuthorizationsCheckInDebugMode()) {
            final File fileName = getTenantPropertiesFile(tenantId, PROPERTIES_FILENAME);
            tenancyProperties = new CustomPermissionsMapping(fileName);
            INSTANCES.put(tenantId, tenancyProperties);
        }
        return tenancyProperties;
    }

    CustomPermissionsMapping(final File fileName) {
        super(fileName);
    }

}

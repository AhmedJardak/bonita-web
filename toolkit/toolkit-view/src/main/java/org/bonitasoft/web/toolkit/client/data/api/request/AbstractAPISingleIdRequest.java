/**
 * Copyright (C) 2011 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.web.toolkit.client.data.api.request;

import org.bonitasoft.web.toolkit.client.data.APIID;
import org.bonitasoft.web.toolkit.client.data.item.ItemDefinition;

/**
 * @author Séverin Moussel
 * 
 */
abstract class AbstractAPISingleIdRequest extends AbstractAPIRequest {

    protected APIID id;

    public AbstractAPISingleIdRequest(final ItemDefinition itemDefinition) {
        super(itemDefinition);
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(final APIID id) {
        this.id = id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(final String id) {
        this.id = APIID.makeAPIID(id);
    }

}

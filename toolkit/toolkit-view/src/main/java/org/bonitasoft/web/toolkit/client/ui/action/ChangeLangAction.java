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
package org.bonitasoft.web.toolkit.client.ui.action;

import org.bonitasoft.web.toolkit.client.ClientApplicationURL;
import org.bonitasoft.web.toolkit.client.common.i18n.AbstractI18n.LOCALE;
import org.bonitasoft.web.toolkit.client.ui.utils.I18n;

/**
 * @author Séverin Moussel
 * 
 */
public class ChangeLangAction extends Action {

    private final LOCALE lang;

    public ChangeLangAction(final LOCALE lang) {
        super();
        this.lang = lang;
    }

    @Override
    public void execute() {

        I18n.getInstance().loadLocale(this.lang, new Action() {

            @Override
            public void execute() {
                ClientApplicationURL.setLang(ChangeLangAction.this.lang);
            }
        });
    }
}

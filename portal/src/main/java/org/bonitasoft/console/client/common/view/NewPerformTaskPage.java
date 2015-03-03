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
package org.bonitasoft.console.client.common.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bonitasoft.console.client.admin.bpm.task.view.TaskListingAdminPage;
import org.bonitasoft.console.client.admin.process.view.ProcessListingAdminPage;
import org.bonitasoft.console.client.angular.AngularIFrameView;
import org.bonitasoft.console.client.user.application.view.ProcessListingPage;
import org.bonitasoft.console.client.user.cases.view.CaseListingPage;
import org.bonitasoft.console.client.user.cases.view.IFrameView;
import org.bonitasoft.console.client.user.task.view.TasksListingPage;
import org.bonitasoft.web.rest.model.bpm.flownode.HumanTaskDefinition;
import org.bonitasoft.web.rest.model.bpm.flownode.HumanTaskItem;
import org.bonitasoft.web.toolkit.client.Session;
import org.bonitasoft.web.toolkit.client.ViewController;
import org.bonitasoft.web.toolkit.client.data.APIID;
import org.bonitasoft.web.toolkit.client.data.item.Definitions;
import org.bonitasoft.web.toolkit.client.ui.component.containers.Container;
import org.bonitasoft.web.toolkit.client.ui.component.core.AbstractComponent;
import org.bonitasoft.web.toolkit.client.ui.component.core.Component;
import org.bonitasoft.web.toolkit.client.ui.component.core.UiComponent;
import org.bonitasoft.web.toolkit.client.ui.page.ItemNotFoundPopup;
import org.bonitasoft.web.toolkit.client.ui.page.PageOnItem;

import com.google.gwt.user.client.Element;

/**
 * @author Séverin Moussel
 *
 */
public class NewPerformTaskPage extends PageOnItem<HumanTaskItem> {

    public final static String TOKEN = "performTaskNew";

    public static final List<String> PRIVILEGES = new ArrayList<String>();

    static {
        PRIVILEGES.add(TasksListingPage.TOKEN);
        PRIVILEGES.add(TaskListingAdminPage.TOKEN); // FIX ME: we should create a humantaskmoredetails admin page so ill never need this
        PRIVILEGES.add(CaseListingPage.TOKEN);
        PRIVILEGES.add(AngularIFrameView.CASE_LISTING_ADMIN_TOKEN);
        PRIVILEGES.add(ProcessListingPage.TOKEN);
        PRIVILEGES.add(ProcessListingAdminPage.TOKEN);
        PRIVILEGES.add("reportlistingadminext");
    }

    public static final String PARAMETER_USER_ID = "userId";

    public final static String ASSIGN_AND_PERFORM_USER_TASK = "true";

    private final String UUID_SEPERATOR = "--";

    public NewPerformTaskPage() {
        super(Definitions.get(HumanTaskDefinition.TOKEN));
    }

    public NewPerformTaskPage(final APIID taskId) {
        this();
        this.addParameter(PARAMETER_ITEM_ID, taskId.toString());
    }

    public NewPerformTaskPage(final APIID taskId, final APIID userId) {
        this();
        this.addParameter(PARAMETER_ITEM_ID, taskId.toString());
        this.addParameter(PARAMETER_USER_ID, userId.toString());
    }

    @Override
    public String defineToken() {
        return TOKEN;
    }

    @Override
    public void defineTitle(final HumanTaskItem item) {
        this.setTitle(item.getDisplayName());
    }

    @Override
    public void buildView(final HumanTaskItem task) {
        addBody(createFormIframe(task));
    }

    /**
     * @return
     */
    private APIID getUserId() {
        return Session.getUserId();
    }

    private Component createFormIframe(final HumanTaskItem item) {
        return new UiComponent(new IFrameView(buildTasksFormURL(item)));
    }

    private String buildTasksFormURL(final HumanTaskItem item) {
        final StringBuilder frameUrl = new StringBuilder();
        frameUrl.append("form/taskInstance/").append(item.getId());
        return frameUrl.toString();
    }

    /**
     * We don't need any header and it screw up the page's size.
     *
     * @param header
     * @return
     */
    @Override
    protected List<Element> makeHeaderElements(final Container<AbstractComponent> header) {
        return null;
    }

    @Override
    protected List<Element> makeFooterElements(final Container<AbstractComponent> footer) {
        return null;
    }

    @Override
    protected void onItemNotFound() {
        ViewController.showPopup(new ItemNotFoundPopup(TasksListingPage.TOKEN));
    }

    @Override
    protected List<String> defineDeploys() {
        return Arrays.asList(HumanTaskItem.ATTRIBUTE_PROCESS_ID);
    }
}

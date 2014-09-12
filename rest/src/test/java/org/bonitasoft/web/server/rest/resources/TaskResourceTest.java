/**
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
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
package org.bonitasoft.web.server.rest.resources;

import static java.util.Arrays.asList;
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.bonitasoft.web.server.rest.assertions.ResponseAssert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import jersey.repackaged.com.google.common.collect.ImmutableMap.Builder;

import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.contract.ContractViolationException;
import org.bonitasoft.engine.bpm.contract.Type;
import org.bonitasoft.engine.bpm.contract.impl.ContractDefinitionImpl;
import org.bonitasoft.engine.bpm.contract.impl.InputDefinitionImpl;
import org.bonitasoft.engine.bpm.contract.impl.RuleDefinitionImpl;
import org.bonitasoft.engine.bpm.flownode.FlowNodeExecutionException;
import org.bonitasoft.engine.bpm.flownode.UserTaskNotFoundException;
import org.bonitasoft.web.server.rest.utils.BonitaJerseyTest;
import org.junit.Test;
import org.mockito.Mock;

public class TaskResourceTest extends BonitaJerseyTest {

    @Mock
    private ProcessAPI processAPI;

    @Override
    protected Application configure() {
        return super.config().register(new TaskResource(processAPI));
    }

    private Entity<Map<String, Object>> someJson() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", "value");
        return json(map);
    }

    private Builder<String, Object> aMap() {
        return ImmutableMap.<String, Object> builder();
    }

    @Test
    public void should_return_a_contract_for_a_given_task_instance() throws Exception {
        ContractDefinitionImpl contract = new ContractDefinitionImpl();
        contract.addInput(new InputDefinitionImpl("anInput", Type.TEXT, "aDescription"));
        contract.addRule(new RuleDefinitionImpl("aRule", "an expression", "an explanation"));
        when(processAPI.getUserTaskContract(2L)).thenReturn(contract);

        Response response = target("tasks/2/contract").request().get();

        assertThat(response).hasStatus(OK);
        assertThat(response).hasJsonBodyEqual(readFile("contract.json"));
    }

    @Test
    public void should_respond_404_Not_found_when_task_is_not_found_when_getting_contract() throws Exception {
        when(processAPI.getUserTaskContract(2L)).thenThrow(new UserTaskNotFoundException("task 2 not found"));

        Response response = target("tasks/2/contract").request().get();

        assertThat(response).hasStatus(NOT_FOUND);
    }

    @Test
    public void should_execute_a_task_with_given_inputs() throws Exception {
        String payload = readFile("executeTask.json");
        Map<String, Object> expectedInputs = aMap().put("aBoolean", true).put("aString", "hello world").build();

        target("tasks/2/execute").request().post(Entity.json(payload));

        verify(processAPI).executeUserTask(2L, expectedInputs);
    }

    @Test
    public void should_respond_400_Bad_request_when_contract_is_not_validated_when_executing_a_task() throws Exception {
        doThrow(new ContractViolationException("aMessage", asList("first explanation", "second explanation")))
                .when(processAPI).executeUserTask(anyLong(), anyMapOf(String.class, Object.class));

        Response response = target("tasks/2/execute").request().post(someJson());

        assertThat(response).hasStatus(BAD_REQUEST);
    }

    @Test
    public void should_respond_500_Internal_server_error_when_error_occurs_on_task_execution() throws Exception {
        doThrow(new FlowNodeExecutionException("aMessage"))
                .when(processAPI).executeUserTask(anyLong(), anyMapOf(String.class, Object.class));

        Response response = target("tasks/2/execute").request().post(someJson());

        assertThat(response).hasStatus(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void should_respond_400_Bad_Request_when_null_parameters() throws Exception {

        Response response = target("tasks/2/execute").request().post(null);

        assertThat(response).hasStatus(BAD_REQUEST);
    }

    @Test
    public void should_respond_400_Bad_request_when_not_json() throws Exception {

        Response response = target("tasks/2/execute").request().post(json("not json"));

        assertThat(response).hasStatus(BAD_REQUEST);
    }

    @Test
    public void should_respond_404_Not_found_when_task_is_not_found_when_trying_to_execute_it() throws Exception {
        doThrow(new UserTaskNotFoundException("task not found")).when(processAPI)
            .executeUserTask(anyLong(), anyMapOf(String.class, Object.class));

        Response response = target("tasks/2/execute").request().post(someJson());

        assertThat(response).hasStatus(NOT_FOUND);
    }
}

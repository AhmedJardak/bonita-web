package org.bonitasoft.web.rest.server.api.extension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpHeaders;
import org.bonitasoft.console.common.server.page.RestApiRenderer;
import org.bonitasoft.console.common.server.page.RestApiResponse;
import org.bonitasoft.console.common.server.page.RestApiResponseBuilder;
import org.bonitasoft.engine.exception.BonitaException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Range;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

/**
 * @author Laurent Leseigneur
 */
@RunWith(MockitoJUnitRunner.class)
public class ApiExtensionResourceTest {

    public static final String RETURN_VALUE = "{'return':'value'}";
    public static final String GET = "GET";

    @Mock
    private RestApiRenderer restApiRenderer;

    @Mock
    private Request request;

    @Mock
    private Response response;

    @InjectMocks
    @Spy
    ApiExtensionResource apiExtensionResource;

    private Method method;

    @Mock
    private ResourceExtensionResolver resourceExtensionResolver;

    @Before
    public void before() throws Exception {
        doReturn(request).when(apiExtensionResource).getRequest();
        doReturn(response).when(apiExtensionResource).getResponse();
    }

    @Test
    public void should_handle_return_result() throws Exception {
        //given
        method = new Method(GET);
        doReturn(method).when(request).getMethod();
        final RestApiResponse restApiResponse = new RestApiResponseBuilder().withResponse(RETURN_VALUE).build();
        doReturn(restApiResponse).when(restApiRenderer).handleRestApiCall(any(HttpServletRequest.class),
                any(ResourceExtensionResolver.class));

        //when
        final Representation representation = apiExtensionResource.doHandle();

        //then
        assertThat(representation.getText()).as("should return response").isEqualTo(RETURN_VALUE);
    }

    @Test
    public void should_handle_return_empty_response() throws Exception {
        //given
        method = new Method(GET);
        doReturn(method).when(request).getMethod();
        doReturn(new RestApiResponseBuilder().build()).when(restApiRenderer).handleRestApiCall(any(HttpServletRequest.class),
                any(ResourceExtensionResolver.class));

        //when
        final Representation representation = apiExtensionResource.doHandle();

        //then
        assertThat(representation.getText()).as("should return response").isEqualTo("");
    }

    @Test
    public void should_handle_return_null() throws Exception {
        //given
        method = new Method(GET);
        doReturn(method).when(request).getMethod();

        doReturn(null).when(restApiRenderer).handleRestApiCall(any(HttpServletRequest.class),
                any(ResourceExtensionResolver.class));

        //when
        final Representation representation = apiExtensionResource.doHandle();

        //then
        assertThat(representation).isNull();
        verify(apiExtensionResource.getResponse()).setStatus(Status.SERVER_ERROR_INTERNAL);
    }

    @Test
    public void should_handle_return_500_error_when_exception_is_thrown() throws Exception {
        //given
        method = new Method(GET);
        doReturn(method).when(request).getMethod();
        final BonitaException bonitaException = new BonitaException("error message");
        doThrow(bonitaException).when(restApiRenderer).handleRestApiCall(any(HttpServletRequest.class),
                any(ResourceExtensionResolver.class));

        //when
        final Representation representation = apiExtensionResource.doHandle();

        //then
        assertThat(representation).isNull();
        verify(apiExtensionResource.getResponse()).setStatus(Status.SERVER_ERROR_INTERNAL);
    }

    @Test
    public void should_handle_content_range_with_restlet_range() throws Exception {
        //given
        method = new Method(GET);
        doReturn(method).when(request).getMethod();
        final RestApiResponse restApiResponse = new RestApiResponseBuilder()
                .withAdditionalHeader(HttpHeaders.CONTENT_RANGE, "1-10/100").build();
        doReturn(restApiResponse).when(restApiRenderer).handleRestApiCall(any(HttpServletRequest.class),
                any(ResourceExtensionResolver.class));
        when(response.getEntity()).thenReturn(new StringRepresentation(""));

        //when
        final Representation representation = apiExtensionResource.doHandle();

        //then
        final Range range = representation.getRange();
        assertThat(range.getIndex()).isEqualTo(1);
        assertThat(range.getSize()).isEqualTo(10);
        assertThat(range.getUnitName()).isNullOrEmpty();
        assertThat(range.getInstanceSize()).isEqualTo(100);
    }

}

/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2014 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.wcm.ui.granite.util;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.adobe.granite.ui.components.Value;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@RunWith(MockitoJUnitRunner.class)
public class GraniteUiTest {

  private static final String CONTENT_PATH = "/my/path";

  @Mock
  private SlingHttpServletRequest request;
  @Mock
  private RequestPathInfo requestPathInfo;
  @Mock
  private ResourceResolver resourceResolver;
  @Mock
  private Resource resource;
  @Mock
  private PageManager pageManager;
  @Mock
  private Page page;

  @Before
  public void setUp() {
    when(request.getResourceResolver()).thenReturn(resourceResolver);
    when(request.getAttribute(Value.CONTENTPATH_ATTRIBUTE)).thenReturn(CONTENT_PATH);
    when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
    when(resourceResolver.getResource(CONTENT_PATH)).thenReturn(resource);
    when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
    when(pageManager.getContainingPage(resource)).thenReturn(page);
  }

  @Test
  public void testGetContentResource() {
    assertSame(resource, GraniteUi.getContentResource(request));
  }

  @Test
  public void testGetContentResource_NoContentPath() {
    when(request.getAttribute(Value.CONTENTPATH_ATTRIBUTE)).thenReturn(null);
    assertNull(GraniteUi.getContentResource(request));
  }

  @Test
  public void testGetContentResource_NoContentPath_FallbackToSuffix() {
    when(request.getAttribute(Value.CONTENTPATH_ATTRIBUTE)).thenReturn(null);
    when(requestPathInfo.getSuffix()).thenReturn(CONTENT_PATH);
    assertSame(resource, GraniteUi.getContentResource(request));
  }

  @Test
  public void testGetContentResource_NoResource() {
    when(resourceResolver.getResource(CONTENT_PATH)).thenReturn(null);
    assertNull(GraniteUi.getContentResource(request));
  }

  @Test
  public void testGetContentPage() throws Exception {
    assertSame(page, GraniteUi.getContentPage(request));
  }

  @Test
  public void testGetContentPage_NoResource() throws Exception {
    when(resourceResolver.getResource(CONTENT_PATH)).thenReturn(null);
    assertNull(GraniteUi.getContentPage(request));
  }

  @Test
  public void shouldGetParentOfMissingContentResource() throws Exception {
    when(request.getAttribute(Value.CONTENTPATH_ATTRIBUTE)).thenReturn(CONTENT_PATH + "/unavailable");
    assertSame(resource, GraniteUi.getContentResourceOrParent(request));
    assertSame(page, GraniteUi.getContentPage(request));
  }

  @Test
  public void shouldGetGrandParentOfMissingContentResource() throws Exception {
    when(request.getAttribute(Value.CONTENTPATH_ATTRIBUTE)).thenReturn(CONTENT_PATH + "/not/existing");
    assertSame(resource, GraniteUi.getContentResourceOrParent(request));
    assertSame(page, GraniteUi.getContentPage(request));
  }

  @Test
  public void shouldGetExistingResource() throws Exception {
    assertSame(resource, GraniteUi.getContentResourceOrParent(request));
  }

  @Test
  public void shouldWorkOnToplevel() throws Exception {
    when(request.getAttribute(Value.CONTENTPATH_ATTRIBUTE)).thenReturn("not_a_single_slash");
    assertNull(GraniteUi.getContentResourceOrParent(request));
    assertNull(GraniteUi.getContentPage(request));
  }

}

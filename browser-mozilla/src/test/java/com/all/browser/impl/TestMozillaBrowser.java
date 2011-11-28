package com.all.browser.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;

import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mozilla.browser.MozillaPanel;

public class TestMozillaBrowser {

	@InjectMocks
	private MozillaBrowser mozilla = new MozillaBrowser();
	@Mock
	private MozillaPanel mozillaPanel;
	@Captor
	private ArgumentCaptor<HierarchyBoundsListener> listenerCaptor;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldGetBrowserPanel() throws Exception {
		JPanel browserPanel = mozilla.getPanel();
		assertEquals(mozillaPanel, browserPanel);
	}

	@Test
	public void shouldSetUrl() throws Exception {
		String url = "http://josdem.blogspot.com/";
		mozilla.loadUrl(url);
		verify(mozillaPanel).load(url);
	}

	@Test
	public void shouldUpdateBoundsWhenAncestorResized() throws Exception {
		mozilla.initialize();

		verify(mozillaPanel).addHierarchyBoundsListener(listenerCaptor.capture());

		HierarchyBoundsListener boundsListener = listenerCaptor.getValue();
		HierarchyEvent boundsEvent = mock(HierarchyEvent.class);
		Container container = mock(Container.class);
		when(boundsEvent.getChangedParent()).thenReturn(container);
		Rectangle bounds = mock(Rectangle.class);
		when(container.getBounds()).thenReturn(bounds);
		boundsListener.ancestorResized(boundsEvent);

		verify(mozillaPanel).setBounds(bounds);
	}
	
	@Test
	public void shouldReload() throws Exception {
		mozilla.refresh();
		verify(mozillaPanel).reload();
	}
}

package com.all.browser.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Canvas;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;

import javax.swing.JPanel;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestSwtBrowser {

	@InjectMocks
	private SwtBrowser browser = new SwtBrowser();
	@Mock
	private Canvas canvas;
	@Mock
	private SwtComponentFactory swtFactory;
	@Mock
	private JPanel containerPanel;
	@Mock
	private Display swtDisplay;
	@Mock
	private Shell swtShell;
	@Mock
	private Browser swtBrowser;
	@Captor
	private ArgumentCaptor<HierarchyBoundsListener> boundsListenerCaptor;

	private String url = "some url";

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldGetContainerPanel() throws Exception {
		assertEquals(containerPanel, browser.getPanel());
	}

	@Test
	public void shouldLoadUrlForTheFirstTime() throws Exception {
		createFactoryExpectations();

		browser.loadUrl(url);

		verify(swtBrowser, timeout(500)).setUrl(url);
		verify(swtShell).open();
		verify(swtDisplay, atLeastOnce()).sleep();
	}

	@Test
	public void shouldNotCreateSwtComponentsAgainWhenLoadingAUrl() throws Exception {
		createFactoryExpectations();

		browser.loadUrl(url);
		browser.loadUrl(url);

		verify(swtBrowser, timeout(500).times(2)).setUrl(url);
		verify(swtShell).open();
		verify(swtDisplay, atLeastOnce()).sleep();
	}

	@Test
	public void shouldDisposeSwtBrowserOnShutdown() throws Exception {
		createFactoryExpectations();

		browser.loadUrl(url);
		browser.shutdown();

		verify(swtDisplay, never()).dispose();
		verify(swtBrowser, never()).dispose();
		verify(swtShell, timeout(500)).dispose();
	}

	@Test
	public void shouldUpdateBoundsWhenAncestorResized() throws Exception {
		browser.initialize();

		verify(containerPanel).addHierarchyBoundsListener(boundsListenerCaptor.capture());
		HierarchyBoundsListener boundsListener = boundsListenerCaptor.getValue();
		HierarchyEvent boundsEvent = mock(HierarchyEvent.class);
		Container container = mock(Container.class);
		when(boundsEvent.getChangedParent()).thenReturn(container);
		Rectangle bounds = new Rectangle(0, 0, SwtBrowser.DEFAULT_WIDTH, SwtBrowser.DEFAULT_HEIGHT);
		when(container.getBounds()).thenReturn(bounds);

		createFactoryExpectations();
		browser.loadUrl(url);

		boundsListener.ancestorResized(boundsEvent);

		verify(swtBrowser, timeout(500)).setSize(bounds.width, bounds.height);
		verify(swtShell, timeout(500)).setSize(bounds.width, bounds.height);
	}

	@Test
	public void shouldDisposeResourcesIfUnexpectedException() throws Exception {
		createFactoryExpectations();
		doThrow(new RuntimeException("Some unexpected exception")).when(swtShell).open();
		browser.loadUrl(url);

		verify(swtDisplay, never()).dispose();
		verify(swtBrowser, never()).dispose();
		verify(swtShell, timeout(500)).dispose();
	}
	
	@Test
	public void shouldRefresh() throws Exception {
		createFactoryExpectations();
		
		browser.loadUrl(url);
		Thread.sleep(1000);
		browser.refresh();
		Thread.sleep(1000);
		
		verify(swtBrowser).refresh();
	}

	private void createFactoryExpectations() {
		when(swtFactory.newDisplay()).thenReturn(swtDisplay);
		when(swtFactory.newShell(swtDisplay, canvas)).thenReturn(swtShell);
		when(swtFactory.newBrowser(swtShell)).thenReturn(swtBrowser);
	}
}

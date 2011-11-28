package com.all.browser.impl;

import static org.junit.Assert.assertNotNull;

import java.awt.Canvas;

import javax.swing.JFrame;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Test;

public class TestSwtComponentFactory {

	private SwtComponentFactory factory = new SwtComponentFactory();
	private JFrame frame;
	private Canvas canvas;
	private Display display;
	private Shell shell;
	private Browser browser;

	@Test
	public void shouldCreateSwtComponents() throws Exception {
		frame = new JFrame("test swt");
		canvas = new Canvas();
		display = factory.newDisplay();
		assertNotNull(display);
		frame.add(canvas);
		// README: NOTICE THAT THE CONTAINER FRAME SHOULD BE VISIBLE BEFORE CREATING
		// THE SHELL.
		frame.setVisible(true);
		shell = factory.newShell(display, canvas);
		assertNotNull(shell);
		browser = factory.newBrowser(shell);
		assertNotNull(browser);
	}

	@After
	public void teardown() {
		if (browser != null) {
			browser.dispose();
		}
		if (shell != null) {
			shell.dispose();
		}
		if (display != null) {
			display.dispose();
		}
		if (frame != null) {
			frame.dispose();
		}
	}
}

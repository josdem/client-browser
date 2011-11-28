package com.all.browser.impl;

import java.awt.Canvas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.springframework.stereotype.Component;

@Component
public class SwtComponentFactory {

	public Shell newShell(Display display, Canvas canvas) {
		return SWT_AWT.new_Shell(display, canvas);
	}

	public Display newDisplay() {
		return new Display();
	}

	public Browser newBrowser(Shell shell) {
		return new Browser(shell, SWT.NONE);
	}
}

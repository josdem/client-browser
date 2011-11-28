package com.all.browser.impl;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class BrowserClient {
	private static final String DEFAULT_URL = "www.google.com";
	MozillaBrowser browser = new MozillaBrowser();
	boolean initialized = false;

	public static void main(String[] args) {
		BrowserClient client = new BrowserClient();
		client.initilize();
	}

	private void initilize() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1024, 480);
		frame.setVisible(true);
		
		loadBrowser(frame);
	}

	private void loadBrowser(JFrame frame) {
		JPanel browserPanel = browser.getPanel();
		frame.add(browserPanel);
		browser.loadUrl(DEFAULT_URL);
	}
	
}

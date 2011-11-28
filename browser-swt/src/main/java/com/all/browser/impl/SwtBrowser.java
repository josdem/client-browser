package com.all.browser.impl;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.all.browser.AllBrowser;
import com.all.commons.IncrementalNamedThreadFactory;

@Scope("prototype")
@Service
public class SwtBrowser implements AllBrowser {
	public static final int DEFAULT_WIDTH = 800;
	public static final int DEFAULT_HEIGHT = 600;
	private static final String DEFAULT_URL = "www.all.com";
	private static final Dimension DEFAULT_SIZE = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	private final Log log = LogFactory.getLog(this.getClass());
	private final JPanel containerPanel = new JPanel(new BorderLayout());
	private final Canvas canvas = new Canvas();
	private final SwtBrowserThread browser = new SwtBrowserThread();
	private final SwtComponentFactory swtFactory = new SwtComponentFactory();
	private String newUrl;

	@PostConstruct
	public void initialize() {
		containerPanel.setSize(DEFAULT_SIZE);
		containerPanel.addHierarchyBoundsListener(new HierarchyBoundsAdapter() {

			@Override
			public void ancestorResized(HierarchyEvent e) {
				browser.setBounds(containerPanel.getBounds());
			}
		});
		canvas.setSize(DEFAULT_SIZE);
		containerPanel.add(canvas, BorderLayout.CENTER);
	}

	@Override
	public JPanel getPanel() {
		return containerPanel;
	}

	@Override
	public void loadUrl(String url) {
		browser.loadUrl(url);
	}

	public String getUrl() {
		return newUrl;
	}

	@PreDestroy
	public void shutdown() {
		log.debug("Calling shutdown from :" + this.hashCode());
		browser.shutdown();
	}

	@Override
	public void refresh() {
		browser.refresh();
	}

	private final class SwtBrowserThread implements Runnable {

		private final ExecutorService browserExecutor = Executors
				.newSingleThreadExecutor(new IncrementalNamedThreadFactory(this.getClass().getSimpleName()));
		private Display display;
		private Shell swtShell;
		private Browser swtBrowser;
		private String currentUrl = DEFAULT_URL;
		private Rectangle bounds;
		private boolean started;
		private boolean updateUrl;
		private boolean quit;
		private boolean refresh;

		@Override
		public void run() {
			try {
				display = swtFactory.newDisplay();
				swtShell = swtFactory.newShell(display, canvas);
				swtBrowser = swtFactory.newBrowser(swtShell);
				swtShell.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
				swtBrowser.setLayoutData(new GridData(GridData.FILL_BOTH));
				swtBrowser.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
				swtBrowser.setUrl(currentUrl);
				swtShell.open();

				while (!swtShell.isDisposed()) {
					if (!display.readAndDispatch()) {
						display.sleep();
						if (quit) {
							break;
						}
						SwtBrowser.this.newUrl = swtBrowser.getUrl();
						verifyContainerSize();
						verifyUrl();
						verifyRefresh();
					}
				}

			} catch (Exception e) {
				log.error("Unexpected error on SWT-Browser thread.", e);
			} finally {
				log.info("Calling dispose to : " + this.hashCode());
				dispose();
			}
		}

		// README: BROWSER SHOULD BE STARTED UNTIL THE PARENT OF THE CONTAINERPANEL
		// IS VISIBLE. FOR NOW IT IS STARTED THE FIRST TIME A URL IS REQUESTED.
		private void start() {
			browserExecutor.execute(this);
			started = true;
		}

		public void shutdown() {
			quit = true;
		}

		public void refresh() {
			refresh = true;
		}

		public void setBounds(Rectangle bounds) {
			this.bounds = bounds;
		}

		private void verifyUrl() {
			if (updateUrl) {
				updateUrl = false;
				swtBrowser.setUrl(currentUrl);
			}
		}

		private void verifyRefresh() {
			if (refresh) {
				refresh = false;
				swtBrowser.refresh();
			}
		}

		private void verifyContainerSize() {
			if (bounds != null) {
				swtBrowser.setSize(bounds.width, bounds.height);
				swtShell.setSize(bounds.width, bounds.height);
				canvas.setBounds(bounds);
				containerPanel.setBounds(bounds);
				containerPanel.revalidate();
				bounds = null;
			}
		}

		public void loadUrl(String url) {
			currentUrl = url;
			if (!started) {
				start();
			} else {
				updateUrl = true;
			}
		}

		private synchronized void dispose() {
			if (!browserExecutor.isShutdown()) {
				browserExecutor.shutdownNow();
			}
			if (swtShell != null) {
				swtShell.dispose();
			}
		}
	}

	@Override
	public boolean isInitialized() {
		return true;
	}

}

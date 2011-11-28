package com.all.browser.impl;

import java.awt.Container;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;

import org.mozilla.browser.IMozillaWindow.VisibilityMode;
import org.mozilla.browser.MozillaPanel;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.all.browser.AllBrowser;

@Scope("prototype")
@Service
public class MozillaBrowser implements AllBrowser {

	private MozillaPanel mozillaPanel = new MozillaPanel(VisibilityMode.FORCED_HIDDEN, VisibilityMode.FORCED_HIDDEN);
	private boolean constructedProperly = true;

	@PostConstruct
	public void initialize() {
		registerListeners();
	}

	@Override
	public JPanel getPanel() {
		return mozillaPanel;
	}

	@Override
	public void loadUrl(String url) {
		mozillaPanel.load(url);
	}

	@Override
	public void refresh() {
		mozillaPanel.reload();
	}

	@Override
	public boolean isInitialized() {
		return constructedProperly;
	}

	@Override
	public String getUrl() {
		return mozillaPanel.getUrl();
	}

	private void registerListeners() {
		mozillaPanel.addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
			@Override
			public void ancestorResized(HierarchyEvent e) {
				Container changedParent = e.getChangedParent();
				if (changedParent != null) {
					mozillaPanel.setBounds(changedParent.getBounds());
				}
			}
		});

		mozillaPanel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if (keyCode == KeyEvent.VK_SPACE) {
					e.consume();
				}
			}
		});
	}
}

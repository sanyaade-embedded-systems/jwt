/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt.examples.widgetgallery;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.lang.ref.*;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.*;
import javax.servlet.*;
import eu.webtoolkit.jwt.*;
import eu.webtoolkit.jwt.chart.*;
import eu.webtoolkit.jwt.utils.*;
import eu.webtoolkit.jwt.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class StyleLayout extends ControlsWidget {
	private static Logger logger = LoggerFactory.getLogger(StyleLayout.class);

	public StyleLayout(EventDisplayer ed) {
		super(ed, true);
		addText(tr("style-layout-intro"), this);
	}

	public void populateSubMenu(WMenu menu) {
		menu.addItem("CSS", this.css());
		menu.addItem("WLoadingIndicator", this.wLoadingIndicator());
		menu.addItem("WBoxLayout", this.wBoxLayout());
		menu.addItem("WGridLayout", this.wGridLayout());
		menu.addItem("WBorderLayout", this.wBorderLayout());
	}

	private WWidget css() {
		return addText(tr("style-and-layout-css"));
	}

	private WWidget wLoadingIndicator() {
		WContainerWidget result = new WContainerWidget();
		this.topic("WLoadingIndicator", result);
		addText(tr("style-WLoadingIndicator"), result);
		WApplication.getInstance().getStyleSheet().addRule("body",
				"margin: 0px");
		addText("Select a loading indicator:  ", result);
		WComboBox cb = new WComboBox(result);
		cb.addItem("WDefaultLoadingIndicator");
		cb.addItem("WOverlayLoadingIndicator");
		cb.addItem("EmwebLoadingIndicator");
		cb.setCurrentIndex(0);
		cb.sactivated().addListener(this, new Signal1.Listener<WString>() {
			public void trigger(WString e1) {
				StyleLayout.this.loadingIndicatorSelected(e1);
			}
		});
		new WBreak(result);
		WPushButton load = new WPushButton("Load!", result);
		load.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent e1) {
				StyleLayout.this.load(e1);
			}
		});
		return result;
	}

	private void loadingIndicatorSelected(CharSequence indicator) {
		if (indicator.toString().equals("WDefaultLoadingIndicator")) {
			WApplication.getInstance().setLoadingIndicator(
					new WDefaultLoadingIndicator());
		} else {
			if (indicator.toString().equals("WOverlayLoadingIndicator")) {
				WApplication.getInstance().setLoadingIndicator(
						new WOverlayLoadingIndicator());
			} else {
				if (indicator.toString().equals("EmwebLoadingIndicator")) {
					WApplication.getInstance().setLoadingIndicator(
							new EmwebLoadingIndicator());
				}
			}
		}
	}

	private void load(WMouseEvent anon1) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}

	private WWidget wBoxLayout() {
		WContainerWidget result = new WContainerWidget();
		this.topic("WHBoxLayout", "WVBoxLayout", result);
		addText(tr("layout-WBoxLayout"), result);
		WContainerWidget container;
		WText item;
		WHBoxLayout hbox;
		WVBoxLayout vbox;
		container = new WContainerWidget(result);
		container.setStyleClass("yellow-box");
		hbox = new WHBoxLayout();
		container.setLayout(hbox);
		item = addText(tr("layout-item1"));
		item.setStyleClass("green-box");
		hbox.addWidget(item);
		item = addText(tr("layout-item2"));
		item.setStyleClass("blue-box");
		hbox.addWidget(item);
		addText(tr("layout-WBoxLayout-stretch"), result);
		container = new WContainerWidget(result);
		container.setStyleClass("yellow-box");
		hbox = new WHBoxLayout();
		container.setLayout(hbox);
		item = addText(tr("layout-item1"));
		item.setStyleClass("green-box");
		hbox.addWidget(item, 1);
		item = addText(tr("layout-item2"));
		item.setStyleClass("blue-box");
		hbox.addWidget(item);
		addText(tr("layout-WBoxLayout-vbox"), result);
		container = new WContainerWidget(result);
		container.resize(new WLength(150), new WLength(150));
		container.setStyleClass("yellow-box centered");
		vbox = new WVBoxLayout();
		container.setLayout(vbox);
		item = addText(tr("layout-item1"));
		item.setStyleClass("green-box");
		vbox.addWidget(item);
		item = addText(tr("layout-item2"));
		item.setStyleClass("blue-box");
		vbox.addWidget(item);
		container = new WContainerWidget(result);
		container.resize(new WLength(150), new WLength(150));
		container.setStyleClass("yellow-box centered");
		vbox = new WVBoxLayout();
		container.setLayout(vbox);
		item = addText(tr("layout-item1"));
		item.setStyleClass("green-box");
		vbox.addWidget(item, 1);
		item = addText(tr("layout-item2"));
		item.setStyleClass("blue-box");
		vbox.addWidget(item);
		addText(tr("layout-WBoxLayout-nested"), result);
		container = new WContainerWidget(result);
		container.resize(new WLength(200), new WLength(200));
		container.setStyleClass("yellow-box centered");
		vbox = new WVBoxLayout();
		container.setLayout(vbox);
		item = addText(tr("layout-item1"));
		item.setStyleClass("green-box");
		vbox.addWidget(item, 1);
		hbox = new WHBoxLayout();
		vbox.addLayout(hbox);
		item = addText(tr("layout-item2"));
		item.setStyleClass("green-box");
		hbox.addWidget(item);
		item = addText(tr("layout-item3"));
		item.setStyleClass("blue-box");
		hbox.addWidget(item);
		return result;
	}

	private WWidget wGridLayout() {
		WContainerWidget result = new WContainerWidget();
		this.topic("WGridLayout", result);
		addText(tr("layout-WGridLayout"), result);
		WContainerWidget container;
		container = new WContainerWidget(result);
		container.resize(WLength.Auto, new WLength(400));
		container.setStyleClass("yellow-box");
		WGridLayout grid = new WGridLayout();
		container.setLayout(grid);
		for (int row = 0; row < 3; ++row) {
			for (int column = 0; column < 4; ++column) {
				WText t = addText(tr("grid-item").arg(row).arg(column));
				if (row == 1 || column == 1 || column == 2) {
					t.setStyleClass("blue-box");
				} else {
					t.setStyleClass("green-box");
				}
				grid.addWidget(t, row, column);
			}
		}
		grid.setRowStretch(1, 1);
		grid.setColumnStretch(1, 1);
		grid.setColumnStretch(2, 1);
		return result;
	}

	private WWidget wBorderLayout() {
		WContainerWidget result = new WContainerWidget();
		this.topic("WBorderLayout", result);
		addText(tr("layout-WBorderLayout"), result);
		WContainerWidget container;
		container = new WContainerWidget(result);
		container.resize(WLength.Auto, new WLength(400));
		container.setStyleClass("yellow-box");
		WBorderLayout layout = new WBorderLayout();
		container.setLayout(layout);
		WText item;
		item = addText(tr("borderlayout-item").arg("North"));
		item.setStyleClass("green-box");
		layout.addWidget(item, WBorderLayout.Position.North);
		item = addText(tr("borderlayout-item").arg("West"));
		item.setStyleClass("green-box");
		layout.addWidget(item, WBorderLayout.Position.West);
		item = addText(tr("borderlayout-item").arg("East"));
		item.setStyleClass("green-box");
		layout.addWidget(item, WBorderLayout.Position.East);
		item = addText(tr("borderlayout-item").arg("South"));
		item.setStyleClass("green-box");
		layout.addWidget(item, WBorderLayout.Position.South);
		item = addText(tr("borderlayout-item").arg("Center"));
		item.setStyleClass("green-box");
		layout.addWidget(item, WBorderLayout.Position.Center);
		return result;
	}
}

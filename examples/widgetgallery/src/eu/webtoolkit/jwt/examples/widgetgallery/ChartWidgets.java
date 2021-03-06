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
import eu.webtoolkit.jwt.examples.charts.*;

class ChartWidgets extends ControlsWidget {
	private static Logger logger = LoggerFactory.getLogger(ChartWidgets.class);

	public ChartWidgets(EventDisplayer ed) {
		super(ed, true);
		addText(tr("charts-intro"), this);
		addText(tr("charts-introduction"), this);
	}

	public void populateSubMenu(WMenu menu) {
		menu.addItem("Category Charts", DeferredWidget
				.deferCreate(new WidgetCreator() {
					public WWidget create() {
						return ChartWidgets.this.category();
					}
				}));
		menu.addItem("Scatter Plots", DeferredWidget
				.deferCreate(new WidgetCreator() {
					public WWidget create() {
						return ChartWidgets.this.scatterplot();
					}
				}));
		menu.addItem("Pie Charts", DeferredWidget
				.deferCreate(new WidgetCreator() {
					public WWidget create() {
						return ChartWidgets.this.pie();
					}
				}));
	}

	private WWidget category() {
		WContainerWidget retval = new WContainerWidget((WContainerWidget) null);
		this.topic("Chart::WCartesianChart", retval);
		new CategoryExample(retval);
		return retval;
	}

	private WWidget scatterplot() {
		WContainerWidget retval = new WContainerWidget((WContainerWidget) null);
		this.topic("Chart::WCartesianChart", retval);
		new TimeSeriesExample(retval);
		new ScatterPlotExample(retval);
		return retval;
	}

	private WWidget pie() {
		WContainerWidget retval = new WContainerWidget((WContainerWidget) null);
		this.topic("Chart::WPieChart", retval);
		new PieExample(retval);
		return retval;
	}
}

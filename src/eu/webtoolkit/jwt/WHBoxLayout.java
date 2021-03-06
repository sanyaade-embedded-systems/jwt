/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt;

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

/**
 * A layout manager which arranges widgets horizontally.
 * <p>
 * 
 * This convenience class creates a horizontal box layout, laying contained
 * widgets out from left to right.
 * <p>
 * See the {@link WBoxLayout} documentation for available member methods and
 * more information.
 * <p>
 * Usage example:
 * <p>
 * 
 * <pre>
 * {
 * 	&#064;code
 * 	WContainerWidget w = new WContainerWidget(this);
 * 
 * 	WHBoxLayout layout = new WHBoxLayout();
 * 	layout.addWidget(new WText(&quot;One&quot;));
 * 	layout.addWidget(new WText(&quot;Two&quot;));
 * 	layout.addWidget(new WText(&quot;Three&quot;));
 * 	layout.addWidget(new WText(&quot;Four&quot;));
 * 
 * 	w.setLayout(layout, AlignmentFlag.AlignTop, AlignmentFlag.AlignJustify);
 * }
 * </pre>
 * <p>
 * <p>
 * <i><b>Note: </b>First consider if you can achieve your layout using CSS !</i>
 * </p>
 * 
 * @see WVBoxLayout
 */
public class WHBoxLayout extends WBoxLayout {
	private static Logger logger = LoggerFactory.getLogger(WHBoxLayout.class);

	/**
	 * Creates a new horizontal box layout.
	 * <p>
	 * Use <code>parent</code> = <code>null</code> to create a layout manager
	 * that can be nested inside other layout managers, or to specify a specific
	 * alignment when setting the layout to a {@link WContainerWidget}.
	 */
	public WHBoxLayout(WWidget parent) {
		super(WBoxLayout.Direction.LeftToRight, parent);
	}

	/**
	 * Creates a new horizontal box layout.
	 * <p>
	 * Calls {@link #WHBoxLayout(WWidget parent) this((WWidget)null)}
	 */
	public WHBoxLayout() {
		this((WWidget) null);
	}
}

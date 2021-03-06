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
 * Flags that indicate table header options.
 * <p>
 * 
 * @see WAbstractItemModel#getHeaderFlags(int section, Orientation orientation)
 */
public enum HeaderFlag {
	/**
	 * Flag that indicates that the column can be expanded.
	 * <p>
	 * 
	 * @see WAbstractItemModel#expandColumn(int column)
	 */
	ColumnIsCollapsed,
	/**
	 * Flag that indicates that the column was expanded to the left.
	 * <p>
	 * 
	 * @see WAbstractItemModel#collapseColumn(int column)
	 */
	ColumnIsExpandedLeft,
	/**
	 * Flag that indicates that the column was expanded to the right.
	 * <p>
	 * 
	 * @see WAbstractItemModel#collapseColumn(int column)
	 */
	ColumnIsExpandedRight,
	/**
	 * Flag that indicates that the header can be checked.
	 */
	HeaderIsUserCheckable,
	/**
	 * Flag that indicates that the item has three states.
	 * <p>
	 * When set, {@link ItemDataRole#CheckStateRole} data is of type
	 * {@link CheckState}
	 */
	HeaderIsTristate;

	/**
	 * Returns the numerical representation of this enum.
	 */
	public int getValue() {
		return ordinal();
	}
}

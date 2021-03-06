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

class PdfUtils {
	private static Logger logger = LoggerFactory.getLogger(PdfUtils.class);

	static String toBase14Font(WFont font) {
		String base = null;
		String italic = null;
		String bold = null;
		switch (font.getGenericFamily()) {
		case Default:
		case Serif:
		case Fantasy:
		case Cursive:
			base = "Times";
			italic = "Italic";
			bold = "Bold";
			break;
		case SansSerif:
			base = "Helvetica";
			italic = "Oblique";
			bold = "Bold";
			break;
		case Monospace:
			base = "Courier";
			italic = "Oblique";
			bold = "Bold";
			break;
		}
		if (font.getSpecificFamilies().equals("Symbol")) {
			base = "Symbol";
		} else {
			if (font.getSpecificFamilies().equals("ZapfDingbats")) {
				base = "ZapfDingbats";
			}
		}
		if (italic != null) {
			switch (font.getStyle()) {
			case NormalStyle:
				italic = null;
				break;
			default:
				break;
			}
		}
		if (font.getWeightValue() <= 400) {
			bold = null;
		}
		String name = base;
		if (bold != null) {
			name += "-" + bold;
			if (italic != null) {
				name += italic;
			}
		} else {
			if (italic != null) {
				name += "-" + italic;
			}
		}
		if (name.equals("Times")) {
			name = "Times-Roman";
		}
		return name;
	}
}

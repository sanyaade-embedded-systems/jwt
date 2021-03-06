/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt.render;

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
 * An XHTML to PDF renderer.
 * <p>
 * 
 * This class implements an XHTML to PDF renderer. The rendering engine supports
 * only a subset of XHTML. See the documentation of {@link WTextRenderer} for
 * more information.
 * <p>
 * The renderer renders to a libharu PDF document. By default it uses a pixel
 * resolution of 72 DPI, which is the default for libharu, but differs from the
 * default used by most browsers (which is 96 DPI and has nothing to do with the
 * actual screen resolution). The resolution can be increased using
 * {@link WPdfRenderer#setDpi(int dpi) setDpi()}. This has the effect of scaling
 * down the rendering. This can be used in conjunction with
 * {@link WTextRenderer#setFontScale(double factor)
 * WTextRenderer#setFontScale()} to scale the font size differently than other
 * content.
 * <p>
 * Usage example:
 * <p>
 * 
 * <pre>
 * {@code
 *  extern "C" {
 *    HPDF_STATUS HPDF_UseUTFEncodings(HPDF_Doc pdf);
 *  }
 * 	
 *  HPDF_Doc pdf = HPDF_New(error_handler, 0);
 *  HPDF_UseUTFEncodings(pdf); // enables UTF-8 encoding with true type fonts
 *  HPDF_Page page = HPDF_AddPage(pdf);
 *  HPDF_Page_SetSize(page, HPDF_PAGE_SIZE_A4, HPDF_PAGE_PORTRAIT);
 * 
 *  Render::WPdfRenderer renderer(pdf, page);
 *  renderer.setMargin(2.54);
 *  renderer.setDpi(96);
 * 
 *  renderer.render("<p style=\"background-color: #c11\">Hello, world !</p>");
 * 
 *  HPDF_SaveToFile(pdf, "hello.pdf");
 *  HPDF_Free(pdf);
 * }
 * </pre>
 */
public class WPdfRenderer extends WTextRenderer {
	private static Logger logger = LoggerFactory.getLogger(WPdfRenderer.class);

	/**
	 * Creates a new PDF renderer.
	 * <p>
	 * The PDF renderer will render on the given <code>pdf</code> (starting) on
	 * the given <code>page</code>.
	 * <p>
	 * Default margins are 0, and the default DPI is 72.
	 */
	public WPdfRenderer(com.pdfjet.PDF pdf, com.pdfjet.Page page) {
		super();
		this.fontCollections_ = new ArrayList<WPdfRenderer.FontCollection>();
		this.pdf_ = pdf;
		this.page_ = page;
		this.dpi_ = 72;
		this.painter_ = null;
		for (int i = 0; i < 4; ++i) {
			this.margin_[i] = 0;
		}
	}

	/**
	 * Sets the page margins.
	 * <p>
	 * This sets page margins, in <code>cm</code>, for one or more
	 * <code>sides</code>.
	 */
	public void setMargin(double margin, EnumSet<Side> sides) {
		if (!EnumUtils.mask(sides, Side.Top).isEmpty()) {
			this.margin_[0] = margin;
		}
		if (!EnumUtils.mask(sides, Side.Right).isEmpty()) {
			this.margin_[1] = margin;
		}
		if (!EnumUtils.mask(sides, Side.Bottom).isEmpty()) {
			this.margin_[2] = margin;
		}
		if (!EnumUtils.mask(sides, Side.Left).isEmpty()) {
			this.margin_[3] = margin;
		}
	}

	/**
	 * Sets the page margins.
	 * <p>
	 * Calls {@link #setMargin(double margin, EnumSet sides) setMargin(margin,
	 * EnumSet.of(side, sides))}
	 */
	public final void setMargin(double margin, Side side, Side... sides) {
		setMargin(margin, EnumSet.of(side, sides));
	}

	/**
	 * Sets the page margins.
	 * <p>
	 * Calls {@link #setMargin(double margin, EnumSet sides) setMargin(margin,
	 * Side.All)}
	 */
	public final void setMargin(double margin) {
		setMargin(margin, Side.All);
	}

	/**
	 * Sets the resolution.
	 * <p>
	 * The resolution used between CSS pixels and actual page dimensions. Note
	 * that his does not have an effect on the <i>de facto</i> standard CSS
	 * resolution of 96 DPI that is used to convert between physical
	 * {@link WLength} units (like <i>cm</i>, <i>inch</i> and <i>point</i>) and
	 * pixels. Instead it has the effect of scaling down or up the rendered
	 * XHTML on the page.
	 * <p>
	 * The dpi setting also affects the {@link WPdfRenderer#pageWidth(int page)
	 * pageWidth()}, {@link WPdfRenderer#pageHeight(int page) pageHeight()}, and
	 * {@link WPdfRenderer#getMargin(Side side) getMargin()} pixel calculations.
	 * <p>
	 * The default resolution is 72 DPI (this is the default resolution used by
	 * libharu).
	 */
	public void setDpi(int dpi) {
		this.dpi_ = dpi;
	}

	/**
	 * Adds a font collection.
	 * <p>
	 */
	public void addFontCollection(String directory, boolean recursive) {
		WPdfRenderer.FontCollection c = new WPdfRenderer.FontCollection();
		c.directory = directory;
		c.recursive = recursive;
		this.fontCollections_.add(c);
	}

	/**
	 * Adds a font collection.
	 * <p>
	 * Calls {@link #addFontCollection(String directory, boolean recursive)
	 * addFontCollection(directory, true)}
	 */
	public final void addFontCollection(String directory) {
		addFontCollection(directory, true);
	}

	/**
	 * Returns the current page.
	 * <p>
	 * This returns the page last created using
	 * {@link WPdfRenderer#createPage(int page) createPage()}, or the initial
	 * page if no additional apges have yet been created.
	 */
	public com.pdfjet.Page getCurrentPage() {
		return this.page_;
	}

	public double pageWidth(int page) {
		return this.page_.getWidth() * this.dpi_ / 72.0;
	}

	public double pageHeight(int page) {
		return this.page_.getHeight() * this.dpi_ / 72.0;
	}

	public double getMargin(Side side) {
		final double CmPerInch = 2.54;
		switch (side) {
		case Top:
			return this.margin_[0] / CmPerInch * this.dpi_;
		case Right:
			return this.margin_[1] / CmPerInch * this.dpi_;
		case Bottom:
			return this.margin_[2] / CmPerInch * this.dpi_;
		case Left:
			return this.margin_[3] / CmPerInch * this.dpi_;
		default:
			logger.error(new StringWriter().append(
					"margin(Side) with invalid side").append(
					String.valueOf((int) side.getValue())).toString());
			return 0;
		}
	}

	public WPaintDevice startPage(int page) {
		if (page > 0) {
			this.page_ = this.createPage(page);
		}
		WPdfImage device = new WPdfImage(this.pdf_, this.page_, 0, 0, this
				.pageWidth(page), this.pageHeight(page));
		WTransform deviceTransform = new WTransform();
		deviceTransform.scale(72.0f / this.dpi_, 72.0f / this.dpi_);
		device.setDeviceTransform(deviceTransform);
		for (int i = 0; i < this.fontCollections_.size(); ++i) {
			device.addFontCollection(this.fontCollections_.get(i).directory,
					this.fontCollections_.get(i).recursive);
		}
		return device;
	}

	public void endPage(WPaintDevice device) {
		;
		this.painter_ = null;
		;
	}

	public WPainter getPainter(WPaintDevice device) {
		if (!(this.painter_ != null)) {
			this.painter_ = new WPainter(device);
		}
		return this.painter_;
	}

	/**
	 * Creates a new page.
	 * <p>
	 * The default implementation creates a new page with the same dimensions as
	 * the previous page.
	 * <p>
	 * You may want to specialize this method to add e.g.~headers and footers.
	 */
	public com.pdfjet.Page createPage(int page) {
		return PdfRenderUtils.createPage(this.pdf_, this.page_.getWidth(),
				this.page_.getHeight());
	}

	static class FontCollection {
		private static Logger logger = LoggerFactory
				.getLogger(FontCollection.class);

		public String directory;
		public boolean recursive;
	}

	private List<WPdfRenderer.FontCollection> fontCollections_;
	private com.pdfjet.PDF pdf_;
	private com.pdfjet.Page page_;
	private double[] margin_ = new double[4];
	private int dpi_;
	private WPainter painter_;
}

package eu.webtoolkit.jwt.chart;

import eu.webtoolkit.jwt.PenCapStyle;
import eu.webtoolkit.jwt.PenJoinStyle;
import eu.webtoolkit.jwt.WBrush;
import eu.webtoolkit.jwt.WColor;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WPen;

/**
 * Standard styling for rendering series in charts.
 * <p>
 * 
 * This class provides four standard palettes, each composed of eight different
 * colors (these are recycled at index 8).
 * <p>
 * The three colored palettes are a variation on those defined at <a
 * href="http://www.modernlifeisrubbish.co.uk/article/web-2.0-colour-palette."
 * >http://www.modernlifeisrubbish.co.uk/article/web-2.0-colour-palette.</a>
 * <p>
 * The following table lists the background color, and font color of the
 * different palettes:
 * <p>
 * <table border="1" cellspacing="3" cellpadding="3">
 * <tr>
 * <td><b>Neutral</b></td>
 * <td><b>Bold</b></td>
 * <td><b>Muted</b></td>
 * <td><b>GrayScale</b></td>
 * </tr>
 * <tr>
 * <td>Gmail blue</td>
 * <td>Mozilla red</td>
 * <td>Ruby on Rails red</td>
 * <td>Gray #1</td>
 * </tr>
 * <tr>
 * <td>Shiny silver</td>
 * <td>Flock blue</td>
 * <td>Mozilla blue</td>
 * <td>Gray #2</td>
 * </tr>
 * <tr>
 * <td>Interactive action yellow</td>
 * <td>RSS orange</td>
 * <td>Etsy vermillion</td>
 * <td>Gray #3</td>
 * </tr>
 * <tr>
 * <td>Qoop mint</td>
 * <td>Techcrunch green</td>
 * <td>Digg blue</td>
 * <td>Gray #4</td>
 * </tr>
 * <tr>
 * <td>Digg blue</td>
 * <td>Flickr pink</td>
 * <td>43 Things gold</td>
 * <td>Gray #5</td>
 * </tr>
 * <tr>
 * <td>Shadows grey</td>
 * <td>Newsvine green</td>
 * <td>Writely olive</td>
 * <td>Gray #6</td>
 * </tr>
 * <tr>
 * <td>Magnolia Mag.nolia</td>
 * <td>Magnolia Mag.nolia</td>
 * <td>Last.fm crimson</td>
 * <td>Gray #7</td>
 * </tr>
 * <tr>
 * <td>RSS orange</td>
 * <td>Rollyo red</td>
 * <td>Basecamp green</td>
 * <td>Gray #8</td>
 * </tr>
 * </table>
 * <p>
 * The border pen is in all cases a gray pen of 0 width, while the stroke pen is
 * a line of width 2 in the background color.
 */
public class WStandardPalette implements WChartPalette {
	/**
	 * Enumeration that indicates the palette flavour.
	 */
	public enum Flavour {
		/**
		 * Neutral palette.
		 */
		Neutral(0),
		/**
		 * Bold palette.
		 */
		Bold(1),
		/**
		 * Muted palette.
		 */
		Muted(2),
		/**
		 * Grayscale palette.
		 */
		GrayScale(255);

		private int value;

		Flavour(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	/**
	 * Create a standard palette of a particular flavour.
	 */
	public WStandardPalette(WStandardPalette.Flavour flavour) {
		super();
		this.flavour_ = flavour;
	}

	public WBrush getBrush(int index) {
		return new WBrush(this.color(index));
	}

	public WPen getBorderPen(int index) {
		return new WPen(new WColor(0x44, 0x44, 0x44));
	}

	public WPen getStrokePen(int index) {
		WPen p = new WPen(this.color(index));
		p.setWidth(new WLength(2));
		p.setJoinStyle(PenJoinStyle.BevelJoin);
		p.setCapStyle(PenCapStyle.FlatCap);
		return p;
	}

	public WColor getFontColor(int index) {
		WColor c = this.color(index);
		if (c.getRed() + c.getGreen() + c.getBlue() > 3 * 128) {
			return WColor.black;
		} else {
			return WColor.white;
		}
	}

	/**
	 * Returns the color for the given index.
	 */
	public WColor color(int index) {
		if (this.flavour_ != WStandardPalette.Flavour.GrayScale) {
			int rgb = standardColors[this.flavour_.getValue()][index % 8];
			return new WColor((rgb & 0xFF0000) >> 16, (rgb & 0x00FF00) >> 8,
					rgb & 0x0000FF);
		} else {
			int v = 255 - index % 8 * 32;
			return new WColor(v, v, v);
		}
	}

	private WStandardPalette.Flavour flavour_;
	private boolean filled_;
	static int[][] standardColors = {
			{ 0xC3D9FF, 0xEEEEEE, 0xFFFF88, 0xCDEB8B, 0x356AA0, 0x36393D,
					0xF9F7ED, 0xFF7400 },
			{ 0xFF1A00, 0x4096EE, 0xFF7400, 0x008C00, 0xFF0084, 0x006E2E,
					0xF9F7ED, 0xCC0000 },
			{ 0xB02B2C, 0x3F4C6B, 0xD15600, 0x356AA0, 0xC79810, 0x73880A,
					0xD01F3C, 0x6BBA70 } };
}
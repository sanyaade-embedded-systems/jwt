/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt;


/**
 * Class that defines a shadow style.
 * <p>
 * 
 * @see WPainter#setShadow(WShadow shadow)
 */
public class WShadow {
	/**
	 * Default constructor.
	 * <p>
	 * Constructs a disabled shadow effect (offsetX = offsetY = blur = 0)
	 */
	public WShadow() {
		this.color_ = WColor.black;
		this.offsetX_ = 0;
		this.offsetY_ = 0;
		this.blur_ = 0;
	}

	/**
	 * Constructs a shadow with given offset and color.
	 */
	public WShadow(double dx, double dy, WColor color, double blur) {
		this.color_ = color;
		this.offsetX_ = dx;
		this.offsetY_ = dy;
		this.blur_ = blur;
	}

	// public WShadow clone() ;
	/**
	 * Indicates whether some other object is "equal to" this one.
	 * <p>
	 * Returns <code>true</code> if the shadows are exactly the same.
	 */
	public boolean equals(WShadow other) {
		return this.color_.equals(other.color_)
				&& this.offsetX_ == other.offsetX_
				&& this.offsetY_ == other.offsetY_ && this.blur_ == other.blur_;
	}

	/**
	 * Returns whether the shadow effect is nihil.
	 */
	public boolean isNone() {
		return this.offsetX_ == 0 && this.offsetY_ == 0 && this.blur_ == 0;
	}

	/**
	 * Sets the shadow offset.
	 */
	public void setOffsets(double dx, double dy) {
		this.offsetX_ = dx;
		this.offsetY_ = dy;
	}

	/**
	 * Returns the shadow X offset.
	 * <p>
	 * 
	 * @see WShadow#setOffsets(double dx, double dy)
	 */
	public double getOffsetX() {
		return this.offsetX_;
	}

	/**
	 * Returns the shadow Y offset.
	 * <p>
	 * 
	 * @see WShadow#setOffsets(double dx, double dy)
	 */
	public double getOffsetY() {
		return this.offsetY_;
	}

	/**
	 * Changes the shadow color.
	 * <p>
	 * 
	 * @see WShadow#getColor()
	 */
	public void setColor(WColor color) {
		this.color_ = color;
	}

	/**
	 * Returns the shadow color.
	 * <p>
	 * 
	 * @see WShadow#setColor(WColor color)
	 */
	public WColor getColor() {
		return this.color_;
	}

	/**
	 * Sets the blur.
	 * <p>
	 * 
	 * @see WShadow#getBlur()
	 */
	public void setBlur(double blur) {
		this.blur_ = blur;
	}

	/**
	 * Returns the blur.
	 * <p>
	 * 
	 * @see WShadow#setBlur(double blur)
	 */
	public double getBlur() {
		return this.blur_;
	}

	private WColor color_;
	private double offsetX_;
	private double offsetY_;
	private double blur_;
}
/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt;


/**
 * A validator that checks the string length of user input
 * <p>
 * 
 * This validator checks whether user input is within the specified range of
 * accepted string lengths.
 * <p>
 * If you only want to limit the length on a line edit, you may also use
 * {@link WLineEdit#setMaxLength(int chars) WLineEdit#setMaxLength()}.
 * <p>
 * <h3>i18n</h3>
 * <p>
 * The strings used in this class can be translated by overriding the default
 * values for the following localization keys:
 * <ul>
 * <li>Wt.WLengthValidator.TooShort: The input must be at least {1} characters</li>
 * <li>Wt.WLengthValidator.BadRange: The input must have a length between {1}
 * and {2} characters</li>
 * <li>Wt.WLengthValidator.TooLong: The input must be no more than {1}
 * characters</li>
 * </ul>
 */
public class WLengthValidator extends WValidator {
	/**
	 * Creates a length validator that accepts input of any length.
	 */
	public WLengthValidator(WObject parent) {
		super(parent);
		this.minLength_ = 0;
		this.maxLength_ = Integer.MAX_VALUE;
		this.tooLongText_ = new WString();
		this.tooShortText_ = new WString();
	}

	/**
	 * Creates a length validator that accepts input of any length.
	 * <p>
	 * Calls {@link #WLengthValidator(WObject parent) this((WObject)null)}
	 */
	public WLengthValidator() {
		this((WObject) null);
	}

	/**
	 * Creates a length validator that accepts input within a length range.
	 */
	public WLengthValidator(int minLength, int maxLength, WObject parent) {
		super(parent);
		this.minLength_ = minLength;
		this.maxLength_ = maxLength;
		this.tooLongText_ = new WString();
		this.tooShortText_ = new WString();
	}

	/**
	 * Creates a length validator that accepts input within a length range.
	 * <p>
	 * Calls
	 * {@link #WLengthValidator(int minLength, int maxLength, WObject parent)
	 * this(minLength, maxLength, (WObject)null)}
	 */
	public WLengthValidator(int minLength, int maxLength) {
		this(minLength, maxLength, (WObject) null);
	}

	/**
	 * Sets the minimum length.
	 * <p>
	 * The default value is 0.
	 */
	public void setMinimumLength(int minLength) {
		if (this.minLength_ != minLength) {
			this.minLength_ = minLength;
			this.repaint();
		}
	}

	/**
	 * Returns the minimum length.
	 * <p>
	 * 
	 * @see WLengthValidator#setMinimumLength(int minLength)
	 */
	public int getMinimumLength() {
		return this.minLength_;
	}

	/**
	 * Sets the maximum length.
	 * <p>
	 * The default value is the maximum integer value.
	 */
	public void setMaximumLength(int maxLength) {
		if (this.maxLength_ != maxLength) {
			this.maxLength_ = maxLength;
			this.repaint();
		}
	}

	/**
	 * Returns the maximum length.
	 * <p>
	 * 
	 * @see WLengthValidator#setMaximumLength(int maxLength)
	 */
	public int getMaximumLength() {
		return this.maxLength_;
	}

	/**
	 * Validates the given input.
	 * <p>
	 * The input is considered valid only when it is blank for a non-mandatory
	 * field, or has a length within the valid range.
	 */
	public WValidator.State validate(String input) {
		String text = input;
		if (this.isMandatory()) {
			if (text.length() == 0) {
				return WValidator.State.InvalidEmpty;
			}
		} else {
			if (text.length() == 0) {
				return WValidator.State.Valid;
			}
		}
		if ((int) text.length() >= this.minLength_
				&& (int) text.length() <= this.maxLength_) {
			return WValidator.State.Valid;
		} else {
			return WValidator.State.Invalid;
		}
	}

	// public void createExtConfig(Writer config) throws IOException;
	/**
	 * Sets the message to display when the input is too short.
	 * <p>
	 * Depending on whether {@link WLengthValidator#getMaximumLength()
	 * getMaximumLength()} is a real bound, the default message is &quot;The
	 * input must have a length between {1} and {2} characters&quot; or &quot;
	 * &quot;The input must be at least {1} characters&quot;.
	 */
	public void setInvalidTooShortText(CharSequence text) {
		this.tooShortText_ = WString.toWString(text);
		this.repaint();
	}

	/**
	 * Returns the message displayed when the input is too short.
	 * <p>
	 * 
	 * @see WLengthValidator#setInvalidTooShortText(CharSequence text)
	 */
	public WString getInvalidTooShortText() {
		if (!(this.tooShortText_.length() == 0)) {
			WString s = this.tooShortText_;
			s.arg(this.minLength_).arg(this.maxLength_);
			return s;
		} else {
			if (this.minLength_ == 0) {
				return new WString();
			} else {
				if (this.maxLength_ == Integer.MAX_VALUE) {
					return WString.tr("Wt.WLengthValidator.TooShort").arg(
							this.minLength_);
				} else {
					return WString.tr("Wt.WLengthValidator.BadRange").arg(
							this.minLength_).arg(this.maxLength_);
				}
			}
		}
	}

	/**
	 * Sets the message to display when the input is too long.
	 * <p>
	 * Depending on whether {@link WLengthValidator#getMinimumLength()
	 * getMinimumLength()} is different from zero, the default message is
	 * &quot;The input must have a length between {1} and {2} characters&quot;
	 * or &quot; &quot;The input must be no more than {2} characters&quot;.
	 */
	public void setInvalidTooLongText(CharSequence text) {
		this.tooLongText_ = WString.toWString(text);
		this.repaint();
	}

	/**
	 * Returns the message displayed when the input is too long.
	 * <p>
	 * 
	 * @see WLengthValidator#setInvalidTooLongText(CharSequence text)
	 */
	public WString getInvalidTooLongText() {
		if (!(this.tooLongText_.length() == 0)) {
			WString s = this.tooLongText_;
			s.arg(this.minLength_).arg(this.maxLength_);
			return s;
		} else {
			if (this.maxLength_ == Integer.MAX_VALUE) {
				return new WString();
			} else {
				if (this.minLength_ == 0) {
					return WString.tr("Wt.WLengthValidator.TooLong").arg(
							this.maxLength_);
				} else {
					return WString.tr("Wt.WLengthValidator.BadRange").arg(
							this.minLength_).arg(this.maxLength_);
				}
			}
		}
	}

	protected String javaScriptValidate(String jsRef) {
		String js = "function(e,te,ts,tb){if(e.value.length==0)";
		if (this.isMandatory()) {
			js += "return {valid:false,message:te};";
		} else {
			js += "return {valid:true};";
		}
		if (this.minLength_ != 0) {
			js += "if(e.value.length<" + String.valueOf(this.minLength_)
					+ ") return {valid:false,message:ts};";
		}
		if (this.maxLength_ != Integer.MAX_VALUE) {
			js += "if(e.value.length>" + String.valueOf(this.maxLength_)
					+ ") return {valid:false,message:tb};";
		}
		js += "return {valid:true};}("
				+ jsRef
				+ ','
				+ WString.toWString(this.getInvalidBlankText())
						.getJsStringLiteral()
				+ ','
				+ WString.toWString(this.getInvalidTooShortText())
						.getJsStringLiteral()
				+ ','
				+ WString.toWString(this.getInvalidTooLongText())
						.getJsStringLiteral() + ')';
		return js;
	}

	private int minLength_;
	private int maxLength_;
	private WString tooLongText_;
	private WString tooShortText_;
}

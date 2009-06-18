package eu.webtoolkit.jwt;


/**
 * A widget that provides a line break between inline widgets
 * <p>
 * 
 * This is an {@link WWidget#setInline(boolean inlined) inline} widget that
 * provides a line break inbetween its sibling widgets (such as {@link WText}).
 * <p>
 * The widget corresponds to the HTML <code>&lt;br /&gt;</code> tag.
 */
public class WBreak extends WWebWidget {
	/**
	 * Construct a line break.
	 */
	public WBreak(WContainerWidget parent) {
		super(parent);
	}

	/**
	 * Construct a line break.
	 * <p>
	 * Calls {@link #WBreak(WContainerWidget parent)
	 * this((WContainerWidget)null)}
	 */
	public WBreak() {
		this((WContainerWidget) null);
	}

	protected DomElementType getDomElementType() {
		return DomElementType.DomElement_BR;
	}
}
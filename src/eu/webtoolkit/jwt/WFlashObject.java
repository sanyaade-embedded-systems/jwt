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
 * A widget that renders a Flash object (also known as Flash movie).
 * <p>
 * 
 * This class loads a .swf Flash file in the browser.
 * <p>
 * Flash objects must have their size set, so do not forget to call
 * {@link WFlashObject#resize(WLength width, WLength height) resize()} after
 * instantiation or your content will be invisible. JWt will modify width and
 * height attributes of the Flash object if
 * {@link WFlashObject#resize(WLength width, WLength height) resize()} is called
 * after the object is instantiated; it is however not clear if this is
 * permitted by the Flash plugin.
 * <p>
 * Any {@link WWidget} can be set with
 * {@link WFlashObject#setAlternativeContent(WWidget alternative)
 * setAlternativeContent()}, and this widget will be shown only when the browser
 * has no Flash support. By default, a &apos;Download Flash&apos; button will be
 * displayed that links to a website where the Flash player can be downloaded.
 * You may modify this to be any widget, such as a {@link WImage}, or a native
 * JWt implementation of the Flash movie.
 * <p>
 * <h3>CSS</h3>
 * <p>
 * Styling through CSS is not applicable.
 */
public class WFlashObject extends WWebWidget {
	private static Logger logger = LoggerFactory.getLogger(WFlashObject.class);

	/**
	 * Constructs a Flash widget.
	 */
	public WFlashObject(String url, WContainerWidget parent) {
		super(parent);
		this.url_ = url;
		this.sizeChanged_ = false;
		this.parameters_ = new HashMap<String, WString>();
		this.variables_ = new HashMap<String, WString>();
		this.alternative_ = null;
		this.ieRendersAlternative_ = new JSignal(this, "IeAltnernative");
		this.replaceDummyIeContent_ = false;
		this.setInline(false);
		this
				.setAlternativeContent(new WAnchor(
						new WLink("http://www.adobe.com/go/getflashplayer"),
						new WImage(
								new WLink(
										"http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif"))));
		this.ieRendersAlternative_.addListener(this, new Signal.Listener() {
			public void trigger() {
				WFlashObject.this.renderIeAltnerative();
			}
		});
	}

	/**
	 * Constructs a Flash widget.
	 * <p>
	 * Calls {@link #WFlashObject(String url, WContainerWidget parent) this(url,
	 * (WContainerWidget)null)}
	 */
	public WFlashObject(String url) {
		this(url, (WContainerWidget) null);
	}

	/**
	 * Destructor.
	 * <p>
	 * The Flash object is removed.
	 */
	public void remove() {
		super.remove();
	}

	public void resize(WLength width, WLength height) {
		this.sizeChanged_ = true;
		super.resize(width, height);
		this.repaint(EnumSet.of(RepaintFlag.RepaintPropertyAttribute));
	}

	/**
	 * Sets a Flash parameter.
	 * <p>
	 * The Flash parameters are items such as quality, scale, menu, ... They are
	 * passed as PARAM objects to the Flash movie. See the adobe website for
	 * more information about these parameters: <a
	 * href="http://www.adobe.com/cfusion/knowledgebase/index.cfm?id=tn_12701"
	 * >http://www.adobe.com/cfusion/knowledgebase/index.cfm?id=tn_12701</a>
	 * <p>
	 * Setting the same Flash parameter a second time will overwrite the
	 * previous value. Flash parameters can only be set before the widget is
	 * rendered for the first time, so it is recommended to call this method
	 * shortly after construction before returning to the idle loop.
	 */
	public void setFlashParameter(String name, CharSequence value) {
		WString v = WString.toWString(value);
		this.parameters_.put(name, v);
	}

	/**
	 * Sets a Flash variable.
	 * <p>
	 * This method is a helper function to set variable values in the flashvars
	 * parameter.
	 * <p>
	 * The flash variables will be properly encoded (URL encoding) before being
	 * passed to the flashvars parameter.
	 * <p>
	 * Setting the same Flash variable a second time will overwrite the previous
	 * value. Flash variables can only be set before the widget is rendered for
	 * the first time, so it is recommended to call this method shortly after
	 * construction before returning to the idle loop.
	 */
	public void setFlashVariable(String name, CharSequence value) {
		WString v = WString.toWString(value);
		this.variables_.put(name, v);
	}

	/**
	 * A JavaScript expression that returns the DOM node of the Flash object.
	 * <p>
	 * The Flash object is not stored in {@link WWidget#getJsRef()
	 * WWidget#getJsRef()}, but in {@link WFlashObject#getJsFlashRef()
	 * getJsFlashRef()}. Use this method in conjuction with
	 * {@link WApplication#doJavaScript(String javascript, boolean afterLoaded)
	 * WApplication#doJavaScript()} or {@link JSlot} in custom JavaScript code
	 * to refer to the Flash content.
	 * <p>
	 * The expression returned by {@link WFlashObject#getJsFlashRef()
	 * getJsFlashRef()} may be null, for example on IE when flash is not
	 * installed.
	 */
	public String getJsFlashRef() {
		return "Wt3_2_2.getElement('" + this.getId() + "_flash')";
	}

	/**
	 * Sets content to be displayed if Flash is not available.
	 * <p>
	 * Any widget can be a placeholder when Flash is not installed in the users
	 * browser. By default, this will show a &apos;Download Flash&apos; button
	 * and link to the Flash download site.
	 * <p>
	 * Call this method with a NULL pointer to remove the alternative content.
	 */
	public void setAlternativeContent(WWidget alternative) {
		if (this.alternative_ != null) {
			if (this.alternative_ != null)
				this.alternative_.remove();
		}
		this.alternative_ = alternative;
		if (this.alternative_ != null) {
			this.addChild(this.alternative_);
		}
	}

	void updateDom(DomElement element, boolean all) {
		if (all) {
			DomElement obj = DomElement
					.createNew(DomElementType.DomElement_OBJECT);
			if (this.isInLayout()) {
				obj.setProperty(Property.PropertyStylePosition, "absolute");
				obj.setProperty(Property.PropertyStyleLeft, "0");
				obj.setProperty(Property.PropertyStyleRight, "0");
				element.setProperty(Property.PropertyStylePosition, "relative");
				StringWriter ss = new StringWriter();
				ss
						.append("function(self, w, h) {v="
								+ this.getJsFlashRef()
								+ ";if(v){v.setAttribute('width', w);v.setAttribute('height', h);}");
				if (this.alternative_ != null) {
					ss
							.append(
									"a=" + this.alternative_.getJsRef()
											+ ";if(a && a.").append(
									WT_RESIZE_JS).append(")a.").append(
									WT_RESIZE_JS).append("(a, w, h);");
				}
				ss.append("}");
				this.setJavaScriptMember(WT_RESIZE_JS, ss.toString());
			}
			obj.setId(this.getId() + "_flash");
			obj.setAttribute("type", "application/x-shockwave-flash");
			if (!WApplication.getInstance().getEnvironment().agentIsIElt(9)) {
				obj.setAttribute("data", this.url_);
			}
			obj.setAttribute("width", toString(this.getWidth()));
			obj.setAttribute("height", toString(this.getHeight()));
			for (Iterator<Map.Entry<String, WString>> i_it = this.parameters_
					.entrySet().iterator(); i_it.hasNext();) {
				Map.Entry<String, WString> i = i_it.next();
				if (!i.getKey().equals("flashvars")) {
					DomElement param = DomElement
							.createNew(DomElementType.DomElement_PARAM);
					param.setAttribute("name", i.getKey());
					param.setAttribute("value", i.getValue().toString());
					obj.addChild(param);
				}
			}
			if (WApplication.getInstance().getEnvironment().agentIsIElt(9)) {
				obj.setAttribute("classid",
						"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000");
				DomElement param = DomElement
						.createNew(DomElementType.DomElement_PARAM);
				param.setAttribute("name", "movie");
				param.setAttribute("value", this.url_);
				obj.addChild(param);
			}
			if (this.variables_.size() > 0) {
				StringWriter ss = new StringWriter();
				for (Iterator<Map.Entry<String, WString>> i_it = this.variables_
						.entrySet().iterator(); i_it.hasNext();) {
					Map.Entry<String, WString> i = i_it.next();
					if (i != this.variables_.entrySet().iterator()) {
						ss.append("&");
					}
					ss.append(Utils.urlEncode(i.getKey())).append("=").append(
							Utils.urlEncode(i.getValue().toString()));
				}
				DomElement param = DomElement
						.createNew(DomElementType.DomElement_PARAM);
				param.setAttribute("name", "flashvars");
				param.setAttribute("value", ss.toString());
				obj.addChild(param);
			}
			if (this.alternative_ != null) {
				if (WApplication.getInstance().getEnvironment().hasJavaScript()
						&& WApplication.getInstance().getEnvironment()
								.agentIsIElt(9)) {
					DomElement dummyDiv = DomElement
							.createNew(DomElementType.DomElement_DIV);
					dummyDiv.setId(this.alternative_.getId());
					dummyDiv.setAttribute("style", "width: expression("
							+ WApplication.getInstance().getJavaScriptClass()
							+ "._p_.ieAlternative(this));");
					obj.addChild(dummyDiv);
				} else {
					obj.addChild(this.alternative_
							.createSDomElement(WApplication.getInstance()));
				}
			}
			element.addChild(obj);
		}
		super.updateDom(element, all);
	}

	void getDomChanges(List<DomElement> result, WApplication app) {
		super.getDomChanges(result, app);
		if (this.sizeChanged_) {
			StringWriter ss = new StringWriter();
			ss.append("var v=").append(this.getJsFlashRef()).append(
					";if(v){v.setAttribute('width', '").append(
					toString(this.getWidth())).append(
					"');v.setAttribute('height', '").append(
					toString(this.getHeight())).append("');}");
			WApplication.getInstance().doJavaScript(ss.toString());
			this.sizeChanged_ = false;
		}
		if (this.alternative_ != null && this.replaceDummyIeContent_) {
			DomElement element = DomElement.getForUpdate(this.alternative_
					.getId(), DomElementType.DomElement_DIV);
			element.replaceWith(this.alternative_.createSDomElement(app));
			result.add(element);
			this.replaceDummyIeContent_ = false;
		}
	}

	DomElementType getDomElementType() {
		return DomElementType.DomElement_DIV;
	}

	private String url_;
	private boolean sizeChanged_;
	private Map<String, WString> parameters_;
	private Map<String, WString> variables_;
	private WWidget alternative_;
	private JSignal ieRendersAlternative_;
	private boolean replaceDummyIeContent_;

	private void renderIeAltnerative() {
		this.replaceDummyIeContent_ = true;
		this.repaint(EnumSet.of(RepaintFlag.RepaintInnerHtml));
	}

	static String toString(WLength length) {
		if (length.isAuto()) {
			return "";
		} else {
			if (length.getUnit() == WLength.Unit.Percentage) {
				return "100%";
			} else {
				return String.valueOf((int) length.toPixels()) + "px";
			}
		}
	}
}

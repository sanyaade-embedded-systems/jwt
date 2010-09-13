/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt;

import java.io.StringWriter;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A widget that renders a Flash object (also known as Flash movie)
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
						"http://www.adobe.com/go/getflashplayer",
						new WImage(
								"http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif")));
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
		return "Wt3_1_5.getElement('" + this.getId() + "_flash')";
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
			if (!WApplication.getInstance().getEnvironment().agentIsIE()) {
				obj.setAttribute("data", this.url_);
			}
			if (this.getWidth().isAuto()) {
				obj.setAttribute("width", "");
			} else {
				if (this.getWidth().getUnit() == WLength.Unit.Percentage) {
					obj.setAttribute("width", "100%");
				} else {
					obj.setAttribute("width", String.valueOf((int) this
							.getWidth().toPixels())
							+ "px");
				}
			}
			if (this.getHeight().isAuto()) {
				obj.setAttribute("height", "");
			} else {
				if (this.getHeight().getUnit() == WLength.Unit.Percentage) {
					obj.setAttribute("height", "100%");
				} else {
					obj.setAttribute("height", String.valueOf((int) this
							.getHeight().toPixels())
							+ "px");
				}
			}
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
			if (WApplication.getInstance().getEnvironment().agentIsIE()) {
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
					ss.append(DomElement.urlEncodeS(i.getKey())).append("=")
							.append(
									DomElement.urlEncodeS(i.getValue()
											.toString()));
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
								.agentIsIE()) {
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
			DomElement obj = DomElement.getForUpdate(this.getId() + "_flash",
					DomElementType.DomElement_OBJECT);
			if (this.getWidth().isAuto()) {
				obj.setAttribute("width", "");
			} else {
				if (this.getWidth().getUnit() == WLength.Unit.Percentage) {
					obj.setAttribute("width", "100%");
				} else {
					obj.setAttribute("width", String.valueOf((int) this
							.getWidth().toPixels())
							+ "px");
				}
			}
			if (this.getHeight().isAuto()) {
				obj.setAttribute("height", "");
			} else {
				if (this.getHeight().getUnit() == WLength.Unit.Percentage) {
					obj.setAttribute("height", "100%");
				} else {
					obj.setAttribute("height", String.valueOf((int) this
							.getHeight().toPixels())
							+ "px");
				}
			}
			result.add(obj);
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
}

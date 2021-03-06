/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt.examples.widgetgallery;

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

class BasicControls extends ControlsWidget {
	private static Logger logger = LoggerFactory.getLogger(BasicControls.class);

	public BasicControls(EventDisplayer ed) {
		super(ed, true);
		addText(tr("basics-intro"), this);
	}

	public void populateSubMenu(WMenu menu) {
		menu.addItem("WContainerWidget", this.wContainerWidget());
		menu.addItem("WTemplate", this.wTemplate());
		menu.addItem("WText", this.wText());
		menu.addItem("WAnchor", this.wAnchor());
		menu.addItem("WBreak", this.wBreak());
		menu.addItem("WImage", this.wImage());
		menu.addItem("WGroupBox", this.wGroupBox());
		menu.addItem("WStackedWidget", this.wStackedWidget());
		menu.addItem("WTable", this.wTable());
		menu.addItem("WMenu", this.wMenu());
		menu.addItem("WTree", this.wTree());
		menu.addItem("WTreeTable", this.wTreeTable());
		menu.addItem("WPanel", this.wPanel());
		menu.addItem("WTabWidget", this.wTabWidget());
		menu.addItem("WProgressBar", this.wProgressBar());
	}

	private WWidget wText() {
		WContainerWidget result = new WContainerWidget();
		this.topic("WText", result);
		addText(tr("basics-WText"), result);
		addText(
				"<p>This WText unexpectedly contains JavaScript, wich the XSS attack preventer detects and disables. <script>alert(\"You are under attack\");</script>A warning is printed in Wt's log messages.</p>",
				result);
		addText(
				"<p>This WText contains malformed XML <h1></h2>.It will be turned into a PlainText formatted string.</p>",
				result);
		addText(tr("basics-WText-events"), result);
		WText text;
		text = addText("This text reacts to <tt>clicked()</tt><br/>", result);
		text.setStyleClass("reactive");
		this.ed_.showSignal(text.clicked(), "Text was clicked");
		text = addText("This text reacts to <tt>doubleClicked()</tt><br/>",
				result);
		text.setStyleClass("reactive");
		this.ed_.showSignal(text.doubleClicked(), "Text was double clicked");
		text = addText("This text reacts to <tt>mouseWentOver()</tt><br/>",
				result);
		text.setStyleClass("reactive");
		this.ed_.showSignal(text.mouseWentOver(), "Mouse went over text");
		text = addText("This text reacts to <tt>mouseWentOut()</tt><br/>",
				result);
		text.setStyleClass("reactive");
		this.ed_.showSignal(text.mouseWentOut(), "Mouse went out text");
		return result;
	}

	private WWidget wTemplate() {
		WContainerWidget result = new WContainerWidget();
		this.topic("WTemplate", result);
		addText(tr("basics-WTemplate"), result);
		WTemplate pre = new WTemplate("<pre>${text}</pre>", result);
		pre.bindString("text", tr("basics-WTemplate-example"),
				TextFormat.PlainText);
		addText(tr("basics-WTemplate2"), result);
		WTemplate temp = new WTemplate(tr("basics-WTemplate-example"), result);
		temp.bindWidget("name-edit", new WLineEdit());
		temp.bindWidget("save-button", new WPushButton("Save"));
		temp.bindWidget("cancel-button", new WPushButton("Cancel"));
		return result;
	}

	private WWidget wBreak() {
		WContainerWidget result = new WContainerWidget();
		this.topic("WBreak", result);
		addText(tr("basics-WBreak"), result);
		new WBreak(result);
		return result;
	}

	private WWidget wAnchor() {
		WContainerWidget result = new WContainerWidget();
		this.topic("WAnchor", result);
		addText(tr("basics-WAnchor"), result);
		WAnchor a1 = new WAnchor(new WLink("http://www.webtoolkit.eu/"),
				"Wt homepage (in a new window)", result);
		a1.setTarget(AnchorTarget.TargetNewWindow);
		addText(tr("basics-WAnchor-more"), result);
		WAnchor a2 = new WAnchor(new WLink("http://www.emweb.be/"), result);
		a2.setTarget(AnchorTarget.TargetNewWindow);
		new WImage(new WLink("pics/emweb_small.jpg"), a2);
		addText(tr("basics-WAnchor-related"), result);
		return result;
	}

	private WWidget wImage() {
		WContainerWidget result = new WContainerWidget();
		this.topic("WImage", result);
		addText(tr("basics-WImage"), result);
		addText("An image: ", result);
		new WImage(new WLink("icons/wt_powered.jpg"), result)
				.setVerticalAlignment(AlignmentFlag.AlignMiddle);
		addText(tr("basics-WImage-more"), result);
		return result;
	}

	private WWidget wTable() {
		WContainerWidget result = new WContainerWidget();
		this.topic("WTable", result);
		addText(tr("basics-WTable"), result);
		WTable table = new WTable(result);
		table.setStyleClass("example-table");
		addText("First warning signal", table.getElementAt(0, 0));
		addText("09:25am", table.getElementAt(0, 1));
		WImage img = new WImage(new WLink("icons/Pennant_One.png"), table
				.getElementAt(0, 2));
		img.resize(WLength.Auto, new WLength(30, WLength.Unit.Pixel));
		addText("First perparatory signal", table.getElementAt(1, 0));
		addText("09:26am", table.getElementAt(1, 1));
		img = new WImage(new WLink("icons/Pennant_One.png"), table
				.getElementAt(1, 2));
		img.resize(WLength.Auto, new WLength(30, WLength.Unit.Pixel));
		img = new WImage(new WLink("icons/Papa.png"), table.getElementAt(1, 2));
		img.resize(WLength.Auto, new WLength(30, WLength.Unit.Pixel));
		addText("Second perparatory signal", table.getElementAt(2, 0));
		addText("09:29am", table.getElementAt(2, 1));
		img = new WImage(new WLink("icons/Pennant_One.png"), table
				.getElementAt(2, 2));
		img.resize(WLength.Auto, new WLength(30, WLength.Unit.Pixel));
		addText("Start", table.getElementAt(3, 0));
		addText("09:30am", table.getElementAt(3, 1));
		addText(tr("basics-WTable-more"), result);
		return result;
	}

	private WWidget wTree() {
		WContainerWidget result = new WContainerWidget();
		this.topic("WTree", "WTreeNode", result);
		addText(tr("basics-WTree"), result);
		WIconPair folderIcon = new WIconPair("icons/yellow-folder-closed.png",
				"icons/yellow-folder-open.png", false);
		WTree tree = new WTree(result);
		tree.setSelectionMode(SelectionMode.ExtendedSelection);
		WTreeNode node = new WTreeNode("Furniture", folderIcon);
		tree.setTreeRoot(node);
		node.getLabel().setTextFormat(TextFormat.PlainText);
		node.setLoadPolicy(WTreeNode.LoadPolicy.NextLevelLoading);
		node.addChildNode(new WTreeNode("Table"));
		node.addChildNode(new WTreeNode("Cupboard"));
		WTreeNode three = new WTreeNode("Chair");
		node.addChildNode(three);
		node.addChildNode(new WTreeNode("Coach"));
		node.expand();
		three.addChildNode(new WTreeNode("Doc"));
		three.addChildNode(new WTreeNode("Grumpy"));
		three.addChildNode(new WTreeNode("Happy"));
		three.addChildNode(new WTreeNode("Sneezy"));
		three.addChildNode(new WTreeNode("Dopey"));
		three.addChildNode(new WTreeNode("Bashful"));
		three.addChildNode(new WTreeNode("Sleepy"));
		addText(tr("basics-WTree-more"), result);
		return result;
	}

	private WWidget wTreeTable() {
		WContainerWidget result = new WContainerWidget();
		this.topic("WTreeTable", "WTreeTableNode", result);
		addText(tr("basics-WTreeTable"), result);
		WTreeTable tt = new WTreeTable(result);
		tt.resize(new WLength(650), new WLength(200));
		tt.getTree().setSelectionMode(SelectionMode.ExtendedSelection);
		tt.addColumn("Yuppie Factor", new WLength(125));
		tt.addColumn("# Holidays", new WLength(125));
		tt.addColumn("Favorite Item", new WLength(125));
		WTreeTableNode ttr = new WTreeTableNode("All Personnel");
		ttr.setImagePack("resources/");
		tt.setTreeRoot(ttr, "Emweb Organigram");
		WTreeTableNode ttr1 = new WTreeTableNode("Upper Management",
				(WIconPair) null, ttr);
		WTreeTableNode ttn;
		ttn = new WTreeTableNode("Chief Anything Officer", (WIconPair) null,
				ttr1);
		ttn.setColumnWidget(1, addText("-2.8"));
		ttn.setColumnWidget(2, addText("20"));
		ttn.setColumnWidget(3, addText("Scepter"));
		ttn = new WTreeTableNode("Vice President of Parties", (WIconPair) null,
				ttr1);
		ttn.setColumnWidget(1, addText("13.57"));
		ttn.setColumnWidget(2, addText("365"));
		ttn.setColumnWidget(3, addText("Flag"));
		ttn = new WTreeTableNode("Vice President of Staplery",
				(WIconPair) null, ttr1);
		ttn.setColumnWidget(1, addText("3.42"));
		ttn.setColumnWidget(2, addText("27"));
		ttn.setColumnWidget(3, addText("Perforator"));
		ttr1 = new WTreeTableNode("Middle management", (WIconPair) null, ttr);
		ttn = new WTreeTableNode("Boss of the house", (WIconPair) null, ttr1);
		ttn.setColumnWidget(1, addText("9.78"));
		ttn.setColumnWidget(2, addText("35"));
		ttn.setColumnWidget(3, addText("Happy Animals"));
		ttn = new WTreeTableNode("Xena caretaker", (WIconPair) null, ttr1);
		ttn.setColumnWidget(1, addText("8.66"));
		ttn.setColumnWidget(2, addText("10"));
		ttn.setColumnWidget(3, addText("Yellow bag"));
		ttr1 = new WTreeTableNode("Actual Workforce", (WIconPair) null, ttr);
		ttn = new WTreeTableNode("The Dork", (WIconPair) null, ttr1);
		ttn.setColumnWidget(1, addText("9.78"));
		ttn.setColumnWidget(2, addText("22"));
		ttn.setColumnWidget(3, addText("Mojito"));
		ttn = new WTreeTableNode("The Stud", (WIconPair) null, ttr1);
		ttn.setColumnWidget(1, addText("8.66"));
		ttn.setColumnWidget(2, addText("46"));
		ttn.setColumnWidget(3, addText("Toothbrush"));
		ttn = new WTreeTableNode("The Ugly", (WIconPair) null, ttr1);
		ttn.setColumnWidget(1, addText("13.0"));
		ttn.setColumnWidget(2, addText("25"));
		ttn.setColumnWidget(3, addText("Paper bag"));
		ttr.expand();
		return result;
	}

	private WWidget wPanel() {
		WContainerWidget result = new WContainerWidget();
		this.topic("WPanel", result);
		addText(tr("basics-WPanel"), result);
		WPanel panel = new WPanel(result);
		panel.setCentralWidget(addText("This is a default panel"));
		new WBreak(result);
		panel = new WPanel(result);
		panel.setTitle("My second WPanel.");
		panel.setCentralWidget(addText("This is a panel with a title"));
		new WBreak(result);
		panel = new WPanel(result);
		panel.setAnimation(new WAnimation(EnumSet.of(
				WAnimation.AnimationEffect.SlideInFromTop,
				WAnimation.AnimationEffect.Fade),
				WAnimation.TimingFunction.EaseOut, 100));
		panel.setTitle("My third WPanel");
		panel
				.setCentralWidget(addText("This is a collapsible panel with a title"));
		panel.setCollapsible(true);
		addText(tr("basics-WPanel-related"), result);
		return result;
	}

	private WWidget wTabWidget() {
		WContainerWidget result = new WContainerWidget();
		this.topic("WTabWidget", result);
		addText(tr("basics-WTabWidget"), result);
		WTabWidget tw = new WTabWidget(result);
		tw.addTab(addText("These are the contents of the first tab"),
				"Picadilly", WTabWidget.LoadPolicy.PreLoading);
		tw
				.addTab(
						addText("The contents of these tabs are pre-loaded in the browser to ensure swift switching."),
						"Waterloo", WTabWidget.LoadPolicy.PreLoading);
		tw
				.addTab(
						addText("This is yet another pre-loaded tab. Look how good this works."),
						"Victoria", WTabWidget.LoadPolicy.PreLoading);
		WMenuItem tab = tw
				.addTab(
						addText("The colors of the tab widget can be changed by modifying some images.You can close this tab by clicking on the close icon"),
						"Tottenham");
		tab.setCloseable(true);
		tw.setStyleClass("tabwidget");
		addText(tr("basics-WTabWidget-more"), result);
		return result;
	}

	private WWidget wContainerWidget() {
		WContainerWidget result = new WContainerWidget();
		this.topic("WContainerWidget", result);
		addText(tr("basics-WContainerWidget"), result);
		return result;
	}

	private WWidget wMenu() {
		WContainerWidget result = new WContainerWidget();
		this.topic("WMenu", result);
		addText(tr("basics-WMenu"), result);
		return result;
	}

	private WWidget wGroupBox() {
		WContainerWidget result = new WContainerWidget();
		this.topic("WGroupBox", result);
		addText(tr("basics-WGroupBox"), result);
		WGroupBox gb = new WGroupBox("A group box", result);
		gb.addWidget(addText(tr("basics-WGroupBox-contents")));
		addText(tr("basics-WGroupBox-related"), result);
		return result;
	}

	private WWidget wStackedWidget() {
		WContainerWidget result = new WContainerWidget();
		this.topic("WStackedWidget", result);
		addText(tr("basics-WStackedWidget"), result);
		return result;
	}

	private WWidget wProgressBar() {
		WContainerWidget result = new WContainerWidget();
		this.topic("WProgressBar", result);
		result.addWidget(addText(tr("basics-WProgressBar")));
		WProgressBar pb = new WProgressBar(result);
		pb.setValue(27);
		return result;
	}
}

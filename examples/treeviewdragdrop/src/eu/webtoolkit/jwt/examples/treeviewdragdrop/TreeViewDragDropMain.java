/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt.examples.treeviewdragdrop;

import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WEnvironment;
import eu.webtoolkit.jwt.WtServlet;

public class TreeViewDragDropMain extends WtServlet {
    private static final long serialVersionUID = 1L;

    public TreeViewDragDropMain() {
        super();

        //getConfiguration().setProgressiveBootstrap(true);
        //getConfiguration().setSendXHTMLMimeType(true);
    }

    public WApplication createApplication(WEnvironment env) {
        WApplication app = new TreeViewDragDropApplication(env);
        app.setTitle("WTreeView Drag & Drop");
        app.useStyleSheet("style/styles.css");

        return app;
    }
}

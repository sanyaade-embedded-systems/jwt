/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt.examples.filetreetable;

import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import eu.webtoolkit.jwt.SelectionMode;
import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WEnvironment;
import eu.webtoolkit.jwt.WtServlet;
import eu.webtoolkit.jwt.WTreeNode.ChildCountPolicy;

public class FileTreeExample extends WtServlet {
    private static final long serialVersionUID = 1L;
    private File startDir;

    public FileTreeExample() {
        super();
    }

    public WApplication createApplication(WEnvironment env) {
        WApplication app = new WApplication(env);
        app.setTitle("File explorer example");
        app.useStyleSheet("style/filetree.css");

        FileTreeTable treeTable = new FileTreeTable(startDir);

        treeTable.resize(500, 300);
        treeTable.getTree().setSelectionMode(SelectionMode.ExtendedSelection);
        treeTable.getTreeRoot().setNodeVisible(false);
        treeTable.getTreeRoot().setChildCountPolicy(ChildCountPolicy.Enabled);

        app.getRoot().addWidget(treeTable);

        return app;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // The start dir is configured in a servlet container variable (see
        // web.xml).
        startDir = new File(config.getInitParameter("startDir"));

        super.init(config);
    }
}
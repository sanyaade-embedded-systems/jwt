/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt.examples.form;

import eu.webtoolkit.jwt.Cursor;
import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WColor;
import eu.webtoolkit.jwt.WCssDecorationStyle;
import eu.webtoolkit.jwt.WEnvironment;
import eu.webtoolkit.jwt.WFont;
import eu.webtoolkit.jwt.WXmlLocalizedStrings;
import eu.webtoolkit.jwt.WtServlet;

/**
 *A simple Form example.
 * 
 * Shows how a simple form can made, with an emphasis on how to handle
 * validation.
 * 
 * When submitting the form, not all fields are filled in in a valid way, a beep
 * sound will be played.
 */
public class FormMain extends WtServlet {
    private static final long serialVersionUID = 1L;

    public FormMain() {
        super();
    }

    public WApplication createApplication(WEnvironment env) {
        WApplication app = new WApplication(env);

        WXmlLocalizedStrings bundle = new WXmlLocalizedStrings();
        bundle.use("/eu/webtoolkit/jwt/examples/form/form-example");
        app.setLocalizedStrings(bundle);

        app.setTitle("Form example");

        app.getRoot().addWidget(new FormExample(null));

        WCssDecorationStyle langStyle = basicDecoration();
        app.getStyleSheet().addRule(".lang", langStyle);

        langStyle = basicDecoration();
        langStyle.setCursor(Cursor.ArrowCursor);
        langStyle.getFont().setWeight(WFont.Weight.Bold);
        app.getStyleSheet().addRule(".langcurrent", langStyle);

        return app;
    }

    private WCssDecorationStyle basicDecoration() {
        WCssDecorationStyle langStyle = new WCssDecorationStyle();

        langStyle.getFont().setSize(WFont.Size.Smaller);
        langStyle.setCursor(Cursor.PointingHandCursor);
        langStyle.setForegroundColor(WColor.blue);
        langStyle
                .setTextDecoration(WCssDecorationStyle.TextDecoration.Underline);

        return langStyle;
    }
}

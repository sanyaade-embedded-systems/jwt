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

class GoogleMapExample extends WContainerWidget {
	private static Logger logger = LoggerFactory
			.getLogger(GoogleMapExample.class);

	public GoogleMapExample(WContainerWidget parent,
			ControlsWidget controlsWidget) {
		super(parent);
		this.controlsWidget_ = controlsWidget;
		WHBoxLayout layout = new WHBoxLayout();
		this.setLayout(layout);
		this.setHeight(new WLength(400));
		this.map_ = new WGoogleMap(WGoogleMap.ApiVersion.Version3);
		layout.addWidget(this.map_, 1);
		this.map_.setMapTypeControl(WGoogleMap.MapTypeControl.DefaultControl);
		this.map_.enableScrollWheelZoom();
		WTemplate controls = new WTemplate(
				tr("specialpurposewidgets-WGoogleMap-controls"));
		layout.addWidget(controls);
		WPushButton zoomIn = new WPushButton("+");
		zoomIn.addStyleClass("zoom");
		controls.bindWidget("zoom-in", zoomIn);
		zoomIn.clicked().addListener(this.map_,
				new Signal1.Listener<WMouseEvent>() {
					public void trigger(WMouseEvent e1) {
						GoogleMapExample.this.map_.zoomIn();
					}
				});
		WPushButton zoomOut = new WPushButton("-");
		zoomOut.addStyleClass("zoom");
		controls.bindWidget("zoom-out", zoomOut);
		zoomOut.clicked().addListener(this.map_,
				new Signal1.Listener<WMouseEvent>() {
					public void trigger(WMouseEvent e1) {
						GoogleMapExample.this.map_.zoomOut();
					}
				});
		WPushButton brussels = new WPushButton("Brussels");
		controls.bindWidget("brussels", brussels);
		brussels.clicked().addListener(this,
				new Signal1.Listener<WMouseEvent>() {
					public void trigger(WMouseEvent e1) {
						GoogleMapExample.this.panToBrussels();
					}
				});
		WPushButton lisbon = new WPushButton("Lisbon");
		controls.bindWidget("lisbon", lisbon);
		lisbon.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent e1) {
				GoogleMapExample.this.panToLisbon();
			}
		});
		WPushButton paris = new WPushButton("Paris");
		controls.bindWidget("paris", paris);
		paris.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent e1) {
				GoogleMapExample.this.panToParis();
			}
		});
		WPushButton savePosition = new WPushButton("Save current position");
		controls.bindWidget("save-position", savePosition);
		savePosition.clicked().addListener(this,
				new Signal1.Listener<WMouseEvent>() {
					public void trigger(WMouseEvent e1) {
						GoogleMapExample.this.savePosition();
					}
				});
		this.returnToPosition_ = new WPushButton("Return to saved position");
		controls.bindWidget("return-to-saved-position", this.returnToPosition_);
		this.returnToPosition_.setEnabled(false);
		this.returnToPosition_.clicked().addListener(this.map_,
				new Signal1.Listener<WMouseEvent>() {
					public void trigger(WMouseEvent e1) {
						GoogleMapExample.this.map_.returnToSavedPosition();
					}
				});
		this.mapTypeModel_ = new WStandardItemModel();
		this.addMapTypeControl("No control",
				WGoogleMap.MapTypeControl.NoControl);
		this.addMapTypeControl("Default",
				WGoogleMap.MapTypeControl.DefaultControl);
		this.addMapTypeControl("Menu", WGoogleMap.MapTypeControl.MenuControl);
		if (this.map_.getApiVersion() == WGoogleMap.ApiVersion.Version2) {
			this.addMapTypeControl("Hierarchical",
					WGoogleMap.MapTypeControl.HierarchicalControl);
		}
		if (this.map_.getApiVersion() == WGoogleMap.ApiVersion.Version3) {
			this.addMapTypeControl("Horizontal bar",
					WGoogleMap.MapTypeControl.HorizontalBarControl);
		}
		WComboBox menuControls = new WComboBox();
		menuControls.setModel(this.mapTypeModel_);
		controls.bindWidget("control-menu-combo", menuControls);
		menuControls.activated().addListener(this,
				new Signal1.Listener<Integer>() {
					public void trigger(Integer e1) {
						GoogleMapExample.this.setMapTypeControl(e1);
					}
				});
		menuControls.setCurrentIndex(1);
		WCheckBox draggingCB = new WCheckBox("Enable dragging ");
		controls.bindWidget("dragging-cb", draggingCB);
		draggingCB.setChecked(true);
		this.map_.enableDragging();
		draggingCB.checked().addListener(this.map_, new Signal.Listener() {
			public void trigger() {
				GoogleMapExample.this.map_.enableDragging();
			}
		});
		draggingCB.unChecked().addListener(this.map_, new Signal.Listener() {
			public void trigger() {
				GoogleMapExample.this.map_.disableDragging();
			}
		});
		WCheckBox enableDoubleClickZoomCB = new WCheckBox(
				"Enable double click zoom ");
		controls.bindWidget("double-click-zoom-cb", enableDoubleClickZoomCB);
		enableDoubleClickZoomCB.setChecked(false);
		this.map_.disableDoubleClickZoom();
		enableDoubleClickZoomCB.checked().addListener(this.map_,
				new Signal.Listener() {
					public void trigger() {
						GoogleMapExample.this.map_.enableDoubleClickZoom();
					}
				});
		enableDoubleClickZoomCB.unChecked().addListener(this.map_,
				new Signal.Listener() {
					public void trigger() {
						GoogleMapExample.this.map_.disableDoubleClickZoom();
					}
				});
		WCheckBox enableScrollWheelZoomCB = new WCheckBox(
				"Enable scroll wheel zoom ");
		controls.bindWidget("scroll-wheel-zoom-cb", enableScrollWheelZoomCB);
		enableScrollWheelZoomCB.setChecked(true);
		this.map_.enableScrollWheelZoom();
		enableScrollWheelZoomCB.checked().addListener(this.map_,
				new Signal.Listener() {
					public void trigger() {
						GoogleMapExample.this.map_.enableScrollWheelZoom();
					}
				});
		enableScrollWheelZoomCB.unChecked().addListener(this.map_,
				new Signal.Listener() {
					public void trigger() {
						GoogleMapExample.this.map_.disableScrollWheelZoom();
					}
				});
		List<WGoogleMap.Coordinate> road = new ArrayList<WGoogleMap.Coordinate>();
		this.roadDescription(road);
		this.map_.addPolyline(road, new WColor(0, 191, 255));
		this.map_.addMarker(new WGoogleMap.Coordinate(50.885069, 4.71958));
		this.map_.setCenter(road.get(road.size() - 1));
		this.map_
				.openInfoWindow(
						road.get(0),
						"<img src=\"http://www.emweb.be/css/emweb_small.jpg\" /><br/><b>Emweb office</b>");
		this.map_.clicked().addListener(this,
				new Signal1.Listener<WGoogleMap.Coordinate>() {
					public void trigger(WGoogleMap.Coordinate e1) {
						GoogleMapExample.this.googleMapClicked(e1);
					}
				});
		this.map_.doubleClicked().addListener(this,
				new Signal1.Listener<WGoogleMap.Coordinate>() {
					public void trigger(WGoogleMap.Coordinate e1) {
						GoogleMapExample.this.googleMapDoubleClicked(e1);
					}
				});
	}

	private void panToBrussels() {
		this.map_.panTo(new WGoogleMap.Coordinate(50.85034, 4.35171));
	}

	private void panToLisbon() {
		this.map_.panTo(new WGoogleMap.Coordinate(38.703731, -9.135475));
	}

	private void panToParis() {
		this.map_.panTo(new WGoogleMap.Coordinate(48.877474, 2.312579));
	}

	private void savePosition() {
		this.returnToPosition_.setEnabled(true);
		this.map_.savePosition();
	}

	private void addMapTypeControl(CharSequence description,
			WGoogleMap.MapTypeControl value) {
		int r = this.mapTypeModel_.getRowCount();
		this.mapTypeModel_.insertRow(r);
		this.mapTypeModel_.setData(r, 0, description);
		this.mapTypeModel_.setData(r, 0, value, ItemDataRole.UserRole);
	}

	private void setMapTypeControl(int row) {
		Object mtc = this.mapTypeModel_.getData(row, 0, ItemDataRole.UserRole);
		this.map_.setMapTypeControl((WGoogleMap.MapTypeControl) mtc);
	}

	private void roadDescription(List<WGoogleMap.Coordinate> roadDescription) {
		roadDescription.add(new WGoogleMap.Coordinate(50.85342, 4.7281));
		roadDescription.add(new WGoogleMap.Coordinate(50.85377, 4.72573));
		roadDescription.add(new WGoogleMap.Coordinate(50.85393, 4.72496));
		roadDescription.add(new WGoogleMap.Coordinate(50.85393, 4.72496));
		roadDescription.add(new WGoogleMap.Coordinate(50.85372, 4.72482));
		roadDescription.add(new WGoogleMap.Coordinate(50.85304, 4.72421));
		roadDescription.add(new WGoogleMap.Coordinate(50.8519, 4.72297));
		roadDescription.add(new WGoogleMap.Coordinate(50.85154, 4.72251));
		roadDescription.add(new WGoogleMap.Coordinate(50.85154, 4.72251));
		roadDescription.add(new WGoogleMap.Coordinate(50.85153, 4.72205));
		roadDescription.add(new WGoogleMap.Coordinate(50.85153, 4.72205));
		roadDescription.add(new WGoogleMap.Coordinate(50.85752, 4.7186));
		roadDescription.add(new WGoogleMap.Coordinate(50.85847, 4.71798));
		roadDescription.add(new WGoogleMap.Coordinate(50.859, 4.71753));
		roadDescription.add(new WGoogleMap.Coordinate(50.8593, 4.71709));
		roadDescription.add(new WGoogleMap.Coordinate(50.8598699, 4.71589));
		roadDescription.add(new WGoogleMap.Coordinate(50.8606, 4.7147));
		roadDescription.add(new WGoogleMap.Coordinate(50.8611, 4.71327));
		roadDescription.add(new WGoogleMap.Coordinate(50.8612599, 4.71293));
		roadDescription.add(new WGoogleMap.Coordinate(50.86184, 4.71217));
		roadDescription.add(new WGoogleMap.Coordinate(50.86219, 4.71202));
		roadDescription.add(new WGoogleMap.Coordinate(50.86346, 4.71178));
		roadDescription.add(new WGoogleMap.Coordinate(50.86406, 4.71146));
		roadDescription.add(new WGoogleMap.Coordinate(50.86478, 4.71126));
		roadDescription.add(new WGoogleMap.Coordinate(50.86623, 4.71111));
		roadDescription.add(new WGoogleMap.Coordinate(50.866599, 4.71101));
		roadDescription.add(new WGoogleMap.Coordinate(50.8668, 4.71072));
		roadDescription.add(new WGoogleMap.Coordinate(50.86709, 4.71018));
		roadDescription.add(new WGoogleMap.Coordinate(50.86739, 4.70941));
		roadDescription.add(new WGoogleMap.Coordinate(50.86751, 4.70921));
		roadDescription.add(new WGoogleMap.Coordinate(50.86869, 4.70843));
		roadDescription.add(new WGoogleMap.Coordinate(50.8691, 4.70798));
		roadDescription.add(new WGoogleMap.Coordinate(50.8691, 4.70798));
		roadDescription.add(new WGoogleMap.Coordinate(50.86936, 4.70763));
		roadDescription.add(new WGoogleMap.Coordinate(50.86936, 4.70763));
		roadDescription.add(new WGoogleMap.Coordinate(50.86874, 4.70469));
		roadDescription.add(new WGoogleMap.Coordinate(50.86858, 4.70365));
		roadDescription.add(new WGoogleMap.Coordinate(50.8684599, 4.70269));
		roadDescription.add(new WGoogleMap.Coordinate(50.86839, 4.70152));
		roadDescription.add(new WGoogleMap.Coordinate(50.86843, 4.70043));
		roadDescription.add(new WGoogleMap.Coordinate(50.86851, 4.69987));
		roadDescription.add(new WGoogleMap.Coordinate(50.8688199, 4.69869));
		roadDescription.add(new WGoogleMap.Coordinate(50.8689, 4.69827));
		roadDescription.add(new WGoogleMap.Coordinate(50.87006, 4.6941));
		roadDescription.add(new WGoogleMap.Coordinate(50.87006, 4.6941));
		roadDescription.add(new WGoogleMap.Coordinate(50.8704599, 4.69348));
		roadDescription.add(new WGoogleMap.Coordinate(50.87172, 4.69233));
		roadDescription.add(new WGoogleMap.Coordinate(50.87229, 4.69167));
		roadDescription.add(new WGoogleMap.Coordinate(50.87229, 4.69167));
		roadDescription.add(new WGoogleMap.Coordinate(50.8725, 4.69123));
		roadDescription.add(new WGoogleMap.Coordinate(50.8725, 4.69123));
		roadDescription.add(new WGoogleMap.Coordinate(50.87408, 4.69142));
		roadDescription.add(new WGoogleMap.Coordinate(50.87423, 4.69125));
		roadDescription.add(new WGoogleMap.Coordinate(50.87464, 4.69116));
		roadDescription.add(new WGoogleMap.Coordinate(50.875799, 4.69061));
		roadDescription.add(new WGoogleMap.Coordinate(50.87595, 4.69061));
		roadDescription.add(new WGoogleMap.Coordinate(50.87733, 4.69073));
		roadDescription.add(new WGoogleMap.Coordinate(50.87742, 4.69078));
		roadDescription.add(new WGoogleMap.Coordinate(50.87784, 4.69131));
		roadDescription.add(new WGoogleMap.Coordinate(50.87784, 4.69131));
		roadDescription.add(new WGoogleMap.Coordinate(50.87759, 4.69267));
		roadDescription.add(new WGoogleMap.Coordinate(50.8775, 4.6935));
		roadDescription.add(new WGoogleMap.Coordinate(50.87751, 4.69395));
		roadDescription.add(new WGoogleMap.Coordinate(50.87768, 4.69545));
		roadDescription.add(new WGoogleMap.Coordinate(50.87769, 4.69666));
		roadDescription.add(new WGoogleMap.Coordinate(50.87759, 4.69742));
		roadDescription.add(new WGoogleMap.Coordinate(50.87734, 4.69823));
		roadDescription.add(new WGoogleMap.Coordinate(50.87734, 4.69823));
		roadDescription.add(new WGoogleMap.Coordinate(50.877909, 4.69861));
	}

	private void googleMapDoubleClicked(WGoogleMap.Coordinate c) {
		StringWriter strm = new StringWriter();
		strm.append("Double clicked at coordinate (").append(
				String.valueOf(c.getLatitude())).append(",").append(
				String.valueOf(c.getLongitude())).append(")");
		this.controlsWidget_.eventDisplayer().setStatus(strm.toString());
	}

	private void googleMapClicked(WGoogleMap.Coordinate c) {
		StringWriter strm = new StringWriter();
		strm.append("Clicked at coordinate (").append(
				String.valueOf(c.getLatitude())).append(",").append(
				String.valueOf(c.getLongitude())).append(")");
		this.controlsWidget_.eventDisplayer().setStatus(strm.toString());
	}

	private WGoogleMap map_;
	private WAbstractItemModel mapTypeModel_;
	private WPushButton returnToPosition_;
	private ControlsWidget controlsWidget_;
}

/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import eu.webtoolkit.jwt.Signal.Listener;

/**
 * Abstract base class for event signals.
 * <p>
 * Event signals are signals that may be triggered from the browser. Listeners that are added
 * to this signal may be implemented in Java only or in JavaScript only or have both Java and
 * JavaScript implementations.
 */
public abstract class AbstractEventSignal extends AbstractSignal {
	/**
	 * An abstract base class for a listener with (learned) JavaScript behavior.
	 * <p>
	 * A learning listener may learn or be explicitly set its JavaScript behavior to avoid
	 * server round-trips.
	 * 
	 * @see Listener
	 */
	public static abstract class LearningListener {
		public abstract void trigger();

		public abstract void undoTrigger();

		void setJavaScript(String javaScript) {
			this.javaScript = javaScript;

			if (signals_ != null)
				for (WeakReference<AbstractEventSignal> a : signals_) {
					AbstractEventSignal s = a.get();
					if (s != null)
						s.ownerRepaint();
				}
		}

		final String getJavaScript() {
			return javaScript;
		}

		final boolean isLearned() {
			return javaScript != null;
		}

		private String javaScript = null;

		abstract SlotType getType();

		void addSignal(AbstractEventSignal signal) {
			if (signals_ == null)
				signals_ = new ArrayList<WeakReference<AbstractEventSignal>>();

			signals_.add(new WeakReference<AbstractEventSignal>(signal));
		}

		void removeSignal(AbstractEventSignal signal) {
			if (signals_ != null) {
				for (WeakReference<AbstractEventSignal> a : signals_) {
					if (a.get() == signal) {
						signals_.remove(a);
						break;
					}
				}
			}
		}

		private List<WeakReference<AbstractEventSignal>> signals_;
	}

	/**
	 * A listener whose JavaScript behavior is learned on its first invocation.
	 * <p>
	 * The JavaScript behavior is learned when invoked for the first time. In this way, the
	 * visual effect of the event listener happens after a round-trip the first time it is
	 * triggered, but for next invocations its behavior is cached in the browser as client-side
	 * JavaScript.
	 * 
	 * @see AbstractEventSignal#addListener(WObject, LearningListener)
	 */
	public static abstract class AutoLearnListener extends LearningListener implements Signal.Listener {
		public final void undoTrigger() {
		}

		SlotType getType() {
			return SlotType.AutoLearnStateless;
		}
	}

	/**
	 * A listener whose JavaScript behavior is learned in advance.
	 * <p>
	 * The JavaScript behavior is learned by invoking {@link #trigger()} and monitoring its effect
	 * on the widget tree. In this way, the visual effect of the event listener
	 * happens immediately in response to an event, without requiring a server round-trip.
	 * Yet, the listener is still run on the server as well, whenever the listener is triggered.
	 * <p>
	 * The {@link #undoTrigger()} method is called after learning and should undo the effect of
	 * {@link #trigger()}.
	 * 
	 * @see AbstractEventSignal#addListener(WObject, LearningListener)
	 */
	public static abstract class PreLearnListener extends LearningListener implements Signal.Listener {
		/**
		 * Undoes the signal trigger.
		 * <p>
		 * This method undoes the effect of {@link #trigger()}, and is used when the listener
		 * was triggered for learning the JavaScript behavior.
		 */
		public abstract void undoTrigger();

		SlotType getType() {
			return SlotType.PreLearnStateless;
		}
	}

	/**
	 * A JavaScript-only listener.
	 * <p>
	 * A listener whose behavior is entirely and solely defined using client-side JavaScript,
	 * and which will not generate a request to the server.
	 * <p>
	 * 
	 * @see AbstractEventSignal#addListener(String)
	 */
	public static class JavaScriptListener extends LearningListener {

		JavaScriptListener(WObject parent, Object object, String javaScript) {
			setJavaScript(javaScript);
		}

		/**
		 * Creates a JavaScript listener with given JavaScript code.
		 * <p>
		 * @param javaScript JavaScript statements that implement the event listener.
		 */
		public JavaScriptListener(String javaScript) {
			setJavaScript(javaScript);
		}

		public final void trigger() {
		}

		public final void undoTrigger() {
		}

		/**
		 * Set the JavaScript code for this listener.
		 */
		public void setJavaScript(String javaScript) {
			super.setJavaScript(javaScript);
		}

		SlotType getType() {
			return SlotType.JavaScript;
		}
	}

	static final int BIT_NEED_UPDATE = 0x1;
	static final int BIT_EXPOSED = 0x2;
	static final int BIT_NEEDS_AUTOLEARN = 0x4;
	static final int BIT_PREVENT_DEFAULT = 0x8;
	static final int BIT_PREVENT_PROPAGATION = 0x10;

	private ArrayList<LearningListener> learningListeners;
	private byte flags_;
	private int id_;
	private String name_;
	private static int nextId_ = 0;
	private WObject sender_;

	AbstractEventSignal(String name, WObject sender) {
		sender_ = sender;
		name_ = name;
		learningListeners = null;
		flags_ = 0;
		id_ = nextId_++;
	}

	/**
	 * Adds a learning listener to this signal.
	 * <p>
	 * An owner object may be passed when the listener is implemented using an
	 * anonymous inner class. In that case the owner object should be the
	 * enclosing object of the listener object, and this is used to bind the
	 * lifetime of the listener: when the owner object is garbage collected, the
	 * listener will be freed as well. This avoids the most common reason for
	 * memory leaks in Java implementations of the Observer pattern: the owner
	 * object will not get garbage collected because of the (anonymous) listener
	 * object having a reference to it, even if the receiver object is no longer
	 * referenced from anywhere. When the owner object is not <code>null</code>,
	 * the listener is stored using a strong reference in the owner object, and
	 * using a weak reference in the signal.
	 * 
	 * Note: The receiver is currently not yet used.
	 * 
	 * @param listenerOwner
	 *            if not <code>null</code>, the enclosing object for an
	 *            anonymous listener
	 * @param listener
	 *            the listener
	 */
	public void addListener(WObject listenerOwner, LearningListener listener) {
		if (learningListeners == null)
			learningListeners = new ArrayList<LearningListener>();

		learningListeners.add(listener);
		listener.addSignal(this);

		if (!(listener instanceof JavaScriptListener))
			listenerAdded();
		else
			ownerRepaint(); // does not need to be exposed
	}

	/**
	 * Adds a learning listener to this signal.
	 * <p>
	 * @see #addListener(WObject, LearningListener)
	 */
	public void addListener(WObject listenerOwner, AutoLearnListener listener) {
		addListener(listenerOwner, (LearningListener) listener);
	}

	/**
	 * Adds a learning listener to this signal.
	 * <p>
	 * @see #addListener(WObject, LearningListener)
	 */
	public void addListener(WObject listenerOwner, PreLearnListener listener) {
		addListener(listenerOwner, (LearningListener) listener);
	}

	/**
	 * Adds a learning listener to this signal.
	 * <p>
	 * @see #addListener(WObject, LearningListener)
	 */
	public void addListener(WObject listenerOwner, JavaScriptListener listener) {
		addListener(listenerOwner, (LearningListener) listener);
	}

	/**
	 * Remove a learning listener from this signal.
	 * 
	 * @param listener a learning listener that was previously added
	 */
	public void removeListener(LearningListener listener) {
		if (learningListeners.remove(listener)) {
			listener.removeSignal(this);
			listenerRemoved();
		}
	}
	
	void listenerAdded() {
		if ((flags_ & BIT_EXPOSED) != 0)
			return;

		WApplication app = WApplication.getInstance();
		
		app.addExposedSignal(this);

		flags_ |= BIT_NEEDS_AUTOLEARN;

		if (app.isExposeSignals())
			flags_ |= BIT_EXPOSED;

		ownerRepaint();
	}

	void listenerRemoved() {
		if (getListenerCount() == 0) {
			if ((flags_ & BIT_NEEDS_AUTOLEARN) != 0) {
				WApplication.getInstance().removeExposedSignal(this);
				flags_ &= ~BIT_EXPOSED;
				flags_ &= ~BIT_NEEDS_AUTOLEARN;
			}

			ownerRepaint();
		}
	}

	private final void ownerRepaint() {
		flags_ |= BIT_NEED_UPDATE;

		if (getSender() instanceof WWebWidget)
			((WWebWidget) getSender()).signalConnectionsChanged();
	}

	WObject getSender() {
		return sender_;
	}

	final void senderRepaint() {
		ownerRepaint();
	}

	@Override
	protected int getListenerCount() {
		return learningListeners != null ? learningListeners.size() : 0;
	}

	boolean isExposedSignal() {
		return (flags_ & BIT_EXPOSED) != 0;
	}

	@Override
	public boolean isConnected() {
		return getListenerCount() > 0;
	}

	String encodeCmd() {
		return String.format("s%x", id_);
	}

	String getJavaScript() {
		String result = "";

		if (learningListeners != null)
			for (LearningListener l : learningListeners) {
				if (l.isLearned()) {
					result += l.getJavaScript();
				}
			}

		if (isDefaultActionPrevented() || isPropagationPrevented()) {
			result += WEnvironment.getJavaScriptWtScope() + ".cancelEvent(e";
			if (isDefaultActionPrevented() && isPropagationPrevented())
				result += ");";
			else if (isDefaultActionPrevented())
				result += ",0x2);";
			else
				result += ",0x1);";
		}

		return result;
	}

	void updateOk() {
		flags_ &= ~BIT_NEED_UPDATE;
	}


	boolean needsUpdate(boolean all) {
		return (!all && (flags_ & BIT_NEED_UPDATE) != 0)
				|| (all && (isConnected() || isDefaultActionPrevented() || isPropagationPrevented()));
	}

	/**
	 * Returns whether the default action is prevented.
	 * 
	 * @return whether the default action is prevented.
	 *
	 * @see #preventPropagation(boolean)
	 */
	public boolean isPropagationPrevented() {
		return (flags_ & BIT_PREVENT_PROPAGATION) != 0;
	}

	/**
	 * Returns whether propagation of the event is prevented.
	 * 
	 * @return whether the default action is prevented.
	 * 
	 * @see #preventDefaultAction(boolean)
	 */
	public boolean isDefaultActionPrevented() {
		return (flags_ & BIT_PREVENT_DEFAULT) != 0;
	}

	/**
	 * Triggers the signal.
	 * <p>
	 * The {@link Listener#trigger()} method of all listeners added to this signal are triggered.
	 */
	public void trigger() {
		if (learningListeners != null)
			for (LearningListener l : learningListeners) {
				l.trigger();
			}
	}

	void processPreLearnStateless(SlotLearnerInterface learner) {
		if (learningListeners != null)
			for (LearningListener l : learningListeners) {
				if (l instanceof PreLearnListener) {
					PreLearnListener pl = (PreLearnListener) l;

					if (!pl.isLearned()) {
						try {
							pl.setJavaScript(learner.learn(pl));
						} catch (IOException e) {
							// This is kind of impossible since we are writing
							// to a god damn string
							e.printStackTrace();
						}
						ownerRepaint();
					}
				}
			}
	}

	void processAutoLearnStateless(SlotLearnerInterface learner) {
		boolean changed = false;

		if (learningListeners != null)
			for (LearningListener l : learningListeners) {
				if (l instanceof AutoLearnListener) {
					AutoLearnListener al = (AutoLearnListener) l;

					if (!al.isLearned()) {
						try {
							al.setJavaScript(learner.learn(al));
						} catch (IOException e) {
							// This is kind of impossible since we are writing
							// to a god damn string
							e.printStackTrace();
						}
						changed = true;
					}
				}
			}

		if (changed)
			ownerRepaint();
	}

	void processLearnedStateless() {
		if (learningListeners != null)
			for (LearningListener l : learningListeners) {
				if (l.isLearned()) {
					l.trigger();
				}
			}
	}

	void processDynamic(JavaScriptEvent jsEvent) {
		if (learningListeners != null)
			for (LearningListener l : learningListeners) {
				if (!l.isLearned())
					l.trigger();
			}

	}

	/**
	 * Adds a JavaScript event listener.
	 * 
	 * @param slot the JavaScript event listener
	 */
	public void addListener(JSlot slot) {
		addListener(null, slot.getSlotimp());
	}

	/**
	 * Adds a JavaScript event listener.
	 * 
	 * This adds a listener that is implemented solely as a client-side JavaScript function.
	 * The argument is a JavaScript function which optionally accepts a DOM element and the
	 * event:
	 * <pre>
	 *   function(element, event)
	 *   {
	 *      ...
	 *   }
	 * </pre>
	 * 
	 * @param javascript the JavaScript function.
	 */
	public void addListener(String javascript) {
		addListener(null, new JavaScriptListener(null, null, "(" + javascript + ")(o,e);"));
	}

	/**
	 * Prevents the default action associated with this event.
	 * <p>
	 * This prevents the default browser action associated with this event.
	 * 
	 * @param prevent whether the default action should be prevented.
	 */
	public void preventDefaultAction(boolean prevent) {
		if (isDefaultActionPrevented() != prevent) {
			flags_ |= BIT_PREVENT_DEFAULT;
			ownerRepaint();
		}
	}

	/**
	 * Prevents the default action associated with this event.
	 * <p>
	 * This prevents the default browser action associated with this event.
	 */
	public void preventDefaultAction() {
		preventDefaultAction(true);
	}

	/**
	 * Prevents the default action associated with this event.
	 * <p>
	 * This prevents the event propagation to ancestors.
	 * 
	 * @param prevent whether the default action should be prevented.
	 */
	public void preventPropagation(boolean prevent) {
		if (isPropagationPrevented() != prevent) {
			flags_ |= BIT_PREVENT_PROPAGATION;
			ownerRepaint();
		}
	}

	/**
	 * Prevents the default action associated with this event.
	 * <p>
	 * This prevents both the default browser action associated with this event as well as the
	 * event bubbling up or cascading its hierarchy.
	 */
	public final void preventPropagation() {
		preventPropagation(true);
	}

	void setNotExposed() {
		flags_ &= ~BIT_EXPOSED;
	}


	public void disconnect(Connection connection) {
		connection.disconnect();
		if ((flags_ & BIT_EXPOSED) != 0)
			if (!isConnected()) {
				WApplication.getInstance().removeExposedSignal(this);
				flags_ &= ~BIT_EXPOSED;
			}
		
		ownerRepaint();
	}

	protected String createUserEventCall(String jsObject, String jsEvent, String name, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6) {
		StringBuilder out = new StringBuilder();

		out.append(getJavaScript());

		WApplication app = WApplication.getInstance();
		{
			out.append(app.getJavaScriptClass()).append(".emit('").append(getSender().getUniqueId());

			if (jsObject != null)
				out.append("', { name:'").append(name).append("', eventObject:").append(jsObject).append(",event:").append(jsEvent).append('}');
			else
				out.append("','").append(name).append('\'');

			if (arg1 != null) {
				out.append(',').append(arg1);
				if (arg2 != null) {
					out.append(',').append(arg2);
					if (arg3 != null) {
						out.append(',').append(arg3);
						if (arg4 != null) {
							out.append(',').append(arg4);
							if (arg5 != null) {
								out.append(',').append(arg5);
								if (arg6 != null) {
									out.append(',').append(arg6);
								}
							}
						}
					}
				}
			}

			out.append(");");
		}

		return out.toString();
	}

	String getName() {
		return name_;
	}
}

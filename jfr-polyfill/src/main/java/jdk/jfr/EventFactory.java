/*
 * Copyright (c) 2016, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package jdk.jfr;

import java.util.List;
import java.util.Objects;

/**
 * Class for defining an event at runtime.
 * <p>
 * It's highly recommended that the event is defined at compile time, if the
 * field layout is known, so the Java Virtual Machine (JVM) can optimize the
 * code, possibly remove all instrumentation if Flight Recorder is inactive or
 * if the enabled setting for this event is set to {@code false}.
 * <p>
 * To define an event at compile time, see {@link Event}.
 * <p>
 * The following example shows how to implement a dynamic {@code Event} class.
 *
 * <pre>
 * {@code
 * List<ValueDescriptor> fields = new ArrayList<>();
 * List<AnnotationElement> messageAnnotations = Collections.singletonList(new AnnotationElement(Label.class, "Message"));
 * fields.add(new ValueDescriptor(String.class, "message", messageAnnotations));
 * List<AnnotationElement> numberAnnotations = Collections.singletonList(new AnnotationElement(Label.class, "Number"));
 * fields.add(new ValueDescriptor(int.class, "number", numberAnnotations));
 *
 * String[] category = { "Example", "Getting Started" };
 * List<AnnotationElement> eventAnnotations = new ArrayList<>();
 * eventAnnotations.add(new AnnotationElement(Name.class, "com.example.HelloWorld"));
 * eventAnnotations.add(new AnnotationElement(Label.class, "Hello World"));
 * eventAnnotations.add(new AnnotationElement(Description.class, "Helps programmer getting started"));
 * eventAnnotations.add(new AnnotationElement(Category.class, category));
 *
 * EventFactory f = EventFactory.create(eventAnnotations, fields);
 *
 * Event event = f.newEvent();
 * event.set(0, "hello, world!");
 * event.set(1, 4711);
 * event.commit();
 * }
 * </pre>
 *
 * @since 8
 */
public final class EventFactory {

    private EventFactory() {
    }

    /**
     * Creates an {@code EventFactory} object.
     * <p>
     * The order of the value descriptors specifies the index to use when setting
     * event values.
     *
     * @param annotationElements list of annotation elements that describes the
     *        annotations on the event, not {@code null}
     *
     * @param fields list of descriptors that describes the fields of the event, not
     *        {@code null}
     *
     * @return event factory, not {@code null}
     *
     * @throws IllegalArgumentException if the input is not valid. For example,
     *         input might not be valid if the field type or name is not valid in
     *         the Java language or an annotation element references a type that
     *         can't be found.
     *
     * @throws SecurityException if a security manager exists and the caller does
     *         not have {@code FlightRecorderPermission("registerEvent")}
     *
     * @see Event#set(int, Object)
     */
    public static EventFactory create(List<AnnotationElement> annotationElements, List<ValueDescriptor> fields) {
        Objects.requireNonNull(fields);
        Objects.requireNonNull(annotationElements);
        return new EventFactory();
    }

    /**
     * Instantiates an event, so it can be populated with data and written to the
     * Flight Recorder system.
     * <p>
     * Use the {@link Event#set(int, Object)} method to set a value.
     *
     * @return an event instance, not {@code null}
     */
    public Event newEvent() {
        return new DummyEvent();
    }

    /**
     * Returns the event type that is associated with this event factory.
     *
     * @return event type that is associated with this event factory, not
     *         {@code null}
     *
     * @throws java.lang.IllegalStateException if the event factory is created with
     *         the {@code Registered(false)} annotation and the event class is not
     *         manually registered before the invocation of this method
     */
    public EventType getEventType() {
        return EventType.getEventType(DummyEvent.class);
    }

    /**
     * Registers an unregistered event.
     * <p>
     * By default, the event class associated with this event factory is registered
     * when the event factory is created, unless the event has the
     * {@link Registered} annotation.
     * <p>
     * A registered event class can write data to Flight Recorder and event metadata
     * can be obtained by invoking {@link FlightRecorder#getEventTypes()}.
     * <p>
     * If the event class associated with this event factory is already registered,
     * the call to this method is ignored.
     *
     * @throws SecurityException if a security manager exists and the caller
     *         does not have {@code FlightRecorderPermission("registerEvent")}
     * @see Registered
     * @see FlightRecorder#register(Class)
     */
    public void register() {
    }

    /**
     * Unregisters the event that is associated with this event factory.
     * <p>
     * A unregistered event class can't write data to Flight Recorder and event
     * metadata can't be obtained by invoking
     * {@link FlightRecorder#getEventTypes()}.
     * <p>
     * If the event class associated with this event factory is not already
     * registered, the call to this method is ignored.
     *
     * @throws SecurityException if a security manager exists and the caller does
     *         not have {@code FlightRecorderPermission("registerEvent")}
     * @see Registered
     * @see FlightRecorder#unregister(Class)
     */
    public void unregister() {
    }

    /**
     * Dummy event returned by this polyfill library
     */
    private static class DummyEvent extends Event {

    }
}
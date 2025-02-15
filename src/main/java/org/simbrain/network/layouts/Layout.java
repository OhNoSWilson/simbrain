/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2005,2007 The Authors.  See http://www.simbrain.net/credits
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.simbrain.network.layouts;

import org.simbrain.network.core.Neuron;
import org.simbrain.util.UserParameter;
import org.simbrain.util.propertyeditor.AnnotatedPropertyEditor;
import org.simbrain.util.propertyeditor.CopyableObject;
import org.simbrain.util.propertyeditor.EditableObject;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

/**
 * Interface for all neuron layout managers, which arrange a set of neurons in
 * different ways.
 *
 * @author Jeff Yoshimi
 */
public interface Layout extends CopyableObject {

    /**
     * Layout a list of neurons.
     *
     * @param neurons the list of neurons
     */
    void layoutNeurons(List<Neuron> neurons);

    /**
     * @return the name of this layout type
     */
    String getDescription();

    /**
     * Set the initial position.
     *
     * @param initialPoint initial position
     */
    void setInitialLocation(final Point2D initialPoint);

    /**
     * Called via reflection using {@link UserParameter#typeListMethod()}.
     */
    public static List<Class> getTypes() {
        return Arrays.asList(LineLayout.class, GridLayout.class, HexagonalGridLayout.class);
    }

    @Override
    default String getName() {
        return getDescription();
    }

    //TODO: A better name would be nice but we can't come up with one...
    /**
     * Layout wrapped for {@link AnnotatedPropertyEditor} to edit.
     */
    class LayoutObject implements EditableObject {

        /**
         * The layout to edit. Default to {@link LineLayout}.
         */
        @UserParameter(label = "Layout", isObjectType = true)
        private Layout layout = new LineLayout();

        /**
         * Construct with default line layout.
         */
        public LayoutObject() {
        }

        /**
         * Contstruct with a specified layout.
         *
         * @param layout layout to use
         */
        public LayoutObject(Layout layout) {
            this.layout = layout;
        }

        public Layout getLayout() {
            return layout;
        }

        public void setLayout(Layout layout) {
            this.layout = layout;
        }

    }
}

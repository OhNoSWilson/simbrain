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
package org.simbrain.network.gui.actions;

import org.simbrain.network.gui.NetworkPanel;
import org.simbrain.network.gui.NetworkSelectionEvent;
import org.simbrain.network.gui.NetworkSelectionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Set neuron properties.
 */
public final class SetTextPropertiesAction extends AbstractAction {

    /**
     * Network panel.
     */
    private final NetworkPanel networkPanel;

    /**
     * Create a new set neuron properties action with the specified network
     * panel.
     *
     * @param networkPanel networkPanel, must not be null
     */
    public SetTextPropertiesAction(final NetworkPanel networkPanel) {

        super("Text Properties...");

        if (networkPanel == null) {
            throw new IllegalArgumentException("networkPanel must not be null");
        }
        putValue(SHORT_DESCRIPTION, "Set the properties of this text, e.g. font and size");


        this.networkPanel = networkPanel;
        updateAction();

        // add a selection listener to update state based on selection
        networkPanel.addSelectionListener(new NetworkSelectionListener() {

            /** @see NetworkSelectionListener */
            public void selectionChanged(final NetworkSelectionEvent event) {
                updateAction();
            }
        });
    }

    /**
     * Set action text based on number of selected neurons.
     */
    private void updateAction() {

    }

    /**
     * @param event
     * @see AbstractAction
     */
    public void actionPerformed(final ActionEvent event) {

        networkPanel.showTextPropertyDialog();

    }
}
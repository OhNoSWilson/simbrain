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
package org.simbrain.network.synapse_update_rules.spikeresponders;

import org.simbrain.network.core.Synapse;

/**
 * A "null" spike responder for the case where non-spiking neurons are connected.
 * The response is pre-synaptic activations times weight strenght.
 * This facilitates a simpler design for {@link Synapse}s.
 */
public class NonResponder extends SpikeResponder {

    @Override
    public void update(Synapse s) {
        s.setPsr(s.getSource().getActivation() * s.getStrength());
    }

    @Override
    public SpikeResponder deepCopy() {
        NonResponder nonResponder = new NonResponder();
        return nonResponder;
    }

    @Override
    public String getDescription() {
        return "None (No spike response)";
    }

    @Override
    public String getName() {
        return "None";
    }

}
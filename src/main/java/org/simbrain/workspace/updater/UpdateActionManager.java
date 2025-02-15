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
package org.simbrain.workspace.updater;

import org.simbrain.workspace.Coupling;
import org.simbrain.workspace.CouplingListener;
import org.simbrain.workspace.WorkspaceComponent;
import org.simbrain.workspace.WorkspaceListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Maintain a list of workspace update actions. When the workspace is iterated
 * once (in the GUI, when the step button is clicked), the actions contained
 * here are invoked, in a specific order. By default a single action, buffered
 * update, is invoked. But custom update orders can be created by manipulating
 * the main update action list here.
 *
 * @author jyoshimi
 */
public class UpdateActionManager {

    /**
     * Listen for changes to update manager.
     */
    public interface UpdateManagerListener {

        /**
         * Called whenever an action is added to the update action manager.
         *
         * @param action The action that was added.
         */
        public void actionAdded(UpdateAction action);

        /**
         * Called whenever an action is removed from the update action manager.
         *
         * @param action The action that was removed.
         */
        public void actionRemoved(UpdateAction action);

        /**
         * Called whenever the order of update actions is changed.
         */
        public void actionOrderChanged();
    }

    /**
     * The list of update actions, in a specific order. One run through these
     * actions constitutes a single iteration of the workspace.
     */
    private final List<UpdateAction> actionList = new CopyOnWriteArrayList<UpdateAction>();

    /**
     * List of listeners on this update manager.
     */
    private final transient List<UpdateManagerListener> listeners = new ArrayList<UpdateManagerListener>();

    /**
     * Reference to workspace.
     */
    private final WorkspaceUpdater workspaceUpdater;

    /**
     * Keep track of relations between coupling and coupling actions so they can
     * be cleaned up.
     */
    private HashMap<Coupling<?>, UpdateCoupling> couplingActionMap = new HashMap<>();

    /**
     * Keep track of relations between component and component actions so they
     * can be cleaned up.
     */
    private HashMap<WorkspaceComponent, UpdateComponent> componentActionMap = new HashMap<>();

    /**
     * Construct a new update manager.
     *
     * @param workspace reference to workspace updater.
     */
    public UpdateActionManager(WorkspaceUpdater workspace) {
        this.workspaceUpdater = workspace;
        setDefaultUpdateActions();
        addListeners();
    }

    /**
     * Perform initialization after deserializing.
     */
    public void postAddInit() {
        addListeners();
    }

    /**
     * Update manager should listen for relevant changes in workspace.
     */
    private void addListeners() {
        // Add / remove component actions as needed
        workspaceUpdater.getWorkspace().addListener(new WorkspaceListener() {

            @Override
            public void workspaceCleared() {
            }

            @Override
            public void newWorkspaceOpened() {
            }

            @Override
            public void componentAdded(WorkspaceComponent component) {
                UpdateComponent componentAction = new UpdateComponent(workspaceUpdater, component);
                componentActionMap.put(component, componentAction);
            }

            @Override
            public void componentRemoved(WorkspaceComponent component) {
                removeAction(componentActionMap.remove(component));
            }

        });

        // Add / remove coupling actions as needed
        workspaceUpdater.getWorkspace().getCouplingManager().addCouplingListener(new CouplingListener() {
            @Override
            public void couplingAdded(Coupling<?> coupling) {
                UpdateCoupling couplingAction = new UpdateCoupling(coupling);
                couplingActionMap.put(coupling, couplingAction);
            }

            @Override
            public void couplingRemoved(Coupling<?> coupling) {
                removeAction(couplingActionMap.remove(coupling));
            }

            @Override
            public void couplingsRemoved(List<Coupling<?>> couplings) {
                for (Coupling coupling : couplings) {
                    removeAction(couplingActionMap.remove(coupling));
                }
            }
        });
    }

    /**
     * Listen for updates to the update manager.
     *
     * @param listener the listener to add
     */
    public void addListener(UpdateManagerListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove listener.
     *
     * @param listener the listener to remove
     */
    public void removeListener(UpdateManagerListener listener) {
        listeners.remove(listener);
    }

    /**
     * @return the actionList
     */
    public List<UpdateAction> getActionList() {
        return actionList;
    }

    /**
     * Swap elements at the specified location.
     *
     * @param index1 index of first element
     * @param index2 index of second element
     */
    public void swapElements(int index1, int index2) {
        Collections.swap(actionList, index1, index2);
        for (UpdateManagerListener listener : listeners) {
            listener.actionOrderChanged();
        }
    }

    /**
     * Add an action to the list.
     *
     * @param action the action to add.
     */
    public void addAction(UpdateAction action) {
        actionList.add(action);
        for (UpdateManagerListener listener : listeners) {
            listener.actionAdded(action);
        }
    }

    /**
     * Completely remove an action.
     *
     * @param action the action to completely remove
     */
    public void removeAction(UpdateAction action) {
        actionList.remove(action);
        for (UpdateManagerListener listener : listeners) {
            listener.actionRemoved(action);
        }
    }

    /**
     * Remove all actions completely.
     */
    public void clear() {
        for (UpdateAction action : actionList) {
            removeAction(action);
        }
    }

    /**
     * Puts the update in its default configuration, with Buffered update as the
     * default action.
     */
    public void setDefaultUpdateActions() {
        clear();
        addAction(new UpdateAllAction(workspaceUpdater));
        addAction(workspaceUpdater.getSyncUpdateAction());
    }

    /**
     * Returns an update action matching the provided name, or null if none is
     * found.
     *
     * @param toFind name of action to find
     * @return the matching update action, or null if none found
     */
    public UpdateAction getAction(String toFind) {
        for (UpdateAction action : getAvailableActionList()) {
            if (action.getDescription().equalsIgnoreCase(toFind)) {
                return action;
            }
        }
        return null;
    }

    /**
     * Returns an update action whose description corresponds to the provided
     * string (case is ignored).
     * <p>
     * TODO: This facilitates access to update actions, but not in a very pretty
     * way. It is used by ElmanPhonemes.bsh and ElmanSentences.bsh. Probably
     * need to add some kind of id field or something to UpdateAction so that
     * it's easier to retrieve the update actions.
     *
     * @param toFind the string to match
     * @return matching update action or null if no match found
     */
    public UpdateAction getUpdateAction(String toFind) {
        for (UpdateAction action : getAvailableActionList()) {
            if (action.getDescription().equalsIgnoreCase(toFind)) {
                return action;
            }
        }
        return null;
    }

    /**
     * Returns a list of network update actions that can be added.
     *
     * @return available action list
     */
    public List<UpdateAction> getAvailableActionList() {
        List<UpdateAction> availableActionList = new ArrayList<UpdateAction>();

        // Default updater
        availableActionList.add(new UpdateAllAction(workspaceUpdater));
        availableActionList.add(new WorkspaceDelayAction(workspaceUpdater.getWorkspace()));

        // Add update actions for all components available
        for (WorkspaceComponent component : workspaceUpdater.getComponents()) {
            availableActionList.add(new UpdateComponent(workspaceUpdater, component));
        }

        // Add update actions for all components available
        for (Coupling coupling : workspaceUpdater.getWorkspace().getCouplings()) {
            availableActionList.add(new UpdateCoupling(coupling));
        }

        return availableActionList;
    }

}

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
package org.simbrain.network.gui.dialogs.group;

import org.simbrain.network.groups.NeuronGroup;
import org.simbrain.network.groups.SynapseGroup;
import org.simbrain.network.gui.NetworkPanel;
import org.simbrain.network.gui.WeightMatrixViewer;
import org.simbrain.network.gui.dialogs.connect.ConnectionSelectorPanel;
import org.simbrain.network.gui.dialogs.connect.SynapsePropertiesPanel;
import org.simbrain.network.gui.dialogs.synapse.SynapseGroupAdjustmentPanel;
import org.simbrain.util.StandardDialog;
import org.simbrain.util.widgets.ApplyPanel;
import org.simbrain.util.widgets.ShowHelpAction;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Dialog for editing synapse groups.
 *
 * @author Jeff Yoshimi
 * @author Zoë Tosi
 */
@SuppressWarnings("serial")
public final class SynapseGroupDialog extends StandardDialog {

    /**
     * Parent network panel.
     */
    private NetworkPanel networkPanel;

    /**
     * Synapse Group.
     */
    private SynapseGroup synapseGroup;

    /**
     * Main tabbed pane.
     */
    private JTabbedPane tabbedPane = new JTabbedPane();

    /**
     * Label Field.
     */
    private JTextField tfSynapseGroupLabel = new JTextField();

    /**
     * Panel for editing synapses in the group.
     */
    private SynapsePropertiesPanel editSynapsesPanel;

    /**
     * Panel for adjusting the connection object.
     */
    private ConnectionSelectorPanel connectionPanel;

    /**
     * Panel for adjusting the synapse group
     */
    private SynapseGroupAdjustmentPanel adjustmentPanel;

    /**
     * If true this is a creation dialog. Otherwise it is an edit dialog.
     */
    private boolean isCreationDialog = false;

    /**
     * Reference to source neuron group.
     */
    private NeuronGroup sourceNeuronGroup = null;

    /**
     * Reference to target neuron group.
     */
    private NeuronGroup targetNeuronGroup = null;

    /**
     * The list of components which are stored here so their tabs can be blanked
     * out. This is what allows the panel to resize when tabs are changed.
     */
    private ArrayList<Component> storedComponents = new ArrayList<Component>();

    /**
     * See {@link SynapseGroup#useGroupLevelSettings}.
     */
    private boolean setUseGroupLevelSettings;

    /**
     * Summary information panel
     */
    private SummaryPanel sumPanel;

    /**
     * When editing the connection strategy must be explicitly applied with a button press.
     */
    private ApplyPanel connectionApplyPanel;

    /**
     * Creates a synapse group dialog based on a source and target neuron group.
     * This should be used when the synapse group being "edited" doesn't exist
     * yet, i.e. it's being created from the parameters in this panel.
     *
     * @param np  the network panel
     * @param src the source neuron group
     * @param tar the target neuron group
     * @return a synapse group dialog for creating a synapse group between the
     * source and target neuron groups.
     */
    public static SynapseGroupDialog createSynapseGroupDialog(final NetworkPanel np, NeuronGroup src, NeuronGroup tar) {
        SynapseGroupDialog sgd = new SynapseGroupDialog(np, src, tar);
        sgd.tabbedPane.setSelectedIndex(0);
        return sgd;
    }

    /**
     * Creates a synapse group dialog based on a given synapse group it goes
     * without saying that this means this dialog will be editing the given
     * synapse group.
     *
     * @param np the network panel
     * @param sg the synapse group being edited
     * @return a synapse group dialog which can edit the specified synapse group
     */
    public static SynapseGroupDialog createSynapseGroupDialog(final NetworkPanel np, final SynapseGroup sg) {
        SynapseGroupDialog sgd = new SynapseGroupDialog(np, sg);
        sgd.tabbedPane.setSelectedIndex(0);
        return sgd;
    }

    /**
     * Create a new synapse group connecting the indicated neuron groups.
     *
     * @param src source neuron group
     * @param tar target neuron group
     * @param np  parent panel
     */
    private SynapseGroupDialog(final NetworkPanel np, NeuronGroup src, NeuronGroup tar) {
        networkPanel = np;
        this.sourceNeuronGroup = src;
        this.targetNeuronGroup = tar;
        isCreationDialog = true;
        init();
    }

    /**
     * Construct the Synapse group dialog.
     *
     * @param np Parent network panel
     * @param sg Synapse group being edited
     */
    private SynapseGroupDialog(final NetworkPanel np, final SynapseGroup sg) {
        networkPanel = np;
        synapseGroup = sg;
        this.sourceNeuronGroup = sg.getSourceNeuronGroup();
        this.targetNeuronGroup = sg.getTargetNeuronGroup();
        init();
    }

    /**
     * Initialize the panel.
     */
    private void init() {

        if (isCreationDialog) {
            setTitle("Create Synapse Group");
        } else {
            setTitle("Edit " + synapseGroup.getLabel());
        }

        setMinimumSize(new Dimension(500, 300));
        fillFieldValues();
        setContentPane(tabbedPane);

        // Summary Info
        JPanel tabSummaryInfo = new JPanel();
        if (isCreationDialog) {
            synapseGroup = new SynapseGroup(sourceNeuronGroup, targetNeuronGroup);
            sumPanel = new SummaryPanel(synapseGroup);
            JPanel container = new JPanel();
            container.add(sumPanel);
            tabSummaryInfo = container;
        } else {
            sumPanel = new SummaryPanel(synapseGroup);
            tabSummaryInfo = ApplyPanel.createApplyPanel(sumPanel);
            ((ApplyPanel) tabSummaryInfo).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setUseGroupLevelSettings = sumPanel.getUseGlobalSettingsChkBx().isSelected();
                }
            });
        }
        JScrollPane summaryScrollWrapper = new JScrollPane(tabSummaryInfo);
        summaryScrollWrapper.setBorder(null);
        storedComponents.add(summaryScrollWrapper);
        tabbedPane.addTab("Properties", summaryScrollWrapper);

        // Connectivity panel
        if (isCreationDialog) {
            connectionPanel = new ConnectionSelectorPanel(sourceNeuronGroup.equals(targetNeuronGroup), this, isCreationDialog);
            JScrollPane connectWrapper = new JScrollPane(connectionPanel);
            connectWrapper.setBorder(null);
            storedComponents.add(connectWrapper);
            tabbedPane.addTab("Connection Type", connectWrapper);
        } else {
            connectionPanel = new ConnectionSelectorPanel(synapseGroup.getConnectionManager(), this, isCreationDialog);
            connectionApplyPanel  =  ApplyPanel.createCustomApplyPanel(connectionPanel,
                    (ActionEvent e) -> {
                connectionPanel.getCurrentConnectionPanel().commitChanges(synapseGroup);
                sumPanel.fillFieldValues(synapseGroup);
                adjustmentPanel.fullUpdate();
            });
            JScrollPane connectWrapper = new JScrollPane(connectionApplyPanel);
            connectWrapper.setBorder(null);
            storedComponents.add(connectWrapper);
            tabbedPane.addTab("Connection Manager", connectWrapper);
        }

        // Weight matrix
        if (!isCreationDialog) {
            if (synapseGroup.size() < 10000) {
                JPanel weightMatrix = new JPanel();
                final JScrollPane matrixScrollPane = new JScrollPane(weightMatrix);
                matrixScrollPane.setBorder(null);
                weightMatrix.add(WeightMatrixViewer.getWeightMatrixPanel(new WeightMatrixViewer(synapseGroup.getSourceNeurons(), synapseGroup.getTargetNeurons(), networkPanel)));
                storedComponents.add(matrixScrollPane);
                tabbedPane.addTab("Matrix", matrixScrollPane);
            }
        }

        // Tab for editing synapses
        editSynapsesPanel = SynapsePropertiesPanel.createSynapsePropertiesPanel(this, synapseGroup);
        if (!isCreationDialog) {
            ActionListener al = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            sumPanel.fillFieldValues(synapseGroup);
                            sumPanel.repaint();
                            repaint();
                            adjustmentPanel.fullUpdate();
                        }
                    });
                }
            };
            editSynapsesPanel.addApplyListenerEx(al);
            editSynapsesPanel.addApplyListenerIn(al);
        }
        JScrollPane editSynapseScrollPane = new JScrollPane(editSynapsesPanel);
        editSynapseScrollPane.setBorder(null);
        storedComponents.add(editSynapseScrollPane);
        tabbedPane.addTab("Synapse Type", editSynapseScrollPane);

        // Synapse Adjustment Panel
        if(!isCreationDialog) {
            adjustmentPanel = SynapseGroupAdjustmentPanel.createSynapseGroupAdjustmentPanel(this, synapseGroup, isCreationDialog);
            adjustmentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            JScrollPane adjustSynScrollPane = new JScrollPane(adjustmentPanel);
            adjustSynScrollPane.setBorder(null);
            storedComponents.add(adjustSynScrollPane);
            tabbedPane.addTab("Synapse Values", adjustSynScrollPane);
        }

        // Set up help button
        Action helpAction;
        helpAction = new ShowHelpAction("Pages/Network/groups/SynapseGroup.html");
        addButton(new JButton(helpAction));

        // Tab-change events
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateTabSizes(((JTabbedPane) e.getSource()).getSelectedIndex());
            }
        });
        updateTabSizes(0);

        if (!isCreationDialog) {
            // If editing, make this dialog based on a done button, rather than
            // ok and cancel. All edits are done with apply
            setAsDoneDialog();
        }
    }

    /**
     * Add listeners.
     */
    private void updateTabSizes(int selectedTab) {
        Component current = storedComponents.get(selectedTab);
        int numTabs = storedComponents.size();
        for (int i = 0; i < numTabs; i++) {
            if (i == selectedTab) {
                tabbedPane.setComponentAt(i, current);
                tabbedPane.repaint();
                continue;
            } else {
                JPanel tmpPanel = new JPanel();
                // Hack...
                // 120 is a guess as to average px length of tabs
                // (not their panels, just the tabs)
                // This is here to prevent "scrunching" of the
                // tabs when one of the panel's widths is too small
                // to accommodate all the tabs on one line
                int minPx = tabbedPane.getTabCount() * 120;
                if (current.getPreferredSize().width < minPx) {
                    tmpPanel.setPreferredSize(new Dimension(minPx, current.getPreferredSize().height));
                } else {
                    tmpPanel.setPreferredSize(current.getPreferredSize());
                }
                tabbedPane.setComponentAt(i, tmpPanel);
            }
        }
        // tabbedPane.invalidate();
        pack();
    }

    /**
     * Set the initial values of dialog components.
     */
    public void fillFieldValues() {
        if (!isCreationDialog) {
            tfSynapseGroupLabel.setText(synapseGroup.getLabel());
        } else {
            tfSynapseGroupLabel.setText("Synapse group");
        }
    }

    /**
     * Commit changes.
     */
    public void commitChanges() {
        if (isCreationDialog) {
            connectionPanel.getCurrentConnectionPanel().commitChanges(synapseGroup);
            synapseGroup.setConnectionManager(connectionPanel.getSelectedConnector());

            sumPanel.commitChanges();
            editSynapsesPanel.commitChanges();

            networkPanel.getNetwork().addGroup(synapseGroup);
            networkPanel.repaint();
        } else {
            // Must be set ONLY after dialog is closed otherwise changes won't
            // take effect
            synapseGroup.setUseGroupLevelSettings(setUseGroupLevelSettings);
            // When editing a synpase group most edits are handled by apply buttons
        }
    }

    @Override
    protected void closeDialogOk() {
        super.closeDialogOk();
        if (isCreationDialog) {
            commitChanges();
        }
    }

    @Override
    public void pack() {
        super.pack();
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int height = gd.getDisplayMode().getHeight();
        if(this.getLocation().y + this.getBounds().height > height) {
            this.setBounds(getLocation().x, getLocation().y, getWidth(), height - getLocation().y);
        }
    }

}
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

package org.simbrain.network.gui.nodes;

import org.piccolo2d.extras.nodes.PStyledText;
import org.simbrain.network.core.NetworkTextObject;
import org.simbrain.network.gui.NetworkPanel;
import org.simbrain.network.gui.actions.SetTextPropertiesAction;
import org.simbrain.network.gui.actions.edit.CopyAction;
import org.simbrain.network.gui.actions.edit.CutAction;
import org.simbrain.network.gui.actions.edit.DeleteAction;
import org.simbrain.network.gui.actions.edit.PasteAction;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * An editable text element, which wraps a PStyledText object.
 */
public class TextNode extends ScreenElement implements PropertyChangeListener {

    /**
     * The text object.
     */
    private final PStyledText pStyledText;

    /**
     * Underlying model text object.
     */
    private final NetworkTextObject textObject;

    /**
     * Construct text object at specified location.
     *
     * @param netPanel reference to networkPanel
     * @param text     the network text object
     */
    public TextNode(final NetworkPanel netPanel, final NetworkTextObject text) {
        super(netPanel);
        this.textObject = text;
        pStyledText = new PStyledText();
        pStyledText.setDocument(new DefaultStyledDocument());
        this.addChild(pStyledText);
        this.setBounds(pStyledText.getBounds());
        addPropertyChangeListener(PROPERTY_FULL_BOUNDS, this);
        text.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("delete".equals(evt.getPropertyName())) {
                    TextNode.this.removeFromParent();
                }
            }
        });
        update();
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public boolean showSelectionHandle() {
        return true;
    }

    @Override
    public boolean isDraggable() {
        return true;
    }

    @Override
    protected boolean hasToolTipText() {
        return false;
    }

    @Override
    protected String getToolTipText() {
        return null;
    }

    @Override
    protected boolean hasContextMenu() {
        return true;
    }

    @Override
    protected JPopupMenu getContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();

        contextMenu.add(new CutAction(getNetworkPanel()));
        contextMenu.add(new CopyAction(getNetworkPanel()));
        contextMenu.add(new PasteAction(getNetworkPanel()));
        contextMenu.addSeparator();

        //contextMenu.add(getNetworkPanel().getActionManager().getGroupAction());
        //contextMenu.addSeparator();

        contextMenu.add(new DeleteAction(getNetworkPanel()));

        if (getNetworkPanel().getSelectedText().size() > 0) {
            contextMenu.addSeparator();
            contextMenu.add(new SetTextPropertiesAction(getNetworkPanel()));
        }

        return contextMenu;
    }

    @Override
    protected boolean hasPropertyDialog() {
        return false;
    }

    @Override
    protected JDialog getPropertyDialog() {
        return null;
    }

    @Override
    public void resetColors() {
    }

    @Override
    public void propertyChange(PropertyChangeEvent arg0) {
        setBounds(pStyledText.getBounds());
    }

    public PStyledText getPStyledText() {
        return pStyledText;
    }

    /**
     * Update the styled text object based on the model object.
     */
    public void update() {
        try {
            AttributeSet as = TextNode.createAttributeSet(textObject.getFontName(), textObject.getFontSize(), textObject.isItalic(), textObject.isBold());
            pStyledText.getDocument().remove(0, pStyledText.getDocument().getLength());
            pStyledText.getDocument().insertString(0, textObject.getText(), as);
            pStyledText.syncWithDocument();
            pullViewPositionFromModel();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the textObject
     */
    public NetworkTextObject getTextObject() {
        return textObject;
    }

    /**
     * Update the position of the model text object based on the global
     * coordinates of this pnode.
     */
    public void pushViewPositionToModel() {
        Point2D p = this.getGlobalTranslation();
        getTextObject().setX(p.getX());
        getTextObject().setY(p.getY());
    }

    @Override
    public void offset(double dx, double dy) {
        super.offset(dx, dy);
        pushViewPositionToModel();
    }

    /**
     * Updates the position of the view text based on the position of the model
     * text object.
     */
    private void pullViewPositionFromModel() {
        Point2D p = new Point2D.Double(getTextObject().getX(), getTextObject().getY());
        this.setGlobalTranslation(p);
    }

    /**
     * Creates an attribute set of the specified kind.
     *
     * @param fontName name of font in attribute set
     * @param fontSize size of font in attribute set
     * @param italic   italic or not
     * @param bold     bold or not
     * @return the resulting attribute set
     * @author Aaron Dixon
     */
    public static SimpleAttributeSet createAttributeSet(String fontName, int fontSize, boolean italic, boolean bold) {
        SimpleAttributeSet as = new SimpleAttributeSet();
        as.addAttribute(StyleConstants.CharacterConstants.FontFamily, fontName);
        as.addAttribute(StyleConstants.CharacterConstants.FontSize, fontSize);
        as.addAttribute(StyleConstants.CharacterConstants.Italic, italic);
        as.addAttribute(StyleConstants.CharacterConstants.Bold, bold);
        as.addAttribute(StyleConstants.ALIGN_RIGHT, true);
        return as;
    }

}

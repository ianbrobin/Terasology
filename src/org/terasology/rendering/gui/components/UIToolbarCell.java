/*
 * Copyright 2011 Benjamin Glatzel <benjamin.glatzel@me.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.rendering.gui.components;

import org.lwjgl.opengl.GL11;
import org.terasology.components.InventoryComponent;
import org.terasology.components.ItemComponent;
import org.terasology.components.LocalPlayerComponent;
import org.terasology.components.PlaceableBlockComponent;
import org.terasology.entitySystem.EntityRef;
import org.terasology.game.Terasology;
import org.terasology.logic.manager.TextureManager;
import org.terasology.model.blocks.Block;
import org.terasology.model.blocks.BlockGroup;
import org.terasology.model.inventory.Icon;
import org.terasology.model.inventory.Inventory;
import org.terasology.model.inventory.Item;
import org.terasology.rendering.gui.framework.UIDisplayElement;
import org.terasology.rendering.gui.framework.UIGraphicsElement;

import javax.vecmath.Vector2f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * A single cell of the toolbar with a small text label and a selection
 * rectangle.
 *
 * @author Benjamin Glatzel <benjamin.glatzel@me.com>
 */
public class UIToolbarCell extends UIDisplayElement {

    private final UIGraphicsElement _selectionRectangle;
    private final UIText _label;

    private int _id;
    private boolean _selected = false;

    public UIToolbarCell(int id) {
        _id = id;

        setSize(new Vector2f(48f, 48f));

        _selectionRectangle = new UIGraphicsElement("gui");
        _selectionRectangle.getTextureSize().set(new Vector2f(24f / 256f, 24f / 256f));
        _selectionRectangle.getTextureOrigin().set(new Vector2f(0.0f, 24f / 256f));
        _selectionRectangle.setSize(new Vector2f(48f, 48f));

        _label = new UIText();
        _label.setVisible(true);
        _label.setPosition(new Vector2f(30f, 20f));
    }

    @Override
    public void update() {
        if (Terasology.getInstance().getActiveWorldRenderer() == null)
            return;

        _selectionRectangle.setVisible(_selected);
        setPosition(new Vector2f((getSize().x - 8f) * _id - 2f, 2f));

        InventoryComponent inventory = Terasology.getInstance().getActivePlayer().getEntity().getComponent(InventoryComponent.class);
        if (inventory.itemSlots.size() > _id) {
            LocalPlayerComponent localPlayer = Terasology.getInstance().getActivePlayer().getEntity().getComponent(LocalPlayerComponent.class);
            if (localPlayer != null) {
                setSelected(localPlayer.selectedTool == _id);
            }

            EntityRef itemStack = inventory.itemSlots.get(_id);
            ItemComponent item = null;
            if (itemStack != null) {
                item = itemStack.getComponent(ItemComponent.class);
            }
            if (item == null || item.stackCount < 2) {
                getLabel().setVisible(false);
            } else {
                getLabel().setVisible(true);
                getLabel().setText(Integer.toString(item.stackCount));
            }
        }
    }

    @Override
    public void render() {
        if (Terasology.getInstance().getActiveWorldRenderer() == null)
            return;

        _selectionRectangle.renderTransformed();

        InventoryComponent inventory = Terasology.getInstance().getActivePlayer().getEntity().getComponent(InventoryComponent.class);
        if (inventory.itemSlots.size() <= _id) 
            return;
        
        EntityRef itemEntity = inventory.itemSlots.get(_id);
        if (itemEntity == null)
            return;
        
        ItemComponent item = itemEntity.getComponent(ItemComponent.class);
        if (item == null)
            return;

        if (item.icon.isEmpty()) {
            PlaceableBlockComponent placeableBlock = itemEntity.getComponent(PlaceableBlockComponent.class);
            if (placeableBlock != null) {
                renderBlockIcon(placeableBlock.blockGroup);
            }
        } else {
            Icon icon = Icon.get(item.icon);
            if (icon != null)
            {
                renderIcon(icon);
            }
        }

        _label.renderTransformed();    

    }
    
    private void renderIcon(Icon icon) {
        glEnable(GL11.GL_DEPTH_TEST);
        glClear(GL11.GL_DEPTH_BUFFER_BIT);
        glPushMatrix();
        glTranslatef(20f, 20f, 0f);
        icon.render();
        glPopMatrix();
        glDisable(GL11.GL_DEPTH_TEST);
    }
    
    private void renderBlockIcon(BlockGroup blockGroup) {
        if (blockGroup == null) return;

        glEnable(GL11.GL_DEPTH_TEST);
        glClear(GL11.GL_DEPTH_BUFFER_BIT);
        glPushMatrix();
        glTranslatef(20f, 20f, 0f);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPushMatrix();
        glTranslatef(4f, 0f, 0f);
        GL11.glScalef(20f, 20f, 20f);
        GL11.glRotatef(170f, 1f, 0f, 0f);
        GL11.glRotatef(-16f, 0f, 1f, 0f);
        TextureManager.getInstance().bindTexture("terrain");

        Block block = blockGroup.getArchetypeBlock();
        block.render();

        GL11.glPopMatrix();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        glPopMatrix();
        glDisable(GL11.GL_DEPTH_TEST);
    }

    public void setSelected(boolean selected) {
        _selected = selected;
    }

    public boolean getSelected() {
        return _selected;
    }

    public UIText getLabel() {
        return _label;
    }
}

package com.github.mdcdi1315.basemodslib.config.gui;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.FormatException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.config.lowlevelapi.Record;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.ArrayValue;
import com.github.mdcdi1315.basemodslib.client.gui.ITickableGuiElement;
import com.github.mdcdi1315.basemodslib.config.lowlevelapi.SerializedField;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.narration.NarrationElementOutput;

import java.util.List;
import java.util.ArrayList;

public class SettingsModifierList
    extends AbstractSelectionList<SettingsModifierList.AbstractSettingEntry>
    implements ITickableGuiElement
{
    public static int ITEMS_HEIGHT = 25;
    public static int ITEMS_WIDTH = 400;

    public SettingsModifierList(Minecraft minecraft, int width, int height, int y0) {
        super(minecraft, width, height, y0, ITEMS_HEIGHT);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput neo) {}

    public int getRowLeft() {
        return super.getRowLeft();
    }

    protected int getScrollbarPosition() {
        return (this.width + ITEMS_WIDTH) / 2;
    }

    @Override
    public int getRowWidth() {
        return ITEMS_WIDTH;
    }

    public void Tick() {
        for (var child : children()) {
            child.Tick();
        }
    }

    public abstract class AbstractSettingEntry
        extends AbstractSelectionList.Entry<AbstractSettingEntry>
        implements ISettingAccessor, ITickableGuiElement
    {
        private boolean focus_status;
        protected final FieldData setting_data;
        @AllowNull
        private final List<Component> desc_tool_tip;

        public AbstractSettingEntry(FieldData data) {
            ArgumentNullException.ThrowIfNull(data, "data");
            setting_data = data;
            focus_status = false;
            desc_tool_tip = setting_data.ConstructTooltipLinesFromComment();
        }

        // ISettingAccessor interface implementation
        @Override
        public final FieldData GetFieldData() {
            return setting_data;
        }

        @Override
        public final boolean isFocused() {
            return focus_status || SettingsModifierList.this.getFocused() == this;
        }

        @Override
        public void setFocused(boolean focused) {
            focus_status = focused;
            super.setFocused(focused);
        }

        public abstract void mouseMoved(double mouseX, double mouseY);

        public abstract boolean charTyped(char codePoint, int modifiers);

        public abstract boolean keyPressed(int keyCode, int scanCode, int modifiers);

        public abstract boolean keyReleased(int keyCode, int scanCode, int modifiers);

        public abstract boolean mouseClicked(double mouseX, double mouseY, int button);

        public abstract boolean mouseReleased(double mouseX, double mouseY, int button);

        public abstract boolean mouseScrolled(double mouseX, double mouseY, double scroll_x , double scroll_y);

        public abstract boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY);

        public abstract void RenderElementValue(GuiGraphics graphics, int top, int left, int width, int height, int mouse_x, int mouse_y, boolean hovering, float partialTick);

        @Override
        public final void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            int left_shift = left+4;
            int wd = (graphics.drawString(minecraft.font, setting_data.GetCulturizedDescription(), left_shift, (top-2) + (height / 2), 0xFF443355) - left_shift) + 10;

            int new_position = left + wd;
            if (desc_tool_tip != null && (mouseX >= left && mouseX < new_position && mouseY >= top && mouseY < top+height)) {
                graphics.renderComponentTooltip(minecraft.font, desc_tool_tip, mouseX, mouseY);
            }

            RenderElementValue(graphics, top, new_position, width-wd-4, height, mouseX, mouseY, hovering, partialTick);
        }

        public void Tick() {}
    }

    protected abstract class MultipleWidgetsSettingEntry
        extends AbstractSettingEntry
    {
        protected final AbstractWidget[] widgets;

        public MultipleWidgetsSettingEntry(FieldData data) {
            super(data);
            widgets = CreateWidgets();
        }

        protected abstract AbstractWidget[] CreateWidgets();

        public void mouseMoved(double mouseX, double mouseY) {
            for (AbstractWidget w : widgets) {
                w.mouseMoved(mouseX, mouseY);
            }
        }

        public boolean charTyped(char codePoint, int modifiers) {
            boolean value = false;
            for (AbstractWidget w : widgets) {
                value |= w.charTyped(codePoint, modifiers);
            }
            return value;
        }

        public boolean keyPressed(int keyCode, int scanCode, int modifiers)
        {
            for (AbstractWidget w : widgets) {
                if (w.keyPressed(keyCode, scanCode, modifiers)) { return true; }
            }
            return false;
        }

        public boolean keyReleased(int keyCode, int scanCode, int modifiers)
        {
            boolean value = false;
            for (AbstractWidget w : widgets) {
                value |= w.keyReleased(keyCode, scanCode, modifiers);
            }
            return value;
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            boolean value = false;
            for (AbstractWidget w : widgets) {
                value |= w.mouseClicked(mouseX, mouseY, button);
            }
            return value;
        }

        public boolean mouseReleased(double mouseX, double mouseY, int button)
        {
            boolean value = false;
            for (AbstractWidget w : widgets) {
                if (w.isMouseOver(mouseX, mouseY)) {
                    value |= w.mouseReleased(mouseX, mouseY, button);
                }
            }
            return value;
        }

        public boolean mouseScrolled(double mouseX, double mouseY, double sx , double sy)
        {
            boolean value = false;
            for (AbstractWidget w : widgets) {
                value |= w.mouseScrolled(mouseX, mouseY, sx, sy);
            }
            return value;
        }

        public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
        {
            boolean value = false;
            for (AbstractWidget w : widgets) {
                value |= w.mouseDragged(mouseX, mouseY, button, dragX, dragY);
            }
            return value;
        }

        public void RenderElementValue(GuiGraphics graphics, int top, int left, int width, int height, int mouse_x, int mouse_y, boolean hovering, float partialTick)
        {
            int advance = left;
            for (AbstractWidget w : widgets) {
                w.setPosition(advance, top);
                w.render(graphics, mouse_x, mouse_y, partialTick);
                advance += w.getWidth() + 4;
            }
        }
    }

    protected abstract class SimpleSettingEntry
        extends AbstractSettingEntry
    {
        protected AbstractWidget widget_to_use;

        public SimpleSettingEntry(FieldData data) {
            super(data);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return widget_to_use.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            return widget_to_use.keyReleased(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
            return widget_to_use.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return widget_to_use.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return widget_to_use.mouseReleased(mouseX,mouseY, button);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double sx, double sy) {
            return widget_to_use.mouseScrolled(mouseX, mouseY, sx, sy);
        }

        @Override
        public boolean charTyped(char codePoint, int modifiers) {
            return widget_to_use.charTyped(codePoint, modifiers);
        }

        @Override
        public void mouseMoved(double mouseX, double mouseY) {
            widget_to_use.mouseMoved(mouseX, mouseY);
        }

        @Override
        public void setFocused(boolean focused) {
            widget_to_use.setFocused(focused);
            super.setFocused(focused);
        }

        @Override
        public void RenderElementValue(GuiGraphics graphics, int top, int left, int width, int height, int mouse_x, int mouse_y, boolean hovering, float partialTick) {
            SetWidgetBounds(left, top, width);
            widget_to_use.render(graphics, mouse_x, mouse_y, partialTick);
        }

        protected void SetWidgetBounds(int left, int top, int new_width)
        {
            widget_to_use.setWidth(new_width);
            widget_to_use.setPosition(left, top);
        }
    }

    protected abstract class AbstractListSettingEntry<T>
        extends MultipleWidgetsSettingEntry
    {
        private int mod_index;
        private EditBox edit_box;
        private final List<T> entries_list;

        public AbstractListSettingEntry(FieldData data, List<T> initial_entries) {
            super(data);
            entries_list = new ArrayList<>(initial_entries);
            mod_index = 0;
            if (!entries_list.isEmpty()) {
                edit_box.setValue(TransformEntryToString(entries_list.get(mod_index = 0)));
            }
        }

        protected abstract String TransformEntryToString(@MaybeNull T entry);

        protected abstract T TransformStringToEntry(String str);

        private void OnButtonPressed(Button b)
        {
            if (b == widgets[0] && !entries_list.isEmpty()) {
                int last_index = mod_index;
                if (--mod_index < 0) { mod_index = 0; }
                entries_list.set(last_index, TransformStringToEntry(edit_box.getValue()));
                edit_box.setValue(TransformEntryToString(entries_list.get(mod_index)));
            } else if (b == widgets[2] && !entries_list.isEmpty()) {
                int last_index = mod_index;
                int size = entries_list.size();
                if (++mod_index >= size) { mod_index = size - 1; }
                entries_list.set(last_index, TransformStringToEntry(edit_box.getValue()));
                edit_box.setValue(TransformEntryToString(entries_list.get(mod_index)));
            } else if (b == widgets[3]) {
                int last_index = mod_index;
                entries_list.add(null);
                mod_index = entries_list.size() - 1;
                entries_list.set(last_index, TransformStringToEntry(edit_box.getValue()));
                edit_box.setValue("Modify this value!");
            }
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            edit_box.setFocused(focused);
        }

        @Override
        protected AbstractWidget[] CreateWidgets() {
            AbstractWidget[] w = new AbstractWidget[4];
            w[0] = Button.builder(Component.literal("<"), this::OnButtonPressed).bounds(0 , 0 , 14, ITEMS_HEIGHT-4).build();
            w[2] = Button.builder(Component.literal(">"), this::OnButtonPressed).bounds(0 , 0 , 14, ITEMS_HEIGHT-4).build();
            w[3] = Button.builder(Component.literal("Add..."), this::OnButtonPressed).bounds(0, 0 , 35, ITEMS_HEIGHT-4).build();
            edit_box = new EditBox(minecraft.font,0 , 0 , 200, ITEMS_HEIGHT-4, Component.literal(StringUtils.Empty));
            w[1] = edit_box;
            return w;
        }

        @Override
        public Object GetValue() {
            return entries_list;
        }

        @Override
        public void Tick() {}
    }

    private class StringListSettingEntry
        extends AbstractListSettingEntry<String>
    {
        public StringListSettingEntry(FieldData data, List<String> values) {
            super(data, values);
        }

        @Override
        protected String TransformEntryToString(String entry) {
            return (entry == null) ? StringUtils.Empty : entry;
        }

        @Override
        protected String TransformStringToEntry(String str) {
            return str;
        }
    }

    private class ResourceLocationListSettingEntry
        extends AbstractListSettingEntry<ResourceLocation>
    {
        private ResourceLocation invalid_location;

        private ResourceLocation GetInvalidLocation()
        {
            if (invalid_location == null) {
                invalid_location = ResourceLocation.tryBuild("mdcdi1315", "invalid");
            }
            return invalid_location;
        }

        public ResourceLocationListSettingEntry(FieldData data, List<ResourceLocation> initial_entries) {
            super(data, initial_entries);
        }

        @Override
        protected String TransformEntryToString(ResourceLocation entry) {
            return ((entry == null) ? GetInvalidLocation() : entry).toString();
        }

        @Override
        protected ResourceLocation TransformStringToEntry(String str) {
            ResourceLocation rl = ResourceLocation.tryParse(str);
            return (rl == null) ? GetInvalidLocation() : rl;
        }
    }

    private final class BooleanSettingEntry
        extends SimpleSettingEntry
    {
        private boolean current_value;

        public BooleanSettingEntry(FieldData data, boolean initial_value) {
            super(data);
            current_value = initial_value;
            widget_to_use = Button.builder(Component.literal(current_value ? "Enabled" : "Disabled"), this::OnButtonPressed).bounds(0, 0 , 10, ITEMS_HEIGHT-2).build();
        }

        private void OnButtonPressed(Button b) {
            if (b == widget_to_use) {
                current_value = !current_value;
                widget_to_use.setMessage(Component.literal(current_value ? "Enabled" : "Disabled"));
            }
        }

        public Object GetValue() {
            return current_value;
        }
    }

    private class StringSettingEntry
        extends SimpleSettingEntry
    {
        private final EditBox edit_box;

        public StringSettingEntry(FieldData data, String initial_value) {
            super(data);
            widget_to_use = edit_box = new EditBox(minecraft.font, 0 , 0 , 0 , ITEMS_HEIGHT-4, Component.literal("what?"));
            edit_box.setValue(initial_value);
        }

        public Object GetValue() {
            return edit_box.getValue();
        }
    }

    private final class ResourceLocationSettingEntry
        extends StringSettingEntry
    {
        public ResourceLocationSettingEntry(FieldData data, ResourceLocation initial_value) {
            super(data, initial_value.toString());
        }

        public Object GetValue() {
            Object value = super.GetValue();
            ResourceLocation location = ResourceLocation.tryParse(value.toString());
            if (location == null) {
                throw new FormatException(String.format("Cannot parse the resource location value. Offending value: %s", value));
            }
            return location;
        }
    }

    public static final class FieldData
    {
        private final String culturized, comment , actual_setting_name;

        public FieldData(String culturized, String actual_setting_name) {
            this(culturized, actual_setting_name, StringUtils.Empty);
        }

        public FieldData(String culturized, String actual_setting_name, String comment)
        {
            this.culturized = culturized;
            this.actual_setting_name = actual_setting_name;
            this.comment = comment;
        }

        public static FieldData ConstructFromFieldAndModID(String mod_id, SerializedField field)
        {
            String name = field.GetName();
            return new FieldData(
                    ConfigGuiUtils.ConstructRootConfigFieldTranslation(mod_id, name).getString(),
                    name,
                    ConfigGuiUtils.ConstructConfigTranslatableString(field.GetComment()).getString()
            );
        }

        public String GetCulturizedDescription() {
            return culturized;
        }

        public String GetComment() {
            return comment;
        }

        @MaybeNull
        public List<Component> ConstructTooltipLinesFromComment()
        {
            if (comment == null || comment.isEmpty()) {
                return null;
            } else {
                var strings = comment.split("\n");
                ArrayList<Component> components = new ArrayList<>(strings.length + 2);
                components.add(Component.literal(GetCulturizedDescription()));
                components.add(Component.empty());
                for (var s : strings) {
                    components.add(Component.literal(s));
                }
                return components;
            }
        }

        public String GetActualSettingName() {
            return actual_setting_name;
        }
    }

    /**
     * Provides the means for accessing data for the given bound field in the {@link SettingsModifierList} implementation.
     */
    public interface ISettingAccessor
    {
        FieldData GetFieldData();

        Object GetValue();
    }

    @Override
    public void setSelected(@MaybeNull SettingsModifierList.AbstractSettingEntry selected) {
        super.setSelected(selected);
        if (selected != null) {
            selected.setFocused(true);
        }
    }

    public void AddBooleanSetting(FieldData data, boolean initial_value) {
        addEntry(new BooleanSettingEntry(data, initial_value));
    }

    public void AddStringSetting(FieldData data, String initial_value) {
        addEntry(new StringSettingEntry(data, initial_value));
    }

    public void AddStringListSetting(FieldData data, List<String> strings) {
        addEntry(new StringListSettingEntry(data, strings));
    }

    public void AddResourceLocationSetting(FieldData data, ResourceLocation initial_value) {
        addEntry(new ResourceLocationSettingEntry(data, initial_value));
    }

    public void AddResourceLocationListSetting(FieldData data, List<ResourceLocation> initial_value) {
        addEntry(new ResourceLocationListSettingEntry(data, initial_value));
    }

    public void AddUnknownListSetting(FieldData data, List<?> list)
    {
        ArgumentNullException.ThrowIfNull(list, "list");
        if (list.isEmpty()) {
            addEntry(new StringListSettingEntry(data, List.of()));
        } else {
            Object o;
            int i = 0;
            do { o = list.get(i++); } while (i < list.size() && o == null);
            if (o == null) {
                addEntry(new StringListSettingEntry(data, List.of()));
            } else if (o.getClass() == ResourceLocation.class) {
                List<ResourceLocation> lt = new ArrayList<>(list.size());
                for (Object lo : list) {
                    lt.add((ResourceLocation) lo);
                }
                addEntry(new ResourceLocationListSettingEntry(data, lt));
            } else if (o.getClass() == String.class) {
                List<String> lt = new ArrayList<>(list.size());
                for (Object lo : list) {
                    lt.add((String) lo);
                }
                addEntry(new StringListSettingEntry(data, lt));
            }
        }
    }

    public void AddAsListFromArrayValue(FieldData data, ArrayValue av)
    {
        ArgumentNullException.ThrowIfNull(av, "av");
        if (av.IsEmpty()) {
            addEntry(new StringListSettingEntry(data, List.of()));
        } else {
            Object o = null;
            for (Object io : av) {
                if (io != null) { o = io; break; }
            }
            if (o == null) {
                addEntry(new StringListSettingEntry(data, List.of()));
            } else if (o.getClass() == ResourceLocation.class) {
                List<ResourceLocation> lt = new ArrayList<>(av.GetCount());
                for (Object lo : av) {
                    lt.add((ResourceLocation) lo);
                }
                addEntry(new ResourceLocationListSettingEntry(data, lt));
            } else if (o.getClass() == String.class) {
                List<String> lt = new ArrayList<>(av.GetCount());
                for (Object lo : av) {
                    lt.add((String) lo);
                }
                addEntry(new StringListSettingEntry(data, lt));
            }
        }
    }

    public void CreateFromRecord(String mod_id, Record record)
    {
        Object value;
        FieldData fd;
        for (SerializedField o : record)
        {
            value = o.GetValue();
            fd = FieldData.ConstructFromFieldAndModID(mod_id, o);
            if (value instanceof ResourceLocation rl) {
                AddResourceLocationSetting(fd, rl);
            } else if (value instanceof String s) {
                AddStringSetting(fd, s);
            } else if (value instanceof Boolean b) {
                AddBooleanSetting(fd, b);
            } else if (value instanceof ArrayValue av) {
                AddAsListFromArrayValue(fd, av);
            }
        }
    }

    public Record ReconstructRecord()
    {
        Record.Builder builder = new Record.Builder(children().size());
        for (AbstractSettingEntry ase : children()) {
            builder.Add(new SerializedField(ase.GetFieldData().GetActualSettingName() , ase.GetValue()));
        }
        return builder.Build();
    }
}

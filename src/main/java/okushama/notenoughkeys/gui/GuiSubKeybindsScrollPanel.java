package okushama.notenoughkeys.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import okushama.notenoughkeys.keys.KeybindTracker;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

/**
 * Original code from Minecraft Forge over at http://minecraftforge.net
 */
public class GuiSubKeybindsScrollPanel extends GuiSlot {
	protected static final ResourceLocation WIDGITS = new ResourceLocation("textures/gui/widgets.png");
	private GuiSubKeybindsMenu controls;
	private GameSettings options;
	private Minecraft mc;
	private String[] message;
	private int _mouseX, _mouseY, selected = -1;

	public KeyBinding[] keyBindings;

	public GuiSubKeybindsScrollPanel(GuiSubKeybindsMenu controls, GameSettings options, Minecraft mc, KeyBinding[] kbs) {
		super(mc, controls.width, controls.height, 16, (controls.height - 32) + 4, 25);
		this.controls = controls;
		this.options = options;
		this.mc = mc;
		keyBindings = kbs;
	}

	@Override
	protected int getSize() {
		return keyBindings.length;
	}

	@Override
	protected void elementClicked(int i, boolean flag, int mouseX, int mouseY) {
		if (!flag) {
			if (selected == -1) {
				selected = i;
			} else {
				KeyBinding glob = getGlobalKeybind(selected);
				options.setOptionKeyBinding(glob, -100);
				selected = -1;
				KeyBinding.resetKeyBindingArrayAndHash();
				KeybindTracker.updateConflictCategory();
			}
		}
	}

	@Override
	protected boolean isSelected(int i) {
		return false;
	}

	@Override
	protected void drawBackground() {}

	@Override
	public void drawScreen(int mX, int mY, float f) {
		_mouseX = mX;
		_mouseY = mY;

		if (selected != -1 && !Mouse.isButtonDown(0) && Mouse.getDWheel() == 0) {
			if (Mouse.next() && Mouse.getEventButtonState()) {
				KeyBinding glob = getGlobalKeybind(selected);
				options.setOptionKeyBinding(glob, -100 + Mouse.getEventButton());
				selected = -1;
				KeyBinding.resetKeyBindingArrayAndHash();
			}
		}
		super.drawScreen(mX, mY, f);
	}

	@Override
	protected void drawSlot(int index, int xPosition, int yPosition, int l, Tessellator tessellator, int mouseX, int mouseY) {
		String s = I18n.format(keyBindings[index].getKeyDescription());
		int width = 70;
		int height = 20;
		xPosition -= 20;
		boolean flag = _mouseX >= xPosition && _mouseY >= yPosition && _mouseX < xPosition + width && _mouseY < yPosition + height;
		int k = (flag ? 2 : 1);

		mc.renderEngine.bindTexture(WIDGITS);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		controls.drawTexturedModalRect(xPosition, yPosition, 0, 46 + k * 20, width / 2, height);
		controls.drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2, 46 + k * 20, width / 2, height);

		controls.drawString(mc.fontRendererObj, s, xPosition + width + 4, yPosition + 6, 0xFFFFFFFF);

		boolean conflict = false;
		KeyBinding globKb = getGlobalKeybind(index);
		for (KeyBinding x : options.keyBindings) {
			if (x != globKb && x.getKeyCode() == globKb.getKeyCode()) {
				conflict = true;
				break;
			}
		}

		String str = (conflict ? EnumChatFormatting.RED : "") + GameSettings.getKeyDisplayString(keyBindings[index].getKeyCode());
		str = (index == selected ? EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + "??? " + EnumChatFormatting.WHITE + "<" : str);
		controls.drawCenteredString(mc.fontRendererObj, str, xPosition + (width / 2), yPosition + (height - 8) / 2, 0xFFFFFFFF);
	}

    public KeyBinding getGlobalKeybind(int localIndex) {
        if(localIndex < 0)
            return null;
        return KeybindTracker.getKeybind(keyBindings[localIndex]);
    }

	public boolean keyTyped(char c, int i) {
		if (selected != -1) {
			KeyBinding glob = getGlobalKeybind(selected);
			options.setOptionKeyBinding(glob, i);
			selected = -1;
			KeyBinding.resetKeyBindingArrayAndHash();
			KeybindTracker.updateConflictCategory();
			return false;
		}
		return true;
	}
}
package okushama.notenoughkeys.keys;

import java.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

/**
 * Some epic code borrowed from ProfMobius' Waila:
 * http://profmobius.blogspot.fr/
 */

public class KeybindTracker {
	public static HashMap<String, ArrayList<KeyBinding>> modKeybinds = new HashMap<String, ArrayList<KeyBinding>>();
	public static HashMap<String, String> modIds = new HashMap<String, String>();

    public static KeyBinding getKeybind(KeyBinding kb) {
        for(KeyBinding keb : Minecraft.getMinecraft().gameSettings.keyBindings) {
            if(keb.equals(kb)) {
                return keb;
            }
        }
        return null;
    }

	private static ArrayList<KeyBinding> getConflictingKeybinds() {
		List<KeyBinding> allTheBinds = Arrays.asList(Minecraft.getMinecraft().gameSettings.keyBindings);
		ArrayList<KeyBinding> allTheConflicts = new ArrayList<KeyBinding>();
		for (KeyBinding bind : allTheBinds) {
			for (KeyBinding obind : allTheBinds) {
				if (!obind.getKeyDescription().equals(bind.getKeyDescription())) {
					if (obind.getKeyCode() == bind.getKeyCode()) {
						// out.put(getHostCategory(bind)+" and "+getHostCategory(obind), new KeyBinding[]{bind, obind});
						allTheConflicts.add(bind);
						allTheConflicts.add(obind);
						// conflict detected here
					}
				}
			}
		}
		HashSet<KeyBinding> hs = new HashSet<KeyBinding>();
		hs.addAll(allTheConflicts);
		allTheConflicts.clear();
		allTheConflicts.addAll(hs);
		return allTheConflicts;
	}

	public static void populate() {
		KeyBinding[] keyBinds = Minecraft.getMinecraft().gameSettings.keyBindings;
		/*Field keyHandlers_Field = getDeclaredField(KeyBindingRegistry.class.getName(), "keyHandlers"); //Commented out until I'm sure that we don't need this anymore
		HashMap<KeyBinding, String> tempKeys = new HashMap<KeyBinding, String>();
		try {
			Set<KeyHandler> keyHandlers = (Set<KeyHandler>) keyHandlers_Field.get(KeyBindingRegistry.instance());
			for (KeyHandler keyhandler : keyHandlers)
				for (int i = 0; i < keyhandler.getKeyBindings().length; i++)
					tempKeys.put(keyhandler.getKeyBindings()[i], idFromObject(keyhandler));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		for (KeyBinding kb : tempKeys.keySet()) {
			String s = tempKeys.get(kb);
			if (!modKeybinds.containsKey(s))
				modKeybinds.put(s, new ArrayList<KeyBinding>());
			modKeybinds.get(s).add(kb);
		}*/
        for(int i = 32; i < keyBinds.length; i++) { //Index 31 is the last vanilla keybinding.
            if(!modKeybinds.containsKey(keyBinds[i].getKeyCategory())) {
                modKeybinds.put(keyBinds[i].getKeyCategory(), new ArrayList<KeyBinding>());
            }
            modKeybinds.get(keyBinds[i].getKeyCategory()).add(keyBinds[i]);
        }
		KeybindTracker.updateConflictCategory();
	}

	public static void updateConflictCategory() {
		if (getConflictingKeybinds().size() > 0) {
			modKeybinds.put("Conflicting", getConflictingKeybinds());
		} else if (modKeybinds.containsKey("Conflicting"))
			modKeybinds.remove("Conflicting");
	}
}
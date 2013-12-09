package okushama.nek;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

/**
 * Some epic code borrowed from ProfMobius' Waila mod,
 * http://profmobius.blogspot.fr/
 * 
 */

public class KeybindTracker {

	public static HashMap<String, ArrayList<KeyBinding>> modKeybinds = new HashMap<String, ArrayList<KeyBinding>>();
	public static ArrayList<KeyBinding> vanillaKeybinds = new ArrayList<KeyBinding>();
	public static HashMap<String, String> modIds = new HashMap<String, String>();
	
	public static int getKeybindIndex(KeyBinding kb){
		for(int i = 0; i < Minecraft.getMinecraft().gameSettings.keyBindings.length; i++){
			KeyBinding keb = Minecraft.getMinecraft().gameSettings.keyBindings[i];
			if(keb.keyDescription.equals(kb.keyDescription)){
				return i;
			}
		}
		return -1;
	}

	public static void populate() {
		KeyBinding[] keyBinds = Minecraft.getMinecraft().gameSettings.keyBindings;
		Field keyHandlers_Field = getDeclaredField("cpw.mods.fml.client.registry.KeyBindingRegistry", "keyHandlers");
		HashMap<KeyBinding, String> tempKeys = new HashMap<KeyBinding, String>();
		try {
			Set<KeyHandler> keyHandlers = (Set<KeyHandler>) keyHandlers_Field
					.get(KeyBindingRegistry.instance());
			for (KeyHandler keyhandler : keyHandlers)
				for (int i = 0; i < keyhandler.getKeyBindings().length; i++)
					tempKeys.put(keyhandler.getKeyBindings()[i], idFromObject(keyhandler));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		for(KeyBinding kb : tempKeys.keySet()){
			String s = tempKeys.get(kb);
			if(!modKeybinds.containsKey(s)){
				modKeybinds.put(s, new ArrayList<KeyBinding>());
			}
			modKeybinds.get(s).add(kb);
		}
	}

	public static Field getDeclaredField(String classname, String fieldname) {

		try {
			Class class_ = Class.forName(classname);
			Field field_ = class_.getDeclaredField(fieldname);
			field_.setAccessible(true);
			return field_;
		} catch (NoSuchFieldException e) {
			NotEnoughKeys.log(String.format("== Field %s %s not found !\n",
					classname, fieldname));
			return null;
		} catch (SecurityException e) {
			NotEnoughKeys.log(String.format(
					"== Field %s %s security exception !\n", classname,
					fieldname));
			return null;
		} catch (ClassNotFoundException e) {
			NotEnoughKeys.log(String.format("== Class %s not found !\n",
					classname));
			return null;
		}
	}

	public static String idFromObject(Object obj) {
		String objPath = obj.getClass().getProtectionDomain().getCodeSource()
				.getLocation().toString();

		try {
			objPath = URLDecoder.decode(objPath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String modName = "<Unknown>";
		
		for (String s : modIds.keySet()){
			if (objPath.contains(s) && !s.contains("nek")) {
				modName = modIds.get(s);
				break;
			}
		}
		if(modName.equals("Not Enough Keys")){
			modName = "Unsorted";
		}

		if (modName.equals("Minecraft Coder Pack"))
			modName = "Minecraft";

		return modName;
	}
}

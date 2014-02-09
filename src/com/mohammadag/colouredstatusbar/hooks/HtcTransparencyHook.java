package com.mohammadag.colouredstatusbar.hooks;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class HtcTransparencyHook {
	public static void doHook(ClassLoader classLoader) {
		if (!android.os.Build.MANUFACTURER.toLowerCase().contains("htc"))
			return;

		findAndHookMethod("com.android.systemui.statusbar.phone.PhoneStatusBar", classLoader,
                "setStatusBarBackground", int.class, XC_MethodReplacement.DO_NOTHING);

        try {
            Class<?> phoneTransitions = findClass("com.android.systemui.statusbar.phone.PhoneStatusBarTransitions", classLoader);

            findAndHookMethod(phoneTransitions, "transitionTo", int.class, boolean.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        int lastMode = XposedHelpers.getIntField(param.thisObject, "lastMode");
                        int newValue = (Integer) param.args[0];

                        // Do not call setupBackground method when leaving the homescreen
                        if(newValue != lastMode && newValue != 0){
                            XposedHelpers.callMethod(param.thisObject, "setupBackground", newValue);
                        }

                        // Save new value to lastMode
                        XposedHelpers.setIntField(param.thisObject, "lastMode", newValue);
                    }
                    catch (Exception ex)
                    {
                        XposedBridge.log("Error while mapping the new method 'transitionTo'");
                    }
                    return null;
                }
            });
        }
        catch(Exception ex){
            XposedBridge.log("Can't hook PhoneStatusBarTransitions");
        }
	}
}

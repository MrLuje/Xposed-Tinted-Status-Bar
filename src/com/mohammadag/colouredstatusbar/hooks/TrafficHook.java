package com.mohammadag.colouredstatusbar.hooks;

import android.widget.TextView;

import com.mohammadag.colouredstatusbar.ColourChangerMod;
import com.mohammadag.colouredstatusbar.Common;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class TrafficHook {

    private final ColourChangerMod mInstance;
    private XC_MethodHook mTrafficHook = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            for (String name : Common.TRAFFIC_TEXT_NAMES) {
                try {
                    TextView view = (TextView) XposedHelpers.getObjectField(param.thisObject, name);
                    mInstance.addTextLabel(view);
                } catch (NoSuchFieldError e) {
                    XposedBridge.log("Couldn't find field " + name + "in class " + param.method.getClass().getName());
                }
            }
        }
    };

    public TrafficHook(ColourChangerMod instance, ClassLoader classLoader) {
        mInstance = instance;
        doHooks(classLoader);
    }

    private void doHooks(ClassLoader classLoader) {
        String methodName = "onAttachedToWindow";
        String className = "com.android.systemui.statusbar.traffic.Traffic";

        try {
            Class<?> traffic = findClass(className, classLoader);

            try {
                findAndHookMethod(traffic, methodName, mTrafficHook);
            } catch (NoSuchMethodError e) {
                XposedBridge.log("Not hooking class: " + className);
            }
        }
        catch(Exception ex) {
            // Really shouldn't happen, but we can't afford a crash here.
            XposedBridge.log("Not hooking method " + className + "." + methodName);
        }
    }
}

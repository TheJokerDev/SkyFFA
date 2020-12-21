package me.thejokerdev.skyffa.utils.variable;

import me.thejokerdev.skyffa.player.SkyPlayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RegisteredVariable {
    private final Method method;
    private final Variable variable;
    private final int replacer;

    public RegisteredVariable(Method var1, Variable var2, int var3) {
        this.method = var1;
        this.variable = var2;
        this.replacer = var3;
    }

    public String invoke(VariableReplacer var1, SkyPlayer var2) {
        try {
            return (String)this.method.invoke(var1, var2);
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public Variable getVariable() {
        return this.variable;
    }

    public int getReplacer() {
        return this.replacer;
    }
}

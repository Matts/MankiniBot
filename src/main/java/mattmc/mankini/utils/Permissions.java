package mattmc.mankini.utils;

import mattmc.mankini.MankiniBot;
import mattmc.mankini.module.ModuleRegular;

/**
 * Project MankiniBot
 * Created by MattMc on 7/14/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */
public class Permissions {
    public static Perms getPermission(String user, Perms permToCheckFor){
        user=user.toLowerCase();
        if(user.equalsIgnoreCase(MankiniBot.Owner)){
            return Perms.MOD;
        }
        try {
            if(permToCheckFor.equals(Perms.REG)) {
                if((boolean)ModuleRegular.class.getMethod("isRegular", String.class).invoke(ModuleRegular.class.newInstance(), user) || ModUtils.moderators.contains(user)){
                    return Perms.REG;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        if(permToCheckFor.equals(Perms.MOD)) {
            if(ModUtils.moderators.contains(user)){
                return Perms.MOD;
            }
        }
        return Perms.ALL;
    }
    public enum Perms {
        ALL, MOD, REG
    }
}

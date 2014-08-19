package mattmc.mankini.utils;

import mattmc.mankini.MankiniBot;
import mattmc.mankini.commands.CommandLinks;
import mattmc.mankini.commands.CommandRegular;
import mattmc.mankini.common.ModCommon;

/**
 * Project MankiniBot
 * Created by MattMc on 7/14/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */
public class Permissions {
    public static Perms getPermission(String user, Perms permToCheckFor){
        user=user.toLowerCase();
        if(user.equalsIgnoreCase(MankiniBot.Owner)){
            if(permToCheckFor==Perms.MOD){
                return Perms.MOD;
            }else if(permToCheckFor==Perms.REG)
                return Perms.REG;
            }else if(permToCheckFor==Perms.ALL){
                return Perms.ALL;
        }

        try {
            if(permToCheckFor.equals(Perms.REG)) {
                if(CommandRegular.class.newInstance().isRegular(user) || ModCommon.moderators.contains(user)){
                    return Perms.REG;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        if(permToCheckFor.equals(Perms.MOD)) {
            if(ModCommon.moderators.contains(user)){
                return Perms.MOD;
            }
        }
        return Perms.ALL;
    }
    public enum Perms {
        ALL, MOD, REG, PERMIT
    }
}

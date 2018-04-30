/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.plajer.villagedefense3.utils;

import pl.plajer.villagedefense3.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class UpdateChecker {

    private static String latestVersion;

    private static boolean checkHigher(String currentVersion, String newVersion) {
        String current = toReadable(currentVersion);
        String newVer = toReadable(newVersion);
        return current.compareTo(newVer) < 0;
    }

    public static void checkUpdate(String currentVersion) {
        String version = getVersion();
        if(version.contains("b")) {
            if(!Main.getPlugin(Main.class).getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions")) return;
        }
        if(checkHigher(currentVersion, version))
            latestVersion = version;
    }

    public static String getLatestVersion() {
        return latestVersion;
    }

    private static String getVersion() {
        String version = null;
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=" + 41869).openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.getOutputStream().write(("resource=" + 41869).getBytes("UTF-8"));
            version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        } catch(IOException ignored) {
        }
        return version;
    }

    private static String toReadable(String version) {
        String[] split = Pattern.compile(".", Pattern.LITERAL).split(version.replace("v", ""));
        version = "";
        for(String s : split)
            version += String.format("%4s", s);
        return version;
    }
}

package com.hctrom.romcontrol.hosts;

import java.io.IOException;

public class FetchHosts {

    public static String[] stringUrl = {
        "http://hosts-file.net/.%5Cad_servers.txt",
        "http://winhelp2002.mvps.org/hosts.txt",
        "http://pgl.yoyo.org/adservers/serverlist.php?hostformat=hosts&showintro=0&mimetype=plaintext",
        "http://adaway.org/hosts.txt"
    };

    public static int successfulSources;

    public static void fetch() throws IOException {

        String cmd = "mount -o rw,remount /system && echo \'foo\' > /etc/started.cfg && sed -i -e '$G' /etc/hosts.alt";

        successfulSources = 0;

        int i = 0;
        while (i <= 3) {
            if (HfmHelpers.isAvailable(stringUrl[i])) {
                cmd = cmd + " && wget \"" + stringUrl[i] + "\" -O /etc/hosts" + i; //Get file
                cmd = cmd + " && sed -i -e '$G' /etc/hosts" + i; //Put newline
                successfulSources++;
            }
            i++;
        }

        cmd = cmd + " && cat /etc/hosts[0-9] /etc/hosts.alt > /etc/hosts.tmp" //Merge old & new hosts
                  + " && sort -u /etc/hosts.tmp -o /etc/hosts.tmp" //Remove duplicate lines
                  + " && sed -i '/^[@#]/ d' /etc/hosts.tmp" //Remove commented lines
                  + " && sed -i '/^$/d' /etc/hosts.tmp" //Remove blank lines
                  + " && sed -i '1i#LiquidSmooth\' /etc/hosts.tmp" // Add LiquidSmooth tag
                  + " && cp -f /etc/hosts.tmp /etc/hosts.alt"
                  + " && rm -f /etc/hosts[0-9] /etc/hosts.tmp /etc/started.cfg" //Clean up
                  + " && mount -o ro,remount /system";

        HfmHelpers.RunAsRoot(cmd);
    }
}

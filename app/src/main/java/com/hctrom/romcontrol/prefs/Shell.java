package com.hctrom.romcontrol.prefs;

import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Ivan on 25/05/2016.
 */
public class Shell {
    //Gets string for shell command to activate reboot menu items, using stericson RootTools lib
    public static void getRebootAction(String command) {
        Command c = new Command(0, command);
        try {
            RootTools.getShell(true).add(c);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (RootDeniedException e) {
            e.printStackTrace();
        }
    }
}

package com.hctrom.romcontrol.sh;

/**
 * Created by Ivan on 26/05/2016.
 */
public class Scripts {
    String script;
    String path = "export PATH=/system/bin:$PATH\n" + "mount -o rw,remount /system\n";

    public String getBateriaBaja(String estado){
        if (estado == "ON") {
            script = "cp -p /system/hct/audio/LowBattery.ogg /system/media/audio/ui/LowBattery.ogg";
        }else{
            script = "cp -p /system/hct/audio/silent.ogg /system/media/audio/ui/LowBattery.ogg";
        }
        return "" + path + script;
    }

    public String getSonidoTeclaVolumen(String estado){
        if (estado == "ON") {
            script = "cp -p /system/hct/audio/TW_Volume_control.ogg /system/media/audio/ui/TW_Volume_control.ogg";
        }else{
            script = "cp -p /system/hct/audio/silent.ogg /system/media/audio/ui/TW_Volume_control.ogg";
        }
        return "" + path + script;
    }

    public String getSonidoCarga(String estado){
        if (estado == "ON") {
            script = "cp -p /system/hct/audio/Charger_Connection.ogg /system/media/audio/ui/Charger_Connection.ogg\n" +
                    "cp -p /system/hct/audio/WirelessChargingStarted.ogg /system/media/audio/ui/WirelessChargingStarted.ogg";
        }else{
            script = "cp -p /system/hct/audio/silent.ogg /system/media/audio/ui/WirelessChargingStarted.ogg\n" +
                    "cp -p /system/hct/audio/silent.ogg /system/media/audio/ui/Charger_Connection.ogg";
        }
        return "" + path + script;
    }

    public String getSonidoCapturaPantalla(String estado){
        if (estado == "ON") {
            script = "cp -p /system/hct/audio/camera_click.ogg /system/media/audio/ui/camera_click.ogg";
        }else{
            script = "cp -p /system/hct/audio/silent.ogg /system/media/audio/ui/camera_click.ogg";
        }

        return "" + path + script;
    }

    public String getNivelSonidoStock(){
        script = "cp -p /system/hct/sound/stock/mixer_paths.xml /system/etc/mixer_paths.xml";

        return "" + path + script;
    }

    public String getNivelSonidoMedio(){
        script = "cp -p /system/hct/sound/mild/mixer_paths.xml /system/etc/mixer_paths.xml";

        return "" + path + script;
    }

    public String getNivelSonidoAlto(){
        script = "cp -p /system/hct/sound/loud/mixer_paths.xml /system/etc/mixer_paths.xml";

        return "" + path + script;
    }

    public String getNivelSonidoExtremo(){
        script = "cp -p /system/hct/sound/extreme/mixer_paths.xml /system/etc/mixer_paths.xml";

        return "" + path + script;
    }

    public String getSonidoInicioStock(){
        script = "cp -p /system/hct/audio/stock/PowerOn.ogg /system/media/audio/ui/PowerOn.ogg\n" +
                "cp -p /system/hct/audio/stock/PowerOff.ogg /system/media/audio/ui/PowerOff.ogg";

        return "" + path + script;
    }

    public String getSonidoInicioHCT(){
        script = "cp -p /system/hct/audio/hct/PowerOn.ogg /system/media/audio/ui/PowerOn.ogg\n" +
                "cp -p /system/hct/audio/hct/PowerOff.ogg /system/media/audio/ui/PowerOff.ogg";

        return "" + path + script;
    }

    public String getSonidoInicioOff(){
        script = "cp -p /system/hct/audio/silent.ogg /system/media/audio/ui/PowerOn.ogg\n" +
                "cp -p /system/hct/audio/silent.ogg /system/media/audio/ui/PowerOff.ogg";

        return "" + path + script;
    }

    public String getEmojisStock(){
        script = "cp -p /system/hct/emoji/Emoji_Samsung/NotoColorEmoji.ttf /system/fonts/NotoColorEmoji.ttf\n" +
                "cp -p /system/hct/emoji/Emoji_Samsung/fallback_fonts.xml /system/etc/fallback_fonts.xml\n" +
                "cp -p /system/hct/emoji/Emoji_Samsung/fallback_fonts_legacy.xml /system/etc/fallback_fonts_legacy.xml\n" +
                "cp -p /system/hct/emoji/Emoji_Samsung/fonts.xml /system/etc/fonts.xml\n" +
                "cp -p /system/hct/emoji/Emoji_Samsung/system_fonts.xml /system/etc/system_fonts.xml";

        return "" + path + script;
    }

    public String getEmojisGoogle(){
        script = "cp -p /system/hct/emoji/Emoji_Google/NotoColorEmoji.ttf /system/fonts/NotoColorEmoji.ttf\n" +
                "cp -p /system/hct/emoji/Emoji_Google/fallback_fonts.xml /system/etc/fallback_fonts.xml\n" +
                "cp -p /system/hct/emoji/Emoji_Google/fallback_fonts_legacy.xml /system/etc/fallback_fonts_legacy.xml\n" +
                "cp -p /system/hct/emoji/Emoji_Google/fonts.xml /system/etc/fonts.xml\n" +
                "cp -p /system/hct/emoji/Emoji_Google/system_fonts.xml /system/etc/system_fonts.xml";

        return "" + path + script;
    }

    public String getEmojisiOS(){
        script = "cp -p /system/hct/emoji/Emoji_IOS/NotoColorEmoji.ttf /system/fonts/NotoColorEmoji.ttf\n" +
                "cp -p /system/hct/emoji/Emoji_IOS/fallback_fonts.xml /system/etc/fallback_fonts.xml\n" +
                "cp -p /system/hct/emoji/Emoji_IOS/fallback_fonts_legacy.xml /system/etc/fallback_fonts_legacy.xml\n" +
                "cp -p /system/hct/emoji/Emoji_IOS/fonts.xml /system/etc/fonts.xml\n" +
                "cp -p /system/hct/emoji/Emoji_IOS/system_fonts.xml /system/etc/system_fonts.xml";

        return "" + path + script;
    }
}

package it.ltm.scp.module.android.devices.printer;


import it.ltm.scp.module.android.utils.Properties;

/**
 * Created by HW64 on 07/10/2016.
 */
public class DefaultPrinterConfig {

    //prop keys
    private static final String ALIGN = "align_default";
    private static final String BOLD = "bold_default";
    private static final String NEGATIVE = "negative_default";
    private static final String SIZE = "size_default";
    private static final String FONT = "font_default";
    private static final String FLIP = "flip_default";
    private static final String ROTATE = "rotate_default";
    private static final String LINE = "line_default";
    private static final String SPACING = "spacing_default";
    private static final String UNDERLINE = "underline_default";

    public static String getAlign(){
        return Properties.get(ALIGN);
    }

    public static boolean getBold(){
        return Boolean.parseBoolean(Properties.get(BOLD));
    }

    public static boolean getNegative(){
        return Boolean.parseBoolean(Properties.get(NEGATIVE));
    }

    public static Integer getSize(){
        return Integer.parseInt(Properties.get(SIZE));
    }

    public static String getFont(){
        return Properties.get(FONT);
    }

    public static boolean getFlip(){
        return Boolean.parseBoolean(Properties.get(FLIP));
    }

    public static boolean getRotate(){
        return Boolean.parseBoolean(Properties.get(ROTATE));
    }

    public static Integer getLine(){
        return Integer.parseInt(Properties.get(LINE));
    }

    public static Integer getSpacing(){
        return Integer.parseInt(Properties.get(SPACING));
    }

    public static Integer getUnderline(){
        return Integer.parseInt(Properties.get(UNDERLINE));
    }


}

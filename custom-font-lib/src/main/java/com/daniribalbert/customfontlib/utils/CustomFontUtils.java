package com.daniribalbert.customfontlib.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.daniribalbert.customfontlib.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to handle font changes within a TextView.
 */
public class CustomFontUtils {
    private static final String FONTS_DIR = "fonts";

    private static final String FONT_BOLD_NAME = "bold";
    private static final String FONT_ITALIC_NAME = "italic";

    private static String fonts[] = {};

    private static final Map<String, Typeface> sTypefaceMap = new HashMap<>();

    public static Typeface getTypeFace(final TextView v,
                                       final AttributeSet attrs) {
        final TypedArray tArray = v.getContext().getTheme()
                .obtainStyledAttributes(attrs, R.styleable.customFont, 0, 0);

        String fontName = "";
        try {
            fontName = tArray.getString(R.styleable.customFont_font);
        } finally {
            tArray.recycle();
        }

        if (!TextUtils.isEmpty(fontName)) {
            final int style;
            final Typeface typeface = v.getTypeface();
            if (typeface == null) {
                style = Typeface.NORMAL;
            } else {
                style = typeface.getStyle();
            }
            String mFontName = getFontFile(v.getContext(), fontName, style);
            if (sTypefaceMap.containsKey(mFontName)) {
                return sTypefaceMap.get(mFontName);
            }

            Typeface newTypeface = typeface;
            if (!TextUtils.isEmpty(mFontName)) {
                try {
                    newTypeface = Typeface.createFromAsset(v.getContext()
                            .getAssets(), FONTS_DIR + "/" + mFontName);
                } catch (Exception e) {
                    newTypeface = typeface;
                }
            }
            sTypefaceMap.put(mFontName, newTypeface);
            return newTypeface;
        }
        return null;
    }

    private static String getFontFile(final Context context,
                                      final String fontName, final int style) {
        if (TextUtils.isEmpty(fontName)) {
            return "";
        }
        String fonts[] = getFontsFile(context);

        for (String currFile : fonts) {
            String currName = currFile.toLowerCase();
            if (currName.contains(fontName.toLowerCase())) {
                if (style == Typeface.BOLD_ITALIC
                        && currName.contains(FONT_BOLD_NAME)
                        && currName.contains(FONT_ITALIC_NAME)) {
                    return currFile;
                } else if (style == Typeface.BOLD
                        && currName.contains(FONT_BOLD_NAME)) {
                    return currFile;
                } else if (style == Typeface.ITALIC
                        && currName.contains(FONT_ITALIC_NAME)) {
                    return currFile;
                }
            }
        }

        // if there's no match for any style return the "regular" font
        for (String currFile : fonts) {
            int lastIndex = currFile.lastIndexOf(".");
            if (lastIndex > 0) {
                String fileName = currFile.substring(0, lastIndex);
                if (fileName.equalsIgnoreCase(fontName)) {
                    return currFile;
                }
            }
        }
        return "";
    }

    private static String[] getFontsFile(Context context) {
        if (fonts == null || fonts.length == 0) {
            try {
                fonts = context.getAssets().list(FONTS_DIR);

                if (fonts == null || fonts.length == 0) {
                }
            } catch (Exception e) {
            }
        }
        return fonts;
    }
}


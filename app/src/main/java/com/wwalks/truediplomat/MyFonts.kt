package com.wwalks.truediplomat

import android.content.Context
import android.graphics.Typeface


/**
 * Created by DELL on 3/8/2018.
 */

object MyFonts {


    val GORDITABLACK = "Gordita-Black.otf"
    val GORDITABLACKITALIC = "Gordita-BlackItalic.otf"
    val GORDITABOLD = "Gordita-Bold.otf"
    val GORDITABOLDITALIC = "Gordita-BoldItalic.otf"
    val GORDITALIGHT = "Gordita-Light.otf"
    val GORDITALIGHTITALIC = "Gordita-LightItalic.otf"
    val GORDITAMEDIUM = "Gordita-Medium.otf"
    val GORDITAMEDIUMITALIC = "Gordita-MediumItalic.otf"
    val GORDITAREGULAR = "Gordita-Regular.otf"
    val GORDITAREGULARITALIC = "Gordita-RegularItalic.otf"
    val GRANDHOTEL = "GrandHotel-Regular.otf"


    fun getFont(context: Context, font: String): Typeface? {
        if (font == GORDITABLACK) {
            return Typeface.createFromAsset(context.assets, GORDITABLACK)
        } else if (font == GORDITABLACKITALIC) {
            return Typeface.createFromAsset(context.assets, GORDITABLACKITALIC)
        } else if (font == GORDITABOLD) {
            return Typeface.createFromAsset(context.assets, GORDITABOLD)
        } else if (font == GORDITABOLDITALIC) {
            return Typeface.createFromAsset(context.assets, GORDITABOLDITALIC)
        } else if (font == GORDITALIGHT) {
            return Typeface.createFromAsset(context.assets, GORDITALIGHT)
        } else if (font == GORDITALIGHTITALIC) {
            return Typeface.createFromAsset(context.assets, GORDITALIGHTITALIC)
        } else if (font == GORDITAMEDIUM) {
            return Typeface.createFromAsset(context.assets, GORDITAMEDIUM)
        } else if (font == GORDITAMEDIUMITALIC) {
            return Typeface.createFromAsset(context.assets, GORDITAMEDIUMITALIC)
        } else if (font == GORDITAREGULAR) {
            return Typeface.createFromAsset(context.assets, GORDITAREGULAR)
        } else if (font == GRANDHOTEL) {
            return Typeface.createFromAsset(context.assets, GRANDHOTEL)
        }
        return null
    }


}

package nl.capaxambi.shared.extensions

import android.content.Context
import android.util.TypedValue

fun Float.dp(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this, context.applicationContext.resources.displayMetrics
    ).toInt()
}

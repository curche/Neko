package eu.kanade.tachiyomi.util.view

import android.content.Context
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.iconics.utils.colorInt
import com.mikepenz.iconics.utils.sizeDp
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.util.system.getResourceColor

class DrawableHelper {
    companion object {
        fun standardIcon(context: Context, icon: IIcon, size: Int = 24, attributeColor: Int = R.attr.colorPrimary): IconicsDrawable {
            return IconicsDrawable(context, icon).apply {
                sizeDp = size
                colorInt = context.getResourceColor(attributeColor)
            }
        }
    }
}

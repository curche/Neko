package eu.kanade.tachiyomi.ui.library

import android.view.View
import android.view.ViewGroup
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.util.system.dpToPx
import eu.kanade.tachiyomi.util.view.gone
import eu.kanade.tachiyomi.util.view.updateLayoutParams
import eu.kanade.tachiyomi.util.view.visible
import kotlinx.android.synthetic.main.manga_list_item.*
import kotlinx.android.synthetic.main.unread_download_badge.*

/**
 * Class used to hold the displayed data of a manga in the library, like the cover or the title.
 * All the elements from the layout file "item_library_list" are available in this class.
 *
 * @param view the inflated view for this holder.
 * @param adapter the adapter handling this holder.
 * @param listener a listener to react to single tap and long tap events.
 * @constructor creates a new library holder.
 */

class LibraryListHolder(
    private val view: View,
    adapter: LibraryCategoryAdapter,
    padEnd: Boolean
) : LibraryHolder(view, adapter) {

    init {
        badge_view?.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            marginEnd = (if (padEnd) 22 else 12).dpToPx
        }
    }

    /**
     * Method called from [LibraryCategoryAdapter.onBindViewHolder]. It updates the data for this
     * holder with the given manga.
     *
     * @param item the manga item to bind.
     */
    override fun onSetValues(item: LibraryItem) {
        title.visible()
        constraint_layout.minHeight = 56.dpToPx
        constraint_layout.minHeight = 0
        if (item.manga.status == -1) {
            title.gone()
        } else
            title.text = itemView.context.getString(R.string.category_is_empty)
        title.textAlignment = View.TEXT_ALIGNMENT_CENTER
        return
    }
}

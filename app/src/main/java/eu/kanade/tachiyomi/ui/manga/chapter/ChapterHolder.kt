package eu.kanade.tachiyomi.ui.manga.chapter

import android.text.format.DateUtils
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.data.download.model.Download
import eu.kanade.tachiyomi.ui.manga.MangaDetailsAdapter
import eu.kanade.tachiyomi.util.system.IconicsDrawable
import eu.kanade.tachiyomi.util.view.gone
import kotlinx.android.synthetic.main.chapters_item.*
import kotlinx.android.synthetic.main.download_button.*
import java.util.Date

class ChapterHolder(
    private val view: View,
    private val adapter: MangaDetailsAdapter
) : BaseChapterHolder(view, adapter) {

    init {
        download_button.setOnLongClickListener {
            adapter.delegate.startDownloadRange(adapterPosition)
            true
        }
    }

    fun bind(item: ChapterItem, manga: Manga) {
        val chapter = item.chapter
        val isLocked = item.isLocked
        chapter_title.text = when (manga.displayMode) {
            Manga.DISPLAY_NUMBER -> {
                val number = adapter.decimalFormat.format(chapter.chapter_number.toDouble())
                itemView.context.getString(R.string.chapter_, number)
            }
            else -> chapter.name
        }

        download_button.visibility = View.VISIBLE

        if (isLocked) download_button.gone()
        if (chapter.read) {
            download_button.alpha = .3f
            bookmark_star.alpha = .5f
        } else {
            download_button.alpha = 1f
            bookmark_star.alpha = 1f
        }

        // Set correct text color
        chapter_title.setTextColor(
            if (chapter.read && !isLocked) adapter.readColor else adapter.unreadColor
        )
        val fontInt = when {
            chapter.read && !isLocked -> R.font.metropolis_regular
            else -> R.font.metropolis_medium

        }
        chapter_title.typeface = ResourcesCompat.getFont(view.context, fontInt)

        if (chapter.bookmark && !isLocked) {
            bookmark_star.visibility = View.VISIBLE
        } else {
            bookmark_star.visibility = View.GONE
        }

        val statuses = mutableListOf<String>()

        if (chapter.date_upload > 0) {
            statuses.add(
                DateUtils.getRelativeTimeSpanString(
                    chapter.date_upload, Date().time, DateUtils.HOUR_IN_MILLIS
                ).toString()
            )
        }

        if (!chapter.read && chapter.last_page_read > 0 && chapter.pages_left > 0 && !isLocked) {
            statuses.add(
                itemView.resources.getQuantityString(
                    R.plurals.pages_left, chapter.pages_left, chapter.pages_left
                )
            )
        } else if (!chapter.read && chapter.last_page_read > 0 && !isLocked) {
            statuses.add(
                itemView.context.getString(
                    R.string.page_, chapter.last_page_read + 1
                )
            )
        }

        if (!chapter.scanlator.isNullOrBlank()) {
            statuses.add(chapter.scanlator!!)
        }

        if (front_view.translationX == 0f) {
            read.setImageDrawable(
                read.context.IconicsDrawable(
                    when {
                        item.read -> CommunityMaterial.Icon.cmd_eye_check
                        else -> CommunityMaterial.Icon.cmd_eye
                    }
                )
            )

            bookmark.setImageDrawable(
                read.context.IconicsDrawable(
                    when {
                        item.bookmark -> CommunityMaterial.Icon.cmd_bookmark_off
                        else -> CommunityMaterial.Icon.cmd_bookmark
                    }
                )
            )
        }
        //always have it tinted
        chapter_scanlator.setTextColor(
            when {
                chapter.read -> adapter.readColor
                else -> adapter.unreadColor
            }
        )
        chapter_scanlator.typeface = ResourcesCompat.getFont(view.context, fontInt)

        chapter_scanlator.text = statuses.joinToString(" • ")
        notifyStatus(
            if (adapter.isSelected(adapterPosition)) Download.CHECKED else item.status,
            item.isLocked,
            item.progress
        )
        resetFrontView()
    }

    override fun getFrontView(): View {
        return front_view
    }

    override fun getRearRightView(): View {
        return right_view
    }

    override fun getRearLeftView(): View {
        return left_view
    }

    private fun resetFrontView() {
        if (front_view.translationX != 0f) itemView.post { adapter.notifyItemChanged(adapterPosition) }
    }

    fun notifyStatus(status: Int, locked: Boolean, progress: Int) = with(download_button) {
        if (locked) {
            gone()
            return
        }
        download_button.visibility = View.VISIBLE
        setDownloadStatus(status, progress)
    }
}

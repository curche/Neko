package eu.kanade.tachiyomi.ui.manga.merge

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import coil.clear
import coil.load
import coil.transform.RoundedCornersTransformation
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.databinding.MergeSearchItemBinding
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.util.view.inflate
import java.util.ArrayList

class MergeSearchAdapter(context: Context) :
    ArrayAdapter<SManga>(context, R.layout.merge_search_item, ArrayList()) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var v = view
        // Get the data item for this position
        val manga = getItem(position)!!
        // Check if an existing view is being reused, otherwise inflate the view
        val holder: MergeSearchHolder // view lookup cache stored in tag
        if (v == null) {
            v = parent.inflate(R.layout.merge_search_item)
            holder = MergeSearchHolder(v)
            v.tag = holder
        } else {
            holder = v.tag as MergeSearchHolder
        }
        holder.onSetValues(manga)
        return v
    }

    fun setItems(syncs: List<SManga>) {
        setNotifyOnChange(false)
        clear()
        addAll(syncs)
        notifyDataSetChanged()
    }

    class MergeSearchHolder(private val view: View) {
        fun onSetValues(manga: SManga) {
            val binding = MergeSearchItemBinding.bind(view)
            binding.mergeSearchTitle.text = manga.title
            binding.mergeSearchCover.clear()
            if (!manga.thumbnail_url.isNullOrEmpty()) {
                binding.mergeSearchCover.load(manga.thumbnail_url) {
                    transformations(RoundedCornersTransformation(2f))
                }
            }
        }
    }
}

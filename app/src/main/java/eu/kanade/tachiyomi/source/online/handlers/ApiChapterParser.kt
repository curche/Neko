package eu.kanade.tachiyomi.source.online.handlers

import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.online.handlers.serializers.ChapterResponse
import eu.kanade.tachiyomi.source.online.utils.MdUtil
import kotlinx.serialization.decodeFromString
import okhttp3.Response
import java.util.Date

class ApiChapterParser {
    fun pageListParse(response: Response, host: String, dataSaver: Boolean): List<Page> {
        val jsonData = response.body!!.string()
        val networkApiChapter = MdUtil.jsonParser.decodeFromString<ChapterResponse>(jsonData)

        val pages = mutableListOf<Page>()

        val atHomeRequestUrl = response.request.url.toUrl().toString()

        val hash = networkApiChapter.data.attributes.hash
        val pageArray = if (dataSaver) {
            networkApiChapter.data.attributes.dataSaver.map { "/data-saver/$hash/$it" }
        } else {
            networkApiChapter.data.attributes.data.map { "/data/$hash/$it" }
        }
        val now = Date().time
        pageArray.forEach { imgUrl ->
            val mdAtHomeUrl = "$host,$atHomeRequestUrl,$now"
            pages.add(Page(pages.size, mdAtHomeUrl, imgUrl))
        }

        return pages
    }

    fun externalParse(response: Response): String {
        val jsonData = response.body!!.string()
        val networkApiChapter = MdUtil.jsonParser.decodeFromString(ChapterResponse.serializer(), jsonData)
        return networkApiChapter.data.attributes.data.first().substringAfterLast("/")
    }
}

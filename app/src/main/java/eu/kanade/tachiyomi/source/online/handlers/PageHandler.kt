package eu.kanade.tachiyomi.source.online.handlers

import com.elvishew.xlog.XLog
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.getOrThrow
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.network.NetworkHelper
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.online.models.dto.AtHomeDto
import eu.kanade.tachiyomi.source.online.models.dto.ChapterDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uy.kohesive.injekt.injectLazy
import java.util.Date

class PageHandler {

    val network: NetworkHelper by injectLazy()
    val preferences: PreferencesHelper by injectLazy()
    val mangaPlusHandler: MangaPlusHandler by injectLazy()
    val imageHandler: ImageHandler by injectLazy()

    suspend fun fetchPageList(chapter: SChapter, isLogged: Boolean): List<Page> {
        return withContext(Dispatchers.IO) {
            XLog.d("fetching page list")
            try {
                val chapterResponse = network.service.viewChapter(chapter.mangadex_chapter_id)
                if (chapterResponse.isSuccessful.not()) {
                    XLog.e(
                        "error returned from chapterResponse ${
                            chapterResponse.errorBody()?.string()
                        }"
                    )
                    throw Exception("error returned from chapterResponse")
                }

                if (chapter.scanlator.equals("mangaplus", true)) {
                    val mpChpId = chapterResponse.body()!!.data.attributes.data.first()
                        .substringAfterLast("/")
                    mangaPlusHandler.fetchPageList(mpChpId)
                } else {
                    val service = if (isLogged) {
                        network.authService
                    } else {
                        network.service
                    }

                    val atHomeResponse =
                        service.getAtHomeServer(
                            chapter.mangadex_chapter_id,
                            preferences.usePort443Only()
                        )

                    when (atHomeResponse) {
                        is ApiResponse.Success -> {
                            XLog.d("successfully got at home host")
                        }
                        is ApiResponse.Failure.Error -> {
                            XLog.e("error returned from atHomeResponse ${
                                atHomeResponse.response.errorBody()?.string()
                            }")
                            throw Exception("Error getting image ${atHomeResponse.response.code()}: ${atHomeResponse.response.errorBody()}")
                        }
                        is ApiResponse.Failure.Exception<*> -> {
                            XLog.e("error returned from atHomeResponse ${atHomeResponse.message}")
                            throw Exception("Error getting image ${atHomeResponse.message}")
                        }
                    }

                    val atHomeDto = atHomeResponse.getOrThrow()


                    pageListParse(
                        chapterResponse.body()!!,
                        atHomeDto,
                        preferences.dataSaver()
                    )
                }
            } catch (e: Exception) {
                XLog.e("error processing page list ", e)
                throw (e)
            }
        }
    }

    fun pageListParse(
        chapterDto: ChapterDto,
        atHomeDto: AtHomeDto,
        dataSaver: Boolean,
    ): List<Page> {
        val hash = chapterDto.data.attributes.hash
        val pageArray = if (dataSaver) {
            chapterDto.data.attributes.dataSaver.map { "/data-saver/$hash/$it" }
        } else {
            chapterDto.data.attributes.data.map { "/data/$hash/$it" }
        }
        val now = Date().time

        val pages = pageArray.mapIndexed { pos, imgUrl ->
            Page(pos + 1, atHomeDto.baseUrl, imgUrl, chapterDto.data.id)
        }

        imageHandler.updateTokenTracker(chapterDto.data.id, now)

        return pages
    }
}

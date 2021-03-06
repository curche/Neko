package eu.kanade.tachiyomi

import android.app.Application
import com.google.gson.Gson
import eu.kanade.tachiyomi.data.cache.ChapterCache
import eu.kanade.tachiyomi.data.cache.CoverCache
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.data.library.CustomMangaManager
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.data.track.TrackManager
import eu.kanade.tachiyomi.network.NetworkHelper
import eu.kanade.tachiyomi.source.SourceManager
import eu.kanade.tachiyomi.source.online.MangaDexLoginHelper
import eu.kanade.tachiyomi.source.online.handlers.ApiMangaParser
import eu.kanade.tachiyomi.source.online.handlers.FilterHandler
import eu.kanade.tachiyomi.source.online.handlers.FollowsHandler
import eu.kanade.tachiyomi.source.online.handlers.MangaHandler
import eu.kanade.tachiyomi.source.online.handlers.MangaPlusHandler
import eu.kanade.tachiyomi.source.online.handlers.PageHandler
import eu.kanade.tachiyomi.source.online.handlers.PopularHandler
import eu.kanade.tachiyomi.source.online.handlers.SearchHandler
import eu.kanade.tachiyomi.source.online.handlers.SimilarHandler
import eu.kanade.tachiyomi.util.chapter.ChapterFilter
import eu.kanade.tachiyomi.v5.db.V5DbHelper
import eu.kanade.tachiyomi.util.manga.MangaShortcutManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingleton
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class AppModule(val app: Application) : InjektModule {

    override fun InjektRegistrar.registerInjectables() {
        addSingleton(app)

        addSingletonFactory { PreferencesHelper(app) }

        addSingletonFactory { DatabaseHelper(app) }

        addSingletonFactory { ChapterCache(app) }

        addSingletonFactory { CoverCache(app) }

        addSingletonFactory { NetworkHelper(app) }

        addSingletonFactory { SourceManager() }

        addSingletonFactory { DownloadManager(app) }

        addSingletonFactory { CustomMangaManager(app) }

        addSingletonFactory { TrackManager(app) }

        addSingletonFactory { Gson() }

        addSingletonFactory { ChapterFilter() }

        addSingletonFactory { Json { ignoreUnknownKeys = true } }

        addSingletonFactory { V5DbHelper(app.applicationContext) }

        addSingleton(FilterHandler())

        addSingleton(FollowsHandler())

        addSingleton(MangaHandler())

        addSingleton(ApiMangaParser())

        addSingleton(PopularHandler())

        addSingleton(SearchHandler())

        addSingleton(PageHandler())

        addSingleton(SimilarHandler())

        addSingleton(MangaDexLoginHelper())

        addSingleton(MangaPlusHandler())

        addSingletonFactory { Json { ignoreUnknownKeys = true } }

        addSingletonFactory { ChapterFilter() }

        addSingletonFactory { MangaShortcutManager() }

        // Asynchronously init expensive components for a faster cold start

        GlobalScope.launch { get<PreferencesHelper>() }

        GlobalScope.launch { get<NetworkHelper>() }

        GlobalScope.launch { get<SourceManager>() }

        GlobalScope.launch { get<DatabaseHelper>() }

        GlobalScope.launch { get<DownloadManager>() }

        GlobalScope.launch { get<CustomMangaManager>() }
    }
}

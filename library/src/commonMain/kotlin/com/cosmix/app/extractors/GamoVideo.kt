package com.cosmix.app.extractors

import com.cosmix.app.SubtitleFile
import com.cosmix.app.app
import com.cosmix.app.extractors.helper.JwPlayerHelper
import com.cosmix.app.utils.ExtractorApi
import com.cosmix.app.utils.ExtractorLink


open class GamoVideo : ExtractorApi() {
    override val name = "GamoVideo"
    override val mainUrl = "https://gamovideo.com"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        app.get(url, referer = referer).document.select("script")
            .firstOrNull { JwPlayerHelper.canParseJwScript(it.data()) }!!.let {
                JwPlayerHelper.extractStreamLinks(it.data(), name, mainUrl, callback, subtitleCallback)
            }
    }
}

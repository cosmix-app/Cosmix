package com.cosmix.app.extractors

import com.cosmix.app.SubtitleFile
import com.cosmix.app.app
import com.cosmix.app.extractors.helper.JwPlayerHelper
import com.cosmix.app.utils.ExtractorApi
import com.cosmix.app.utils.ExtractorLink
import com.cosmix.app.utils.getAndUnpack
import com.cosmix.app.utils.getPacked

open class StreamoUpload : ExtractorApi() {
    override val name = "StreamoUpload"
    override val mainUrl = "https://streamoupload.xyz"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val response = app.get(url, referer = referer)
        response.document.select("script").map { script ->
            if (getPacked(script.data()) != null) {
                val data = getAndUnpack(script.data())
                JwPlayerHelper.extractStreamLinks(data, name, mainUrl, callback, subtitleCallback)
            }
        }
    }
}

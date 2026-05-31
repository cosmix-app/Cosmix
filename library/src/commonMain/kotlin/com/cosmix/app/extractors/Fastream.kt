package com.cosmix.app.extractors

import com.cosmix.app.SubtitleFile
import com.cosmix.app.amap
import com.cosmix.app.app
import com.cosmix.app.extractors.helper.JwPlayerHelper
import com.cosmix.app.utils.ExtractorApi
import com.cosmix.app.utils.ExtractorLink
import com.cosmix.app.utils.getAndUnpack
import com.cosmix.app.utils.getPacked

open class Fastream : ExtractorApi() {
    override var mainUrl = "https://fastream.to"
    override var name = "Fastream"
    override val requiresReferer = false

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val idregex = Regex("emb.html\\?(.*)=")
        val response = if (url.contains(Regex("(emb.html.*fastream)"))) {
            val id = idregex.find(url)?.destructured?.component1() ?: ""
            app.post(
                "$mainUrl/dl", allowRedirects = false,
                data = mapOf(
                    "op" to "embed",
                    "file_code" to id,
                    "auto" to "1"
                )
            ).document
        } else {
            app.get(url, referer = url).document
        }
        response.select("script").amap { script ->
            if (getPacked(script.data()) != null) {
                val unPacked = getAndUnpack(script.data())
                JwPlayerHelper.extractStreamLinks(unPacked, name, mainUrl, callback, subtitleCallback)
            }
        }
    }
}
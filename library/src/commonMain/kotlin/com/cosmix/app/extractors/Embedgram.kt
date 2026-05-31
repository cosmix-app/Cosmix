package com.cosmix.app.extractors

import com.cosmix.app.SubtitleFile
import com.cosmix.app.app
import com.cosmix.app.utils.ExtractorApi
import com.cosmix.app.utils.ExtractorLink
import com.cosmix.app.utils.getQualityFromName
import com.cosmix.app.utils.httpsify
import com.cosmix.app.utils.newExtractorLink

open class Embedgram : ExtractorApi() {
    override val name = "Embedgram"
    override val mainUrl = "https://embedgram.com"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val document = app.get(url, referer = referer).document
        val link = document.select("video source:last-child").attr("src")
        val quality = document.select("video source:last-child").attr("title")
        callback.invoke(
            newExtractorLink(
                this.name,
                this.name,
                httpsify(link),
            ) {
                this.referer = "$mainUrl/"
                this.quality = getQualityFromName(quality)
                this.headers = mapOf(
                    "Range" to "bytes=0-"
                )
            }
        )
    }
}
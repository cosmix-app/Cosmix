package com.cosmix.app.extractors

import com.cosmix.app.SubtitleFile
import com.cosmix.app.app
import com.cosmix.app.utils.ExtractorApi
import com.cosmix.app.utils.ExtractorLink
import com.cosmix.app.utils.INFER_TYPE
import com.cosmix.app.utils.Qualities
import com.cosmix.app.utils.newExtractorLink

open class Wibufile : ExtractorApi() {
    override val name: String = "Wibufile"
    override val mainUrl: String = "https://wibufile.com"
    override val requiresReferer = false

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val res = app.get(url).text
        val video = Regex("src: ['\"](.*?)['\"]").find(res)?.groupValues?.get(1)

        callback.invoke(
            newExtractorLink(
                name,
                name,
                video ?: return,
            ) {
                this.referer = "$mainUrl/"
                this.quality = Qualities.Unknown.value
            }
        )
    }
}
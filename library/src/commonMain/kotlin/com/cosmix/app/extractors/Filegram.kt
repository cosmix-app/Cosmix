package com.cosmix.app.extractors

import com.cosmix.app.SubtitleFile
import com.cosmix.app.USER_AGENT
import com.cosmix.app.app
import com.cosmix.app.extractors.helper.JwPlayerHelper
import com.cosmix.app.utils.ExtractorApi
import com.cosmix.app.utils.ExtractorLink
import com.cosmix.app.utils.getAndUnpack
import org.jsoup.nodes.Element

open class Filegram : ExtractorApi() {
    override val name = "Filegram"
    override val mainUrl = "https://filegram.to"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val header = mapOf(
            "Accept" to "*/*",
            "Accept-language" to "en-US,en;q=0.9",
            "Origin" to mainUrl,
            "Accept-Encoding" to "gzip, deflate, br, zstd",
            "Connection" to "keep-alive",
            "Sec-Fetch-Dest" to "empty",
            "Sec-Fetch-Mode" to "cors",
            "Sec-Fetch-Site" to "same-site",
            "user-agent" to USER_AGENT,
        )

        val doc = app.get(getEmbedUrl(url), referer = referer).document
        val unpackedJs = unpackJs(doc).toString()

        JwPlayerHelper.extractStreamLinks(unpackedJs, name, mainUrl, callback, subtitleCallback, headers = header)
    }

    private fun unpackJs(script: Element): String? {
        return script.select("script").find { it.data().contains("eval(function(p,a,c,k,e,d)") }
            ?.data()?.let { getAndUnpack(it) }
    }

    private fun getEmbedUrl(url: String): String {
        return if (!url.contains("/embed-")) {
            val videoId = url.substringAfter("$mainUrl/")
            "$mainUrl/embed-$videoId"
        } else url
    }
}

package com.cosmix.app.extractors

import com.cosmix.app.SubtitleFile
import com.cosmix.app.USER_AGENT
import com.cosmix.app.app
import com.cosmix.app.extractors.helper.JwPlayerHelper
import com.cosmix.app.utils.ExtractorApi
import com.cosmix.app.utils.ExtractorLink

class Vidmolyme : Vidmoly() {
    override val mainUrl = "https://vidmoly.me"
}

class Vidmolyto : Vidmoly() {
    override val mainUrl = "https://vidmoly.to"
}

class Vidmolybiz : Vidmoly() {
    override val mainUrl = "https://vidmoly.biz"
}

open class Vidmoly : ExtractorApi() {
    override val name = "Vidmoly"
    override val mainUrl = "https://vidmoly.net"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val headers = mapOf(
            "user-agent" to USER_AGENT,
            "Sec-Fetch-Dest" to "iframe"
        )
        
        val newUrl = if (url.contains("/w/")) 
            url.replaceFirst("/w/", "/embed-") + ".html" 
            else url

        val script = app.get(newUrl, headers = headers, referer = referer)
            .document.select("script")
            .firstOrNull { it.data().contains("sources:") }
            ?.data()

        // Extracts and parses videoData
        JwPlayerHelper.extractStreamLinks(script.orEmpty(), name, mainUrl, callback, subtitleCallback)
    }
}

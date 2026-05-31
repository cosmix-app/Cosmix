package com.cosmix.app.extractors

import com.cosmix.app.SubtitleFile
import com.cosmix.app.app
import com.cosmix.app.extractors.helper.JwPlayerHelper
import com.cosmix.app.utils.ExtractorApi
import com.cosmix.app.utils.ExtractorLink
import com.cosmix.app.utils.JsUnpacker


open class Vtbe : ExtractorApi() {
    override var name = "Vtbe"
    override var mainUrl = "https://vtbe.to"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val response = app.get(url,referer=mainUrl).document
        val extractedpack = response.selectFirst("script:containsData(function(p,a,c,k,e,d))")?.data().toString()
        JsUnpacker(extractedpack).unpack()?.let { unPacked ->
            JwPlayerHelper.extractStreamLinks(unPacked, name, mainUrl, callback, subtitleCallback)
        }
    }
}

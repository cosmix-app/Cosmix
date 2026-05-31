package com.cosmix.app.extractors

import com.cosmix.api.Log
import com.cosmix.app.SubtitleFile
import com.cosmix.app.app
import com.cosmix.app.extractors.helper.JwPlayerHelper
import com.cosmix.app.utils.ExtractorApi
import com.cosmix.app.utils.ExtractorLink
import com.cosmix.app.utils.JsUnpacker
import com.cosmix.app.utils.fixUrl
import kotlinx.coroutines.delay

class Up4FunTop : Up4Stream() {
    override var mainUrl: String = "https://up4fun.top"
}

open class Up4Stream : ExtractorApi() {
    override var name = "Up4Stream"
    override var mainUrl = "https://up4stream.com"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val movieId = url.substringAfterLast("/").substringBefore(".html")

        // redirect from "wait 5 seconds" page to actual movie page
        val redirectResponse = app.get(url, cookies = mapOf("id" to movieId))
        val redirectForm = redirectResponse.document.selectFirst("form[method=POST]") ?: return
        val redirectUrl = fixUrl(redirectForm.attr("action"))
        val redirectParams = redirectForm.select("input[type=hidden]").associate { input ->
            input.attr("name") to input.attr("value")
        }

        // wait for 5 seconds, otherwise the below md5 hash is invalid
        delay(5000)
        val response = app.post(redirectUrl, data = redirectParams).document

        // starting here, this works similar to many other extractors like StreamWish
        val extractedpack =
            response.selectFirst("script:containsData(function(p,a,c,k,e,d))")?.data()
        if (extractedpack == null) {
            Log.e("up4stream", "file not ready: delay too short")
        }

        JsUnpacker(extractedpack).unpack()?.let { unPacked ->
            JwPlayerHelper.extractStreamLinks(unPacked, name, mainUrl, callback, subtitleCallback)
        }
    }
}
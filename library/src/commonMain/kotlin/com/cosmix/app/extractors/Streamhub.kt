package com.cosmix.app.extractors

import com.cosmix.app.app
import com.cosmix.app.utils.ExtractorApi
import com.cosmix.app.utils.ExtractorLink
import com.cosmix.app.utils.ExtractorLinkType
import com.cosmix.app.utils.INFER_TYPE
import com.cosmix.app.utils.JsUnpacker
import com.cosmix.app.utils.Qualities
import com.cosmix.app.utils.newExtractorLink

open class Streamhub : ExtractorApi() {
    override var mainUrl = "https://streamhub.to"
    override var name = "Streamhub"
    override val requiresReferer = false

    override fun getExtractorUrl(id: String): String {
        return "$mainUrl/e/$id"
    }

    override suspend fun getUrl(url: String, referer: String?): List<ExtractorLink>? {
        val response = app.get(url).text
        Regex("eval((.|\\n)*?)</script>").find(response)?.groupValues?.get(1)?.let { jsEval ->
            JsUnpacker("eval$jsEval").unpack()?.let { unPacked ->
                Regex("sources:\\[\\{src:\"(.*?)\"").find(unPacked)?.groupValues?.get(1)?.let { link ->
                    return listOf(
                        newExtractorLink(
                            source = this.name,
                            this.name,
                            link,
                        )
                    )
                }
            }
        }
        return null
    }
}
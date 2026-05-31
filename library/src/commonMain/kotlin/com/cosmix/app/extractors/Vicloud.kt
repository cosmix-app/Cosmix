package com.cosmix.app.extractors

import com.fasterxml.jackson.annotation.JsonProperty
import com.cosmix.app.CsxHolder.unixTimeMS
import com.cosmix.app.SubtitleFile
import com.cosmix.app.app
import com.cosmix.app.utils.ExtractorApi
import com.cosmix.app.utils.ExtractorLink
import com.cosmix.app.utils.getQualityFromName
import com.cosmix.app.utils.newExtractorLink

open class Vicloud : ExtractorApi() {
    override val name: String = "Vicloud"
    override val mainUrl: String = "https://vicloud.sbs"
    override val requiresReferer = false

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val id = Regex("\"apiQuery\":\"(.*?)\"").find(app.get(url).text)?.groupValues?.getOrNull(1)
        app.get(
            "$mainUrl/api/?$id=&_=$unixTimeMS",
            headers = mapOf(
                "X-Requested-With" to "XMLHttpRequest"
            ),
            referer = url
        ).parsedSafe<Responses>()?.sources?.map { source ->
            callback.invoke(
                newExtractorLink(
                    name,
                    name,
                    source.file ?: return@map null,
                ) {
                    this.referer = url
                    this.quality = getQualityFromName(source.label)
                }
            )
        }

    }

    private data class Sources(
        @JsonProperty("file") val file: String? = null,
        @JsonProperty("label") val label: String? = null,
    )

    private data class Responses(
        @JsonProperty("sources") val sources: List<Sources>? = arrayListOf(),
    )

}
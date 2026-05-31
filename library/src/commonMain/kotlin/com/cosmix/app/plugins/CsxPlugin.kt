package com.cosmix.app.plugins

import com.fasterxml.jackson.annotation.JsonProperty
import com.cosmix.app.CsxHolder
import com.cosmix.app.CsxApi
import com.cosmix.app.utils.ExtractorApi
import com.cosmix.api.Log
import com.cosmix.app.utils.extractorApis

const val EXTENSION_TAG = "PluginInstance"

abstract class CsxPlugin {
    /**
     * Used to register providers instances of CsxApi
     * @param element CsxApi provider you want to register
     */
    fun registerCsxApi(element: CsxApi) {
        Log.i(EXTENSION_TAG, "Adding ${element.name} (${element.mainUrl}) CsxApi")
        element.sourcePlugin = this.filename
        CsxHolder.allProviders.add(element)
        CsxHolder.addPluginMapping(element)
    }

    /**
     * Used to register extractor instances of ExtractorApi
     * @param element ExtractorApi provider you want to register
     */
    fun registerExtractorApi(element: ExtractorApi) {
        Log.i(EXTENSION_TAG, "Adding ${element.name} (${element.mainUrl}) ExtractorApi")
        element.sourcePlugin = this.filename
        extractorApis.add(element)
    }

    /**
     * Called when your Plugin is being unloaded
     */
    @Throws(Throwable::class)
    open fun beforeUnload() {
    }

    /**
     * Called when your Plugin is loaded
     */
    @Throws(Throwable::class)
    open fun load() {
    }

    /** Full file path to the plugin. */
    @Deprecated(
        "Renamed to `filename` to follow conventions",
        replaceWith = ReplaceWith("filename"),
        level = DeprecationLevel.ERROR
    )
    var __filename: String?
        get() = filename
        set(value) {
            filename = value
        }
    var filename: String? = null


    class Manifest {
        @JsonProperty("name")
        var name: String? = null

        @JsonProperty("pluginClassName")
        var pluginClassName: String? = null

        @JsonProperty("version")
        var version: Int? = null

        @JsonProperty("requiresResources")
        var requiresResources: Boolean = false
    }
}
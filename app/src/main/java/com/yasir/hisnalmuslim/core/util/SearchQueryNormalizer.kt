package com.yasir.hisnalmuslim.core.util

object SearchQueryNormalizer {
    fun normalize(rawQuery: String): String {
        return rawQuery
            .trim()
            .replace(Regex("\\s+"), " ")
    }
}

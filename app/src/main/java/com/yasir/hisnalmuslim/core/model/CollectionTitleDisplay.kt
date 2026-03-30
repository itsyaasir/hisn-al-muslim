package com.yasir.hisnalmuslim.core.model

fun Collection.displayTitle(settings: AppSettings): String {
    return when (settings.collectionTitleLanguage) {
        CollectionTitleLanguage.ARABIC -> subtitle?.takeIf { it.isNotBlank() } ?: title
        CollectionTitleLanguage.ENGLISH -> title
    }
}

fun Dhikr.displayCollectionTitle(settings: AppSettings): String {
    return when (settings.collectionTitleLanguage) {
        CollectionTitleLanguage.ARABIC -> collectionSubtitle?.takeIf { it.isNotBlank() } ?: collectionTitle
        CollectionTitleLanguage.ENGLISH -> collectionTitle
    }
}

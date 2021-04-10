package net.adoptium.api.model

import com.fasterxml.jackson.annotation.JsonProperty

// TODO check if openjdk-api-v3-models can be used
data class AvailableReleasesResponse(
        @JsonProperty("available_lts_releases") val ltsReleases: Array<Int>,
        @JsonProperty("available_releases") val allReleases: Array<Int>,
        @JsonProperty("most_recent_feature_release") val mostRecentRelease: Int,
        @JsonProperty("most_recent_feature_version") val mostRecentVersion: Int,
        @JsonProperty("most_recent_lts") val mostRecentLts: Int,
        @JsonProperty("tip_version") val tipVersion: Int,
)

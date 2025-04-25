package snagtype.bingobongo.config

import kotlinx.serialization.Serializable

@Serializable

enum class GameMode {
    LINE, BLACKOUT
}
enum class TagOption {
    TAGS, IGNORE_TAGS, EXCLUDE_LARGE_TAGS, WEIGHTED_TAGS
}
@Serializable
data class BingoConfig(
    var mode: GameMode = GameMode.LINE,
    var tagOption: TagOption = TagOption.TAGS,
    var enableRandomRewards: Boolean = false,
    var enableFreeSpace: Boolean = false,
    var modList: List<String>? = null,
    var modWhiteList: List<String>? = null,
    var modBlackList: List<String>? = null
)
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
    var excludeTagLimit: Int = 30,
    var modList: MutableList<String>? = null,
    var modWhiteList: MutableList<String>? = null,
    var modBlackList: MutableList<String>? = null
)
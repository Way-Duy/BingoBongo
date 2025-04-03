package snagtype.bingobongo.utils

import com.google.common.base.Preconditions
import net.minecraft.item.Item
import java.io.File

//todo given a list of 24/25 items, create a a bingo sheet
// (optional) make separate bingo advancements tab
// grid will something look like this if vanilla:
// advancements go left to right, but we can use some tricks to bypass this
// [x] = actual advancement
// [ ] = blank advancement necessary for skipping ahead or visibility iirc
// [ ]->[ ]->[ ]->[ ]->[ ]
// [x]->[x]->[x]->[x]->[x]->[ ]
// [x]->[x]->[x]->[x]->[x]->[ ]
// [x]->[x]->[x]->[x]->[x]->[ ]
// [x]->[x]->[x]->[x]->[x]->[ ]
// [x]->[x]->[x]->[x]->[x]->[ ]

fun BingoAdvancementPage( exportDirectory: File,  itemList: List<Item>, isFreeSpaceEnabled: Boolean) {

}

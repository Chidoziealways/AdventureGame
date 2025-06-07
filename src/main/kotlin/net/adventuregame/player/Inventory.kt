package net.adventuregame.player

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.adventuregame.items.Item
import net.adventuregame.registries.ItemRegistry
import kotlin.math.min

class Inventory {
    private val items: MutableList<Item?> = ArrayList(9)
    var selectedIndex: Int = 0
        private set

    constructor() {
        for (i in 0..8) {
            items.add(null)
        }
    }

    constructor(items: List<Item?>, selectedIndex: Int) {
        require(items.size == 9) { "Inventory must have exactly 9 slots." }
        this.items.addAll(items)
        this.selectedIndex = selectedIndex
    }

    fun addItem(item: Item) {
        for (i in items.indices) {
            if (items[i] == null) {
                items[i] = item
                return
            }
        }
        println("Inventory is full. Couldn't add item: " + item.name)
    }

    fun hasItem(itemType: Class<out Item?>): Boolean {
        for (item in items) {
            if (item != null && itemType.isInstance(item)) {
                return true
            }
        }
        return false
    }

    fun <T : Item?> getItem(itemType: Class<T>): T? {
        for (item in items) {
            if (item != null && itemType.isInstance(item)) {
                return itemType.cast(item)
            }
        }
        return null
    }

    fun removeItem(item: Item) {
        for (i in items.indices) {
            if (items[i] === item) {
                items[i] = null
                break
            }
        }
    }

    val selectedItem: Item?
        get() = items[selectedIndex]

    fun selectSlot(index: Int) {
        if (index >= 0 && index < items.size) {
            selectedIndex = index
        }
    }

    fun scrollSlot(offset: Int) {
        selectedIndex = (selectedIndex + offset + items.size) % items.size
    }

    val allItems: List<Item?>
        get() = items

    override fun toString(): String {
        return "Inventory{" +
                "items=" + items +
                ", selectedIndex=" + selectedIndex +
                '}'
    }

    fun selectNext() {
        if (items.isEmpty()) return
        selectedIndex = (selectedIndex + 1) % items.size
    }

    fun selectPrevious() {
        if (items.isEmpty()) return
        selectedIndex = (selectedIndex - 1 + items.size) % items.size
    }

    fun getSelectedItemName(): String? = selectedItem?.name


    companion object {
        // ======== CODEC =========
        val CODEC: Codec<Inventory> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<Inventory> ->
            instance.group(
                Codec.STRING.listOf().fieldOf("items")
                    .forGetter { inv: Inventory ->
                        inv.items.stream()
                            .map { item: Item? -> if (item == null) "" else item.name }
                            .toList()
                    },
                Codec.INT.fieldOf("selectedIndex")
                    .forGetter { inv: Inventory -> inv.selectedIndex }
            ).apply(
                instance
            ) { names: List<String?>, selected: Int ->
                val inventory = Inventory()
                for (i in 0..<min(names.size.toDouble(), 9.0).toInt()) {
                    val name = names[i]
                    if (name == null || name.isEmpty()) {
                        inventory.items[i] = null
                    } else {
                        val item = ItemRegistry.get(name)
                        inventory.items[i] = item
                    }
                }
                inventory.selectedIndex = selected
                inventory
            }
        }
    }
}

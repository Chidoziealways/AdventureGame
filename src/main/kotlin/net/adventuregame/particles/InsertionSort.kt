package net.adventuregame.particles

/**
 * A simple implementation of an insertion sort. I implemented this very quickly
 * the other day so it may not be perfect or the most efficient! Feel free to
 * implement your own sorter instead.
 *
 * @author Karl
 */
object InsertionSort {
    /**
     * Sorts a list of particles so that the particles with the highest distance
     * from the camera are first, and the particles with the shortest distance
     * are last.
     *
     * @param list
     * - the list of particles needing sorting.
     */
    fun sortHighToLow(list: MutableList<Particle>) {
        for (i in 1..<list.size) {
            val item = list[i]
            if (item.distance > list[i - 1].distance) {
                sortUpHighToLow(list, i)
            }
        }
    }


    private fun sortUpHighToLow(list: MutableList<Particle>, i: Int) {
        val item = list[i]
        var attemptPos = i - 1
        while (attemptPos != 0 && list[attemptPos - 1].distance < item.distance) {
            attemptPos--
        }
        list.removeAt(i)
        list.add(attemptPos, item)
    }
}


package lesson5

import ru.spbstu.kotlin.generate.util.nextString
import java.util.*
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

abstract class AbstractOpenAddressingSetTest {

    abstract fun <T : Any> create(bits: Int): MutableSet<T>

    protected fun doAddTest() {
        val random = Random()
        for (iteration in 1..100) {
            val controlSet = mutableSetOf<Int>()
            val bitsNumber = random.nextInt(4) + 5
            val openAddressingSet = create<Int>(bitsNumber)
            assertTrue(openAddressingSet.size == 0, "Size of an empty set is not zero.")
            for (i in 1..50) {
                val nextInt = random.nextInt(32)
                val additionResult = openAddressingSet.add(nextInt)
                assertEquals(
                    nextInt !in controlSet, additionResult,
                    "An element was ${if (additionResult) "" else "not"} added when it ${if (additionResult) "was already in the set" else "should have been"}."
                )
                controlSet += nextInt
                assertTrue(nextInt in openAddressingSet, "A supposedly added element is not in the set.")
                assertEquals(controlSet.size, openAddressingSet.size, "The size of the set is not as expected.")
            }
            val smallSet = create<Int>(bitsNumber)
            assertFailsWith<IllegalStateException>("A table overflow is not being prevented.") {
                for (i in 1..4000) {
                    smallSet.add(random.nextInt())
                }
            }
        }
    }

    protected fun doRemoveTest() {

        //Мои тесты
        for (iteration in 1..9) {
            val controlSet = mutableSetOf<String>()
            var bitsNumber: Int
            var openAddressingSet: MutableSet<String>
            var removeElement: String
            when (iteration) {

                1 -> {
                    bitsNumber = 5
                    openAddressingSet = create(bitsNumber)
                    removeElement = "cb"

                    openAddressingSet.add("c3bc1")
                    openAddressingSet.add("cc")
                    openAddressingSet.add("cb")
                    openAddressingSet.add("32")
                    openAddressingSet.add("c2b3c")
                    openAddressingSet.add("b")

                    controlSet.add("c2b3c")
                    controlSet.add("c3bc1")
                    controlSet.add("cc")
                    controlSet.add("32")
                    controlSet.add("b")

                }

                2 -> {
                    bitsNumber = 5
                    openAddressingSet = create(bitsNumber)
                    removeElement = "32acb"

                    openAddressingSet.add("a")
                    openAddressingSet.add("32acb")
                    openAddressingSet.add("bc")
                    openAddressingSet.add("2ba31")
                    openAddressingSet.add("a1c31")
                    openAddressingSet.add("3bb")

                    controlSet.add("a1c31")
                    controlSet.add("a")
                    controlSet.add("3bb")
                    controlSet.add("bc")
                    controlSet.add("2ba31")
                }

                3 -> {
                    bitsNumber = 3
                    openAddressingSet = create(bitsNumber)
                    removeElement = "2c"

                    openAddressingSet.add("acaa2")
                    openAddressingSet.add("23")
                    openAddressingSet.add("2c")
                    openAddressingSet.add("1cb3")
                    openAddressingSet.add("233a3")
                    openAddressingSet.add("c")

                    controlSet.add("233a3")
                    controlSet.add("23")
                    controlSet.add("acaa2")
                    controlSet.add("1cb3")
                    controlSet.add("c")
                }

                4 -> {
                    bitsNumber = 3
                    openAddressingSet = create(bitsNumber)
                    removeElement = "2c"

                    openAddressingSet.add("2c")
                    openAddressingSet.add("cba")
                    openAddressingSet.add("c")
                    openAddressingSet.add("c3ca1")
                    openAddressingSet.add("b3c")
                    openAddressingSet.add("a1133")

                    controlSet.add("c")
                    controlSet.add("c3ca1")
                    controlSet.add("cba")
                    controlSet.add("b3c")
                    controlSet.add("a1133")
                }

                5 -> {
                    bitsNumber = 6
                    openAddressingSet = create(bitsNumber)
                    removeElement = "a"

                    openAddressingSet.add("a1")
                    openAddressingSet.add("a")
                    openAddressingSet.add("222")
                    openAddressingSet.add("1c")
                    openAddressingSet.add("2")
                    openAddressingSet.add("3a3c")

                    controlSet.add("222")
                    controlSet.add("1c")
                    controlSet.add("2")
                    controlSet.add("a1")
                    controlSet.add("3a3c")
                }

                6 -> {
                    bitsNumber = 2
                    openAddressingSet = create(bitsNumber)
                    removeElement = "1"

                    openAddressingSet.add("0")
                    openAddressingSet.add("2")
                    openAddressingSet.add("1")
                    openAddressingSet.add("3")

                    controlSet.add("2")
                    controlSet.add("0")
                    controlSet.add("3")

                }

                7 -> {
                    bitsNumber = 5
                    openAddressingSet = create(bitsNumber)
                    removeElement = "cb"

                    openAddressingSet.add("c3bc1")
                    openAddressingSet.add("cc")
                    openAddressingSet.add("cb")
                    openAddressingSet.add("32")
                    openAddressingSet.add("33")
                    openAddressingSet.add("b")

                    controlSet.add("33")
                    controlSet.add("c3bc1")
                    controlSet.add("cc")
                    controlSet.add("32")
                    controlSet.add("b")

                }

                8 -> {
                    bitsNumber = 4
                    openAddressingSet = create(bitsNumber)
                    removeElement = "cb"

                    openAddressingSet.add("23")
                    openAddressingSet.add("bc")
                    openAddressingSet.add("c")
                    openAddressingSet.add("1aca")
                    openAddressingSet.add("cb")
                    openAddressingSet.add("c1")

                    controlSet.add("23")
                    controlSet.add("1aca")
                    controlSet.add("c")
                    controlSet.add("c1")
                    controlSet.add("bc")
                }

                else -> {
                    bitsNumber = 6
                    openAddressingSet = create(bitsNumber)
                    removeElement = "1"

                    openAddressingSet.add("23")
                    openAddressingSet.add("2b11")
                    openAddressingSet.add("2c")
                    openAddressingSet.add("22b3b")
                    openAddressingSet.add("1")
                    openAddressingSet.add("2")

                    controlSet.add("23")
                    controlSet.add("2b11")
                    controlSet.add("2c")
                    controlSet.add("22b3b")
                    controlSet.add("2")
                }
            }

            assertTrue(
                openAddressingSet.remove(removeElement),
                "An element wasn't removed contrary to expected."
            )
            assertFalse(
                removeElement in openAddressingSet,
                "A supposedly removed element is still in the set."
            )

            for (element in controlSet) {
                assertTrue(
                    openAddressingSet.contains(element),
                    "Open addressing set doesn't have the element $element from the control set."
                )
            }
            controlSet.clear()

            assertFalse(
                openAddressingSet.remove(removeElement),
                "A removed element was supposedly removed twice."
            )

        }

        val random = Random()
        for (iteration in 1..100) {
            val bitsNumber = random.nextInt(4) + 6
            val openAddressingSet = create<Int>(bitsNumber)
            for (i in 1..50) {
                val firstInt = random.nextInt(32)
                val secondInt = firstInt + (1 shl bitsNumber)
                openAddressingSet += secondInt
                openAddressingSet += firstInt
                val expectedSize = openAddressingSet.size - 1
                assertTrue(
                    openAddressingSet.remove(secondInt),
                    "An element wasn't removed contrary to expected."
                )
                assertFalse(
                    secondInt in openAddressingSet,
                    "A supposedly removed element is still in the set."
                )
                assertTrue(
                    firstInt in openAddressingSet,
                    "The removal of the element prevented access to the other elements."
                )
                assertEquals(
                    expectedSize, openAddressingSet.size,
                    "The size of the set is not as expected."
                )
                assertFalse(
                    openAddressingSet.remove(secondInt),
                    "A removed element was supposedly removed twice."
                )
                assertEquals(
                    expectedSize, openAddressingSet.size,
                    "The size of the set is not as expected."
                )
            }
        }
    }

    protected fun doIteratorTest() {
        val random = Random()
        for (iteration in 1..100) {
            val controlSet = mutableSetOf<String>()
            for (i in 1..15) {
                val string = random.nextString("abcdefgh12345678", 1, 15)
                controlSet.add(string)
            }
            println("Control set: $controlSet")
            val openAddressingSet = create<String>(random.nextInt(6) + 4)
            assertFalse(
                openAddressingSet.iterator().hasNext(),
                "Iterator of an empty set should not have any next elements."
            )
            for (element in controlSet) {
                openAddressingSet += element
            }
            val iterator1 = openAddressingSet.iterator()
            val iterator2 = openAddressingSet.iterator()
            println("Checking if calling hasNext() changes the state of the iterator...")
            while (iterator1.hasNext()) {
                assertEquals(
                    iterator2.next(), iterator1.next(),
                    "Calling OpenAddressingSetIterator.hasNext() changes the state of the iterator."
                )
            }
            val openAddressingSetIter = openAddressingSet.iterator()
            println("Checking if the iterator traverses the entire set...")
            while (openAddressingSetIter.hasNext()) {
                controlSet.remove(openAddressingSetIter.next())
            }
            assertTrue(
                controlSet.isEmpty(),
                "OpenAddressingSetIterator doesn't traverse the entire set."
            )
            assertFailsWith<IllegalStateException>("Something was supposedly returned after the elements ended") {
                openAddressingSetIter.next()
            }
            println("All clear!")
        }
    }

    protected fun doIteratorRemoveTest() {

        //Мои тесты
        for (iteration in 1..8) {
            var bitsNumber: Int
            val controlSet = mutableSetOf<String>()
            var toRemove: String

            when (iteration) {

                //Удаление предпоследнего элемента коллекции с перемещение последнего на его позицию
                1 -> {
                    toRemove = "32b3c"
                    bitsNumber = 3

                    controlSet.add("b211")
                    controlSet.add("1")
                    controlSet.add("13")
                    controlSet.add("3")
                    controlSet.add("32b3c")
                    controlSet.add("a3aa")
                }

                //Удаление последнего элемента с перемещение элемента сверху на его позицию
                2 -> {
                    toRemove = "caab"
                    bitsNumber = 7

                    controlSet.add("c1aa3")
                    controlSet.add("2")
                    controlSet.add("c")
                    controlSet.add("babaa")
                    controlSet.add("caab")
                    controlSet.add("c23c")
                }

                //Удаление элемента с перемещением вниз, уже рассмотренного элемента
                3 -> {
                    toRemove = "4bh8a611d3b7a"
                    bitsNumber = 4

                    controlSet.add("1")
                    controlSet.add("2")
                    controlSet.add("1d4e3dc8cbea")
                    controlSet.add("daa7cg")
                    controlSet.add("ce5eb1e25")
                    controlSet.add("ahe62a")
                    controlSet.add("4bh8a611d3b7a")
                    controlSet.add("4f4d5agd")
                    controlSet.add("aha")
                    controlSet.add("b58hde7161ef7")
                    controlSet.add("75cf7286e36")
                    controlSet.add("2afba5hc31hfd")
                    controlSet.add("gffd7ge72hd")
                    controlSet.add("72344h")
                    controlSet.add("hd")

                }

                //просто удаление элемента без перемещния других
                4 -> {
                    toRemove = "2a36c152"
                    bitsNumber = 9

                    controlSet.add("2c6b77")
                    controlSet.add("1b7a")
                    controlSet.add("2a36c152")
                    controlSet.add("3c2a2a")
                    controlSet.add("ca3a6a3")
                    controlSet.add("b1517bc")
                    controlSet.add("61a77c2c")
                    controlSet.add("3b7c71")
                }

                //удаление предпоследнего без перемещения других
                5 -> {
                    toRemove = "1bc11"
                    bitsNumber = 5

                    controlSet.add("b1ac3")
                    controlSet.add("22a13")
                    controlSet.add("b1332")
                    controlSet.add("3b")
                    controlSet.add("1bc11")
                    controlSet.add("3bb")
                }

                //удаление элемента с перемещением элементов, лежащих ниже
                6 -> {
                    toRemove = "b2675"
                    bitsNumber = 4

                    controlSet.add("bc213322")
                    controlSet.add("2177a")
                    controlSet.add("7cac1")
                    controlSet.add("b2675")
                    controlSet.add("abc115")
                    controlSet.add("6")
                    controlSet.add("51b1a")
                    controlSet.add("cba6")
                }

                //Удаление предпоследнего с перемещение на его место уже рассмотренного выше элемента
                7 -> {
                    toRemove = "63a888a"
                    bitsNumber = 4

                    controlSet.add("58d")
                    controlSet.add("7767dhegdf417")
                    controlSet.add("7eg82")
                    controlSet.add("d")
                    controlSet.add("75cg233bd")
                    controlSet.add("h2623e157c")
                    controlSet.add("cc5b86ca")
                    controlSet.add("g3ec")
                    controlSet.add("f1b2")
                    controlSet.add("ag175ac")
                    controlSet.add("hga85abhb2e")
                    controlSet.add("63a888a")
                    controlSet.add("a3a6b8c5")
                    controlSet.add("5ab1cd5")
                    controlSet.add("f7h2")
                }

                //Удаляем элемент и на его место перемещается элемент уже рассмотренный ранее
                else -> {
                    toRemove = "5a"
                    bitsNumber = 4

                    controlSet.add("e42h7g843c")
                    controlSet.add("e6e32")
                    controlSet.add("8b737dg3eba7f")
                    controlSet.add("828feb8edh")
                    controlSet.add("hf5a")
                    controlSet.add("65g3")
                    controlSet.add("5a")
                    controlSet.add("e2bfg3")
                    controlSet.add("bf83h6321b2bbc")
                    controlSet.add("76d25aa545")
                    controlSet.add("e77a")
                    controlSet.add("67g61fhghdd564")
                    controlSet.add("186ba")
                    controlSet.add("ea726111bb127")
                    controlSet.add("gh31ee4")
                }
            }

            val openAddressingSet = create<String>(bitsNumber)
            for (element in controlSet) {
                openAddressingSet += element
            }
            controlSet.remove(toRemove)
            val iterator = openAddressingSet.iterator()
            assertFailsWith<IllegalStateException>("Something was supposedly deleted before the iteration started") {
                iterator.remove()
            }

            var counter = openAddressingSet.size
            while (iterator.hasNext()) {
                val element = iterator.next()
                counter--
                if (element == toRemove) {
                    iterator.remove()
                }
            }
            assertEquals(
                0, counter,
                "OpenAddressingSetIterator.remove() changed iterator position: ${abs(counter)} elements were ${if (counter > 0) "skipped" else "revisited"}."
            )
            assertEquals(
                controlSet.size, openAddressingSet.size,
                "The size of the set is incorrect: was ${openAddressingSet.size}, should've been ${controlSet.size}."
            )
            for (element in controlSet) {
                assertTrue(
                    openAddressingSet.contains(element),
                    "Open addressing set doesn't have the element $element from the control set."
                )
            }
            for (element in openAddressingSet) {
                assertTrue(
                    controlSet.contains(element),
                    "Open addressing set has the element $element that is not in control set."
                )
            }
            controlSet.clear()
        }

        val random = Random()
        for (iteration in 1..100) {
            val controlSet = mutableSetOf<String>()
            val removeIndex = random.nextInt(15) + 1
            var toRemove = ""
            for (i in 1..15) {
                val string = random.nextString("abcdefgh12345678", 1, 15)
                controlSet.add(string)
                if (i == removeIndex) {
                    toRemove = string
                }
            }
            println("Initial set: $controlSet")
            val openAddressingSet = create<String>(random.nextInt(6) + 4)
            for (element in controlSet) {
                openAddressingSet += element
            }
            controlSet.remove(toRemove)
            println("Control set: $controlSet")
            println("Removing element \"$toRemove\" from open addressing set through the iterator...")
            val iterator = openAddressingSet.iterator()
            assertFailsWith<IllegalStateException>("Something was supposedly deleted before the iteration started") {
                iterator.remove()
            }
            var counter = openAddressingSet.size
            while (iterator.hasNext()) {
                val element = iterator.next()
                counter--
                if (element == toRemove) {
                    iterator.remove()
                }
            }
            assertEquals(
                0, counter,
                "OpenAddressingSetIterator.remove() changed iterator position: ${abs(counter)} elements were ${if (counter > 0) "skipped" else "revisited"}."
            )
            assertEquals(
                controlSet.size, openAddressingSet.size,
                "The size of the set is incorrect: was ${openAddressingSet.size}, should've been ${controlSet.size}."
            )
            for (element in controlSet) {
                assertTrue(
                    openAddressingSet.contains(element),
                    "Open addressing set doesn't have the element $element from the control set."
                )
            }
            for (element in openAddressingSet) {
                assertTrue(
                    controlSet.contains(element),
                    "Open addressing set has the element $element that is not in control set."
                )
            }
            println("All clear!")
        }
    }
}
package lesson4

import java.util.*
import kotlin.math.abs
import ru.spbstu.kotlin.generate.util.nextString
import kotlin.test.*

abstract class AbstractTrieTest {

    abstract fun create(): MutableSet<String>

    private fun <R> implementationTest(function: () -> R) {
        try {
            function()
        } catch (e: Error) {
            if (e is NotImplementedError) {
                throw e
            }
        } catch (e: Exception) {
            // let it slide for now
        }
    }

    protected fun doGeneralTest() {
        val random = Random()
        for (iteration in 1..100) {
            val trie = create()
            assertEquals(0, trie.size)
            assertFalse("some" in trie)
            var wordCounter = 0
            val wordList = mutableSetOf<String>()
            val removeIndex = random.nextInt(15) + 1
            var toRemove = ""
            for (i in 1..15) {
                val string = random.nextString("abcdefgh", 1, 15)
                wordList += string
                if (i == removeIndex) {
                    toRemove = string
                }
                if (trie.add(string)) {
                    wordCounter++
                }
                assertTrue(
                    string in trie,
                    "An element wasn't added to trie when it should've been."
                )
                if (string.length != 1) {
                    val substring = string.substring(0, random.nextInt(string.length - 1))
                    if (substring !in wordList) {
                        assertTrue(
                            substring !in trie,
                            "An element is considered to be in trie when it should not be there."
                        )
                    }
                }
            }
            assertEquals(wordCounter, trie.size)
            trie.remove(toRemove)
            assertEquals(wordCounter - 1, trie.size)
            assertFalse(
                toRemove in trie,
                "A supposedly removed element is still considered to be in trie."
            )
            trie.clear()
            assertEquals(0, trie.size)
            assertFalse("some" in trie)
        }
    }

    protected fun doIteratorTest() {

        /*Проверим, что в дереве
                                          ''
                                a          b         c
                                       a   ''   c

         Итератор правильно сработает и найдет следующее слово после "ba" (это будет "b")
         */

        implementationTest { create().iterator().hasNext() }
        implementationTest { create().iterator().next() }
        for (iteration in 1..1) {
            val controlSet = mutableSetOf<String>()
            controlSet.add("a")
            controlSet.add("ba")
            controlSet.add("b")
            controlSet.add("bc")
            controlSet.add("c")
            val trieSet = create()
            assertFalse(
                trieSet.iterator().hasNext(),
                "Iterator of an empty set should not have any next elements."
            )
            for (element in controlSet) {
                trieSet += element
            }
            val iterator1 = trieSet.iterator()
            val iterator2 = trieSet.iterator()
            println("Checking if calling hasNext() changes the state of the iterator...")
            while (iterator1.hasNext()) {
                assertEquals(
                    iterator2.next(), iterator1.next(),
                    "Calling TrieIterator.hasNext() changes the state of the iterator."
                )
            }
            val trieIter = trieSet.iterator()
            println("Checking if the iterator traverses the entire set...")
            while (trieIter.hasNext()) {
                controlSet.remove(trieIter.next())
            }
            assertTrue(
                controlSet.isEmpty(),
                "TrieIterator doesn't traverse the entire set."
            )
            assertFailsWith<IllegalStateException>("Something was supposedly returned after the elements ended") {
                trieIter.next()
            }
            println("All clear!")
        }

        implementationTest { create().iterator().hasNext() }
        implementationTest { create().iterator().next() }
        val random = Random()
        for (iteration in 1..100) {
            val controlSet = mutableSetOf<String>()
            for (i in 1..15) {
                val string = random.nextString("abcdefgh", 1, 15)
                controlSet.add(string)
            }
            println("Control set: $controlSet")
            val trieSet = create()
            assertFalse(
                trieSet.iterator().hasNext(),
                "Iterator of an empty set should not have any next elements."
            )
            for (element in controlSet) {
                trieSet += element
            }
            val iterator1 = trieSet.iterator()
            val iterator2 = trieSet.iterator()
            println("Checking if calling hasNext() changes the state of the iterator...")
            while (iterator1.hasNext()) {
                assertEquals(
                    iterator2.next(), iterator1.next(),
                    "Calling TrieIterator.hasNext() changes the state of the iterator."
                )
            }
            val trieIter = trieSet.iterator()
            println("Checking if the iterator traverses the entire set...")
            while (trieIter.hasNext()) {
                controlSet.remove(trieIter.next())
            }
            assertTrue(
                controlSet.isEmpty(),
                "TrieIterator doesn't traverse the entire set."
            )
            assertFailsWith<IllegalStateException>("Something was supposedly returned after the elements ended") {
                trieIter.next()
            }
            println("All clear!")
        }
    }

    protected fun doIteratorRemoveTest() {

        implementationTest { create().iterator().remove() }
        for (iteration in 1..22) {
            val controlSet = mutableSetOf<String>()
            var toRemove: String
            when (iteration) {
                // Тесты для правильного определения lastWordOfThisChild (функция thisIsTheLastWordOfThisChild())
                1 -> {
                    controlSet.add("ac")
                    controlSet.add("adel")
                    controlSet.add("adfab")
                    controlSet.add("adfak")
                    controlSet.add("ak")
                    controlSet.add("b")
                    toRemove = "adel"
                }
                2 -> {
                    controlSet.add("ac")
                    controlSet.add("adel")
                    controlSet.add("adfab")
                    controlSet.add("adfak")
                    controlSet.add("ak")
                    controlSet.add("b")
                    toRemove = "adfab"
                }
                3 -> {
                    controlSet.add("ac")
                    controlSet.add("adel")
                    controlSet.add("adfab")
                    controlSet.add("adfak")
                    controlSet.add("ak")
                    controlSet.add("b")
                    toRemove = "adfak"
                }
                4 -> {
                    controlSet.add("ac")
                    controlSet.add("adel")
                    controlSet.add("adfab")
                    controlSet.add("adfak")
                    controlSet.add("ak")
                    controlSet.add("b")
                    toRemove = "ak"
                }

                //Тесты для правильного определения currentIndex (функция getCurrentIndex())
                5 -> {
                    controlSet.add("adbdac")
                    controlSet.add("ecgbdacdaab")
                    controlSet.add("gddgga")
                    controlSet.add("abgab")
                    controlSet.add("dhfehgd")
                    controlSet.add("geaf")
                    controlSet.add("fgefcgfah")
                    controlSet.add("aagebegehgd")
                    controlSet.add("gedabhceebffce")
                    controlSet.add("fdgebfa")
                    controlSet.add("g")
                    controlSet.add("cdbb")
                    controlSet.add("hh")
                    controlSet.add("hbghggfhb")
                    controlSet.add("ggebhea")
                    toRemove = "geaf"
                }
                6 -> {
                    controlSet.add("aa")
                    controlSet.add("af")
                    controlSet.add("cf")
                    controlSet.add("ch")
                    controlSet.add("ehad")
                    controlSet.add("e")
                    controlSet.add("edf")
                    controlSet.add("eaedece")
                    controlSet.add("fh")
                    controlSet.add("gd")
                    controlSet.add("gea")
                    controlSet.add("geg")
                    controlSet.add("gf")
                    controlSet.add("gh")
                    controlSet.add("hg")
                    toRemove = "eaedece"
                }
                7 -> {
                    controlSet.add("a")
                    controlSet.add("hgfa")
                    controlSet.add("ae")
                    controlSet.add("d")
                    controlSet.add("fheg")
                    controlSet.add("e")
                    controlSet.add("bdf")
                    controlSet.add("b")
                    controlSet.add("hbec")
                    controlSet.add("acc")
                    controlSet.add("ffg")
                    controlSet.add("ce")
                    controlSet.add("c")
                    toRemove = "a"
                }
                8 -> {
                    controlSet.add("bgga")
                    controlSet.add("ff")
                    controlSet.add("bb")
                    controlSet.add("cefg")
                    controlSet.add("f")
                    controlSet.add("hdf")
                    controlSet.add("hd")
                    controlSet.add("cgf")
                    controlSet.add("abfc")
                    controlSet.add("cd")
                    controlSet.add("efb")
                    controlSet.add("g")
                    controlSet.add("a")
                    controlSet.add("ccb")
                    controlSet.add("gb")
                    toRemove = "cd"
                }

                //Тест для удаления первого и последнего потомка root
                9 -> {
                    controlSet.add("a")
                    controlSet.add("b")
                    controlSet.add("c")
                    toRemove = "a"
                }
                10 -> {
                    controlSet.add("a")
                    controlSet.add("b")
                    controlSet.add("c")
                    toRemove = "c"
                }

                //Тест с одним потомком root
                11 -> {
                    controlSet.add("ab")
                    toRemove = "ab"
                }

                //Тест для правильного удаления внутри центрального потомка root
                12 -> {
                    controlSet.add("a")
                    controlSet.add("ba")
                    controlSet.add("b")
                    controlSet.add("bc")
                    controlSet.add("c")
                    toRemove = "ba"
                }
                13 -> {
                    controlSet.add("a")
                    controlSet.add("ba")
                    controlSet.add("b")
                    controlSet.add("bc")
                    controlSet.add("c")
                    toRemove = "b"
                }
                14 -> {
                    controlSet.add("a")
                    controlSet.add("ba")
                    controlSet.add("b")
                    controlSet.add("bc")
                    controlSet.add("c")
                    toRemove = "bc"
                }
                15 -> {
                    controlSet.add("a")
                    controlSet.add("bc")
                    controlSet.add("bda")
                    controlSet.add("cad")
                    toRemove = "bda"
                }

                //Тесты для терминальных вершин
                16 -> {
                    controlSet.add("ab")
                    controlSet.add("a")
                    controlSet.add("b")
                    toRemove = "ab"
                }
                17 -> {
                    controlSet.add("abc")
                    controlSet.add("ab")
                    controlSet.add("a")
                    controlSet.add("b")
                    toRemove = "abc"
                }
                18 -> {
                    controlSet.add("abc")
                    controlSet.add("ab")
                    controlSet.add("a")
                    controlSet.add("b")
                    toRemove = "ab"
                }
                19 -> {
                    controlSet.add("abc")
                    controlSet.add("ab")
                    controlSet.add("a")
                    controlSet.add("b")
                    toRemove = "a"
                }
                20 -> {
                    controlSet.add("abc")
                    controlSet.add("ab")
                    controlSet.add("abk")
                    controlSet.add("a")
                    controlSet.add("b")
                    toRemove = "ab"
                }
                21 -> {
                    controlSet.add("abc")
                    controlSet.add("ab")
                    controlSet.add("abk")
                    controlSet.add("a")
                    controlSet.add("b")
                    toRemove = "abk"
                }
                else -> {
                    controlSet.add("abc")
                    controlSet.add("ab")
                    controlSet.add("abk")
                    controlSet.add("a")
                    controlSet.add("b")
                    toRemove = "a"
                }
            }

            val trieSet = create()
            for (element in controlSet) {
                trieSet += element
            }
            controlSet.remove(toRemove)
            val iterator = trieSet.iterator()
            assertFailsWith<IllegalStateException>("Something was supposedly deleted before the iteration started") {
                iterator.remove()
            }
            var counter = trieSet.size
            while (iterator.hasNext()) {
                val element = iterator.next()
                counter--
                if (element == toRemove) {
                    iterator.remove()
                    assertFailsWith<IllegalStateException>("Trie.remove() was successfully called twice in a row.") {
                        iterator.remove()
                    }
                }
            }
            assertEquals(
                0, counter,
                "TrieIterator.remove() changed iterator position: ${abs(counter)} elements were ${if (counter > 0) "skipped" else "revisited"}."
            )
            assertEquals(
                controlSet.size, trieSet.size,
                "The size of the set is incorrect: was ${trieSet.size}, should've been ${controlSet.size}."
            )
            for (element in controlSet) {
                assertTrue(
                    trieSet.contains(element),
                    "Trie set doesn't have the element $element from the control set."
                )
            }
            for (element in trieSet) {
                assertTrue(
                    controlSet.contains(element),
                    "Trie set has the element $element that is not in control set."
                )
            }
            println("All clear!")
        }

        implementationTest { create().iterator().remove() }
        val random = Random()
        for (iteration in 1..100) {
            val controlSet = mutableSetOf<String>()
            val removeIndex = random.nextInt(15) + 1
            var toRemove = ""
            for (i in 1..15) {
                val string = random.nextString("abcdefgh", 1, 15)
                controlSet.add(string)
                if (i == removeIndex) {
                    toRemove = string
                }
            }
            println("Initial set: $controlSet")
            val trieSet = create()
            for (element in controlSet) {
                trieSet += element
            }
            controlSet.remove(toRemove)
            println("Control set: $controlSet")
            println("Removing element \"$toRemove\" from trie set through the iterator...")
            val iterator = trieSet.iterator()
            assertFailsWith<IllegalStateException>("Something was supposedly deleted before the iteration started") {
                iterator.remove()
            }
            var counter = trieSet.size
            while (iterator.hasNext()) {
                val element = iterator.next()
                counter--
                if (element == toRemove) {
                    iterator.remove()
                    assertFailsWith<IllegalStateException>("Trie.remove() was successfully called twice in a row.") {
                        iterator.remove()
                    }
                }
            }
            assertEquals(
                0, counter,
                "TrieIterator.remove() changed iterator position: ${abs(counter)} elements were ${if (counter > 0) "skipped" else "revisited"}."
            )
            assertEquals(
                controlSet.size, trieSet.size,
                "The size of the set is incorrect: was ${trieSet.size}, should've been ${controlSet.size}."
            )
            for (element in controlSet) {
                assertTrue(
                    trieSet.contains(element),
                    "Trie set doesn't have the element $element from the control set."
                )
            }
            for (element in trieSet) {
                assertTrue(
                    controlSet.contains(element),
                    "Trie set has the element $element that is not in control set."
                )
            }
            println("All clear!")
        }
    }

}
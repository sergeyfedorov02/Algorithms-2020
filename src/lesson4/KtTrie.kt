package lesson4

/**
 * Префиксное дерево для строк
 */
class KtTrie : AbstractMutableSet<String>(), MutableSet<String> {

    private class Node {
        val children: MutableMap<Char, Node> = linkedMapOf()
    }

    private var root = Node()

    override var size: Int = 0
        private set

    override fun clear() {
        root.children.clear()
        size = 0
    }

    private fun String.withZero() = this + 0.toChar()

    private fun findNode(element: String): Node? {
        var current = root
        for (char in element) {
            current = current.children[char] ?: return null
        }
        return current
    }

    override fun contains(element: String): Boolean =
        findNode(element.withZero()) != null

    override fun add(element: String): Boolean {
        var current = root
        var modified = false
        for (char in element.withZero()) {
            val child = current.children[char]
            if (child != null) {
                current = child
            } else {
                modified = true
                val newChild = Node()
                current.children[char] = newChild
                current = newChild
            }
        }
        if (modified) {
            size++
        }
        return modified
    }

    override fun remove(element: String): Boolean {
        val current = findNode(element) ?: return false
        if (current.children.remove(0.toChar()) != null) {
            size--
            return true
        }
        return false
    }

    /**
     * Итератор для префиксного дерева
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Сложная
     */
    override fun iterator(): MutableIterator<String> =
        KtTrie()

    data class PairOfCharToNumber(val char: Char, var number: Int)

    inner class KtTrie internal constructor() : MutableIterator<String> {

        /* Для решения данной задачи я ввел пару функций, которые будут искать следующее слово - getNextWord,
        первое слово - getFirstWord у определенного потомка от root и также последнее - getLastWord.

        Также были введены переменные, listOfCharsToNumberOfChild - список, который хранит вершину (ее чар) и
        на каком потомке от этой вершины мы сейчас находимся.

        numberOfRootChild - номер потомка, которого мы сейчас рассматриваем.
        currentIndex - это индекс определенной вершины в listOfCharsToNumberOfChild, number которой надо уменьшить, тк произошло удаление.

        Для реализации Iterator.remove() я ввел несколько доп функций:
        1) removeWord - функция удаления слова из дерева
        2) getCurrentIndex - для изменения переменной currentIndex
        3) thisIsTheLastWordOfThisChild - функция, которая определяет, являтеся ли currentWord - последним словом в каком-то потомке,
           она изменяет значение lastWordFlag.

        Пусть N - высота дерева
         */

        private var listOfCharsToNumberOfChild = mutableListOf<PairOfCharToNumber>()
        private var currentWord: String = ""
        private var removeFlag = false
        private var numberOfRootChild = 0
        private var currentIndex: Int = -1
        private var lastWordFlag = false

        //Функция для получения следующего слова
        private fun getNextWord(currentWord: String): String {

            //Если мы обошли всё дерево
            if (currentWord.isEmpty() && numberOfRootChild + 1 >= root.children.size)
                return ""

            //Если мы дошли до root, но ещё не рассмотрели всех его потомков
            if (currentWord.isEmpty()) {
                numberOfRootChild++
                return getFirstWord(numberOfRootChild)
            }

            //Ищем следующее слово в одном из потомков root (индекс которого == numberOfRootChild)
            val next = findNode(currentWord)

            val numberOfChild = listOfCharsToNumberOfChild.last().number
            var result: String

            if (numberOfChild < next!!.children.size) {
                listOfCharsToNumberOfChild.last().number++

                var nextChar = getChar(next, numberOfChild)
                var nextNode = getNode(next, numberOfChild)
                result = currentWord

                while (nextNode.children.isNotEmpty()) {
                    listOfCharsToNumberOfChild.add(PairOfCharToNumber(nextChar, 1))
                    result += nextChar
                    nextChar = getChar(nextNode, 0)
                    nextNode = getNode(nextNode, 0)
                }
                return result
            } else {
                var nextWord = ""
                listOfCharsToNumberOfChild = listOfCharsToNumberOfChild.dropLast(1).toMutableList()
                listOfCharsToNumberOfChild.forEach { nextWord += it.char }
                return getNextWord(nextWord)
            }
        }

        //Получение значения вершины(для удобства)
        private fun getChar(node: Node, number: Int): Char {
            return node.children.toList()[number].first
        }

        //Получение следующей вершины(для удобства)
        private fun getNode(node: Node, number: Int): Node {
            return node.children.toList()[number].second
        }

        //Дополнительная функция для получения первого слова от определенного(number) потомка root
        private fun getFirstWord(number: Int): String {

            //Если у root нет потомков
            if (root.children.isEmpty())
                return ""

            //Инициализация первых переменных
            var nextNode = getNode(root, number)
            var nextChar = getChar(root, number)
            var result = ""

            while (nextNode.children.isNotEmpty()) {
                listOfCharsToNumberOfChild.add(PairOfCharToNumber(nextChar, 1))
                result += nextChar
                nextChar = getChar(nextNode, 0)
                nextNode = getNode(nextNode, 0)
            }

            return result
        }

        //Дополнительная функция для получения самого последнего слова в данном потомке(number) от root
        private fun getLastWord(number: Int): String {

            if (root.children.isEmpty())
                return ""

            var nextNumberOfChild = number
            var nextNode = getNode(root, nextNumberOfChild)
            var nextChar = getChar(root, nextNumberOfChild)
            var result = ""

            while (nextNode.children.isNotEmpty()) {
                nextNumberOfChild = nextNode.children.size - 1
                result += nextChar
                nextChar = getChar(nextNode, nextNumberOfChild)
                nextNode = getNode(nextNode, nextNumberOfChild)
            }

            return result
        }

        /**
         * Проверка наличия следующего элемента
         *
         * Функция возвращает true, если итерация по множеству ещё не окончена (то есть, если вызов next() вернёт
         * следующий элемент множества, а не бросит исключение); иначе возвращает false.
         */

        /* Трудоемксоть:
                1) в худшем - O(N), когда будет вызов getLastWord и длина этого слова будет равна N
                2) в лучшем - O(1), когда не будет вызова getLastWord
           Ресурсоемкость - O(1)
               */

        override fun hasNext(): Boolean {
            if (root.children.isEmpty()) {
                return false
            }
            if (removeFlag) {
                return currentWord.isNotEmpty()
            }
            return currentWord != getLastWord(root.children.size - 1)
        }

        /**
         * Получение следующего элемента
         *
         * Функция возвращает следующий элемент множества.
         *
         * Бросает IllegalStateException, если все элементы уже были возвращены.
         */

        /* Трудоемксоть:
                1) Когда будет вызов getFirstWord - O(N),
                2) Когда будет вызов getNextWord,
                    а длина currentWord будет равна длине следующего слова, и равна N - O(N)
                3) в лучшем случае - O(1), когда removeFlag == true
           Ресурсоемкость - O(1)
               */

        override fun next(): String {
            if (removeFlag) {
                removeFlag = false
            } else {
                currentWord = if (currentWord.isEmpty()) {
                    getFirstWord(0)
                } else {
                    getNextWord(currentWord)
                }
            }

            if (currentWord.isEmpty())
                throw IllegalStateException()

            return currentWord
        }

        /**
         * Удаление предыдущего элемента
         *
         * Функция удаляет из множества элемент, возвращённый крайним вызовом функции next().
         *
         * Бросает IllegalStateException, если функция была вызвана до первого вызова next() или же была вызвана
         * более одного раза после любого вызова next().
         */

        //Доп функция для определения является ли currentWord - последним словом у данного потомка
        private fun thisIsTheLastWordOfThisChild(): Boolean {

            if (getLastWord(numberOfRootChild) == currentWord)
                return true

            var word = listOfCharsToNumberOfChild.fold("", { total, it -> total + it.char })
            var numberOfChildInThisBranch = 1

            for (i in listOfCharsToNumberOfChild.size - 1 downTo 0) {
                val node = findNode(word)

                if (numberOfChildInThisBranch != 1) {
                    return true
                }

                if (listOfCharsToNumberOfChild[i].number != node!!.children.size && node.children.isNotEmpty()) {
                    return false
                } else {
                    numberOfChildInThisBranch = node.children.size
                }

                word = word.dropLast(1)
            }

            return true
        }

        //Доп функция, которая возвращает currentIndex
        private fun getCurrentIndex(): Int {
            val word = listOfCharsToNumberOfChild.fold("", { total, it -> total + it.char })
            val minIndex = minOf(currentWord.length, word.length)

            var index = 0

            while (index < minIndex && currentWord[index] == word[index]) {
                index++
            }

            return index - 1
        }

        //Функция для удаления слова из дерева
        private fun removeWord(element: String): Boolean {

            remove(element)

            var currentElement = element
            var currentNode = findNode(currentElement) ?: return false

            //Удаление слова из дерева
            while (currentNode.children.isEmpty() && currentElement.isNotEmpty()) {
                val currentChar = currentElement.last()
                currentElement = currentElement.dropLast(1)
                currentNode = findNode(currentElement)!!
                currentNode.children.remove(currentChar)
            }

            //Если это не последнее слово в данной ветке, то надо изменить currentIndex
            if (!lastWordFlag) {
                currentIndex = getCurrentIndex()
            }

            return true
        }

        /* Трудоемксоть:
                1) в худшем - O(N), когда придется выполнить все операции после check
                2) в лучшем - O(1), когда функция дальше check не пойдет
           Ресурсоемкость - O(1)
               */

        override fun remove() {
            check(currentWord.isNotEmpty() && !removeFlag)

            val lastWordInThisChildOfRoot = getLastWord(numberOfRootChild) // O(N)
            val checkNumber = listOfCharsToNumberOfChild.filter { it.number > 1 }
            lastWordFlag = thisIsTheLastWordOfThisChild() // O(N)

            val nextWord = getNextWord(currentWord) // O(N)
            removeWord(currentWord) // O(N)
            removeFlag = true

            // Если мы полностью удаляем потомка от root -> глобальная переменная numberOfRootChild не должна поменяться(в getNextWord она поменялась)
            if (lastWordInThisChildOfRoot == currentWord && checkNumber.isEmpty()) {
                numberOfRootChild--
            }

            //Произошло удаление слова -> надо изменить состояние listOfCharsToNumberOfChild(в getNextWord оно поменялось)
            if (currentIndex != -1) {
                listOfCharsToNumberOfChild[currentIndex].number--
                currentIndex = -1
            }

            currentWord = nextWord
        }
    }

}
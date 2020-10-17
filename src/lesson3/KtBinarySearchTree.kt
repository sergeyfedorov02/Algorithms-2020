package lesson3

import java.util.*
import kotlin.math.max

// attention: Comparable is supported but Comparator is not
class KtBinarySearchTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private class Node<T>(
        val value: T
    ) {
        var left: Node<T>? = null
        var right: Node<T>? = null
    }

    private var root: Node<T>? = null

    override var size = 0
        private set

    private fun find(value: T): Node<T>? =
        root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    /**
     * Добавление элемента в дерево
     *
     * Если элемента нет в множестве, функция добавляет его в дерево и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     *
     * Спецификация: [java.util.Set.add] (Ctrl+Click по add)
     *
     * Пример
     */
    override fun add(element: T): Boolean {
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    /**
     * Удаление элемента из дерева
     *
     * Если элемент есть в множестве, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: [java.util.Set.remove] (Ctrl+Click по remove)
     * (в Котлине тип параметера изменён с Object на тип хранимых в дереве данных)
     *
     * Средняя
     */

    /* Для решения данной задачи используем алгоритм из книги
        "Алгоритмы: построение и анализ" от авторов: Т.Кормен, Ч.Лейзерсон, Р.Ривест
            глава 13 Двоичные деревья поиска, пункт 13.3 Добавление и удаление элемента(стр 242)

    Решение:
        Рассмотрим 3 случая
            1) У узла вообще нет потомков
            2) У узла есть только один потомок(левый или правый)
            3) У узла есть оба потомка

    Общая оценка трудоемкости и ресурсоемкости:
        Пусть N = значению высоты дерева, тогда
            1) Если у нас дерево не сбалансировано - худший случай, то
                Трудоемкость - O(N)
                Ресурсоемкость - O(1)

            2) Если дерево сбалансировано - лучший случай, то
                Трудоемкость - O(log(N))
                Ресурсоемкость - O(1)
*/


    /* Вспомогательная функция для получения Родителя узла

            Трудоемкость - O(N) - в худшем случае
            Трудоемкость - O(log(N)) - в лучшем случае
            Ресурсоемкость - O(1)
    */
    private fun getParent(node: Node<T>): Node<T>? {
        var parent: Node<T>? = null
        var child = root

        while (child != node) {
            val x = child!!.value.compareTo(node.value)

            if (x > 0) {
                parent = child
                child = child.left
            } else {
                parent = child
                child = child.right
            }
        }
        return parent
    }

    /* Данная функция требуется для реализации 3его случая, чтобы найти
       следующий по значению элемент без левого потомка,
       то есть при поиске будем брать правого потомка удаляемого узла и далее идти по его левой ветке

            Трудоемкость - O(N) - в худшем случае
            Трудоемкость - O(log(N)) - в лучшем случае
            Ресурсоемкость - O(1)
        */
    private fun findMinChildWithoutLeftChild(node: Node<T>): Node<T>? {

        //Если у Узла нет правого потомка, то выводим null, так как это будет относиться ко 2му случаю
        var min: Node<T>? = node.right ?: return null

        var result = min

        while (min!!.left != null) {
            min = min.left
            result = min

        }

        return result
    }

    /*
    Трудоемкость - O(N) - в худшем случае
    Трудоемкость - O(log(N)) - в лучшем случае
    Ресурсоемкость - O(1)
    */
    override fun remove(element: T): Boolean {

        val node = find(element)
        if (node == null || element != node.value) return false

        /* Дополнительная функция, чтобы после удаления элемента
           установить связь между его родителем и новым элементом на позиции удаленного узла

            Трудоемкость - O(N) - в худшем случае
            Трудоемкость - O(log(N)) - в лучшем случае
            Ресурсоемкость - O(1)
            */
        fun change(nodeToChange: Node<T>?) {
            val parent = getParent(node)

            if (parent == null) {
                root = nodeToChange
            } else {
                val result = parent.value.compareTo(element)

                if (result > 0) {
                    parent.left = nodeToChange
                } else if (result < 0) {
                    parent.right = nodeToChange
                }
            }
        }

        when {
            // Покрывает 1ый случай и частично 2ой(когда нет левого потомка)
            node.left == null -> change(node.right)

            // Покрывает 2ой случай (когда нет правого потомка)
            node.right == null -> change(node.left)

            // Покрывает 3ий случай (есть оба потомка)
            else -> {
                val y = findMinChildWithoutLeftChild(node)
                val parentY = getParent(y!!)

                // Если первый правый потомок подходит, то это 2ой случай + присоединение левого потомка удаляемого Узла
                if (parentY == node) {
                    y.left = node.left
                    change(node.right)
                } else {

                    //избавляемся от потомков Y (присоединяем их к родителю Y)
                    parentY!!.left = y.right

                    //Запоминаем потомков удаляемого узла и делаем их потомками нового узла (узла Y)
                    y.left = node.left
                    y.right = node.right
                    change(y)
                }
            }
        }

        size--
        return true
    }

    override fun comparator(): Comparator<in T>? =
        null

    override fun iterator(): MutableIterator<T> =
        BinarySearchTreeIterator()

    inner class BinarySearchTreeIterator internal constructor() : MutableIterator<T> {

        private var currentNode: Node<T>? = null
        private var removeFlag = false

        /*Дополнительная функция для поиска самого минимального элемента в дереве
            Трудоемкость:
                1)в худшем случае(когда дерево не сбалансировано) - O(N), где N - высота дерева
                2)в лучшем случае(когда дерево сбалансировано) - O(logN), где N - высота дерева
            Ресурсоемкость - O(1) */

        private fun getMinNode(): Node<T>? {
            if (root == null)
                return null

            var min: Node<T>? = root!!.left ?: return root
            var result = min

            while (min!!.left != null) {
                min = min.left
                result = min

            }
            return result
        }

        /* Дополнительная функция для поиска следующей вершины
            Трудоемкость:
                1)в худшем случае(когда дерево не сбалансировано) - O(N), где N - высота дерева
                2)в лучшем случае(когда дерево сбалансировано) - O(logN), где N - высота дерева */

        private fun getNextNode(currentNode: Node<T>?): Node<T>? {
            if (currentNode == null)
                return null

            if (currentNode.right != null) {
                return findMinChildWithoutLeftChild(currentNode)
            }

            var y = getParent(currentNode)
            var node = currentNode

            while (y != null && node == y.right) {
                node = y
                y = getParent(y)
            }
            return y
        }

        /**
         * Проверка наличия следующего элемента
         *
         * Функция возвращает true, если итерация по множеству ещё не окончена (то есть, если вызов next() вернёт
         * следующий элемент множества, а не бросит исключение); иначе возвращает false.
         *
         * Спецификация: [java.util.Iterator.hasNext] (Ctrl+Click по hasNext)
         *
         * Средняя
         */

        /* Трудоемксоть:
                1) в худшем - O(N)
                2) в лучшем - O(logN)
           Ресурсоемкость - O(1)
             */

        override fun hasNext(): Boolean {
            if (removeFlag) {
                return currentNode != null
            }
            if (currentNode == null) {
                return root != null
            }
            return getNextNode(currentNode) != null
        }

        /**
         * Получение следующего элемента
         *
         * Функция возвращает следующий элемент множества.
         * Так как BinarySearchTree реализует интерфейс SortedSet, последовательные
         * вызовы next() должны возвращать элементы в порядке возрастания.
         *
         * Бросает NoSuchElementException, если все элементы уже были возвращены.
         *
         * Спецификация: [java.util.Iterator.next] (Ctrl+Click по next)
         *
         * Средняя
         */

        /* Трудоемксоть:
                1) в худшем - O(N)
                2) в лучшем - O(logN)
            Ресурсоемкость - O(1)
               */

        override fun next(): T {
            if (removeFlag) {
                removeFlag = false
            } else {
                currentNode = if (currentNode == null) {
                    getMinNode()
                } else {
                    getNextNode(currentNode)
                }
            }

            if (currentNode == null)
                throw IllegalStateException()

            return currentNode!!.value
        }

        /**
         * Удаление предыдущего элемента
         *
         * Функция удаляет из множества элемент, возвращённый крайним вызовом функции next().
         *
         * Бросает IllegalStateException, если функция была вызвана до первого вызова next() или же была вызвана
         * более одного раза после любого вызова next().
         *
         * Спецификация: [java.util.Iterator.remove] (Ctrl+Click по remove)
         *
         * Сложная
         */

        /* Трудоемксоть:
                1) в худшем - O(N)
                2) в лучшем - O(logN)
           Ресурсоемкость - O(1)
               */

        override fun remove() {
            check(currentNode != null && !removeFlag)

            val nextNode = getNextNode(currentNode)
            this@KtBinarySearchTree.remove(currentNode!!.value)

            removeFlag = true
            currentNode = nextNode
        }

    }

    /**
     * Подмножество всех элементов в диапазоне [fromElement, toElement)
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева, которые
     * больше или равны fromElement и строго меньше toElement.
     * При равенстве fromElement и toElement возвращается пустое множество.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.subSet] (Ctrl+Click по subSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Очень сложная (в том случае, если спецификация реализуется в полном объёме)
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Подмножество всех элементов строго меньше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева строго меньше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.headSet] (Ctrl+Click по headSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun headSet(toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Подмножество всех элементов нестрого больше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева нестрого больше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.tailSet] (Ctrl+Click по tailSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun tailSet(fromElement: T): SortedSet<T> {
        TODO()
    }

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }

    override fun height(): Int =
        height(root)

    private fun height(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }

    override fun checkInvariant(): Boolean =
        root?.let { checkInvariant(it) } ?: true

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

}
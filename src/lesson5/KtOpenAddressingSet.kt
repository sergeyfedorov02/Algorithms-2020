package lesson5

/**
 * Множество(таблица) с открытой адресацией на 2^bits элементов без возможности роста.
 */
class KtOpenAddressingSet<T : Any>(private val bits: Int) : AbstractMutableSet<T>() {
    init {
        require(bits in 2..31)
    }

    private val capacity = 1 shl bits

    private val storage = Array<Any?>(capacity) { null }

    override var size: Int = 0

    /**
     * Индекс в таблице, начиная с которого следует искать данный элемент
     */
    private fun T.startingIndex(): Int {
        return hashCode() and (0x7FFFFFFF shr (31 - bits))
    }

    /**
     * Проверка, входит ли данный элемент в таблицу
     */
    override fun contains(element: T): Boolean {
        var index = element.startingIndex()
        var current = storage[index]
        while (current != null) {
            if (current == element) {
                return true
            }
            index = (index + 1) % capacity
            current = storage[index]
        }
        return false
    }

    /**
     * Добавление элемента в таблицу.
     *
     * Не делает ничего и возвращает false, если такой же элемент уже есть в таблице.
     * В противном случае вставляет элемент в таблицу и возвращает true.
     *
     * Бросает исключение (IllegalStateException) в случае переполнения таблицы.
     * Обычно Set не предполагает ограничения на размер и подобных контрактов,
     * но в данном случае это было введено для упрощения кода.
     */
    override fun add(element: T): Boolean {
        val startingIndex = element.startingIndex()
        var index = startingIndex
        var current = storage[index]
        while (current != null) {
            if (current == element) {
                return false
            }
            index = (index + 1) % capacity
            check(index != startingIndex) { "Table is full" }
            current = storage[index]
        }
        storage[index] = element
        size++
        return true
    }

    /**
     * Удаление элемента из таблицы
     *
     * Если элемент есть в таблица, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: [java.util.Set.remove] (Ctrl+Click по remove)
     *
     * Средняя
     */

    /*Пусть N - размер таблицы

    Трудоемкость - O(N), в худшем случае, когда element находится в самом конце множества(самый последний индекс),
      а значение element.startingIndex() - самый первый индекс в этом множестве

    Ресурсоемкость - O(1)
    */
    override fun remove(element: T): Boolean {

        //Трудоемкость - O(1)
        if (!contains(element)) {
            return false
        }

        //Ресурсоемкость - O(1)
        var currentIndex = element.startingIndex()
        var nextIndex = currentIndex
        var nextElement = storage[nextIndex]

        //Ищем позицию удаляемого объекта
        //Трудоемкость - O(N), в худшем случае, который описан перед задачей
        while (nextElement != element) {
            nextIndex = (nextIndex + 1) % capacity
            nextElement = storage[nextIndex]
        }

        //Присваиваем currentIndex значение позиции удаляемого элемента
        currentIndex = nextIndex

        //Удаляем наш element
        storage[currentIndex] = null

        //Изначальная позиция element
        val firstPos = currentIndex

        //Переприсваиваем значение следующего элемента и его индекса
        nextIndex = (nextIndex + 1) % capacity
        nextElement = storage[nextIndex]

        //Трудоемкость - O(N), в худшем случае,
        // если удаляем самый первый объект, а все остальные объекты идут за ним по порядку
        //  так придется сделать полный круг
        while (nextIndex != firstPos) {

            if (nextElement == null) {
                break
            }

            val startIndex = (nextElement as T).startingIndex()

            //перенос вверх
            if (currentIndex < nextIndex) {

                //первое условие для переноса
                if (startIndex <= currentIndex) {

                    currentIndex = move(currentIndex, nextIndex)

                } else {
                    //второе условие для переноса (сдучай с загибанием)
                    if (startIndex > nextIndex && currentIndex < startIndex) {

                        currentIndex = move(currentIndex, nextIndex)

                    }
                }
            } else {
                //перенос вниз
                if (startIndex == currentIndex) {

                    currentIndex = move(currentIndex, nextIndex)

                } else if (startIndex in (nextIndex + 1)..currentIndex) {
                    currentIndex = move(currentIndex, nextIndex)
                }
            }

            //Ищем элемент от следующего индекса
            nextIndex = (nextIndex + 1) % capacity
            nextElement = storage[nextIndex]

        }

        size--
        return true
    }

    //Дополнительная функция, которая совершает перемещение двух элементов и возвращает новый currentIndex
    private fun move(currentIndex: Int, nextIndex: Int): Int {

        val nextElement = storage[nextIndex]
        //На позицию удаляемого ставим тот, который не на своем месте
        storage[currentIndex] = nextElement

        //Перемещаем удаляемый элемент на новую позицию
        storage[nextIndex] = null

        return nextIndex
    }

    /**
     * Создание итератора для обхода таблицы
     *
     * Не забываем, что итератор должен поддерживать функции next(), hasNext(),
     * и опционально функцию remove()
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Средняя (сложная, если поддержан и remove тоже)
     */

    /*
    Пусть N - размер таблицы
    Тогда для итератора:

        Трудоемкость - O(N), в худшем случае, которые описаны для каждого метода в их реализации:
                                1) При использовании next и hasNext будет использоваться updateNextIndex
                                2) При использовании Iterator.remove, будет использован remove

        Ресурсоемкость - O(N), в худшем случае, так как надо хранить listOfReviewedElements (подробнее написано ниже)
     */
    override fun iterator(): MutableIterator<T> =
        OpenAddressingSetIterator()

    inner class OpenAddressingSetIterator internal constructor() : MutableIterator<T> {

        private var nextIndex = 0
        private var currentIndex = -1

        //Если элемент уже был рассмотрен ранее, то этот флаг изменится, чтобы при вызове next правильно определить значение
        private var containsFlag = false

        //Если при удалении элемента на его позиции не оказался другой элемент -> этот флаг изменится
        private var notNullElementFlag = false

        //Флаг для проверки в Iterator.remove, чтобы не было вызвано удаление два раза подряд
        private var removeFlag = false

        /*Так как Iterator.remove использует remove, а там возможны перемещения элементов
          То может произойти ситуация, что ранее рассмотренный элемент переместиться вниз(ниже currentIndex) и будет рассмотрен дважды
          Для предотвращения таких ситуаций заведем список элементов, которые могут переместиться вниз

          Реусорсоемкость - O(N) в худшем случае, когда все у всех элементов startingIndex больше их текущего индекса

          */
        private val listOfReviewedElements = mutableSetOf<T>()

        init {
            updateNextIndex()
        }

        //Трудоемкоть - O(N), в худшем случае, когда бежим с нулевого индекса и до capacity

        //Функция для обновления следующего индекса во множестве
        private fun updateNextIndex() {

            //После первого изменения currentIndex у нас storage[nextIndex] будет != null -> следующий цикл с while не запуститься
            if (currentIndex != -1) {
                nextIndex++
            }

            while (nextIndex < capacity && storage[nextIndex] == null) {
                nextIndex++
            }

        }

        /**
         * Проверка наличия следующего элемента
         *
         * Функция возвращает true, если итерация по множеству ещё не окончена (то есть, если вызов next() вернёт
         * следующий элемент множества, а не бросит исключение); иначе возвращает false.
         */

        //Трудоемкость - O(N), в худшем случае, так как тут мы используем updateNextIndex
        // и может быть такая ситуация, что в таблице всего 1 элемент на нулевой позиции -> будем бежать до capacity
        override fun hasNext(): Boolean {

            //Если было удаление, то найдем index текущего элемента
            val index = if (notNullElementFlag) {
                currentIndex // если storage[currentIndex] != null
            } else nextIndex //В обратном случае -> возьмем следующий элемент

            //Проверка, был ли рассмотрен ранее этот элемент
            if (nextIndex < capacity && listOfReviewedElements.contains(storage[index])) {
                //Если removeFlag == true -> index = currentIndex, Но currentIndex != nextIndex,
                //тогда при вызове updateNextIndex() мы потеряем 1 элемент
                nextIndex = index
                updateNextIndex()
                currentIndex = nextIndex
                containsFlag = true
            }

            return nextIndex < capacity
        }

        /**
         * Получение следующего элемента
         *
         * Функция возвращает следующий элемент множества.
         *
         * Бросает IllegalStateException, если все элементы уже были возвращены.
         */

        /*
        Трудоемкость - O(N), так как используем updateNextIndex, подробнее случай описан в hasNext
         */
        override fun next(): T {

            if (nextIndex >= capacity) {
                throw IllegalStateException()
            }

            //Если элемент уже был рассмотрен -> в hasNext мы уже поменяли currentIndex
            if (containsFlag) {
                containsFlag = false

            } else {

                if (!notNullElementFlag) {
                    currentIndex = nextIndex
                } else {
                    nextIndex = currentIndex
                }
            }

            removeFlag = false
            notNullElementFlag = false
            val element = storage[currentIndex]
            updateNextIndex()

            //Если у элемента startingIndex > currentIndex -> он после вызова remove может переметситься вниз, добдавляем такой элемент
            if ((element as T).startingIndex() > currentIndex) {
                listOfReviewedElements.add(element)
            }

            return element
        }


        /**
         * Удаление предыдущего элемента
         *
         * Функция удаляет из множества элемент, возвращённый крайним вызовом функции next().
         *
         * Бросает IllegalStateException, если функция была вызвана до первого вызова next() или же была вызвана
         * более одного раза после любого вызова next().
         */

        //Трудоемксоть - O(N) в худшем случае, так как используем remove
        override fun remove() {

            check(currentIndex != -1 && !removeFlag)

            //Трудоемксоть - O(N), в худшем случае (случай описан в реализации remove)
            remove(storage[currentIndex]!!)

            removeFlag = true

            //Если удаляем последний элемент во множестве -> currentIndex не меняется, а в hasNext будет false
            if (nextIndex < capacity) {

                //Если при удалении элемента на его месте оказывается null-> меняем currentIndex
                if (storage[currentIndex] == null) {
                    currentIndex = nextIndex
                } else notNullElementFlag = true
            }

        }
    }
}
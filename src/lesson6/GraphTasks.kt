@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson6

/**
 * Эйлеров цикл.
 * Средняя
 *
 * Дан граф (получатель). Найти по нему любой Эйлеров цикл.
 * Если в графе нет Эйлеровых циклов, вернуть пустой список.
 * Соседние дуги в списке-результате должны быть инцидентны друг другу,
 * а первая дуга в списке инцидентна последней.
 * Длина списка, если он не пуст, должна быть равна количеству дуг в графе.
 * Веса дуг никак не учитываются.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Вариант ответа: A, E, J, K, D, C, H, G, B, C, I, F, B, A
 *
 * Справка: Эйлеров цикл -- это цикл, проходящий через все рёбра
 * связного графа ровно по одному разу
 */
fun Graph.findEulerLoop(): List<Graph.Edge> {
    TODO()
}

/**
 * Минимальное остовное дерево.
 * Средняя
 *
 * Дан связный граф (получатель). Найти по нему минимальное остовное дерево.
 * Если есть несколько минимальных остовных деревьев с одинаковым числом дуг,
 * вернуть любое из них. Веса дуг не учитывать.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ:
 *
 *      G    H
 *      |    |
 * A -- B -- C -- D
 * |    |    |
 * E    F    I
 * |
 * J ------------ K
 */
fun Graph.minimumSpanningTree(): Graph {
    TODO()
}

/**
 * Максимальное независимое множество вершин в графе без циклов.
 * Сложная
 *
 * Дан граф без циклов (получатель), например
 *
 *      G -- H -- J
 *      |
 * A -- B -- D
 * |         |
 * C -- F    I
 * |
 * E
 *
 * Найти в нём самое большое независимое множество вершин и вернуть его.
 * Никакая пара вершин в независимом множестве не должна быть связана ребром.
 *
 * Если самых больших множеств несколько, приоритет имеет то из них,
 * в котором вершины расположены раньше во множестве this.vertices (начиная с первых).
 *
 * В данном случае ответ (A, E, F, D, G, J)
 *
 * Если на входе граф с циклами, бросить IllegalArgumentException
 *
 * Эта задача может быть зачтена за пятый и шестой урок одновременно
 */

//Дополнительный класс для поиска цикла в графе
private class CheckGraph(private val graph: Graph) {

    private var listOfNodes = mutableListOf<Graph.Vertex>()
    var checkFlag: Boolean = false

    //Проверка наличия цикла в графе начиная с вершины - node
    fun containsCycle(node: Graph.Vertex) {

        //Проверка на наличие цикла
        if (listOfNodes.size > 1) {
            val checkList = graph.getNeighbors(node)
            checkList.remove(listOfNodes.last())

            val filterCheckList = checkList.filter { !listOfNodes.contains(it) }

            if (checkList.size != filterCheckList.size) {
                checkFlag = true
                return
            }
        }

        listOfNodes.add(node)

        //Переход к следующему потомку
        graph.getNeighbors(node).forEach {
            if (!listOfNodes.contains(it)) {
                containsCycle(it)
            }
        }
        listOfNodes.removeAt(listOfNodes.size - 1)
    }
}


//Функция для поиска максимального независимого множества вершин, начиная с node
private fun Graph.findSet(node: Graph.Vertex): Set<Graph.Vertex> {

    val result = hashSetOf(node)
    val curVertexes = hashSetOf<Graph.Vertex>()
    curVertexes.addAll(vertices)
    curVertexes.removeAll(getNeighbors(node) + node)

    while (curVertexes.isNotEmpty()) {

        val element = curVertexes.first()
        result.add(element)
        val neighbors = getNeighbors(element)
        curVertexes.removeAll(neighbors + element)
    }

    return result
}

/*
Пусть N - количество вершин в графе
Трудоемкость - O(N^2)
Ресурсоемкость - O(N)
 */
fun Graph.largestIndependentVertexSet(): Set<Graph.Vertex> {

    //Если граф пуст, то выводим пустой set
    if (vertices.isEmpty())
        return setOf()

    //Проверка графа на ацикличность
    //Трудоемкость - O(N)
    val checkGraph = CheckGraph(this)
    checkGraph.containsCycle(vertices.first())
    check(!checkGraph.checkFlag)


    //Поиск максимального независимого множества вершин, начиная с node
    //Трудоемкость - O(N^2), тк в forEach будем пробегаться по каждой вершине - O(N),
    //а для каждой вершины будем бежать по оставшимся вершинам - O(N) будет в худшем случае, когда ни одна вершина не имеет связей вообще

    var result: Set<Graph.Vertex> = setOf()

    vertices.forEach {
        val answer = findSet(it)
        if (answer.size > result.size)
            result = answer
    }

    return result
}

/**
 * Наидлиннейший простой путь.
 * Сложная
 *
 * Дан граф (получатель). Найти в нём простой путь, включающий максимальное количество рёбер.
 * Простым считается путь, вершины в котором не повторяются.
 * Если таких путей несколько, вернуть любой из них.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ: A, E, J, K, D, C, H, G, B, F, I
 */

//Дополнительный класс для поиска максимального пути начиная с определенной вершины
private class PathSearch(private val graph: Graph) {

    private var currentPath = mutableListOf<Graph.Vertex>()
    private var longestPath = Path()

    //Создание Path из списка вершин
    private fun getPath(list: List<Graph.Vertex>): Path {

        val result = Path(list.first())
        return list.drop(1).fold(result, { total, it -> Path(total, graph, it) })
    }

    // Обход графа начиная с node
    private fun visit(node: Graph.Vertex) {

        currentPath.add(node)

        graph.getNeighbors(node).forEach {
            if (!currentPath.contains(it)) {
                visit(it)
            }
        }

        if (currentPath.size - 1 > longestPath.length) {
            longestPath = getPath(currentPath)
        }

        currentPath.removeAt(currentPath.size - 1)
    }

    //Поиск максимального пути в графе с указанного node
    fun search(node: Graph.Vertex): Path {
        currentPath.clear()
        longestPath = Path()
        visit(node)
        return longestPath
    }

}

/*
Пусть N - количество вершин в графе
Трудоемкость - O(N^2)
Ресурсоемкость - O(N)
 */
fun Graph.longestSimplePath(): Path {

    var result = Path()

    //Трудоемкость O(N) - данная трудоемкость относится именно к строке forEach (тут мы пробегаемся по всем вершинам графа)

    //А внутри forEach из-за использования класса PathSearch,
    //общая трудоемкость станет O (N^2) - (тут уже от определенной вершины мы бежим по ребрам к следующим вершинам)
    vertices.forEach {

        // использование класса, трудоемкость которого - O(N)
        val pathSearch = PathSearch(this)
        val longestFromVertex = pathSearch.search(it)
        if (longestFromVertex.length > result.length) {
            result = longestFromVertex
        }
    }

    return result
}

/**
 * Балда
 * Сложная
 *
 * Задача хоть и не использует граф напрямую, но решение базируется на тех же алгоритмах -
 * поэтому задача присутствует в этом разделе
 *
 * В файле с именем inputName задана матрица из букв в следующем формате
 * (отдельные буквы в ряду разделены пробелами):
 *
 * И Т Ы Н
 * К Р А Н
 * А К В А
 *
 * В аргументе words содержится множество слов для поиска, например,
 * ТРАВА, КРАН, АКВА, НАРТЫ, РАК.
 *
 * Попытаться найти каждое из слов в матрице букв, используя правила игры БАЛДА,
 * и вернуть множество найденных слов. В данном случае:
 * ТРАВА, КРАН, АКВА, НАРТЫ
 *
 * И т Ы Н     И т ы Н
 * К р а Н     К р а н
 * А К в а     А К В А
 *
 * Все слова и буквы -- русские или английские, прописные.
 * В файле буквы разделены пробелами, строки -- переносами строк.
 * Остальные символы ни в файле, ни в словах не допускаются.
 */
fun baldaSearcher(inputName: String, words: Set<String>): Set<String> {
    TODO()
}
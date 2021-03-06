@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson8

import lesson6.Graph
import lesson6.Path
import lesson7.knapsack.Fill
import lesson7.knapsack.Item
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.pow

// Примечание: в этом уроке достаточно решить одну задачу

/**
 * Решить задачу о ранце (см. урок 6) любым эвристическим методом
 *
 * Очень сложная
 *
 * load - общая вместимость ранца, items - список предметов
 *
 * Используйте parameters для передачи дополнительных параметров алгоритма
 * (не забудьте изменить тесты так, чтобы они передавали эти параметры)
 */
fun fillKnapsackHeuristics(load: Int, items: List<Item>, vararg parameters: Any): Fill {
    TODO()
}

/**
 * Решить задачу коммивояжёра (см. урок 5) методом колонии муравьёв
 * или любым другим эвристическим методом, кроме генетического и имитации отжига
 * (этими двумя методами задача уже решена в под-пакетах annealing & genetic).
 *
 * Очень сложная
 *
 * Граф передаётся через получатель метода
 *
 * Используйте parameters для передачи дополнительных параметров алгоритма
 * (не забудьте изменить тесты так, чтобы они передавали эти параметры)
 */

private class Ant(
    private val graph: Graph,
    private val firstNode: Graph.Vertex,
    private val pheromonesMap: MutableMap<Graph.Edge, Double>,

    // значения alpha и beta могут быть любыми, но должно выполняться равенство:
    // alpha + beta = 1.0

    // параметр, контролирующий влияние феромонов на выбор ребра
    private val alpha: Double = 2.0,
    // параметр, контролирующий влияние привлекательности ребра (1/weight, где weight - вес ребра)
    private val beta: Double = -1.0
) {

    private var currentNode = firstNode
    //Список посещенных вершин
    private val visitedNodes: MutableList<Graph.Vertex> = mutableListOf()

    //Список посещенных вершин(необходимо для renewalPheromoneMap в классе Runner)
    val visitedEdges: MutableList<Graph.Edge> = mutableListOf()

    //Функция, которая по формуле будет выбирать следующую вершину для посещения
    private fun getNextNode(): Graph.Vertex? {

        //применение формулы и расчет привлекательности nextNode
        //Трудоемкость - O(N), если граф полносвязный
        fun nodeSelectionProbability(nextNode: Graph.Vertex): Double {

            //Доп функция для вычисления произведения количества феромонов * привлекательность ребра (числитель формулы)
            fun getNumerator(edge: Graph.Edge): Double {

                //Получение значения феромона из pheromonesMap
                val numberOfPheromones = pheromonesMap.getOrDefault(edge, 0.0)

                return numberOfPheromones.pow(alpha) + 1.0 / edge.weight.toDouble().pow(beta)
            }

            //Вычисление числителя
            val numerator = getNumerator(graph.getConnection(currentNode, nextNode)!!)

            //Вычисление знаменателя
            val denominator =
                graph.getNeighbors(currentNode).map { graph.getConnection(currentNode, it)!! }.sumByDouble {
                    getNumerator(it)
                }

            return numerator / (denominator + numerator)
        }

        //Выбор следующей вершины
        //Трудоемкость - O(N), в худшем случае, когда граф полносвязный
        var random = ThreadLocalRandom.current().nextDouble(0.0, 1.0)
        val unvisitedNeighbors =
            graph.getNeighbors(currentNode).filter { it !in visitedNodes }

        if (unvisitedNeighbors.isEmpty()) {
            return null
        }

        //Трудоемкость - O(N), в худшем случае, когда граф полносвязный и visitedNodes пуст
        for (i in 0 until unvisitedNeighbors.size - 1) {

            val currentNeighbor = unvisitedNeighbors[i]
            random -= nodeSelectionProbability(currentNeighbor)

            if (random <= 0.0) {
                return currentNeighbor
            }

        }

        return unvisitedNeighbors.last()
    }

    //Создание Path из списка вершин
    private fun getPath(list: List<Graph.Vertex>): Path {

        val result = Path(list.first())
        return list.drop(1).fold(result, { total, it -> Path(total, graph, it) })
    }

    //Путешествие муравья по графу и поиск path
    //Трудоемкость - O(N^2)
    fun findPath(): Path? {

        //Трудоемкость - O(N^2)
        while (visitedNodes.size != graph.vertices.size - 1) {

            //если муравей попал в тупик
            //Трудоемкость - O(N)
            val nextNode = getNextNode() ?: return null

            visitedEdges.add(graph.getConnection(currentNode, nextNode)!!)
            visitedNodes.add(currentNode)
            currentNode = nextNode

        }

        return if (firstNode !in graph.getNeighbors(currentNode))
            null //Муравей не добрался до начальной вершины -> такой путь учитывать не надо
        else getPath(visitedNodes + currentNode + firstNode)
    }
}

//Класс для итерации по графу муравьями и изменение таблицы феромонов
class Runner(
    private val graph: Graph,

    //Контролируемый параметр количества обходов графа всеми муравьями,
    // где обход графа - это запуск муравья из каждой вершины графа
    private val size: Int = graph.vertices.size * 3,
    //Контролируемый параметр скорости испарения феромонов
    private val evaporationRate: Double = 0.5
) {

    //Ресурсоемкость - O(K)
    private val pheromonesMap = mutableMapOf<Graph.Edge, Double>()
    private var path: Path? = null

    //Трудоемкость - O(N^3 * M)
    fun run(): Path? {

        //Необходимое количество проходов по всем вершинам(подбирается вручную)
        //Трудоемкость - O(M)
        for (i in 1..size) {

            val setOfVertexes = hashSetOf<Graph.Vertex>()
            setOfVertexes.addAll(graph.vertices)

            //Запуск муравьев изо всех вершин графа по очереди
            //Трудоемкость - O(N^3)
            while (setOfVertexes.isNotEmpty()) {
                val currentNode = setOfVertexes.first()

                val ant = Ant(graph, currentNode, pheromonesMap)
                //Трудоемкость - O(N^2)
                val currentPath = ant.findPath()

                //Если муравей добрался до исходной вершины
                if (currentPath != null) {
                    //Обновляем таблицу феромонов
                    renewalPheromoneMap(ant.visitedEdges)

                    //Если данный муравей был первопроходцем, то сразу присвоим path значение пути этого муравья
                    if (path == null) {
                        path = currentPath
                    }
                    //Иначе, если пройденный им путь оказался короче, чем есть сейчас (значение path) -> меняем path
                    else if (currentPath.length < path!!.length) {
                        path = currentPath
                    }
                }

                setOfVertexes.remove(currentNode)
            }

            //Испарение феромонов
            pheromonesMap.mapValues { (1.0 - evaporationRate) * it.value }
        }

        return path
    }

    //Функция для обновления таблицы феромонов (pheromonesMap)
    //Трудоемкость - O(N)
    private fun renewalPheromoneMap(visitedEdges: MutableList<Graph.Edge>) {

        val weightOfTheDistanceTraveled = visitedEdges.sumBy { it.weight }
        visitedEdges.forEach {
            pheromonesMap[it] = pheromonesMap.getOrDefault(it, 0.0) + 1.0 / weightOfTheDistanceTraveled
        }
    }

}

/*
Ссылка на сайт с описанием алгоритма: https://habr.com/ru/post/105302/\

Пусть N - количество вершин
      M - значение параметра size в классе Runner (число проходов по всем вершинами)
      K - количество ребер

Трудоемкость - O(N^3 * M)
Ресурсоемкость - O(K), так как мы должны хранить pheromonesMap
 */
fun Graph.findVoyagingPathHeuristics(vararg parameters: Any): Path {

    val runner = Runner(this)

    //Проверка на наличие цикла Гамильтона
    //Если цикла нет -> программа вернет пустой путь,
    // так как ни один муравей не нашел пути обхода всего графа(не смог вернуться в исходную вершину)
    return runner.run() ?: return Path()
}

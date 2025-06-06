# Graph Analysis Application

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg)
![Gradle](https://img.shields.io/badge/Gradle-8.13-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-21-yellow.svg)
![License](https://img.shields.io/badge/License-GPLv3-blue.svg)
![Build](https://img.shields.io/github/actions/workflow/status/spbu-coding-2024/graphs-graphs-team-1/build.yml)
[![CodeFactor](https://www.codefactor.io/repository/github/spbu-coding-2024/graphs-graphs-team-1/badge/main)](https://www.codefactor.io/repository/github/spbu-coding-2024/graphs-graphs-team-1/overview/main)
### We present MVVM graph application designed to simplify user interaction with graphs and instruments to explore its properties.
![img.png](/resources/images/intro.png)

## Technologies

* Kotlin 1.9.20
* JUnit 5
* Gradle 8.13 
* Java 21
* Neo4j
* Jetpack Compose 1.5.10

## Get started

* To download project:
```
    git clone git@github.com:spbu-coding-2024/trees-trees-team-1.git
```
* To build project:
```
    ./gradlew build
```

* To run application:
```
    ./gradlew run
```

## Architecture
Application is based on MVVM pattern. So, there are three blocks:
* **model** - basic types of graphs, algorithms implementation and classes to load/save graphs
* **view model** - set of classes to communicate between user interface and basic classes from model
* **view** - set of classes to create gui

Application was created with idea, that basic classes from model can be used separately from gui application.

## Model
There are several types of graphs in our model:
* [Directed Graph](https://en.wikipedia.org/wiki/Directed_graph)
* [Undirected Graph](https://en.wikipedia.org/wiki/Graph_(discrete_mathematics))
* [Directed Weighted Graph](https://en.wikipedia.org/wiki/Graph_(discrete_mathematics)#Weighted_graph)
* [Undirected Weighted Graph](https://en.wikipedia.org/wiki/Graph_(discrete_mathematics)#Weighted_graph)
* Empty Graph

These types represents set of relations that can exist in specific graph: either edges in graph can
have "weight" - some label of numeric type (in our case - Int) and either there are mutual relation between vertices or one-way

To learn more about graphs see corresponding links

Vertex class represents nodes in graph. It stores two values:
* ``key`` - represents some label of set type, that might be used in algorithms
* ``value`` - some specific information that might be stored in node
  To differ various vertices in system links (or hashcode) are used.

## Control
There are several ways for user to interact with graph: through menu, keyboard and mouse (or touchpad)
![img.gif](/resources/images/menu.gif)
1. *Menu*
    * `Graph` - allows to choose graph type; also that is required action, user won`t be able 
   to perform any action until he/she/it specify object type; also that is a way to create new graph in current 
   window (be careful, current graph will be lost)
    * `Download` - allows user to add graph stored in internal format (see available variants in format section); 
   all nodes and edges from downloaded graph will be added to current graph
    * `Upload` - allows to save graph in some internal format (see available variants in format section)
    * `Algorithms` - allows to apply selected algorithm to current graph (see available variants in algorithms section);
   some algorithms might give unexpected results on some graph types: they are completely correct, but might be 
   incorrect for your problem
   * `Paint` - allows to modify graph in interactive mode: add/delete vertices/edges; these functions 
   works with vertices, selected by click; there are several modes available in edge addition depending on graph type:
   basic mode is sequential: selected sequence represents order in which edges are added, but also weighted graph 
   allow to set edge label, undirected graphs allow to connect all selected vertices
   * `Other` - secondary functions to ease user communications
   
2. Mouse
   * `Click on vertex` - add vertex to selected list (in corresponding order)
   * `Double-click on vertex` - open window with vertex data, where you can see key/value and modify them if necessary 
   * `Drag action on vertex` - change position of vertex (drag action)
   * `Drag action on empty space` - change position of all vertices (drag action)
3. Keyboard
   * `V` - add vertex
   * `E` - add edges
   * `R` - reset selected vertices
   * `Shift + E` - delete edges
   * `Shift + V` - delete vertices
   * `Ctrl + N` - select graph type/create new graph
   * `Ctrl + Z` - undo last element addition
   * `- (Minus)` - decrease graph (accommodate graph)
   * `+ (Plus)` - enlarge graph (focus on center of screen)
   * arrow keys to move graph right/left/up/down
   
Also `Tab` button allows to switch between textfields/buttons/radiobuttons etc.

## Algorithms

The application supports a set of classical graph algorithms, implemented directly in the `model` package and accessible through the user interface:

* [Dijkstra's algorithm](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm) — finds the shortest path in a weighted graph with **non-negative** edge weights (if you use an algorithm for a graph with negative edge weights, it may not work or work incorrectly).
    <details>

    <summary>Example image</summary>
  
    ![img.png](/resources/images/dijkstra.png)
    </details>  

* [Bellman-Ford algorithm](https://en.wikipedia.org/wiki/Bellman%E2%80%93Ford_algorithm) — computes shortest paths in graphs that may contain negative edge weights.
    <details>
    <summary>Example image</summary>

  ![img.png](/resources/images/fordbellman.png)
    </details>  
* [Cycle detection algorithm](https://en.wikipedia.org/wiki/Cycle_(graph_theory)#Algorithm) — identifies whether a cycle exists that includes a selected vertex.
    <details>
    <summary>Example image</summary>

  ![img.png](/resources/images/cycles.png)
    </details>  
  
* [Kosaraju's algorithm](https://en.wikipedia.org/wiki/Kosaraju%27s_algorithm) — detects and extracts strongly connected components in directed graphs.
  
    <details>
    <summary>Example image</summary>

  ![img.png](/resources/images/connectedComponents.png)
    </details>  
    

In addition to classical graph algorithms, the application includes built-in implementations of layout algorithms used for automatic graph visualization:

* [Force Atlas 2](https://github.com/gephi/gephi/wiki/Force-Atlas-2) — a force-directed layout algorithm adapted from the [Gephi](http://gephi.org/) platform, useful for positioning nodes based on repulsive and attractive forces.
    <details>
    <summary>Example image</summary>

  ![img.png](/resources/images/forceatlas2.gif)
    </details>  
* [Yifan-Hu layout](http://yifanhu.net/PUB/graph_draw_small.pdf) — an efficient graph drawing algorithm that balances speed and aesthetic quality, also inspired by [Gephi](http://gephi.org/).
    <details>
    <summary>Example image</summary>
    
    ![img.png](/resources/images/yuifanhu.gif)
    </details>


**License**: **GNU General Public License v3.0**  
**Compatibility**: Confirmed as GPLv3-compatible by [FSF](https://www.gnu.org/licenses/gpl-faq.en.html#AllCompatibility)


The application also supports algorithm for finding key vertices:

* [Harmonic Centrality](https://en.wikipedia.org/wiki/Centrality#Harmonic_centrality) —
  a measure of vertex influence implemented using [JGraphT](https://jgrapht.org/)'s optimized [algorithm](https://github.com/jgrapht/jgrapht/blob/master/jgrapht-core/src/main/java/org/jgrapht/alg/scoring/HarmonicCentrality.java) ([Javadoc](https://jgrapht.org/javadoc/org.jgrapht.core/org/jgrapht/alg/scoring/HarmonicCentrality.html)),
  calculating centrality as the sum of reciprocal shortest path distances to all other reachable nodes.
    <details>

    <summary>More about algorithm</summary>

    Provides two analysis modes:
    * `findTopKeyVertices(count: Int)` - Retrieves top N vertices by centrality score
    * `findVerticesWithMinCentrality(threshold: Double)` - Filters vertices by centrality threshold
       ![img.png](/resources/images/keyVerticesWindow.png)

    </details>

  
**License**: **LGPL-2.1-or-later** (selected from dual LGPL-2.1-or-later/EPL-2.0)  
**Compatibility**: Confirmed as GPLv3-compatible by [FSF](https://www.gnu.org/licenses/gpl-faq.en.html#AllCompatibility)











## Graph Formats

There are several formats to download/upload graph. Class `InternalFormatFactory` provides methods to convert graph 
of type `Graph` to one of formats available. Class `GraphFactory` on the other hand creates graph from internal format.
Formats available:
* JSON file 
* Neo4j database

***Be Careful! Only user defines which type of graph he wants to create from source. So, if there is weighted directed 
graph stored in neo4j database, but you choose to create undirected graph, you will get undirected graph. 
Application is not responsible for compliance between data and expected result***

There some important information, that needs to be known about graphs representations in corresponding formats:  
Any format that you might use have to define objects correctly in terms of our classes architecture.
It means that field, that specifies properties of classes `Vertex - value, key` and `Edge - weigth`
must be present in graph representation
* **Neo4j** - any graph from neo4j database will be suitable, because vertex id is used on downloading process; 
but there is a special attribute in vertex while uploading graph - special id (hash-code of object is used) 
that helps to identify nodes from each other
* **JSON** - see an examples of the file's structure in `resources/examples`


## Contributing

Contributions are welcome! There are multiple ways to make our application better. If you have an idea of a feature, 
eager to contribute or found bug:

1. Describe your idea/bug in issue
2. Wait for owners to reply with questions/approves/denials
3. If idea is accepted create new branch
4. Create pull request on complete and wait for owners to respond (you may want to mark them as reviewers to notify)

Before submitting, make sure:
- All tests pass 
- Code is formatted consistently

## Licence

[GNU GENERAL PUBLIC LICENSE Version 3](https://www.gnu.org/licenses/gpl-3.0.txt)

## Авторы проекта

* [Михаил Свирюков](https://github.com/MikhailSvirukov)
* [Дарья Нечаева](https://github.com/DaryaNechaeva)

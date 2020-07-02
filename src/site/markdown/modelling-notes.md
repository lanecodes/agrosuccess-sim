# AgroSuccess modelling notes

This is a document to store notes about model implementation which don't have
a natural home in academic writing about the model.

## Soil moisture calculation

As of June 2020 it was necessary to rework the soil moisture model to correct
a misunderstanding about the way runoff is represented in the Millington LFSM.
See equations specifying the soil moisture calculation in the PhD thesis
associlated with AgroSuccess (these build on those in Millington, 2009). Here
we explain the logic behind the corrected algorithm.

The total volume of water entering grid cell $i$ in each time step is total
annual precipitation _plus_ total runoff from all the cells which drain into
cell $i$. To do this calculation we need to ensure that the runoff and soil
moisture for the cells which drain into cell $i$ are evaluated before we
calculate runoff and soil moisture for cell $i$ itself. In the Millington LFSM
this is done by calculating the upslope area for every grid cell in advance
and providing this as model input. We instead use a flow network-based
approach.

A flow direction map is a spatially explicit representation of how water flows
over the landscape. Given a Digital Elevation Model (DEM) standard tools (e.g.
[TauDEM](https://hydrology.usu.edu/taudem/taudem5/index.html)) can be used to
infer the neighbouring cell which each grid cell drains into. By contrast a
flow source network is an aspatial representation of the same information which
emphasises the connectivity and direction of flow between cells, rather than
their spatial relationship to each other as a flow direction map does.
Formally a flow source network can be represented by a simple directed
tree-like graph. <a href="#fig-flow-drain-graph">Figure 1</a> shows a flow
direction map and the equivalent flow source network. An important feature of
the flow source network representation which makes it more useful than the flow
direction map for doing the soil moisture calculations in AgroSuccess is that
it makes the dependency relationship between each cell and those that drain
into them explicit. Cells which don't have any sources (i.e. nothing flows into
them) correspond to ridges in the represented landscape.

<figure id="fig-flow-drain-graph"> <img src="img/flow-drain-graph.svg"
  alt="Flow direction map and equivalent drainage graph." width="500">
  <figcaption>
  Figure 1. Illustration of how a flow direction map can be
  encoded as a graph. On the left we show a flow direction map in a Cartesian
  grid. On the right is the equivalent flow source network, in which an arrow
  points from cell $i$ to cell $j$ if cell $j$ drains into cell $i$. For
  example, cells (2, 1), (1, 0) and (2, 0) all drain into cell (1, 1).
  </figcaption>
</figure>

### Description of flow source network approach

Having constructed a flow source network such as that shown in <a
href="#fig-flow-drain-graph">Figure 1</a> we can use standard graph theory
algorithms to analyse its structure to both:

1. Determine an ordering of grid cells which ensures that no cell's soil
   moisture and runoff are calculated before the soil moisture and runoff of
   the cells which drain into it are calculated (if any).
2. Simplify code implementing the soil moisture and runoff calculations by
   using the fact that each grid cell in the graph has an explicit reference to
   all the other cells which drain into it.

To derive an ordering of grid cells which satisfies the requirement that the
cells draining into a cell must be evaluated before the cell itself, we use
breadth-first search (BFS) (Cormen et al. 1990) on the flow source
network. Starting from each of the outlets in the landscape, BFS traverses the
network such that cells which are i steps away from their outlet are reached
only when all the cells which are i-1 steps away from the outlet. The algorithm
stops which the graph's leaves (corresponding to ridges in the landscape)
because they have nothing draining into them (i.e. runoff = 0). An ordering
produced by this algorithm is called a BFS order. The reverse of this
ordering---reverse BFS order---guarantees cells are reached in decreasing order
of distance from the outlet, with cells which are equally distant ordered
arbitrarily.

We don't need to implement BFS because the work has already been done for us in
the [JGraphT](https://jgrapht.org) project. We can use a
[SimpleDirectedGraph](https://jgrapht.org/javadoc/org/jgrapht/graph/SimpleDirectedGraph.html)
instance with the
[BreadthFirstIterator](https://jgrapht.org/javadoc/org.jgrapht.core/org/jgrapht/traverse/BreadthFirstIterator.html#%3Cinit%3E(org.jgrapht.Graph,V))
to obtain a BFS ordering for each outlet. See tutorial on BFS [here](Tutorial
on breadth first search
https://www.freecodecamp.org/news/breadth-first-search-a-bfs-graph-traversal-guide-with-3-leetcodeexamples/)

Code can be simplified with respect to Millington LFSM by using a map as a
supporting data structure. Keys are `GridPoint` objects and values are runoff
values. Default value is null. Used to check that cell soil moisture not
evaluated until all inputs evaluated. As we iterate through the reverse BFS
ordered grid cells, we query the graph to determine which cells runoff into the
current cell, and then retrieve these already calculated values from the map
instance.

### Comparison of flow source network and upslope area approaches

Using reverse BFS ordering, grid cells which are furthest from the outlet have
their runoff and soil moisture evaluated first, before proceeding to evaluate
cells which they drain into (as well as any lower ridges), and so on. This
guarantees that no cell can have its soil moisture and runoff calculated before
the cells which drain into it have been evaluated. This is because if cell i
drains into cell j, cell j must be further from the outlet than cell i, and
according to reverse BFS ordering cell j would be evaluated before cell
i. Reverse BFS ordering will produce a different order compared to IUA ordering
because in IUA ordering all ridges are evaluated first, whereas in reverse BFS
ordering only the highest ridges are evaluated first. However, either ordering
is functionally equivalent for the purposes of calculating soil moisture and
runoff in AgroSuccess.

Looking at <a href="#fig-flow-drain-graph">Figure 1</a> we see that if soil moisture is evaluated in order of increasing upslope area, the following is a valid ordering:

| Grid cell | Evaluation order | Upslope area | Distance from sink |
| :-------- | ---------------: | -----------: | -----------------: |
|(2, 1)     | 1                | 0            | 3                  |
|(1, 0)     | 2                | 0            | 3                  |
|(2, 0)     | 3                | 0            | 3                  |
|(0, 0)     | 4                | 0            | 2                  |
|(2, 2)     | 5                | 0            | 2                  |
|(0, 1)     | 6                | 1            | 1                  |
|(1, 1)     | 7                | 3            | 2                  |
|(1, 2)     | 8                | 5            | 1                  |
|(0, 2)     | 9                | 8            | 0                  |


### Advantages of flow source network representation

#### Fewer data inputs

The approach to using increasing upslope area to determine the order in which
grid cells should be evaluated for runoff and soil moisture to provide
additional data specifying each cell's upslope area which must be calculated in
advance. By contrast the flow drain network approach requires only the flow
direction map as input. This simplifies testing as there is less input data to
mock.

#### Opportunities for parallelisation

Creates opportunities for parallelisation in cases where there are multiple
outlets in the simulated landscape. This is because every cell eventually
drains into only one outlet, so multiple outlets are independent of each other.


#### Reduced time complexity

Increasing upslope area approach requires sorting cells by upslope
area. Assuming the use of the quicksort algorithm, for example, this has time
complexity of $\mathcal{O}(n_x n_y \log(n_x n_y))$ where $n_x$ and $n_y$ are
the lengths of the simulation grid edges in the $x$ and $y$ directions
respectively.

By contrast the flow source network approach requires

- Search perimeter of grid for outlets $\mathcal{O}(n_x + n_y)$ ($\sqrt{n}$
  time where $n$ is number of grid cells).
- Construct flow source network from flow direction map $\mathcal{O}(n_x n_y)$
- Perform BFS $\mathcal{O}(V + E) = \mathcal{O}(n_x n_y)$ (each cell trains
  into one other cell except outlets)

Although flow source network approach requires three separate algorithms to
obtain the grid cell ordering their combined time complexity is linear in $n =
n_x n_y$. By contrast increasing upslope area approach runs on average in
linearithmic time in $n$. In any case we see that time complexity is not worse
for flow source network approach.

## References

Cormen, T. H., Leiserson, C. E., Rivest, R. L., & Stein,
C. (1990). Introduction to Algorithms (2nd ed.). MIT Press.

Millington, J. D. A., Wainwright, J., Perry, G. L. W., Romero-Calcerrada, R., &
Malamud, B. D. (2009). Modelling Mediterranean landscape succession-disturbance
dynamics: A landscape fire-succession model. Environmental Modelling and
Software, 24(10), 1196–1208. https://doi.org/10.1016/j.envsoft.2009.03.013

## Meta

Maven site plugin does not seem to be able to render HTML inside markdown despite this being part of the markdown [specification](https://daringfireball.net/projects/markdown/syntax#html):

> For any markup that is not covered by Markdown’s syntax, you simply use
> HTML itself. There’s no need to preface it or delimit it to indicate that
> you’re switching from Markdown to HTML; you just use the tags.

I think this might be a bug in maven-site-plugin, because pandoc can convert this page to html no problem.

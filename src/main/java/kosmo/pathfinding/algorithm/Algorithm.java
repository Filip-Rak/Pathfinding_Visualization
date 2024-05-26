package kosmo.pathfinding.algorithm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum Algorithm
{
    // Enums
    DIJKSTRA(
            Dijkstra.class,
            "Efficiently finds shortest paths from a source using a priority queue."
    ),

    A_STAR(
            AStar.class,
            "Enhances Dijkstra by using heuristics to predict distances, speeding up the survey."
    ),
    GFB(
            kosmo.pathfinding.algorithm.GFB.class,
            "Greedy First Best. Explores paths greedily based on promising leads, focusing on likely best first."
    ),
    DFS_RECURSIVE(
            DFSRecursive.class,
            "Depth First Search. Explores deepest paths first using recursion."
    ),
    DFS_ITERATIVE(
            DFSIterative.class,
            "Depth First Search. Deep path exploration using a stack, iteratively."
    );

    // Attributes
    private final Class<?> algorithmClass;
    private final String description;

    // Constructor
    Algorithm(Class<?> algorithmClass, String description)
    {
        this.algorithmClass = algorithmClass;
        this.description = description;
    }

    // Methods
    public Runnable createInstance(Object argument)
    {
        try
        {
            Constructor<?> constructor = algorithmClass.getConstructor(argument.getClass());
            return (Runnable) constructor.newInstance(argument);
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            throw new RuntimeException("Failed to instantiate algorithm", e);
        }
    }

    // Getters
    public String getDescription()
    {
        return description;
    }
}
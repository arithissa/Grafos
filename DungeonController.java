package graph;

import javax.swing.*;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class DungeonController
{
    private static final Logger LOGGER = Logger.getLogger("DungeonController.class");

    private static AbstractGraph dungeon;

    private Room entrance;
    private Room exit;

    private DungeonController()
    {
    }

    public static void main(String[] args)
    {
        DungeonController dungeonController = new DungeonController();
        createRandomDungeon(dungeonController);
        
        // Cria arestas
        DelaunayTriangulation.triangulateGraphVertices(dungeonController.dungeon);
        
        // Cria a arvore geradora minima com Prim
        PrimMSTTraversal prim = new PrimMSTTraversal(dungeonController.dungeon);
        prim.traverseGraph(dungeonController.dungeon.getVertices().get(0), null);
        
        // Pega a lista de vertices predecessores
        int predecessores[] = prim.getPredecessorArray();
        
        // Muda apenas as arestas da Dungeon
        for(int i = 1; i < predecessores.length; i++){
            createEdge(i, predecessores[i]);
        }

        // Faz a travessia com o BFT e imprime
        BreadthFirstTraversal bft = new BreadthFirstTraversal(dungeonController.dungeon);
        bft.traverseGraph(dungeonController.dungeon.getVertices().get(0), null);
        
    }
    
    // Cria uma aresta que liga o vertice de origem ao destino
    private static void createEdge(int origin, int destination){
        dungeon.addEdge(dungeon.getVertices().get(origin), dungeon.getVertices().get(destination));
    }
    

    private static void CreateDungeonGraphic(DungeonController dungeonController)
    {
        SwingUtilities.invokeLater(() -> new DungeonGraphic(dungeonController.dungeon, null).setVisible(true));
    }

    private static void CreateDungeonGraphic(DungeonController dungeonController, List<Vertex> traversalPath)
    {
        SwingUtilities.invokeLater(() -> new DungeonGraphic(dungeonController.dungeon, traversalPath).setVisible(true));
    }

    private static void createRandomDungeon(DungeonController dungeonController)
    {
        //System.out.println("What will be the random seed?");
        Scanner scanner = new Scanner(System.in);
        int seed = Integer.parseInt(scanner.nextLine());
        RandomSingleton.getInstance(seed);
        //System.out.println("How many rooms will the dungeon have?");
        int nRooms = Integer.parseInt(scanner.nextLine());
        RandomDungeonGenerator randomDungeonGenerator = new RandomDungeonGenerator(nRooms);
        dungeonController.dungeon = randomDungeonGenerator.getDungeon();

    }

    /*private static void ReplaceDungeonWithMST(DungeonController dungeonController)
    {
        AbstractGraph dungeon = dungeonController.dungeon;
        TraversalStrategyInterface traversalStrategy;
        traversalStrategy = new PrimMSTTraversal(dungeon);
        traversalStrategy.traverseGraph(dungeon.getVertices().get(0), null);
        dungeonController.dungeon = GraphConverter.predecessorListToGraph(dungeon, traversalStrategy.getPredecessorArray());
    }

    private static void setSpecialRooms(DungeonController dungeonController)
    {
        AbstractGraph dungeon = dungeonController.dungeon;
        TraversalStrategyInterface traversalStrategy = new FloydWarshallTraversal(dungeon);
        traversalStrategy.traverseGraph(dungeon.getVertices().get(0), null);
        Room center = (Room) dungeon.getCentermostVertex(((FloydWarshallTraversal)traversalStrategy).getDistanceMatrix());
        center.setCheckpoint(true);
        Room entrance = (Room) dungeon.getOuterMostVertex(((FloydWarshallTraversal)traversalStrategy).getDistanceMatrix());
        entrance.setEntrance(true);
        dungeonController.entrance = entrance;
        Room exit = (Room) dungeon.getMostDistantVertex(((FloydWarshallTraversal)traversalStrategy).getDistanceMatrix(), entrance);
        exit.setExit(true);
        dungeonController.exit = exit;
    }*/

    private static List<Vertex> getPathFromEntranceToExit(DungeonController dungeonController)
    {
        AbstractGraph dungeon = dungeonController.dungeon;
        TraversalStrategyInterface aStar = new AStartPathFind(dungeon);
        aStar.traverseGraph(dungeonController.entrance, dungeonController.exit);
        return aStar.getShortestPath(dungeonController.entrance, dungeonController.exit);
    }
}

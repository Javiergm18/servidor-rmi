package com.mycompany.servidor.rmi;

import mpi.*;
import java.util.*;
import java.io.*;

public class KMeansMPI {

    public static void main(String[] args) throws Exception {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();


        int numClusters = 3;
        int maxIterations = 10;
        double[][] centroids = null;


        if (rank == 0) {
            System.out.println("Nodo principal inicializando datos...");
            double[][] data = loadData("data.txt");
            centroids = initializeCentroids(data, numClusters);


            List<double[]>[] partitions = partitionData(data, size);
            for (int i = 1; i < size; i++) {
                MPI.COMM_WORLD.Send(partitions[i], 0, partitions[i].size(), MPI.OBJECT, i, 0);
            }

            System.out.println("Centroides iniciales:");
            printCentroids(centroids);
        } else {
            System.out.println("Nodo " + rank + " esperando datos...");
        }

        List<double[]> localData = new ArrayList<>();
        Object[] recvBuffer = new Object[1];
        MPI.COMM_WORLD.Recv(recvBuffer, 0, 1, MPI.OBJECT, 0, 0);
        localData = (List<double[]>) recvBuffer[0];

        for (int iter = 0; iter < maxIterations; iter++) {
            if (rank == 0) {
                for (int i = 1; i < size; i++) {
                    MPI.COMM_WORLD.Send(centroids, 0, centroids.length, MPI.OBJECT, i, 1);
                }
            } else {
                centroids = new double[numClusters][];
                MPI.COMM_WORLD.Recv(centroids, 0, numClusters, MPI.OBJECT, 0, 1);
            }

            int[] assignments = assignToClusters(localData, centroids);
            if (rank != 0) {
                MPI.COMM_WORLD.Send(assignments, 0, assignments.length, MPI.INT, 0, 2);
            } else {
                int[][] allAssignments = new int[size][];
                allAssignments[0] = assignments;
                for (int i = 1; i < size; i++) {
                    int[] recvAssignments = new int[localData.size()];
                    MPI.COMM_WORLD.Recv(recvAssignments, 0, recvAssignments.length, MPI.INT, i, 2);
                    allAssignments[i] = recvAssignments;
                }

                centroids = updateCentroids(data, allAssignments, numClusters);
                System.out.println("Iteración " + (iter + 1) + " completada. Nuevos centroides:");
                printCentroids(centroids);
            }
        }

        if (rank == 0) {
            System.out.println("K-means completado.");
        }

        MPI.Finalize();
    }



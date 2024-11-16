package com.mycompany.servidor.rmi;
import java.util.*;

public class KMeans {
    private int numClusters;
    private int maxIterations;

    public KMeans(int numClusters, int maxIterations) {
        this.numClusters = numClusters;
        this.maxIterations = maxIterations;
    }

    public Map<Integer, List<double[]>> clusterData(List<double[]> data) {
        Random random = new Random();


        List<double[]> centroids = new ArrayList<>();
        for (int i = 0; i < numClusters; i++) {
            centroids.add(data.get(random.nextInt(data.size())));
        }

        Map<Integer, List<double[]>> clusters = new HashMap<>();
        for (int iter = 0; iter < maxIterations; iter++) {
            clusters.clear();


            for (double[] point : data) {
                int closestCluster = findClosestCluster(point, centroids);
                clusters.computeIfAbsent(closestCluster, k -> new ArrayList<>()).add(point);
            }


            for (int i = 0; i < numClusters; i++) {
                List<double[]> clusterPoints = clusters.getOrDefault(i, new ArrayList<>());
                if (!clusterPoints.isEmpty()) {
                    centroids.set(i, calculateMean(clusterPoints));
                }
            }
        }
        return clusters;
    }

    private int findClosestCluster(double[] point, List<double[]> centroids) {
        double minDistance = Double.MAX_VALUE;
        int clusterIndex = -1;
        for (int i = 0; i < centroids.size(); i++) {
            double distance = calculateDistance(point, centroids.get(i));
            if (distance < minDistance) {
                minDistance = distance;
                clusterIndex = i;
            }
        }
        return clusterIndex;
    }

    private double calculateDistance(double[] point1, double[] point2) {
        double sum = 0.0;
        for (int i = 0; i < point1.length; i++) {
            sum += Math.pow(point1[i] - point2[i], 2);
        }
        return Math.sqrt(sum);
    }

    private double[] calculateMean(List<double[]> points) {
        int dimensions = points.get(0).length;
        double[] mean = new double[dimensions];
        for (double[] point : points) {
            for (int i = 0; i < dimensions; i++) {
                mean[i] += point[i];
            }
        }
        for (int i = 0; i < dimensions; i++) {
            mean[i] /= points.size();
        }
        return mean;
    }
}

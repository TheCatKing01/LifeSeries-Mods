package net.mat0u5.lifeseries.utils.other;
import java.util.Random;

public class WeightedRandomizer {
    private Random random;

    public WeightedRandomizer() {
        this.random = new Random();
    }

    public WeightedRandomizer(long seed) {
        this.random = new Random(seed);
    }

    public int getWeightedRandom(int minValue, int maxValue, int biasLevel, int maxBiasLevel, double biasStrength) {
        if (minValue > maxValue) {
            throw new IllegalArgumentException("minValue cannot be greater than maxValue");
        }
        if (biasLevel < 1 || biasLevel > maxBiasLevel) {
            throw new IllegalArgumentException("biasLevel must be between 1 and " + maxBiasLevel);
        }
        if (biasStrength <= 0) {
            throw new IllegalArgumentException("biasStrength must be positive");
        }

        int range = maxValue - minValue + 1;
        double[] weights = new double[range];

        // Calculate the target center based on bias level
        // biasLevel 1 = bias toward minValue, maxBiasLevel = bias toward maxValue
        double normalizedBias = (double)(biasLevel - 1) / (maxBiasLevel - 1); // 0.0 to 1.0
        double targetCenter = normalizedBias * (range - 1); // 0 to (range-1)

        // Create weights using exponential decay from target center
        for (int i = 0; i < range; i++) {
            double distance = Math.abs(i - targetCenter);
            weights[i] = Math.exp(-distance * (biasStrength/10)) + 0.05; // +0.05 ensures all values possible
        }

        // Select weighted random index and convert back to actual value
        int selectedIndex = weightedRandomSelect(weights);
        return minValue + selectedIndex;
    }

    public int getWeightedRandom(int minValue, int maxValue, int biasLevel, int maxBiasLevel) {
        return getWeightedRandom(minValue, maxValue, biasLevel, maxBiasLevel, 1.0);
    }

    private int weightedRandomSelect(double[] weights) {
        double totalWeight = 0;
        for (double weight : weights) {
            totalWeight += weight;
        }

        double randomValue = random.nextDouble() * totalWeight;
        double currentWeight = 0;

        for (int i = 0; i < weights.length; i++) {
            currentWeight += weights[i];
            if (randomValue <= currentWeight) {
                return i;
            }
        }

        return weights.length - 1; // Fallback
    }

    public void testDistribution(int min, int max, int minBias, int maxBias, double strength) {
        for (int bias = minBias; bias <= maxBias; bias++) {
            System.out.printf("\nBias Level %d (targeting %s):\n", bias,
                    bias == minBias ? "low values" :
                            bias == maxBias ? "high values" : "middle values");

            int[] counts = new int[max - min + 1];

            for (int i = 0; i < 500000; i++) {
                int result = getWeightedRandom(min, max, bias, maxBias, strength);
                counts[result - min]++;
            }

            int[] topIndices = new int[Math.min(10, counts.length)];
            for (int i = 0; i < topIndices.length; i++) {
                int maxIndex = 0;
                for (int j = 1; j < counts.length; j++) {
                    if (counts[j] > counts[maxIndex]) {
                        maxIndex = j;
                    }
                }
                topIndices[i] = maxIndex;
                System.out.printf("  %d: %.1f%% ", min + maxIndex, (counts[maxIndex] / 500000.0) * 100);
                counts[maxIndex] = -1;
            }
            System.out.println();
        }
    }
}
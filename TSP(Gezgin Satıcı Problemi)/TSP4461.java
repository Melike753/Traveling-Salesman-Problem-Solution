import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TSP4461 {

    static class City {
        int x, y;

        public City(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        ArrayList<City> cities = readCitiesFromFile("tsp_4461_1.txt");

        // TPORT yaklaşımı ile başlangıç çözümü oluştur
        ArrayList<City> solution = TPORT(cities);

        // Başlangıç çözümünü 2-Opt sezgiseli ile iyileştir
        long startTime = System.currentTimeMillis();
        twoOpt(solution);
        long endTime = System.currentTimeMillis();

        // En düşük maliyetli yolun maliyetini hesapla
        int cost = calculateCost(solution);

        // Yolu ve maliyetini yazdır
        System.out.println("En düşük maliyetli yol:");
        for (City city : solution) {
            System.out.print("(" + city.x + ", " + city.y + ") ");
        }
        System.out.println("\nMaliyet: " + cost);
        System.out.println("Çalışma Süresi: " + (endTime - startTime) + " milisaniye");

        // Sonuçları txt dosyasına yaz
        writeResultToFile("output4461.txt", solution, cost, endTime - startTime);
    }

    // Dosyadan şehir verilerini oku
    static ArrayList<City> readCitiesFromFile(String filename) {
        ArrayList<City> cities = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                if (lineNumber > 0) {
                    String[] parts = line.split(" ");
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    cities.add(new City(x, y));
                }
                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cities;
    }

    // TPORT yaklaşımı
    static ArrayList<City> TPORT(ArrayList<City> cities) {
        ArrayList<City> solution = new ArrayList<>(cities);
        solution.add(cities.get(0)); // Başlangıç şehrini sona ekle
        return solution;
    }

    // 2-Opt sezgiseli ile çözümü iyileştir
    static void twoOpt(ArrayList<City> solution) {
        int size = solution.size();
        boolean improved = true;
        while (improved) {
            improved = false;
            for (int i = 1; i < size - 2; i++) {
                for (int j = i + 1; j < size - 1; j++) {
                    int distBefore = distance(solution.get(i - 1), solution.get(i)) + 
                                     distance(solution.get(j), solution.get(j + 1));
                    int distAfter = distance(solution.get(i - 1), solution.get(j)) + 
                                    distance(solution.get(i), solution.get(j + 1));
                    if (distAfter < distBefore) {
                        reverse(solution, i, j);
                        improved = true;
                    }
                }
            }
        }
    }

    // İki şehir arasındaki mesafeyi hesapla
    static int distance(City city1, City city2) {
        return (int) Math.sqrt(Math.pow(city1.x - city2.x, 2) + Math.pow(city1.y - city2.y, 2));
    }

    // İki şehir arasındaki yolu tersine çevir
    static void reverse(ArrayList<City> solution, int i, int j) {
        while (i < j) {
            City temp = solution.get(i);
            solution.set(i, solution.get(j));
            solution.set(j, temp);
            i++;
            j--;
        }
    }

    // Yolun maliyetini hesapla
    static int calculateCost(ArrayList<City> solution) {
        int cost = 0;
        for (int i = 0; i < solution.size() - 1; i++) {
            cost += distance(solution.get(i), solution.get(i + 1));
        }
        return cost;
    }

    // Sonuçları txt dosyasına yaz
    static void writeResultToFile(String filename, ArrayList<City> solution, int cost, long duration) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Maliyeti dosyaya yaz
            writer.write("Maliyet: " + cost);
            writer.newLine();

            // Çalışma süresini dosyaya yaz
            writer.write("Çalışma Süresi: " + duration + " milisaniye");
            writer.newLine();

            // TSP yolunu dosyaya yaz
            writer.write("TSP Yolu:");
            writer.newLine();
            for (City city : solution) {
                writer.write("(" + city.x + ", " + city.y + ")");
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package simulation;

import java.util.*;

public class WorldMap extends AbstractWorldMap {
    private static final int ANIMALS_NO = 10, PLANTS_NO = 100;
    private ArrayList<Animal> animals = new ArrayList<>();
    private LinkedList<Plant> plants = new LinkedList<>();
    private Random random;

    public WorldMap(int width, int height) {
        super(width, height);
        random = new Random();
        for (int i = 0; i < ANIMALS_NO; i++) {
            animals.add(new Animal(getRandomPosition()));
        }
        for (int i = 0; i < PLANTS_NO; i++) {
            placePlantOnMap();
        }
    }

    private void placePlantOnMap() {
        Vector2D position = getRandomPosition();
        while (isOccupiedByPlant(position)) position = getRandomPosition();
        plants.add(new Plant(position));
    }

    private boolean isOccupiedByPlant(Vector2D position) {
        return getPlantAtPosition(position) != null;
    }

    private Plant getPlantAtPosition(Vector2D position) {
        for (Plant plant : plants) {
            if (plant.getPosition().equals(position)) return plant;
        }
        return null;
    }

    private Vector2D getRandomPosition() {
        return new Vector2D(random.nextInt(getWidth()), random.nextInt(getHeight()));
    }

    @Override
    public void run() {
        for (Animal animal : animals) {
            animal.move(MapDirection.values()[random.nextInt(MapDirection.values().length)]);
        }
    }

    public void eat() {
        for (Animal animal : animals) {
            Plant plant = getPlantAtPosition(animal.getPosition());
            if (plant != null) {
                System.out.println("Animal ate plant on position " + animal.getPosition());
                plants.remove(plant);
                placePlantOnMap();
            }
        }
    }
}

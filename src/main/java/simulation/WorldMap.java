package simulation;

import java.util.*;
import java.util.stream.Collectors;

public class WorldMap extends AbstractWorldMap {
    private static final int ANIMALS_NO = 200, PLANTS_NO = 600;
    private static final int INITIAL_ENERGY = 20;
    private static final int PLANT_ENERGY = 10;
    private HashMap<Vector2D, List<Animal>> animalsPositions = new HashMap<>();
    private List<Animal> animals = new ArrayList<>();
    private HashMap<Vector2D, Plant> plants = new HashMap<>();
    private Random random;
    private int dayNumber = 1;

    public WorldMap(int width, int height) {
        super(width, height);
        random = new Random();
        for (int i = 0; i < ANIMALS_NO; i++) {
            addNewAnimal(new Animal(getRandomPosition(), INITIAL_ENERGY));
        }
        for (int i = 0; i < PLANTS_NO; i++) {
            placePlantOnMap();
        }
    }

    private void placeAnimalOnMap(Animal animal) {
        animalsPositions.computeIfAbsent(animal.getPosition(), pos -> new LinkedList<>()).add(animal);
    }

    private void placePlantOnMap() {
        Vector2D position = getRandomPosition();
        while (isOccupiedByPlant(position)) position = getRandomPosition();
        plants.put(position, new Plant(position));
    }

    private boolean isOccupiedByPlant(Vector2D position) {
        return getPlantAtPosition(position) != null;
    }

    private Plant getPlantAtPosition(Vector2D position) {
        return plants.get(position);
    }

    private Vector2D getRandomPosition() {
        return new Vector2D(random.nextInt(getWidth()), random.nextInt(getHeight()));
    }

    @Override
    public void run() {
        System.out.println("Today is day number " + dayNumber);
        animalsPositions.clear();
        animals.forEach(animal -> {
            animal.move(MapDirection.values()[random.nextInt(MapDirection.values().length)]);
            placeAnimalOnMap(animal);
        });
    }

    public void eat() {
        animalsPositions.forEach((position, animals) -> {
            if (isOccupiedByPlant(position)) {
                animals.stream()
                        .max(Animal::compareTo)
                        .ifPresent(this::eatPlant);
            }
        });
    }

    private void eatPlant(Animal animal) {
        System.out.println("Animal ate plant on position " + animal.getPosition());
        animal.setEnergy(animal.getEnergy() + PLANT_ENERGY);
        plants.remove(animal.getPosition());
        placePlantOnMap();
    }

    @Override
    public void atTheEndOfDay() {
        animals = animals.stream()
                .map(Animal::aging)
                .map(animal -> animal.setEnergy(animal.getEnergy() - INITIAL_ENERGY / 2))
                .filter(animal -> animal.getEnergy() > 0)
                .collect(Collectors.toList());
        dayNumber++;
    }

    @Override
    public void reproduce() {
        List<Animal> children = new LinkedList<>();
        animalsPositions.forEach((position, animals) -> {
            List<Animal> parents = animals.stream()
                    .filter(a -> a.getEnergy() > INITIAL_ENERGY / 2)
                    .sorted(Collections.reverseOrder())
                    .limit(2)
                    .collect(Collectors.toList());
            if (parents.size() == 2) {
                Animal child = new Animal(parents.get(0), parents.get(1));
                System.out.println("Animal " + child.getAnimalId() + " was born on position " + position);
                children.add(child);
            }
        });
        children.forEach(this::addNewAnimal);
    }

    private void addNewAnimal(Animal animal) {
        animals.add(animal);
        placeAnimalOnMap(animal);
    }
}

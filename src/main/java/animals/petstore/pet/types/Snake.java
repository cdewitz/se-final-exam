package animals.petstore.pet.types;

import animals.AnimalType;
import animals.petstore.pet.Pet;
import animals.petstore.pet.attributes.Breed;
import animals.petstore.pet.attributes.Gender;
import animals.petstore.pet.attributes.PetType;
import animals.petstore.pet.attributes.Skin;

import java.math.BigDecimal;

/**
 * Snake class representing a pet snake in the store.
 * Extends Pet to include snake-specific attributes.
 */
public class Snake extends Pet
{
    private AnimalType animalType;
    private Skin skin;
    private Breed breed;
    private boolean venomous;

    /**
     * Constructor without pet store ID
     * @param animalType The {@link AnimalType} of the snake
     * @param skin The {@link Skin} type of the snake
     * @param gender The {@link Gender} of the snake
     * @param breed The {@link Breed} of the snake
     * @param cost The cost of the snake
     * @param venomous Whether the snake is venomous
     */
    public Snake(AnimalType animalType, Skin skin, Gender gender, Breed breed,
                 BigDecimal cost, boolean venomous)
    {
        this(animalType, skin, gender, breed, cost, venomous, 0);
    }

    /**
     * Constructor with pet store ID
     * @param animalType The {@link AnimalType} of the snake
     * @param skin The {@link Skin} type of the snake
     * @param gender The {@link Gender} of the snake
     * @param breed The {@link Breed} of the snake
     * @param cost The cost of the snake
     * @param venomous Whether the snake is venomous
     * @param petStoreId The pet store ID
     */
    public Snake(AnimalType animalType, Skin skin, Gender gender, Breed breed,
                 BigDecimal cost, boolean venomous, int petStoreId)
    {
        super(PetType.SNAKE, cost, gender, petStoreId);
        this.animalType = animalType;
        this.skin = skin;
        this.breed = breed;
        this.venomous = venomous;
    }

    /**
     * Gets the animal type of the snake
     * @return {@link AnimalType}
     */
    public AnimalType getAnimalType()
    {
        return animalType;
    }

    /**
     * Gets the skin type of the snake
     * @return {@link Skin}
     */
    public Skin getSkin()
    {
        return skin;
    }

    /**
     * Gets the breed of the snake
     * @return {@link Breed}
     */
    public Breed getBreed()
    {
        return breed;
    }

    /**
     * Checks if the snake is venomous
     * @return true if venomous, false otherwise
     */
    public boolean isVenomous()
    {
        return venomous;
    }

    /**
     * Sets whether the snake is venomous
     * @param venomous true if venomous, false otherwise
     */
    public void setVenomous(boolean venomous)
    {
        this.venomous = venomous;
    }

    @Override
    public String toString()
    {
        String baseInfo = "The type of pet is " + this.getPetType() + "!\n";

        if (this.getPetStoreId() != 0)
        {
            baseInfo += "The " + this.getPetType() + " pet store id is " + this.getPetStoreId() + "!\n";
        }

        baseInfo += "The " + this.getPetType() + " breed is " + this.breed + "!\n" +
                "The " + this.getPetType() + " gender is " + this.getGender() + "!\n" +
                "The " + this.getPetType() + " skin type is " + this.skin + "!\n" +
                "The " + this.getPetType() + " is " + (this.venomous ? "venomous" : "non-venomous") + "!\n" +
                "The " + this.getPetType() + " cost is $" + this.getCost() + "!\n";

        return baseInfo;
    }
}
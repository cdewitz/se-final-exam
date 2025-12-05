package animals.petstore.store;

import animals.AnimalType;
import animals.petstore.pet.Pet;
import animals.petstore.pet.attributes.Breed;
import animals.petstore.pet.attributes.Gender;
import animals.petstore.pet.attributes.PetType;
import animals.petstore.pet.attributes.Skin;
import animals.petstore.pet.types.Cat;
import animals.petstore.pet.types.Dog;
import animals.petstore.pet.types.Snake;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PetStore
{
    private List<Pet> petsForSale;

    private List<Pet> petsSold;

    public PetStore()
    {
        petsForSale = new ArrayList<>();
        petsSold = new ArrayList<>();

    }

    /**
     * Initialize the pet inventory list
     */
    public void init()
    {
        this.addPetInventoryItem(new Dog(AnimalType.DOMESTIC, Skin.FUR, Gender.MALE, Breed.MALTESE,
                new BigDecimal("750.00"), 3));
        this.addPetInventoryItem(new Dog(AnimalType.DOMESTIC, Skin.FUR, Gender.MALE, Breed.POODLE,
                new BigDecimal("650.00"), 1));
        this.addPetInventoryItem(new Cat(AnimalType.DOMESTIC, Skin.HAIR, Gender.MALE, Breed.BURMESE,
                new BigDecimal("65.00"),1));
        this.addPetInventoryItem(new Dog(AnimalType.DOMESTIC, Skin.HAIR, Gender.MALE, Breed.GERMAN_SHEPARD,
                new BigDecimal("50.00"), 2));
        this.addPetInventoryItem(new Cat(AnimalType.DOMESTIC, Skin.UNKNOWN, Gender.FEMALE, Breed.SPHYNX,
                new BigDecimal("100.00"),2));
        this.addPetInventoryItem(new Snake(AnimalType.DOMESTIC, Skin.SCALES, Gender.FEMALE, Breed.BALL_PYTHON,
                new BigDecimal("200.00"), false, 5));


    }

    /**
     * Constructor used for unit testing
     * @param addPet - used to add item to the default list without using addInventory.
     */
    public void initAddDuplicateItem(Pet addPet)
    {
        this.init();
        this.addPetInventoryItem(addPet);
    }
    /**
     * Print the inventory left in the pets for sale list
     */
    public void printInventory()
    {
        Consumer<Pet> action = System.out::println;
        List sortedPets = this.petsForSale.stream()
                .sorted(Comparator.comparing(Pet::getPetType))
                .collect(Collectors.toList());
        sortedPets.stream()
                .forEach(action);
    }

    /**
     * Remove pet items from the petsForSale list
     * @param soldPet the type of {@link Pet} that will be sold
     * @return {@link Pet} that was sold
     * @throws DuplicatePetStoreRecordException if the pet store record is duplicated
     * @throws PetNotFoundSaleException if the pet is not in any store
     */
    public Pet soldPetItem(Pet soldPet) throws DuplicatePetStoreRecordException, PetNotFoundSaleException {

        if (soldPet.getPetStoreId()==0)
        {
            throw new PetNotFoundSaleException("The Pet is not part of the pet store!!");
        }

        if (soldPet instanceof Dog)
        {
            Dog foundDog = this.identifySoldDogFromInventory((Dog) soldPet);
            this.removePetFromInventoryByPetId(PetType.DOG, soldPet.getPetStoreId());
            return foundDog;
        }

        if (soldPet instanceof Cat)
        {
            Cat foundCat = this.identifySoldCatFromInventory((Cat) soldPet);
            this.removePetFromInventoryByPetId(PetType.CAT, soldPet.getPetStoreId());
            return foundCat;
        }

        if (soldPet instanceof Snake)
        {
            Snake foundSnake = this.identifySoldSnakeFromInventory((Snake) soldPet);
            this.removePetFromInventoryByPetId(PetType.SNAKE, soldPet.getPetStoreId());
            return foundSnake;
        }

        throw new PetNotFoundSaleException("Unknown pet type!");
    }

    /**
     * Add item to the inventory list
     * @param pet {@link Pet} to be added to the inventory
     */
    public void addPetInventoryItem(Pet pet)
    {
        this.petsForSale.add(pet);
    }

    /**
     * Remove the Pet from the pet store by id and type of pet
     * @param petType the type of pet
     * @param petStoreId the pet store id
     */
    private void removePetFromInventoryByPetId(PetType petType, int petStoreId)
    {
        List<Pet> otherPets = this.petsForSale.stream()
                .filter(p -> (p.getPetType() != petType))
                .collect(Collectors.toList());
        switch(petType)
        {
            case CAT:
                this.petsForSale = this.petsForSale.stream()
                        .filter(p -> ((p instanceof Cat)
                                && (p.getPetStoreId() != petStoreId)))
                        .collect(Collectors.toList());
                break;

            case SNAKE:
                this.petsForSale = this.petsForSale.stream()
                        .filter(p -> ((p instanceof Snake)
                         && (p.getPetStoreId() != petStoreId)))
                        .collect(Collectors.toList());
                break;

            default: //remove the dog
                this.petsForSale = this.petsForSale.stream()
                        .filter(p -> ((p instanceof Dog)
                                && (p.getPetStoreId() != petStoreId)))
                        .collect(Collectors.toList());

                break;
        }
        this.petsForSale.addAll(otherPets);
    }

    /**
     * Identify the Dog to remove from the inventory list
     * @param soldDog the {@link Dog} that will be sold
     * @return the {@link Dog} that was sold
     * @throws DuplicatePetStoreRecordException if there is duplicate dog record
     * @throws PetNotFoundSaleException if the dog is not found in inventory
     */
    private Dog identifySoldDogFromInventory(Dog soldDog) throws DuplicatePetStoreRecordException, PetNotFoundSaleException {
        List<Pet> dogPets= this.petsForSale.stream()
                .filter(p -> ((p instanceof Dog)
                        && (p.getPetStoreId() == soldDog.getPetStoreId())))
                .collect(Collectors.toList());
        if (dogPets.size()==1)
        {
            return (Dog) dogPets.get(0);
        }
        else if (dogPets.size() == 0) {
            throw new PetNotFoundSaleException("Dog with store id [" + soldDog.getPetStoreId() + "] not found in inventory");
        }
        else {
            throw new DuplicatePetStoreRecordException ("Duplicate Dog record store id [" + soldDog.getPetStoreId() + "]");
        }
    }

    /**
     * Identify the cat which was sold from the inventory list
     * @param soldCat the {@link Cat} that will be sold
     * @return the {@link Cat} that was sold
     * @throws DuplicatePetStoreRecordException if there is duplicate cat record
     * @throws PetNotFoundSaleException if the cat is not found in inventory
     */
    private Cat identifySoldCatFromInventory(Cat soldCat) throws DuplicatePetStoreRecordException, PetNotFoundSaleException
    {
        List<Pet> catPets = this.petsForSale.stream()
                .filter(p -> ((p instanceof Cat)
                        && (p.getPetStoreId() == soldCat.getPetStoreId())))
                .collect(Collectors.toList());

        if (catPets.size()==1)
        {
            return (Cat) catPets.get(0);
        }
        else if (catPets.size() == 0) {
            throw new PetNotFoundSaleException("Cat with store id [" + soldCat.getPetStoreId() + "] not found in inventory");
        }
        else {
            throw new DuplicatePetStoreRecordException ("Duplicate Cat record store id [" + soldCat.getPetStoreId() + "]");
        }
    }

    /**
     * Identify the snake which was sold from the inventory list
     * @param soldSnake the {@link Snake} that will be sold
     * @return the {@link Snake} that was sold
     * @throws DuplicatePetStoreRecordException if there is duplicate snake record
     * @throws PetNotFoundSaleException if the snake is not found in inventory
     */
    private Snake identifySoldSnakeFromInventory(Snake soldSnake) throws DuplicatePetStoreRecordException, PetNotFoundSaleException
    {
        List<Pet> snakePets = this.petsForSale.stream()
                .filter(p -> ((p instanceof Snake)
                        && (p.getPetStoreId() == soldSnake.getPetStoreId())))
                .collect(Collectors.toList());

        if (snakePets.size()==1)
        {
            return (Snake) snakePets.get(0);
        }
        else if (snakePets.size() == 0) {
            throw new PetNotFoundSaleException("Snake with store id [" + soldSnake.getPetStoreId() + "] not found in inventory");
        }
        else {
            throw new DuplicatePetStoreRecordException ("Duplicate Snake record store id [" + soldSnake.getPetStoreId() + "]");
        }
    }

    public List<Pet> getPetsForSale()
    {
        return petsForSale;
    }

}

package tests;

import animals.AnimalType;
import animals.petstore.pet.attributes.Breed;
import animals.petstore.pet.attributes.Gender;
import animals.petstore.pet.attributes.Skin;
import animals.petstore.pet.attributes.PetType;
import animals.petstore.pet.types.Cat;
import animals.petstore.pet.types.Dog;
import animals.petstore.pet.types.Snake;
import animals.petstore.store.DuplicatePetStoreRecordException;
import animals.petstore.store.PetNotFoundSaleException;
import animals.petstore.store.PetStore;
import number.Numbers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class PetStoreTest
{
    private static PetStore petStore;

    @BeforeEach
    public void loadThePetStoreInventory()
    {
        petStore = new PetStore();
        petStore.init();
    }

    @Test
    @DisplayName("Inventory Count Test")
    public void validateInventory()
    {
        assertEquals(6, petStore.getPetsForSale().size(),"Inventory counts are off!");
    }

    @Test
    @DisplayName("Print Inventory Test")
    public void printInventoryTest()
    {
        petStore.printInventory();
    }

    @Test
    @DisplayName("Sale of Poodle Remove Item Test")
    public void poodleSoldTest() throws DuplicatePetStoreRecordException, PetNotFoundSaleException {
        int inventorySize = petStore.getPetsForSale().size() - 1;
        Dog poodle = new Dog(AnimalType.DOMESTIC, Skin.FUR, Gender.MALE, Breed.POODLE,
                new BigDecimal("650.00"), 1);

        // Validation
        petStore.soldPetItem(poodle);
        assertEquals(inventorySize, petStore.getPetsForSale().size(), "Expected inventory does not match actual");
    }

    @Test
    @DisplayName("Poodle Duplicate Record Exception Test")
    public void poodleDupRecordExceptionTest() {
        petStore.addPetInventoryItem(new Dog(AnimalType.DOMESTIC, Skin.FUR, Gender.MALE, Breed.POODLE,
                new BigDecimal("650.00"), 1));
        Dog poodle = new Dog(AnimalType.DOMESTIC, Skin.FUR, Gender.MALE, Breed.POODLE,
                new BigDecimal("650.00"), 1);

        // Validation
        String expectedMessage = "Duplicate Dog record store id [1]";
        Exception exception = assertThrows(DuplicatePetStoreRecordException.class, () ->{
            petStore.soldPetItem(poodle);});
        assertEquals(expectedMessage, exception.getMessage(), "DuplicateRecordExceptionTest was NOT encountered!");

    }

    @Test
    @DisplayName("Sale of Sphynx Remove Item Test")
    public void sphynxSoldTest() throws DuplicatePetStoreRecordException, PetNotFoundSaleException {
        int inventorySize = petStore.getPetsForSale().size() - 1;

        Cat sphynx = new Cat(AnimalType.DOMESTIC, Skin.UNKNOWN, Gender.FEMALE, Breed.SPHYNX,
                new BigDecimal("100.00"),2);
        Cat removedItem = (Cat) petStore.soldPetItem(sphynx);

        // Validation
        assertEquals(inventorySize, petStore.getPetsForSale().size(), "Expected inventory does not match actual");
        assertEquals(sphynx.getPetStoreId(), removedItem.getPetStoreId(), "The cat items are identical");
    }

    /**
     * Limitations to test factory as it does not instantiate before all
     * @return list of {@link DynamicNode} that contains the test results
     * @throws DuplicatePetStoreRecordException if duplicate pet record is found
     * @throws PetNotFoundSaleException if pet is not found
     */
    @TestFactory
    @DisplayName("Sale of Sphynx Remove Item Test2")
    public Stream<DynamicNode> sphynxSoldTest2() throws DuplicatePetStoreRecordException, PetNotFoundSaleException {
        int inventorySize = petStore.getPetsForSale().size() - 1;

        Cat sphynx = new Cat(AnimalType.DOMESTIC, Skin.UNKNOWN, Gender.FEMALE, Breed.SPHYNX,
                new BigDecimal("100.00"),2);
        Cat removedItem = (Cat) petStore.soldPetItem(sphynx);

        // Validation
        List<DynamicNode> nodes = new ArrayList<>();
        List<DynamicTest> dynamicTests = Arrays.asList(
                dynamicTest("Inventory Check Size Test ", () -> assertEquals(inventorySize,
                        petStore.getPetsForSale().size())),
                dynamicTest("The cat objects match ", () -> assertEquals(sphynx.toString(),
                        removedItem.toString()))
                );
        nodes.add(dynamicContainer("Cat Item 2 Test", dynamicTests));//dynamicNode("", dynamicContainers);

        return nodes.stream();
    }

    /**
     * Example of parameterized test
     * @param number to be tested
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 4, 6, -10, 128, Integer.MIN_VALUE}) // six numbers
    void isNumberEven(int number)
    {
        assertTrue(Numbers.isEven(number));
    }

    // New tests (Part 1)

    /**
     * Tests whether adding a new pet to the inventory increases the inventory count.
     * This ensures the method {@code addPetInventoryItem()} works correctly.
     */
    @Test
    @DisplayName("Add Pet to Inventory Test")
    public void addPetToInventoryTest()
    {
        int inventoryBefore = petStore.getPetsForSale().size();

        Dog lab = new Dog(
                AnimalType.DOMESTIC,
                Skin.FUR,
                Gender.MALE,
                Breed.POODLE,
                new BigDecimal("500.00"),
                99
        );

        petStore.addPetInventoryItem(lab);

        assertEquals(inventoryBefore + 1, petStore.getPetsForSale().size(),
                "Inventory did not increase after adding new pet.");
    }

    /**
     * Ensures a sold pet is matched and removed based strictly on its store ID.
     * Confirms updated inventory size and correct matching behavior.
     */
    @Test
    @DisplayName("Sale Removes Correct Pet by ID Test")
    public void saleMatchesByIdTest() throws DuplicatePetStoreRecordException, PetNotFoundSaleException
    {
        var anyPet = petStore.getPetsForSale().get(0);
        int sizeBefore = petStore.getPetsForSale().size();

        var sold = petStore.soldPetItem(anyPet);

        assertEquals(anyPet.getPetStoreId(), sold.getPetStoreId(),
                "Sold pet ID did not match expected");
        assertEquals(sizeBefore - 1, petStore.getPetsForSale().size(),
                "Inventory size did not update correctly");
    }

    // Enhanced Coverage Tests

    /**
     * Ensures that selling a pet not included in the store inventory
     * throws the expected {@link PetNotFoundSaleException}.
     */
    @Test
    @DisplayName("Selling Non-existent Dog Throws Exception")
    public void petNotFoundExceptionTest()
    {
        Dog ghostDog = new Dog(
                AnimalType.UNKNOWN,
                Skin.FUR,
                Gender.FEMALE,
                Breed.POODLE,
                new BigDecimal("800.00"),
                999
        );

        assertThrows(PetNotFoundSaleException.class,
                () -> petStore.soldPetItem(ghostDog),
                "Expected PetNotFoundSaleException was not thrown!");
    }

    /**
     * Tests that selling a cat not in inventory throws PetNotFoundSaleException.
     * This covers the Cat branch in identifySoldCatFromInventory.
     */
    @Test
    @DisplayName("Selling Non-existent Cat Throws Exception")
    public void catNotFoundExceptionTest()
    {
        Cat ghostCat = new Cat(
                AnimalType.UNKNOWN,
                Skin.HAIR,
                Gender.MALE,
                Breed.BURMESE,
                new BigDecimal("200.00"),
                888
        );

        assertThrows(PetNotFoundSaleException.class,
                () -> petStore.soldPetItem(ghostCat),
                "Expected PetNotFoundSaleException for cat was not thrown!");
    }

    /**
     * Tests that attempting to sell a pet with petStoreId of 0 throws PetNotFoundSaleException.
     * This covers the first if branch in soldPetItem method.
     */
    @Test
    @DisplayName("Selling Pet with ID Zero Throws Exception")
    public void petWithZeroIdThrowsException()
    {
        Dog dogWithZeroId = new Dog(
                AnimalType.DOMESTIC,
                Skin.FUR,
                Gender.MALE,
                Breed.MALTESE,
                new BigDecimal("500.00"),
                0
        );

        String expectedMessage = "The Pet is not part of the pet store!!";
        Exception exception = assertThrows(PetNotFoundSaleException.class,
                () -> petStore.soldPetItem(dogWithZeroId));
        assertEquals(expectedMessage, exception.getMessage(),
                "Exception message does not match expected");
    }

    /**
     * Tests duplicate cat records throw DuplicatePetStoreRecordException.
     * This ensures the cat duplicate detection branch is covered.
     */
    @Test
    @DisplayName("Cat Duplicate Record Exception Test")
    public void catDupRecordExceptionTest()
    {
        petStore.addPetInventoryItem(new Cat(
                AnimalType.DOMESTIC,
                Skin.UNKNOWN,
                Gender.FEMALE,
                Breed.SPHYNX,
                new BigDecimal("100.00"),
                2));

        Cat duplicateCat = new Cat(
                AnimalType.DOMESTIC,
                Skin.UNKNOWN,
                Gender.FEMALE,
                Breed.SPHYNX,
                new BigDecimal("100.00"),
                2);

        String expectedMessage = "Duplicate Cat record store id [2]";
        Exception exception = assertThrows(DuplicatePetStoreRecordException.class,
                () -> petStore.soldPetItem(duplicateCat));
        assertEquals(expectedMessage, exception.getMessage(),
                "DuplicateRecordExceptionTest for cat was NOT encountered!");
    }

    /**
     * Tests that multiple pets can be added to inventory.
     * This ensures addPetInventoryItem works correctly for multiple additions.
     */
    @Test
    @DisplayName("Add Multiple Pets to Inventory Test")
    public void addMultiplePetsTest()
    {
        int inventoryBefore = petStore.getPetsForSale().size();

        Dog retriever = new Dog(
                AnimalType.DOMESTIC,
                Skin.FUR,
                Gender.FEMALE,
                Breed.GERMAN_SHEPARD,
                new BigDecimal("700.00"),
                100
        );

        Cat persian = new Cat(
                AnimalType.DOMESTIC,
                Skin.FUR,
                Gender.FEMALE,
                Breed.BURMESE,
                new BigDecimal("300.00"),
                101
        );

        petStore.addPetInventoryItem(retriever);
        petStore.addPetInventoryItem(persian);

        assertEquals(inventoryBefore + 2, petStore.getPetsForSale().size(),
                "Inventory did not increase by 2 after adding two pets.");
    }

    /**
     * Tests that the correct dog is returned when sold.
     * Validates the return value from soldPetItem for dogs.
     */
    @Test
    @DisplayName("Sold Dog Returns Correct Dog Object")
    public void soldDogReturnsCorrectObject() throws DuplicatePetStoreRecordException, PetNotFoundSaleException
    {
        Dog maltese = new Dog(
                AnimalType.DOMESTIC,
                Skin.FUR,
                Gender.MALE,
                Breed.MALTESE,
                new BigDecimal("750.00"),
                3
        );

        Dog soldDog = (Dog) petStore.soldPetItem(maltese);

        assertNotNull(soldDog, "Sold dog should not be null");
        assertEquals(maltese.getPetStoreId(), soldDog.getPetStoreId(),
                "Sold dog ID should match");
        assertEquals(Breed.MALTESE, soldDog.getBreed(),
                "Sold dog breed should be MALTESE");
    }

    /**
     * Tests that selling all pets results in an empty inventory.
     * This provides comprehensive method coverage for soldPetItem.
     */
    @Test
    @DisplayName("Sell All Pets Empties Inventory")
    public void sellAllPetsTest() throws DuplicatePetStoreRecordException, PetNotFoundSaleException
    {
        List<Dog> dogs = List.of(
                new Dog(AnimalType.DOMESTIC, Skin.FUR, Gender.MALE, Breed.POODLE,
                        new BigDecimal("650.00"), 1),
                new Dog(AnimalType.DOMESTIC, Skin.FUR, Gender.MALE, Breed.MALTESE,
                        new BigDecimal("750.00"), 3),
                new Dog(AnimalType.DOMESTIC, Skin.HAIR, Gender.MALE, Breed.GERMAN_SHEPARD,
                        new BigDecimal("50.00"), 2)
        );

        List<Cat> cats = List.of(
                new Cat(AnimalType.DOMESTIC, Skin.HAIR, Gender.MALE, Breed.BURMESE,
                        new BigDecimal("65.00"), 1),
                new Cat(AnimalType.DOMESTIC, Skin.UNKNOWN, Gender.FEMALE, Breed.SPHYNX,
                        new BigDecimal("100.00"), 2)
        );

        petStore.soldPetItem(new Snake(AnimalType.DOMESTIC, Skin.SCALES, Gender.FEMALE, Breed.BALL_PYTHON,
                new BigDecimal("200.00"), false, 5));

        for (Dog dog : dogs) {
            petStore.soldPetItem(dog);
        }

        for (Cat cat : cats) {
            petStore.soldPetItem(cat);
        }


        assertEquals(0, petStore.getPetsForSale().size(),
                "Inventory should be empty after selling all pets");
    }

    /**
     * Tests the initAddDuplicateItem method to ensure it properly initializes
     * the store and adds a duplicate item.
     */
    @Test
    @DisplayName("Init Add Duplicate Item Test")
    public void initAddDuplicateItemTest()
    {
        PetStore newStore = new PetStore();
        Dog duplicateDog = new Dog(
                AnimalType.DOMESTIC,
                Skin.FUR,
                Gender.MALE,
                Breed.POODLE,
                new BigDecimal("650.00"),
                1
        );

        newStore.initAddDuplicateItem(duplicateDog);

        assertEquals(7, newStore.getPetsForSale().size(),
                "Store should have 7 items (6 from init + 1 duplicate)");
    }

    /**
     * Tests that getPetsForSale returns a non-null list.
     * Provides coverage for the getter method.
     */
    @Test
    @DisplayName("Get Pets For Sale Returns Valid List")
    public void getPetsForSaleTest()
    {
        List<animals.petstore.pet.Pet> pets = petStore.getPetsForSale();

        assertNotNull(pets, "getPetsForSale should not return null");
        assertFalse(pets.isEmpty(), "getPetsForSale should not return empty list after init");
    }

    /**
     * Tests that a pet can be sold and then attempting to sell it again throws exception.
     * This validates that the pet is properly removed from inventory.
     */
    @Test
    @DisplayName("Cannot Sell Already Sold Pet")
    public void cannotSellAlreadySoldPet() throws DuplicatePetStoreRecordException, PetNotFoundSaleException
    {
        Dog germanShepard = new Dog(
                AnimalType.DOMESTIC,
                Skin.HAIR,
                Gender.MALE,
                Breed.GERMAN_SHEPARD,
                new BigDecimal("50.00"),
                2
        );

        petStore.soldPetItem(germanShepard);

        assertThrows(PetNotFoundSaleException.class,
                () -> petStore.soldPetItem(germanShepard),
                "Should throw PetNotFoundSaleException when trying to sell already sold pet");
    }

    /**
     * Tests that the CAT case in removePetFromInventoryByPetId works correctly.
     * This ensures proper removal of cats from inventory.
     */
    @Test
    @DisplayName("Remove Cat By ID Works Correctly")
    public void removeCatByIdTest() throws DuplicatePetStoreRecordException, PetNotFoundSaleException
    {
        Cat burmese = new Cat(
                AnimalType.DOMESTIC,
                Skin.HAIR,
                Gender.MALE,
                Breed.BURMESE,
                new BigDecimal("65.00"),
                1
        );

        int sizeBefore = petStore.getPetsForSale().size();
        petStore.soldPetItem(burmese);

        assertEquals(sizeBefore - 1, petStore.getPetsForSale().size(),
                "Inventory should decrease by 1 after selling cat");

        long remainingCatsWithSameId = petStore.getPetsForSale().stream()
                .filter(p -> p instanceof Cat && p.getPetStoreId() == 1)
                .count();

        assertEquals(0, remainingCatsWithSameId,
                "No cats with ID 1 should remain in inventory");
    }

    // ========== SNAKE TESTS ==========

    /**
     * Tests that selling a snake from inventory works correctly.
     * Validates the snake branch in soldPetItem method.
     */
    @Test
    @DisplayName("Sale of Snake Remove Item Test")
    public void snakeSoldTest() throws DuplicatePetStoreRecordException, PetNotFoundSaleException
    {
        int inventorySize = petStore.getPetsForSale().size() - 1;

        Snake python = new Snake(
                AnimalType.DOMESTIC,
                Skin.SCALES,
                Gender.FEMALE,
                Breed.BALL_PYTHON,
                new BigDecimal("200.00"),
                false,
                5
        );

        Snake removedItem = (Snake) petStore.soldPetItem(python);

        assertEquals(inventorySize, petStore.getPetsForSale().size(),
                "Expected inventory does not match actual after selling snake");
        assertEquals(python.getPetStoreId(), removedItem.getPetStoreId(),
                "The snake items should match by ID");
    }

    /**
     * Tests that selling a non-existent snake throws PetNotFoundSaleException.
     * Covers the snake branch in identifySoldSnakeFromInventory when size is 0.
     */
    @Test
    @DisplayName("Selling Non-existent Snake Throws Exception")
    public void snakeNotFoundExceptionTest()
    {
        Snake ghostSnake = new Snake(
                AnimalType.WILD,
                Skin.SCALES,
                Gender.MALE,
                Breed.CORN,
                new BigDecimal("150.00"),
                false,
                777
        );

        assertThrows(PetNotFoundSaleException.class,
                () -> petStore.soldPetItem(ghostSnake),
                "Expected PetNotFoundSaleException for snake was not thrown!");
    }

    /**
     * Tests duplicate snake records throw DuplicatePetStoreRecordException.
     * This ensures the snake duplicate detection branch is covered.
     */
    @Test
    @DisplayName("Snake Duplicate Record Exception Test")
    public void snakeDupRecordExceptionTest()
    {
        petStore.addPetInventoryItem(new Snake(
                AnimalType.DOMESTIC,
                Skin.SCALES,
                Gender.FEMALE,
                Breed.BALL_PYTHON,
                new BigDecimal("200.00"),
                false,
                5));

        Snake duplicateSnake = new Snake(
                AnimalType.DOMESTIC,
                Skin.SCALES,
                Gender.FEMALE,
                Breed.BALL_PYTHON,
                new BigDecimal("200.00"),
                false,
                5);

        String expectedMessage = "Duplicate Snake record store id [5]";
        Exception exception = assertThrows(DuplicatePetStoreRecordException.class,
                () -> petStore.soldPetItem(duplicateSnake));
        assertEquals(expectedMessage, exception.getMessage(),
                "DuplicateRecordExceptionTest for snake was NOT encountered!");
    }

    /**
     * Tests adding a snake to inventory increases the count.
     * Validates addPetInventoryItem works for snakes.
     */
    @Test
    @DisplayName("Add Snake to Inventory Test")
    public void addSnakeToInventoryTest()
    {
        int inventoryBefore = petStore.getPetsForSale().size();

        Snake cornSnake = new Snake(
                AnimalType.DOMESTIC,
                Skin.SCALES,
                Gender.MALE,
                Breed.CORN,
                new BigDecimal("150.00"),
                false,
                50
        );

        petStore.addPetInventoryItem(cornSnake);

        assertEquals(inventoryBefore + 1, petStore.getPetsForSale().size(),
                "Inventory did not increase after adding snake.");
    }

    /**
     * Tests that the correct snake is returned when sold.
     * Validates the return value from soldPetItem for snakes.
     */
    @Test
    @DisplayName("Sold Snake Returns Correct Snake Object")
    public void soldSnakeReturnsCorrectObject() throws DuplicatePetStoreRecordException, PetNotFoundSaleException
    {
        Snake python = new Snake(
                AnimalType.DOMESTIC,
                Skin.SCALES,
                Gender.FEMALE,
                Breed.BALL_PYTHON,
                new BigDecimal("200.00"),
                false,
                5
        );

        Snake soldSnake = (Snake) petStore.soldPetItem(python);

        assertNotNull(soldSnake, "Sold snake should not be null");
        assertEquals(python.getPetStoreId(), soldSnake.getPetStoreId(),
                "Sold snake ID should match");
        assertEquals(Breed.BALL_PYTHON, soldSnake.getBreed(),
                "Sold snake breed should be PYTHON");
        assertFalse(soldSnake.isVenomous(), "Sold snake should not be venomous");
    }

    /**
     * Tests that the SNAKE case in removePetFromInventoryByPetId works correctly.
     * This ensures proper removal of snakes from inventory.
     */
    @Test
    @DisplayName("Remove Snake By ID Works Correctly")
    public void removeSnakeByIdTest() throws DuplicatePetStoreRecordException, PetNotFoundSaleException
    {
        Snake python = new Snake(
                AnimalType.DOMESTIC,
                Skin.SCALES,
                Gender.FEMALE,
                Breed.BALL_PYTHON,
                new BigDecimal("200.00"),
                false,
                5
        );

        int sizeBefore = petStore.getPetsForSale().size();
        petStore.soldPetItem(python);

        assertEquals(sizeBefore - 1, petStore.getPetsForSale().size(),
                "Inventory should decrease by 1 after selling snake");

        long remainingSnakesWithSameId = petStore.getPetsForSale().stream()
                .filter(p -> p instanceof Snake && p.getPetStoreId() == 5)
                .count();

        assertEquals(0, remainingSnakesWithSameId,
                "No snakes with ID 1 should remain in inventory");
    }

    /**
     * Tests that a sold snake cannot be sold again.
     * Validates proper removal from inventory.
     */
    @Test
    @DisplayName("Cannot Sell Already Sold Snake")
    public void cannotSellAlreadySoldSnake() throws DuplicatePetStoreRecordException, PetNotFoundSaleException
    {
        Snake python = new Snake(
                AnimalType.DOMESTIC,
                Skin.SCALES,
                Gender.FEMALE,
                Breed.BALL_PYTHON,
                new BigDecimal("200.00"),
                false,
                5
        );

        petStore.soldPetItem(python);

        assertThrows(PetNotFoundSaleException.class,
                () -> petStore.soldPetItem(python),
                "Should throw PetNotFoundSaleException when trying to sell already sold snake");
    }

    /**
     * Tests adding multiple different snake types to inventory.
     */
    @Test
    @DisplayName("Add Multiple Snake Types to Inventory Test")
    public void addMultipleSnakeTypesTest()
    {
        int inventoryBefore = petStore.getPetsForSale().size();

        Snake cornSnake = new Snake(
                AnimalType.DOMESTIC,
                Skin.SCALES,
                Gender.MALE,
                Breed.CORAL,
                new BigDecimal("150.00"),
                false,
                10
        );

        Snake kingSnake = new Snake(
                AnimalType.WILD,
                Skin.SCALES,
                Gender.FEMALE,
                Breed.CORN,
                new BigDecimal("175.00"),
                true,
                11
        );

        Snake boaConstrictor = new Snake(
                AnimalType.DOMESTIC,
                Skin.SCALES,
                Gender.MALE,
                Breed.COPPERHEAD,
                new BigDecimal("400.00"),
                false,
                12
        );

        petStore.addPetInventoryItem(cornSnake);
        petStore.addPetInventoryItem(kingSnake);
        petStore.addPetInventoryItem(boaConstrictor);

        assertEquals(inventoryBefore + 3, petStore.getPetsForSale().size(),
                "Inventory should increase by 3 after adding three snakes");
    }

    /**
     * Tests the inventory contains the snake added during init.
     */
    @Test
    @DisplayName("Init Inventory Contains Snake")
    public void initInventoryContainsSnake()
    {
        long snakeCount = petStore.getPetsForSale().stream()
                .filter(p -> p instanceof Snake)
                .count();

        assertEquals(1, snakeCount, "Init should add exactly 1 snake to inventory");
    }
}


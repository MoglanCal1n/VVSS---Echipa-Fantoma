package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import drinkshop.repository.file.FileProductRepository;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@Tag("BBT")
@DisplayName("BBT pentru functia updateProduct din ProductService")
class ProductUpdateTest {

    private final String TEST_FILE_PATH = "repo_test_produse.txt";

    // Arrange (instanțierea claselor necesare)
    Repository<Integer, Product> productRepo = new FileProductRepository(TEST_FILE_PATH);
    private ProductService productService = new ProductService(productRepo);

    // Dummy objects pentru parametrii neinvestigați
    private final int dummyId = 100;
    private final CategorieBautura dummyCategorie = CategorieBautura.CLASSIC_COFFEE;
    private final TipBautura dummyTip = TipBautura.WATER_BASED;

    // ==========================================
    // Ne asiguram ca mai intai avem un produs in DB inainte sa il actualizam
    // ==========================================
    @BeforeEach
    @DisplayName("Setup: Adaugă un produs inițial în repo înainte de fiecare test")
    void setUp() {
        Product produsInitial = new Product(dummyId, "Nume Initial", 50.0, dummyCategorie, dummyTip);
        productRepo.save(produsInitial);
    }

    // ==========================================
    // Clean-Up: Stergem DB-ul dupa fiecare test
    // ==========================================
    @AfterEach
    @DisplayName("Clean-Up: Șterge fișierul de test după fiecare rulare")
    void tearDown() {
        File testFile = new File(TEST_FILE_PATH);

        if (testFile.exists()) {
            boolean isDeleted = testFile.delete();
            if (!isDeleted) {
                System.out.println("Avertisment: Nu s-a putut șterge fișierul de test!");
            }
        }
    }

    // ==========================================
    // Cautam produsul dupa ID-ul Dummy, apoi verificam daca are field-urile actualizate
    // ==========================================
    void assertIsUpdated(String nume, double pret){
        Product prod = productRepo.findOne(dummyId);
        assertNotNull(prod, "Produsul nu a fost gasit in baza de date dupa update!");
        assertEquals(nume, prod.getNume(), "Numele nu s-a updatat corect!");
        assertEquals(pret, prod.getPret(), "Pretul nu s-a updatat corect!");
    }

    // ==========================================
    // 1. TESTE ECP (Equivalence Class Partitioning)
    // ==========================================

    @Test
    @DisplayName("ECP Valid: Nume completat și Preț pozitiv")
    @Tag("ECP")
    void testUpdateProduct_ValidECP_ShouldPass() {
        // Arrange
        String validName = "Espresso";
        double validPrice = 15.0;

        // Act & Assert
        assertDoesNotThrow(() -> {
            productService.updateProduct(dummyId, validName, validPrice, dummyCategorie, dummyTip);
        });

        assertIsUpdated(validName, validPrice);
    }

    @Test
    @DisplayName("ECP Non-valid: Nume gol sau Preț negativ aruncă excepție")
    @Tag("ECP")
    void testUpdateProduct_InvalidECP_ShouldThrowException() {
        // Înlocuim @CsvSource cu un array pentru TestLink
        Object[][] testData = {
                {null, 15.0},         // EC Non-valid: Nume null
                {"   ", 15.0},        // EC Non-valid: Nume doar cu spații
                {"Latte", -5.0}       // EC Non-valid: Preț negativ
        };

        for (Object[] data : testData) {
            String invalidName = (String) data[0];
            double invalidPrice = (double) data[1];

            assertThrows(ValidationException.class, () -> {
                productService.updateProduct(dummyId, invalidName, invalidPrice, dummyCategorie, dummyTip);
            }, "A picat (nu a aruncat excepție) pentru datele: nume='" + invalidName + "', pret=" + invalidPrice);
        }
    }

    // ==========================================
    // 2. TESTE BVA (Boundary Value Analysis)
    // ==========================================

    @Test
    @DisplayName("BVA Valid: Limite de intrare valide")
    @Tag("BVA")
    void testUpdateProduct_ValidBVA_ShouldPass() {
        Object[][] testData = {
                {"A", 0.01},   // Limită validă: nume format dintr-un caracter, preț minim pozitiv
                {"A", 100.0}   // Limită validă: nume minim, preț normal din clasa de echivalență
        };

        for (Object[] data : testData) {
            String dummyName = (String) data[0];
            double boundaryPrice = (double) data[1];

            // Act & Assert
            assertDoesNotThrow(() -> {
                productService.updateProduct(dummyId, dummyName, boundaryPrice, dummyCategorie, dummyTip);
            }, "A aruncat excepție pentru date valide: nume='" + dummyName + "', pret=" + boundaryPrice);

            assertIsUpdated(dummyName, boundaryPrice);
        }
    }

    @Test
    @DisplayName("BVA Non-valid: Limite de intrare invalide")
    @Tag("BVA")
    void testUpdateProduct_InvalidBVA_ShouldThrowException() {
        Object[][] testData = {
                {"", 10.0},     // Limită non-validă: lungime nume 0 (șir gol)
                {"A", 0.0},     // Limită non-validă: preț exact 0.0
                {"A", -0.01}    // Limită non-validă: preț imediat sub 0
        };

        for (Object[] data : testData) {
            String dummyName = (String) data[0];
            double outOfBoundsPrice = (double) data[1];

            // Act & Assert
            assertThrows(ValidationException.class, () -> {
                productService.updateProduct(dummyId, dummyName, outOfBoundsPrice, dummyCategorie, dummyTip);
            }, "A picat (nu a aruncat excepție) pentru datele: nume='" + dummyName + "', pret=" + outOfBoundsPrice);
        }
    }
}
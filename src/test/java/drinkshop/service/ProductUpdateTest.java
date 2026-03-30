package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import drinkshop.repository.file.FileProductRepository;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @ParameterizedTest
    @DisplayName("ECP Non-valid: Nume gol sau Preț negativ aruncă excepție")
    @CsvSource({
            ", 15.0",           // EC Non-valid: Nume null
            "'   ', 15.0",      // EC Non-valid: Nume doar cu spații
            "Latte, -5.0"       // EC Non-valid: Preț negativ
    })
    @Tag("ECP")
    void testUpdateProduct_InvalidECP_ShouldThrowException(String invalidName, double invalidPrice) {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            productService.updateProduct(dummyId, invalidName, invalidPrice, dummyCategorie, dummyTip);
        });


    }

    // ==========================================
    // 2. TESTE BVA (Boundary Value Analysis)
    // ==========================================

    @ParameterizedTest
    @DisplayName("BVA Valid: Limite de intrare valide")
    @CsvSource({
            "A, 0.01",   // Limită validă: nume format dintr-un caracter, preț minim pozitiv
            "A, 100.0"   // Limită validă: nume minim, preț normal din clasa de echivalență
    })
    @Tag("BVA")
    void testUpdateProduct_ValidBVA_ShouldPass(String dummyName, double boundaryPrice) {
        // Act & Assert
        assertDoesNotThrow(() -> {
            productService.updateProduct(dummyId, dummyName, boundaryPrice, dummyCategorie, dummyTip);
        });

        assertIsUpdated(dummyName, boundaryPrice);
    }

    @ParameterizedTest
    @DisplayName("BVA Non-valid: Limite de intrare invalide")
    @CsvSource({
            "'', 10.0",   // Limită non-validă: lungime nume 0 (șir gol)
            "A, 0.0",     // Limită non-validă: preț exact 0.0
            "A, -0.01"    // Limită non-validă: preț imediat sub 0
    })
    @Tag("BVA")
    void testUpdateProduct_InvalidBVA_ShouldThrowException(String dummyName, double outOfBoundsPrice) {
        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            productService.updateProduct(dummyId, dummyName, outOfBoundsPrice, dummyCategorie, dummyTip);
        });
    }
}
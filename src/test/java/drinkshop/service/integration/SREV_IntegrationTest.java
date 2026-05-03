package drinkshop.service.integration;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.file.FileProductRepository;
import drinkshop.service.ProductService;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class SREV_IntegrationTest {

    private FileProductRepository realRepo;
    private ProductService productService;
    private final String TEST_FILE = "test_step4_srev.txt";

    @BeforeEach
    void setUp() throws IOException {
        File file = new File(TEST_FILE);
        if (file.exists()) file.delete();
        file.createNewFile();

        // Integrare completă S + R + E + V
        realRepo = new FileProductRepository(TEST_FILE);
        productService = new ProductService(realRepo);
    }

    @AfterEach
    void tearDown() {
        new File(TEST_FILE).delete();
    }

    @Test
    @DisplayName("Step 4: Integrare S+R+E+V - Update cu date valide trece prin validator și se salvează")
    void testUpdateProduct_ValidData_IntegratesWithValidator() {
        // Arrange
        Product p = new Product(10, "Ceai Negru", 8.0, CategorieBautura.TEA, TipBautura.WATER_BASED);
        productService.addProduct(p); // addProduct nu validează, doar pune în repo

        // Act - updateProduct instanțiază intern un E, îl trece prin V, apoi îl dă la R
        productService.updateProduct(10, "Ceai Negru Premium", 10.5, CategorieBautura.TEA, TipBautura.WATER_BASED);

        // Assert
        Product updatedProduct = productService.findById(10);
        assertEquals("Ceai Negru Premium", updatedProduct.getNume());
        assertEquals(10.5, updatedProduct.getPret());
    }

    @Test
    @DisplayName("Step 4: Integrare S+R+E+V - Update cu preț negativ este blocat de Validator")
    void testUpdateProduct_InvalidData_ThrowsValidationException() {
        // Arrange
        Product p = new Product(10, "Ceai Negru", 8.0, CategorieBautura.TEA, TipBautura.WATER_BASED);
        productService.addProduct(p);

        // Act & Assert - Validatorul (V) aruncă excepție, Repository-ul (R) rămâne neatins cu date corupte
        ValidationException exception = assertThrows(ValidationException.class, () ->
                productService.updateProduct(10, "Ceai Negru", -5.0, CategorieBautura.TEA, TipBautura.WATER_BASED)
        );

        assertTrue(exception.getMessage().contains("Pret invalid"), "Mesajul de eroare din Validator nu a apărut");

        // Ne asigurăm că datele vechi sunt intacte în Repo
        Product unchangedProduct = productService.findById(10);
        assertEquals(8.0, unchangedProduct.getPret(), "Prețul nu ar fi trebuit să fie modificat");
    }
}
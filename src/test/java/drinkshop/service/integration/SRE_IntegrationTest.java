package drinkshop.service.integration;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.file.FileProductRepository;
import drinkshop.service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SRE_IntegrationTest {

    private FileProductRepository realRepo;
    private ProductService productService;
    private final String TEST_FILE = "test_step3_sre.txt";

    @BeforeEach
    void setUp() throws IOException {
        File file = new File(TEST_FILE);
        if (file.exists()) file.delete();
        file.createNewFile();

        // Tot fluxul S -> R -> E este real
        realRepo = new FileProductRepository(TEST_FILE);
        productService = new ProductService(realRepo);
    }

    @AfterEach
    void tearDown() {
        new File(TEST_FILE).delete();
    }

    @Test
    @DisplayName("Step 3: Integrare S+R+E - Adăugare și regăsire folosind entități reale")
    void testFullIntegration_AddAndRetrieveEntities() {
        // Arrange - Entitate reală (E)
        Product p1 = new Product(1, "Latte", 15.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);

        // Act - S interacționează cu R pentru a salva E reală
        productService.addProduct(p1);

        // Assert
        assertEquals(1, productService.getAllProducts().size());
        assertEquals("Latte", productService.findById(1).getNume());
    }

    @Test
    @DisplayName("Step 3: Integrare S+R+E - Filtrare folosind logica entităților reale")
    void testFullIntegration_FilterByTip() {
        // Arrange
        productService.addProduct(new Product(1, "Latte", 15.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY));
        productService.addProduct(new Product(2, "Limonadă", 12.0, CategorieBautura.JUICE, TipBautura.WATER_BASED));

        // Act - Folosim un filtru din S care citește prin R și folosește logica din E (getTip)
        List<Product> waterBasedDrinks = productService.filterByTip(TipBautura.WATER_BASED);

        // Assert
        assertEquals(1, waterBasedDrinks.size(), "Ar trebui să găsim exact o băutură pe bază de apă");
        assertEquals("Limonadă", waterBasedDrinks.get(0).getNume(), "Integrarea S->R->E a eșuat la filtrare");
    }

    @Test
    @DisplayName("Step 3: Integrare S+R+E - Stergerea unui produs real functioneaza corect")
    void testFullIntegration_DeleteRealProduct() {
        // Arrange - Entitate reala (E)
        Product p = new Product(50, "Iced Latte", 18.0, CategorieBautura.ICED_COFFEE, TipBautura.DAIRY);
        productService.addProduct(p);

        // Verificam ca a fost adaugat cu succes in Repo-ul real
        assertEquals(1, productService.getAllProducts().size());
        assertNotNull(productService.findById(50));

        // Act - S interacționeaza cu R pentru a sterge E
        productService.deleteProduct(50);

        // Assert - Repo-ul real nu ar trebui sa mai conțina entitatea
        assertEquals(0, productService.getAllProducts().size(), "Produsul ar fi trebuit sters din Repo-ul real");
        assertNull(productService.findById(50), "Cautarea dupa ID-ul sters ar trebui sa returneze null");
    }
}
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SR_IntegrationTest {

    private FileProductRepository realRepo;
    private ProductService productService;
    private final String TEST_FILE = "test_step2_sr.txt";

    @Mock
    private Product mockProduct;

    @BeforeEach
    void setUp() throws IOException {
        // Setup fișier curat pentru Repo real
        File file = new File(TEST_FILE);
        if (file.exists()) file.delete();
        file.createNewFile();

        // Integrare reală S + R
        realRepo = new FileProductRepository(TEST_FILE);
        productService = new ProductService(realRepo);
    }

    @AfterEach
    void tearDown() {
        new File(TEST_FILE).delete();
    }

    @Test
    @DisplayName("Step 2: Integrare S+R - addProduct salvează un Mock E în Repository real")
    void testAddProduct_WithRealRepo_AndMockEntity() {
        // Arrange - Repo-ul real va apela metodele entității pentru a scrie pe disc
        when(mockProduct.getId()).thenReturn(99);
        when(mockProduct.getNume()).thenReturn("Mocked Coffee");
        when(mockProduct.getPret()).thenReturn(25.0);
        when(mockProduct.getCategorie()).thenReturn(CategorieBautura.SPECIAL_COFFEE);
        when(mockProduct.getTip()).thenReturn(TipBautura.BASIC);

        // Act - S apelează R, care scrie Mock-ul E
        productService.addProduct(mockProduct);

        // Assert - Verificăm dacă S+R funcționează împreună citind înapoi din repo-ul real
        Product retrieved = productService.findById(99);
        assertNotNull(retrieved, "Produsul mock ar fi trebuit să fie salvat în fișier și recuperat");
        assertEquals("Mocked Coffee", retrieved.getNume());
        assertEquals(25.0, retrieved.getPret());

        // Verify - verificăm interacțiunea cu Mock-ul E
        verify(mockProduct, atLeastOnce()).getId();
        verify(mockProduct, atLeastOnce()).getNume();
    }

    @Test
    @DisplayName("Step 2: Integrare S+R - deleteProduct șterge corect folosind Repo real")
    void testDeleteProduct_WithRealRepo() {
        // Arrange
        when(mockProduct.getId()).thenReturn(100);
        when(mockProduct.getNume()).thenReturn("To Be Deleted");
        when(mockProduct.getPret()).thenReturn(10.0);
        when(mockProduct.getCategorie()).thenReturn(CategorieBautura.TEA);
        when(mockProduct.getTip()).thenReturn(TipBautura.WATER_BASED);

        productService.addProduct(mockProduct);
        assertEquals(1, productService.getAllProducts().size());

        // Act
        productService.deleteProduct(100);

        // Assert
        assertEquals(0, productService.getAllProducts().size(), "Repository-ul real ar trebui să fie gol după ștergere");
    }

    @Test
    @DisplayName("Step 2: Integrare S+R - getAllProducts returneaza corect mock-urile salvate")
    void testGetAllProducts_WithRealRepo_AndMockEntities() {
        // Arrange - Creăm un al doilea mock local pentru acest test
        Product mockProduct2 = org.mockito.Mockito.mock(Product.class);

        // Stubbing pentru primul mock
        when(mockProduct.getId()).thenReturn(1);
        when(mockProduct.getNume()).thenReturn("Mock Cafea");
        when(mockProduct.getPret()).thenReturn(10.0);
        when(mockProduct.getCategorie()).thenReturn(CategorieBautura.CLASSIC_COFFEE);
        when(mockProduct.getTip()).thenReturn(TipBautura.BASIC);

        // Stubbing pentru al doilea mock
        when(mockProduct2.getId()).thenReturn(2);
        when(mockProduct2.getNume()).thenReturn("Mock Ceai");
        when(mockProduct2.getPret()).thenReturn(12.0);
        when(mockProduct2.getCategorie()).thenReturn(CategorieBautura.TEA);
        when(mockProduct2.getTip()).thenReturn(TipBautura.WATER_BASED);

        // Salvăm ambele mock-uri prin S -> R
        productService.addProduct(mockProduct);
        productService.addProduct(mockProduct2);

        // Act
        var allProducts = productService.getAllProducts();

        // Assert - Verificăm starea Repo-ului real
        assertEquals(2, allProducts.size(), "Ar trebui sa gasim 2 produse in repo-ul real");

        // Verify - Ne asigurăm că Repo-ul real a extras ID-urile din entitățile noastre Mock pentru a le salva
        verify(mockProduct, atLeastOnce()).getId();
        verify(mockProduct2, atLeastOnce()).getId();
    }
}
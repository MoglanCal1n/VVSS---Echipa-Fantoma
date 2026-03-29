package drinkshop.service.validator;

import drinkshop.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@Tag("BBT") // Adnotare specială #1
@DisplayName("Teste Unitare BBT pentru ProductValidator") // Adnotare specială #2
public class ProductValidatorTest {

    private final ProductValidator validator = new ProductValidator();

    /**
     * Metodă ajutătoare pentru a crea Dummy Objects.
     * Parametrii neinvestigați (id, categorie, tip) au mereu valori valide.
     */
    private Product createDummyProduct(String nume, double pret) {
        // ID-ul este mereu 1 (valid), categoriile sunt null (nevalidate aici)
        return new Product(1, nume, pret, null, null);
    }

    // ==========================================
    // 1. TESTE ECP (Equivalence Class Partitioning)
    // Minim 3/4 cazuri (valide și non-valide)
    // ==========================================

    @Test
    @DisplayName("ECP Valid: Nume completat și Preț pozitiv")
    public void testValidate_ECP_Valid() {
        // Arrange (A)
        Product p = createDummyProduct("Espresso", 15.0);

        // Act & Assert (A & A)
        // Ne așteptăm să NU arunce nicio excepție
        assertDoesNotThrow(() -> validator.validate(p));
    }

    // Adnotare specială #3 (@ParameterizedTest) și #4 (@CsvSource)
    @ParameterizedTest
    @CsvSource({
            ", 15.0, Numele nu poate fi gol!",     // EC Non-valid: Nume null
            "'   ', 15.0, Numele nu poate fi gol!", // EC Non-valid: Nume doar cu spații
            "Latte, -5.0, Pret invalid!"           // EC Non-valid: Preț negativ
    })
    @DisplayName("ECP Non-valid: Nume gol sau Preț negativ aruncă excepție")
    public void testValidate_ECP_Invalid(String nume, double pret, String expectedErrorMessage) {
        // Arrange (A)
        Product p = createDummyProduct(nume, pret);

        // Act (A)
        ValidationException exception = assertThrows(ValidationException.class, () -> validator.validate(p));

        // Assert (A)
        assertTrue(exception.getMessage().contains(expectedErrorMessage));
    }

    // ==========================================
    // 2. TESTE BVA (Boundary Value Analysis)
    // Minim 4 cazuri (2 valide, 2 non-valide)
    // ==========================================

    @ParameterizedTest
    @CsvSource({
            "A, 0.01",   // Limită validă: nume format dintr-un caracter, preț minim pozitiv
            "A, 100.0"   // Limită validă: nume minim, preț normal din clasa de echivalență
    })
    @DisplayName("BVA Valid: Limite de intrare valide")
    public void testValidate_BVA_Valid(String nume, double pret) {
        // Arrange (A)
        Product p = createDummyProduct(nume, pret);

        // Act & Assert (A & A)
        assertDoesNotThrow(() -> validator.validate(p));
    }

    @ParameterizedTest
    @CsvSource({
            "'', 10.0, Numele nu poate fi gol!", // Limită non-validă: lungime nume 0 (șir gol)
            "A, 0.0, Pret invalid!",             // Limită non-validă: preț exact 0.0
            "A, -0.01, Pret invalid!"            // Limită non-validă: preț imediat sub 0
    })
    @DisplayName("BVA Non-valid: Limite de intrare invalide")
    public void testValidate_BVA_Invalid(String nume, double pret, String expectedErrorMessage) {
        // Arrange (A)
        Product p = createDummyProduct(nume, pret);

        // Act (A)
        ValidationException exception = assertThrows(ValidationException.class, () -> validator.validate(p));

        // Assert (A)
        assertTrue(exception.getMessage().contains(expectedErrorMessage));
    }
}
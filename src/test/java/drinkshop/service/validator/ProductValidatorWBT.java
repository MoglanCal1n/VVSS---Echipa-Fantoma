package drinkshop.service.validator;

import drinkshop.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@Tag("WBT") // Adnotare specială #1
@DisplayName("Teste Unitare WBT pentru ProductValidator") // Adnotare specială #2
public class ProductValidatorWBT {

    private final ProductValidator validator = new ProductValidator();

    /**
     * Metodă ajutătoare pentru a crea Objects.
     * Spre deosebire de BBT, la WBT trebuie să controlăm și ID-ul
     * pentru a atinge ramura `id <= 0`.
     */
    private Product createProduct(int id, String nume, double pret) {
        return new Product(id, nume, pret, null, null);
    }

    // ==========================================
    // TC01: Statement / Path Coverage (Calea validă)
    // ==========================================

    @Test
    @DisplayName("TC01: Statement Coverage - Produs complet valid")
    public void testValidate_TC01_Valid() {
        // Arrange (A)
        Product p = createProduct(1, "Apa", 5.0);

        // Act & Assert (A & A)
        assertDoesNotThrow(() -> validator.validate(p));
    }

    // ==========================================
    // TC02 & TC03: Loop Coverage (Bucla while)
    // ==========================================

    @ParameterizedTest
    @CsvSource({
            "1, Apa, 0.0, Pret invalid!",  // TC02: Bucla executată exact o dată (0 -> 1)
            "1, Apa, -1.0, Pret invalid!"  // TC03: Bucla executată de 2 ori (-1 -> 0 -> 1)
    })
    @DisplayName("TC02 & TC03: Loop Coverage - Validare preț <= 0")
    public void testValidate_LoopCoverage(int id, String nume, double pret, String expectedErrorMessage) {
        // Arrange (A)
        Product p = createProduct(id, nume, pret);

        // Act (A)
        ValidationException exception = assertThrows(ValidationException.class, () -> validator.validate(p));

        // Assert (A)
        assertTrue(exception.getMessage().contains(expectedErrorMessage));
    }

    // ==========================================
    // TC04, TC05 & TC06: Condition / Decision Coverage
    // ==========================================

    @ParameterizedTest
    @CsvSource({
            "1, , 5.0, Numele nu poate fi gol!",    // TC04: Condiție nume null (Scurtcircuitare true)
            "1, '', 5.0, Numele nu poate fi gol!",  // TC05: Condiție nume gol/blank (true)
            "0, Apa, 5.0, ID invalid!"              // TC06: Condiție id <= 0 (true)
    })
    @DisplayName("TC04, TC05, TC06: Condition Coverage - Erori individuale izolate")
    public void testValidate_ConditionCoverage(int id, String nume, double pret, String expectedErrorMessage) {
        // Arrange (A)
        Product p = createProduct(id, nume, pret);

        // Act (A)
        ValidationException exception = assertThrows(ValidationException.class, () -> validator.validate(p));

        // Assert (A)
        assertTrue(exception.getMessage().contains(expectedErrorMessage));
    }

    // ==========================================
    // TC07: Multiple Condition / Path (Cumularea tuturor erorilor)
    // ==========================================

    @Test
    @DisplayName("TC07: Path Coverage - Toate condițiile evaluate la true simultan")
    public void testValidate_TC07_MultipleErrors() {
        // Arrange (A)
        // Prețul e 0 (va intra în buclă), numele e gol, id-ul este -1
        Product p = createProduct(-1, "", 0.0);

        // Act (A)
        ValidationException exception = assertThrows(ValidationException.class, () -> validator.validate(p));

        // Assert (A)
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("Pret invalid!"));
        assertTrue(actualMessage.contains("Numele nu poate fi gol!"));
        assertTrue(actualMessage.contains("ID invalid!"));
    }
}
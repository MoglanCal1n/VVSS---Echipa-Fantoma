package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Tag("UNIT-MOCK")
@DisplayName("Unit testing pentru ProductService (S) - scenariul S -> R -> E, top-down depth first")
@ExtendWith(MockitoExtension.class)
class ProductServiceMockTest {

    @Mock
    private Repository<Integer, Product> productRepo;

    @Mock
    private Product productMock;

    @Test
    @DisplayName("addProduct delega catre repo.save si trimite exact entitatea primita")
    void addProduct_delegatesToRepository() {
        ProductService service = new ProductService(productRepo);

        service.addProduct(productMock);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepo, times(1)).save(captor.capture());
        assertSame(productMock, captor.getValue(),
                "Service-ul trebuie sa salveze exact entitatea primita ca parametru");
        verifyNoMoreInteractions(productRepo);
        verifyNoInteractions(productMock);
    }

    @Test
    @DisplayName("findById returneaza produsul de la repo si nu interactioneaza cu entitatea")
    void findById_returnsRepoResult() {
        when(productRepo.findOne(42)).thenReturn(productMock);
        ProductService service = new ProductService(productRepo);

        Product result = service.findById(42);

        assertSame(productMock, result, "Service-ul trebuie sa returneze produsul oferit de repo");
        verify(productRepo, times(1)).findOne(42);
        verifyNoMoreInteractions(productRepo);
        verifyNoInteractions(productMock);
    }

    @Test
    @DisplayName("filterByCategorie(ALL) returneaza tot din repo, fara sa interogheze entitatea")
    void filterByCategorie_all_returnsEverything() {
        when(productRepo.findAll()).thenReturn(List.of(productMock));
        ProductService service = new ProductService(productRepo);

        List<Product> result = service.filterByCategorie(CategorieBautura.ALL);

        assertEquals(1, result.size());
        assertSame(productMock, result.get(0));
        verify(productRepo, times(1)).findAll();
        verifyNoInteractions(productMock);
    }

    @Test
    @DisplayName("filterByCategorie pastreaza doar produsele cu categorie potrivita (verify pe entitate)")
    void filterByCategorie_filtersByCategory() {
        Product matching = org.mockito.Mockito.mock(Product.class);
        Product other = org.mockito.Mockito.mock(Product.class);
        when(matching.getCategorie()).thenReturn(CategorieBautura.CLASSIC_COFFEE);
        when(other.getCategorie()).thenReturn(CategorieBautura.SPECIAL_COFFEE);
        when(productRepo.findAll()).thenReturn(List.of(matching, other));
        ProductService service = new ProductService(productRepo);

        List<Product> result = service.filterByCategorie(CategorieBautura.CLASSIC_COFFEE);

        assertEquals(1, result.size(), "Trebuie sa ramana exact un produs dupa filtrare");
        assertSame(matching, result.get(0));
        verify(productRepo, times(1)).findAll();
        verify(matching, times(1)).getCategorie();
        verify(other, times(1)).getCategorie();
        verify(matching, never()).getTip();
        verify(other, never()).getTip();
    }

    @Test
    @DisplayName("filterByTip pastreaza doar produsele cu tipul cerut")
    void filterByTip_filtersByType() {
        Product water = org.mockito.Mockito.mock(Product.class);
        Product milk = org.mockito.Mockito.mock(Product.class);
        when(water.getTip()).thenReturn(TipBautura.WATER_BASED);
        when(milk.getTip()).thenReturn(TipBautura.DAIRY);
        when(productRepo.findAll()).thenReturn(List.of(water, milk));
        ProductService service = new ProductService(productRepo);

        List<Product> result = service.filterByTip(TipBautura.DAIRY);

        assertEquals(1, result.size());
        assertSame(milk, result.get(0));
        verify(productRepo, times(1)).findAll();
        verify(water, times(1)).getTip();
        verify(milk, times(1)).getTip();
    }

    @Test
    @DisplayName("deleteProduct deleaga catre repo.delete cu acelasi id si nu atinge entitatea")
    void deleteProduct_delegatesToRepository() {
        ProductService service = new ProductService(productRepo);

        service.deleteProduct(7);

        verify(productRepo, times(1)).delete(7);
        verifyNoMoreInteractions(productRepo);
        verifyNoInteractions(productMock);
    }

    @Test
    @DisplayName("findById returneaza null cand repo nu gaseste produsul")
    void findById_repoReturnsNull() {
        when(productRepo.findOne(999)).thenReturn(null);
        ProductService service = new ProductService(productRepo);

        Product result = service.findById(999);

        assertNull(result);
        verify(productRepo).findOne(999);
        verifyNoMoreInteractions(productRepo);
    }

    @Test
    @DisplayName("getAllProducts returneaza fix lista data de repo")
    void getAllProducts_returnsRepoList() {
        when(productRepo.findAll()).thenReturn(List.of(productMock));
        ProductService service = new ProductService(productRepo);

        List<Product> result = service.getAllProducts();

        assertTrue(result.contains(productMock));
        assertEquals(1, result.size());
        verify(productRepo, times(1)).findAll();
        verifyNoMoreInteractions(productRepo);
        verifyNoInteractions(productMock);
    }
}

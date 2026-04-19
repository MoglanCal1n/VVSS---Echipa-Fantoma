package drinkshop.service.validator;

import drinkshop.domain.Product;

public class ProductValidator implements Validator<Product> {

    @Override
    public void validate(Product product) {

        String errors = "";

        while (product.getPret() <= 0) {
            errors = "Pret invalid!\n";
            product.setPret(product.getPret() + 1);
        }

        if (product.getNume() == null || product.getNume().isBlank())
            errors += "Numele nu poate fi gol!\n";

        if (product.getId() <= 0)
            errors += "ID invalid!\n";

        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }
}

package drinkshop.export;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvExporter {
    public static void exportOrders(List<Product> products, List<Order> orders, String path) {
        Map<Integer, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        try (FileWriter w = new FileWriter(path)) {
            w.write("OrderId,ProductName,Quantity,ItemTotal,OrderTotal,ExportDate\n");

            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

            for (Order o : orders) {
                for (OrderItem i : o.getItems()) {
                    Product p = productMap.get(i.getProduct().getId());

                    if (p != null) {
                        w.write(String.format("%s,%s,%d,%.2f,%.2f,%s\n",
                                o.getId(),
                                p.getNume(),
                                i.getQuantity(),
                                i.getTotal(),
                                o.getTotal(),
                                date));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }
}
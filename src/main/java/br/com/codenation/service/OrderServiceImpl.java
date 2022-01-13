package br.com.codenation.service;

import java.util.*;

import br.com.codenation.model.OrderItem;
import br.com.codenation.model.Product;
import br.com.codenation.repository.ProductRepository;
import br.com.codenation.repository.ProductRepositoryImpl;

public class OrderServiceImpl implements OrderService {

	private ProductRepository productRepository = new ProductRepositoryImpl();

	/**
	 * Calculate the sum of all OrderItems
	 */
	@Override
	public Double calculateOrderValue(List<OrderItem> items) {
		List<Double> values = new ArrayList<>();
		Double sum = 0.00;

		items.stream().forEach(item -> {
			Optional<Product> optionalProduct = productRepository.findById(item.getProductId());

			if (optionalProduct.get().getIsSale()) {
				Double productValue = applyDiscount(optionalProduct.get().getValue());
				values.add(productValue * item.getQuantity());
			} else {
				values.add(optionalProduct.get().getValue() * item.getQuantity());
			}
		});

		sum = values.stream().reduce(0.00, Double::sum);

		return sum;
	}

	/**
	 * Map from idProduct List to Product Set
	 */
	@Override
	public Set<Product> findProductsById(List<Long> ids) {
		Set<Product> productList = new HashSet<Product>();

		for (Long i : ids) {
			Optional<Product> product = productRepository.findById(i);

			product.ifPresent(prod -> productList.add(prod));
		}

		return productList;
	}

	/**
	 * Calculate the sum of all Orders(List<OrderIten>)
	 */
	@Override
	public Double calculateMultipleOrders(List<List<OrderItem>> orders) {
		Double total = 0.00;

		for (List<OrderItem> order : orders) {
			total += calculateOrderValue(order);
		}

		return total;
	}

	/**
	 * Group products using isSale attribute as the map key
	 */
	@Override
	public Map<Boolean, List<Product>> groupProductsBySale(List<Long> productIds) {
		Map<Boolean, List<Product>> mapProductsBySale = new HashMap<>();
		List<Product> productsSaleTrue = new ArrayList<>();
		List<Product> productsSaleFalse = new ArrayList<>();

		for (Long i : productIds) {
			Optional<Product> product = productRepository.findById(i);

			if (product.get().getIsSale()) {
				productsSaleTrue.add(product.get());
			} else {
				productsSaleFalse.add(product.get());
			}
		}

		mapProductsBySale.put(true, productsSaleTrue);
		mapProductsBySale.put(false, productsSaleFalse);

		return mapProductsBySale;
	}

	public Double applyDiscount(Double price) {
		Double discount = price * 0.20;

		return price - discount;
	}
}
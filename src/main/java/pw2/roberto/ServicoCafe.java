package pw2.roberto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ServicoCafe {

	private Map<Integer, Cafe> listaCafe = new HashMap<>();

	private AtomicLong counter = new AtomicLong(0);

	@CircuitBreaker(requestVolumeThreshold = 4)
	public Integer getAvailability(Cafe cafe) {
		maybeFail();

		return new Random().nextInt(30);
	}

	private void maybeFail() {
		final Long invocationNumber = counter.getAndIncrement();
		if (invocationNumber % 4 > 1) {
			throw new RuntimeException("O serviço falhou");
		}
	}

	public ServicoCafe() {
		listaCafe.put(1, new Cafe(1, "Melita", "Brasil", 10));
		listaCafe.put(2, new Cafe(2, "Nescafé", "Brasil", 20));
		listaCafe.put(3, new Cafe(3, "Super Mocacchino", "EUA", 50));
	}

	public List<Cafe> getAllCafes() {
		return new ArrayList<>(listaCafe.values());
	}

	public Cafe getCafeById(Integer id) {

		return listaCafe.get(id);
	}

	public List<Cafe> getRecomendations(Integer id) {

		if (id == null) {
			return Collections.emptyList();
		}

		return listaCafe.values().stream()
				.filter(cafe -> !id.equals(cafe.getId()))
				.limit(2)
				.collect(Collectors.toList());
	}
}

package pw2.roberto;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/cafe")
public class CafeRecurso {

	private static final Logger LOGGER = Logger.getLogger(CafeRecurso.class);

	@Inject
	ServicoCafe serviceCafe;

	private AtomicLong counter = new AtomicLong(0);

	@GET
	@Retry(maxRetries = 4)
	public List<Cafe> cafes() {

		final Long invocationNumber = counter.getAndIncrement();

		maybeFail(String.format("CafeRecurso#cafes() invocação #%d falhou", invocationNumber));

		LOGGER.infof("CafeRecurso#cafes invocação #%d retornando com sucesso", invocationNumber);

		return serviceCafe.getAllCafes();
	}

	private void maybeFail(String failureLogMessage) {
		if (new Random().nextBoolean()) {
			LOGGER.error(failureLogMessage);

			throw new RuntimeException("Falha de Recurso");
		}
	}

	@GET
	@Path("/{id}/recomendations")
	@Timeout(250)
	public List<Cafe> recomendations(int id) {
		long started = System.currentTimeMillis();

		final long invocationNumber = counter.getAndIncrement();

		try {
			randomDelay();
			LOGGER.infof("CafeRecurso#recomendations() invocação #%d retornando com sucesso", invocationNumber);

			return serviceCafe.getRecomendations(id);

		} catch (InterruptedException e) {
			LOGGER.errorf("CafeRecurso#recomendations() invocação #%d expirou depois de %d ms", invocationNumber,
					System.currentTimeMillis() - started);
			return null;
		}
	}

	private void randomDelay() throws InterruptedException {
		Thread.sleep(new Random().nextInt(500));
	}

	public List<Cafe> fallbackRecomendations(int id) {
		LOGGER.info("Fazendo um fallback de RecomendationsResource#fallbackRecomendations()");

		return Collections.singletonList(serviceCafe.getCafeById(1));
	}

	@Path("/{id}/availability")
	@GET
	public Response availability(int id) {
		final Long invocationNumber = counter.getAndIncrement();

		Cafe cafe = serviceCafe.getCafeById(id);

		if (cafe == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		try {
			Integer availability = serviceCafe.getAvailability(cafe);
			LOGGER.infof("CafeRecurso#availability() invocação #%d retornando com sucesso", invocationNumber);

			return Response.ok(availability).build();

		} catch (RuntimeException e) {
			String message = e.getClass().getSimpleName() + ": " + e.getMessage();

			LOGGER.errorf("CafeRecurso#availability() invocação #%d falhou: %s", invocationNumber, message);

			// @formatter: off
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(message)
					.type(MediaType.TEXT_PLAIN_TYPE)
					.build();
			// @formatter: on
		}
	}
}

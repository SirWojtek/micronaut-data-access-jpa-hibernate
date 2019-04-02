package example.micronaut;

import static org.junit.jupiter.api.Assertions.assertFalse;

import io.micronaut.test.annotation.MicronautTest;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;

@MicronautTest
public class TransactionTest {

  @PersistenceContext
  EntityManager entityManager;

  @Test
  public void isInTransactionTest() {
    assertFalse(entityManager.getTransaction().isActive());
  }

}

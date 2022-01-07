package edu.neu.ecommerce.config;

import edu.neu.ecommerce.entity.Country;
import edu.neu.ecommerce.entity.Order;
import edu.neu.ecommerce.entity.Product;
import edu.neu.ecommerce.entity.ProductCategory;
import edu.neu.ecommerce.entity.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class DataRestConfig implements RepositoryRestConfigurer {

  @Value("${allowed.origins}")
  private String[] allowedOrigins;

  private final EntityManager entityManager;

  @Autowired
  public DataRestConfig(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config,
      CorsRegistry cors) {
    HttpMethod[] theUnsupportedActions =
        {HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.PATCH};

    disableHttpMethods(Product.class, config, theUnsupportedActions);
    disableHttpMethods(ProductCategory.class, config, theUnsupportedActions);
    disableHttpMethods(Country.class, config, theUnsupportedActions);
    disableHttpMethods(State.class, config, theUnsupportedActions);
    disableHttpMethods(Order.class, config, theUnsupportedActions);

    exposeIds(config);

    // configure cors mapping
    cors.addMapping(config.getBasePath() + "/**").allowedOrigins(allowedOrigins);
  }

  private void disableHttpMethods(Class theClass,
      RepositoryRestConfiguration config,
      HttpMethod[] theUnsupportedActions) {
    config.getExposureConfiguration()
        .forDomainType(theClass)
        .withItemExposure(((metadata, httpMethods) -> httpMethods.disable(theUnsupportedActions)))
        .withCollectionExposure(
            (metadata, httpMethods) -> httpMethods.disable(theUnsupportedActions));
  }

  private void exposeIds(RepositoryRestConfiguration config) {
    // Get a list of all entity classes from the entity manager
    Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
    // Create an array of the entity types
    List<Class> entityClasses = new ArrayList<>();
    // Get the entity types for the entities
    for (EntityType entity : entities) {
      entityClasses.add(entity.getJavaType());
    }
    // Expose the entity ids for the array of entity/domain types
    Class[] domainTypes = entityClasses.toArray(new Class[0]);
    config.exposeIdsFor(domainTypes);
  }
}

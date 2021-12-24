package edu.neu.ecommerce.config;

import edu.neu.ecommerce.entity.Product;
import edu.neu.ecommerce.entity.ProductCategory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class MyDataRestConfig implements RepositoryRestConfigurer {

  private EntityManager entityManager;

  @Autowired
  public MyDataRestConfig(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config,
      CorsRegistry cors) {
    HttpMethod[] theUnsupportedActions = {HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE};

    config.getExposureConfiguration().forDomainType(Product.class)
        .withItemExposure(((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions)))
        .withCollectionExposure(
            (metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions));

    config.getExposureConfiguration().forDomainType(ProductCategory.class)
        .withItemExposure(((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions)))
        .withCollectionExposure(
            (metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions));

    exposeIds(config);
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

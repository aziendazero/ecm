package it.tredi.ecm.config;

import org.javers.core.Javers;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.metamodel.clazz.EntityDefinitionBuilder;
import org.javers.core.metamodel.clazz.ValueObjectDefinitionBuilder;
import org.javers.hibernate.integration.HibernateUnproxyObjectAccessHook;
import org.javers.spring.boot.sql.JaversProperties;
import org.javers.spring.jpa.TransactionalJaversBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.repository.sql.SqlRepositoryBuilder;
import org.springframework.transaction.PlatformTransactionManager;

import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.DettaglioAttivitaRES;
import it.tredi.ecm.dao.entity.Partner;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.entity.PersonaFullEvento;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.ProgrammaGiornalieroRES;
import it.tredi.ecm.dao.entity.RendicontazioneInviata;
import it.tredi.ecm.dao.entity.Sponsor;

import org.javers.repository.sql.DialectName;
import org.javers.core.MappingStyle;

@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties({JaversProperties.class, JpaProperties.class})
@AutoConfigureAfter({HibernateJpaAutoConfiguration.class})
public class JaversSqlConfiguration {
	@Autowired DialectName javersSqlDialectName;

	@Autowired
	private JaversProperties javersProperties;

	@Bean(name={"javers"})
	  public Javers javers(ConnectionProvider connectionProvider, PlatformTransactionManager transactionManager)
	  {
	    JaversSqlRepository sqlRepository = SqlRepositoryBuilder.sqlRepository()
	      .withConnectionProvider(connectionProvider)
	      .withDialect(javersSqlDialectName)
	      .build();

	    return TransactionalJaversBuilder.javers()
	      .withTxManager(transactionManager)
  		//.registerValueObject(PersonaEvento.class)
	      //.registerValueObject(ValueObjectDefinitionBuilder.valueObjectDefinition(PersonaEvento.class).withTypeName("PersonaEvento").build())
	      .registerValueObject(ValueObjectDefinitionBuilder.valueObjectDefinition(RendicontazioneInviata.class).withTypeName("RendicontazioneInviata").build())
	      .registerValueObject(ValueObjectDefinitionBuilder.valueObjectDefinition(PersonaFullEvento.class).withTypeName("PersonaFullEvento").build())
	      .registerValueObject(ValueObjectDefinitionBuilder.valueObjectDefinition(Sponsor.class).withTypeName("Sponsor").build())
	      .registerValueObject(ValueObjectDefinitionBuilder.valueObjectDefinition(Partner.class).withTypeName("Partner").build())
	      .registerValueObject(ValueObjectDefinitionBuilder.valueObjectDefinition(ProgrammaGiornalieroRES.class).withTypeName("ProgrammaGiornalieroRES").build())
	      .registerValueObject(ValueObjectDefinitionBuilder.valueObjectDefinition(DettaglioAttivitaRES.class).withTypeName("DettaglioAttivitaRES").build())
	      .registerValueObject(ValueObjectDefinitionBuilder.valueObjectDefinition(Anagrafica.class).withTypeName("Anagrafica").build())
	      //.registerValueObject(ValueObjectDefinitionBuilder.valueObjectDefinition(Professione.class).withTypeName("Professione").build())

	      //.registerEntity(EntityDefinitionBuilder.entityDefinition(Persona.class).withTypeName("Persona").)

	      .registerJaversRepository(sqlRepository)
	      .withObjectAccessHook(new HibernateUnproxyObjectAccessHook())
	      .withListCompareAlgorithm(ListCompareAlgorithm.valueOf(this.javersProperties.getAlgorithm().toUpperCase()))
	      .withMappingStyle(MappingStyle.valueOf(this.javersProperties.getMappingStyle().toUpperCase()))
	      .withNewObjectsSnapshot(this.javersProperties.isNewObjectSnapshot())
	      .withPrettyPrint(this.javersProperties.isPrettyPrint())
	      .withTypeSafeValues(this.javersProperties.isTypeSafeValues())
	      .withPackagesToScan(this.javersProperties.getPackagesToScan())
	      .build();
	  }
}

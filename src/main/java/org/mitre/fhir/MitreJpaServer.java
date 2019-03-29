
package org.mitre.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.jpa.config.BaseJavaConfigDstu3;
import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.jpa.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.JpaConformanceProviderDstu2;
import ca.uhn.fhir.jpa.provider.JpaSystemProviderDstu2;
import ca.uhn.fhir.jpa.provider.dstu3.JpaConformanceProviderDstu3;
import ca.uhn.fhir.jpa.provider.dstu3.JpaSystemProviderDstu3;
import ca.uhn.fhir.jpa.provider.dstu3.TerminologyUploaderProviderDstu3;
import ca.uhn.fhir.jpa.rp.dstu3.PatientResourceProvider;
import ca.uhn.fhir.jpa.search.DatabaseBackedPagingProvider;
import ca.uhn.fhir.jpa.subscription.SubscriptionInterceptorLoader;
import ca.uhn.fhir.model.dstu2.composite.MetaDt;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.ETagSupportEnum;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Meta;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim Shaffer
 */
public class MitreJpaServer extends RestfulServer {
    private static final long serialVersionUID = 1L;

    @Override
    protected void initialize() throws ServletException {
        super.initialize();

        // This will add all the resources available in HAPI DSTU3.
        FhirVersionEnum fhirVersion = FhirVersionEnum.DSTU3;
        setFhirContext(new FhirContext(fhirVersion));

        // Get the Spring context from the web container (it's declared in web.xml)
        WebApplicationContext appContext = ContextLoaderListener.getCurrentWebApplicationContext();

        // myResourceProvidersDstu3 is generated as a part of hapi-fhir-jpaserver-base.
        // It contains bean definitions for a resource provider for each resource type.
        @SuppressWarnings({"unchecked", "ConstantConditions"})
        List<IResourceProvider> beans = appContext.getBean("myResourceProvidersDstu3", List.class);
        setResourceProviders(beans);

        // mySystemProviderDstu3 is generated as a part of hapi-fhir-jpaserver-base.
        // The system provider implements non-resource-type methods, such as transaction, and global history.
        Object systemProvider = appContext.getBean("mySystemProviderDstu3", JpaSystemProviderDstu3.class);
        registerProviders(systemProvider);

        // mySystemDaoDstu3 is generated as a part of hapi-fhir-jpaserver-base.
        // The conformance provider exports the supported resources, search parameters, etc for this server.
        // The JPA version adds resource counts to the exported statement, so it is a nice addition.
        @SuppressWarnings("unchecked")
        IFhirSystemDao<Bundle, Meta> systemDao = appContext.getBean("mySystemDaoDstu3", IFhirSystemDao.class);
        JpaConformanceProviderDstu3 confProvider = new JpaConformanceProviderDstu3(this, systemDao, appContext.getBean(DaoConfig.class));
        confProvider.setImplementationDescription("Example Server");
        setServerConformanceProvider(confProvider);

        // Enable e-tag support.
        setETagSupport(ETagSupportEnum.ENABLED);

        // Dynamically generate narratives.
        // FhirContext ctx = getFhirContext();
        // ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());

        // Default to JSON and pretty printing
        setDefaultPrettyPrint(true);
        setDefaultResponseEncoding(EncodingEnum.JSON);

        // This configures the server to page search results to and from the database, instead of only to memory.
        // This may mean a performance hit when performing searches that return lots of results,
        // but makes the server much more scalable.
        setPagingProvider(appContext.getBean(DatabaseBackedPagingProvider.class));

        // Register interceptors for the server based on DaoConfig.getSupportedSubscriptionTypes()
        SubscriptionInterceptorLoader subscriptionInterceptorLoader = appContext.getBean(SubscriptionInterceptorLoader.class);
        subscriptionInterceptorLoader.registerInterceptors();

        // If you are using DSTU3+, you may want to add a terminology uploader.
        // This allows uploading of external terminologies such as Snomed CT.
        // It does not have any security attached (any anonymous user may use it by default).
        // Consider using an AuthorizationInterceptor with this feature.
        registerProvider(appContext.getBean(TerminologyUploaderProviderDstu3.class));
    }

}

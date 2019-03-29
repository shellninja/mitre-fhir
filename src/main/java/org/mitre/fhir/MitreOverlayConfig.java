package org.mitre.fhir;

import ca.uhn.fhir.to.TesterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.to.FhirTesterMvcConfig;

/**
 * @author Tim Shaffer
 */
@Configuration
@Import(FhirTesterMvcConfig.class)
public class MitreOverlayConfig {

    @Bean
    public TesterConfig testerConfig() {
        TesterConfig config = new TesterConfig();
        config.addServer()
                .withId("home")
                .withFhirVersion(FhirVersionEnum.DSTU3)
                .withBaseUrl("${serverBase}/dstu3")
                .withName("MITRE DSTU3");
        return config;
    }

}

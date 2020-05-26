package io.reflectoring.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindException;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * We create Spring Application dynamically to catch and test application context startup exceptions
 */
class PropertiesInvalidInputTest {

    SpringApplication application;
    Properties properties;

    @BeforeEach
    void setup() {
        // create Spring Application dynamically
        application = new SpringApplication(ValidationApplication.class);

        // setting test properties for our Spring Application
        properties = new Properties();

        ConfigurableEnvironment environment = new StandardEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.addFirst(new PropertiesPropertySource("application-test", properties));
        application.setEnvironment(environment);
    }

    @Test
    void whenGivenNameEmpty_thenNotEmptyValidationFails() {

        properties.put("app.properties.name", "");

        assertThatThrownBy(application::run)
                .isInstanceOf(ConfigurationPropertiesBindException.class)
                .hasRootCauseInstanceOf(BindValidationException.class)
                .hasStackTraceContaining("Field error in object 'app.properties' on field 'name'")
                .hasStackTraceContaining("[must not be blank]");

    }

    @Test
    void whenGivenReportIntervalInDaysMoreThan30_thenMaxValidationFails() {

        properties.put("app.properties.report.interval-in-days", "31");

        assertThatThrownBy(application::run)
                .isInstanceOf(ConfigurationPropertiesBindException.class)
                .hasRootCauseInstanceOf(BindValidationException.class)
                .hasStackTraceContaining("Field error in object 'app.properties' on field 'report.intervalInDays'")
                .hasStackTraceContaining("[must be less than or equal to 30]");

    }

    @Test
    void whenGivenReportIntervalInDaysLessThan7_thenMinValidationFails() {

        properties.put("app.properties.report.interval-in-days", "6");

        assertThatThrownBy(application::run)
                .isInstanceOf(ConfigurationPropertiesBindException.class)
                .hasRootCauseInstanceOf(BindValidationException.class)
                .hasStackTraceContaining("Field error in object 'app.properties' on field 'report.intervalInDays'")
                .hasStackTraceContaining("[must be greater than or equal to 7]");

    }

    @Test
    void whenGivenReportEmailAddressIsNotWellFormed_thenEmailValidationFails() {

        properties.put("app.properties.report.email-address", "manager.analysisapp.com");

        assertThatThrownBy(application::run)
                .isInstanceOf(ConfigurationPropertiesBindException.class)
                .hasRootCauseInstanceOf(BindValidationException.class)
                .hasStackTraceContaining("Field error in object 'app.properties' on field 'report.emailAddress'")
                .hasStackTraceContaining("[must be a well-formed email address]");

    }

    @Test
    void whenGivenReportEmailAddressDoesNotContainAnalysisappDomain_thenCustomEmailValidatorFails() {

        properties.put("app.properties.report.email-address", "manager@notanalysisapp.com");

        assertThatThrownBy(application::run)
                .isInstanceOf(ConfigurationPropertiesBindException.class)
                .hasRootCauseInstanceOf(BindValidationException.class)
                .hasStackTraceContaining("Field error in object 'app.properties.report' on field 'emailAddress'")
                .hasStackTraceContaining("[The email address must contain [@analysisapp.com] domain]");

    }

    @Test
    void whenGivenThirdPartyComponentNameIsEmpty_thenNotEmptyValidationFails() {

        properties.put("app.third-party.properties.name", "");

        assertThatThrownBy(application::run)
                .isInstanceOf(ConfigurationPropertiesBindException.class)
                .hasRootCauseInstanceOf(BindValidationException.class)
                .hasStackTraceContaining("Field error in object 'app.third-party.properties' on field 'name'")
                .hasStackTraceContaining("[must not be blank]");

    }

}
package no.difi.move.deploymanager.config;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Mod11Check;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.net.URL;

@Data
@Validated
@ConfigurationProperties(prefix = "deploymanager")
public class DeployManagerProperties {

    /**
     * Organization number to run as.
     */
    @NotNull(message = "deploymanager.orgnumber is not set. This property is required.")
    @Digits(integer = 9, fraction = 0, message = "deploymanager.orgnumber must be exactly 9 digits")
    @Length(min = 9, max = 9, message = "deploymanager.orgnumber must be exactly 9 digits")
    @Mod11Check(threshold = 7, message = "deploymanager.orgnumber has wrong control character")
    private String orgnumber;

    @NotNull
    private URL mavenCentral;

    @NotNull
    private String groupId;

    @NotNull
    private String artifactId;

    @NotNull
    @Positive
    private Integer actuatorConnectTimeoutInMs;

    @NotNull
    @Positive
    private Integer actuatorReadTimeoutInMs;

    @NotNull
    @Positive
    private Integer shutdownRetries;

    @NotNull
    @Positive
    private Integer shutdownPollIntervalInMs;

    @NotNull
    @Positive
    private Integer launchTimeoutInMs;

    @NotNull
    @Positive
    private Integer launchPollIntervalInMs;

    @NotNull
    @Positive
    private Integer mavenCentralConnectTimeoutInMs;

    @NotNull
    @Positive
    private Integer mavenCentralReadTimeoutInMs;

    @NotNull
    @NotEmpty
    private String schedulerCronExpression;

    @Valid
    @NestedConfigurationProperty
    private BlocklistProperties blocklist;

    @Valid
    @NestedConfigurationProperty
    private IntegrasjonspunktProperties integrasjonspunkt;

    @Valid
    @NestedConfigurationProperty
    private VerificationProperties verification;

    @Valid
    @NestedConfigurationProperty
    private MailProperties mail;

    @NestedConfigurationProperty
    private EnvironmentProperties environment;

}

Feature: Synchronization - Upgrade

  Background:
    Given a "GET" request to "/manage/info" will respond with status "200" and the following "application/vnd.spring-boot.actuator.v1+json;charset=UTF-8"
    """
    {
        "build": {
                      "version": "1.7.92-SNAPSHOT",
                      "artifact": "integrasjonspunkt",
                      "name": "Meldingsutveksling Integrasjonspunkt",
                      "group": "no.difi.meldingsutveksling",
                      "time": "2021-02-23T14:23:09.751Z"
                  }
    }
    """
    And the "integrasjonspunkt-1.7.92-SNAPSHOT.jar" exists as a copy of "/cucumber/success.jar"
    And the latest integrasjonspunkt version is "2.2.1"
    And the early bird setting is not activated
    And the supported major version is "2"
    And a "GET" request to "/maven2/no/difi/meldingsutveksling/integrasjonspunkt/2.2.1/integrasjonspunkt-2.2.1.jar" will respond with status "200" and the following "application/java-archive" in "/cucumber/success.jar"
    And a "GET" request to "/maven2/no/difi/meldingsutveksling/integrasjonspunkt/2.2.1/integrasjonspunkt-2.2.1.jar.sha1" will respond with status "200" and the following "application/octet-stream"
    """
    39ba01879f7ededa62f7e5129f140089795e05bc
    """
    And a "GET" request to "/maven2/no/difi/meldingsutveksling/integrasjonspunkt/2.2.1/integrasjonspunkt-2.2.1.jar.md5" will respond with status "200" and the following "application/octet-stream"
    """
    e343ab4e4151f822331e7f5998b26ecc
    """
    And a "GET" request to "/maven2/no/difi/meldingsutveksling/integrasjonspunkt/2.2.1/integrasjonspunkt-2.2.1.jar.asc" will respond with status "200" and the following "text/plain" in "/cucumber/success.jar.asc"
    And state is "Started" then a "GET" request to "/manage/health" will respond with status "200" and the following "application/vnd.spring-boot.actuator.v1+json;charset=UTF-8"
    """
    {
        "status": "UP"
    }
    """
    And state is "Started" then a "POST" request to "/manage/shutdown" will set the state to "Stopped" and respond with status "200" and the following "application/vnd.spring-boot.actuator.v1+json;charset=UTF-8"
    """
    {
        "message": "Shutting down, bye..."
    }
    """
    And state is "Stopped" then a "GET" request to "/manage/health" will set the state to "Started" and respond with connection refused

  Scenario: Upgrade
    Given the synchronization handler is triggered
    Then the "integrasjonspunkt-2.2.1.jar" is successfully launched
    And an email is sent with subject "Upgrade SUCCESS integrasjonspunkt-2.2.1.jar" and content:
    """
    Started IntegrasjonspunktApplication
    """
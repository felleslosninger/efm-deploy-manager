package no.difi.move.kosmos.service.launcher;

import lombok.SneakyThrows;
import no.difi.move.kosmos.config.KosmosProperties;
import no.difi.move.kosmos.config.IntegrasjonspunktProperties;
import no.difi.move.kosmos.domain.HealthStatus;
import no.difi.move.kosmos.service.actuator.ActuatorService;
import no.difi.move.kosmos.service.launcher.dto.LaunchStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LauncherServiceImpl.class, File.class})
public class LauncherServiceImplTest {
    @Mock
    private KosmosProperties properties;
    @Mock
    private ActuatorService actuatorService;
    @Mock
    private EnvironmentService environmentService;
    @Mock
    private ProcessExecutor processExecutorMock;
    @Mock
    private StartedProcess startedProcessMock;
    @Mock
    private Future<ProcessResult> futureMock;
    @Mock
    private File fileMock;
    @Mock
    private StartupLog startupLogMock;

    @InjectMocks
    private LauncherServiceImpl launcherService;

    @Captor
    private ArgumentCaptor<List<String>> listArgumentCaptor;

    @Before
    @SneakyThrows
    public void before() {
        given(properties.getLaunchPollIntervalInMs()).willReturn(100);
        given(properties.getLaunchTimeoutInMs()).willReturn(300);
        IntegrasjonspunktProperties integrasjonspunktProperties = mock(IntegrasjonspunktProperties.class);
        given(integrasjonspunktProperties.isIncludeLog()).willReturn(false);
        given(properties.getIntegrasjonspunkt()).willReturn(integrasjonspunktProperties);
        given(properties.getIntegrasjonspunkt()).willReturn(
                new IntegrasjonspunktProperties()
                        .setProfile("staging")
                        .setHome("/tmp/root")
        );
        given(properties.getOrgnumber()).willReturn("910077473");
        whenNew(StartupLog.class).withAnyArguments().thenReturn(startupLogMock);
        given(startupLogMock.getLog()).willReturn("theStartUpLog");
        whenNew(ProcessExecutor.class).withAnyArguments().thenReturn(processExecutorMock);
        whenNew(File.class).withAnyArguments().thenReturn(fileMock);
        given(processExecutorMock.directory(any())).willReturn(processExecutorMock);
        given(processExecutorMock.environment(any())).willReturn(processExecutorMock);
        given(processExecutorMock.redirectOutput(any())).willReturn(processExecutorMock);
        given(processExecutorMock.start()).willReturn(startedProcessMock);
        given(startedProcessMock.getFuture()).willReturn(futureMock);
    }

    @Test
    @SneakyThrows
    public void testLaunchIntegrasjonspunkt_whenSuccess() {
        given(actuatorService.getStatus()).willReturn(HealthStatus.UP);
        given(environmentService.getChildProcessEnvironment()).willReturn(new HashMap<>());

        assertThat(launcherService.launchIntegrasjonspunkt("test.jar"))
                .hasFieldOrPropertyWithValue("jarPath", "test.jar")
                .hasFieldOrPropertyWithValue("status", LaunchStatus.SUCCESS)
                .hasFieldOrPropertyWithValue("startupLog", "theStartUpLog");

        verifyNew(StartupLog.class).withArguments(false);
        verifyNew(File.class).withArguments("/tmp/root");
        verifyNew(ProcessExecutor.class).withArguments(listArgumentCaptor.capture());

        assertThat(listArgumentCaptor.getValue()).containsExactly("java", "-jar", "test.jar",
                "--management.endpoint.shutdown.enabled=true",
                "--app.logger.enableSSL=false",
                "--spring.profiles.active=staging"
        );

        verify(futureMock, never()).cancel(anyBoolean());
    }

    @Test
    @SneakyThrows
    public void testLaunchIntegrasjonspunkt_whenActuatorReturnsFailure() {
        given(actuatorService.getStatus()).willReturn(HealthStatus.UNKNOWN);

        assertThat(launcherService.launchIntegrasjonspunkt("test.jar"))
                .hasFieldOrPropertyWithValue("jarPath", "test.jar")
                .hasFieldOrPropertyWithValue("status", LaunchStatus.FAILED)
                .hasFieldOrPropertyWithValue("startupLog", "theStartUpLog");
    }

    @Test
    @SneakyThrows
    public void testLaunchIntegrasjonspunkt_whenStartUpLogReturnsFailure() {
        assertThat(launcherService.launchIntegrasjonspunkt("test.jar"))
                .hasFieldOrPropertyWithValue("jarPath", "test.jar")
                .hasFieldOrPropertyWithValue("status", LaunchStatus.FAILED)
                .hasFieldOrPropertyWithValue("startupLog", "theStartUpLog");

        verify(futureMock).cancel(true);
    }

    @Test
    @SneakyThrows
    public void testLaunchIntegrasjonspunkt_whenTimeOut() {
        assertThat(launcherService.launchIntegrasjonspunkt("test.jar"))
                .hasFieldOrPropertyWithValue("jarPath", "test.jar")
                .hasFieldOrPropertyWithValue("status", LaunchStatus.FAILED)
                .hasFieldOrPropertyWithValue("startupLog", "theStartUpLog");

        verify(futureMock).cancel(true);
    }

    @Test
    @SneakyThrows(IOException.class)
    public void testLaunchIntegrasjonspunkt_whenIOException() {
        IOException exception = new IOException("test exception");
        given(processExecutorMock.start()).willThrow(exception);

        assertThat(launcherService.launchIntegrasjonspunkt("test.jar"))
                .hasFieldOrPropertyWithValue("jarPath", "test.jar")
                .hasFieldOrPropertyWithValue("status", LaunchStatus.FAILED)
                .hasFieldOrPropertyWithValue("startupLog", "test exception");
    }
}

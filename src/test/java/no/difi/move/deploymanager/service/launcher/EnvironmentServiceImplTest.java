package no.difi.move.deploymanager.service.launcher;

import no.difi.move.deploymanager.config.DeployManagerProperties;
import no.difi.move.deploymanager.config.EnvironmentProperties;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EnvironmentServiceImpl.class, System.class})
public class EnvironmentServiceImplTest {

    @InjectMocks
    private EnvironmentServiceImpl target;

    @Mock
    private DeployManagerProperties properties;
    private static final Map<String, String> MOCK_ENVIRONMENT = new HashMap<String, String>() {{
        put("prefix1.name.here", "value1");
        put("prefix2.name.here", "value2");
    }};

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(System.class);
        PowerMockito.when(System.getenv()).thenReturn(MOCK_ENVIRONMENT);
    }

    @Test
    public void getChildProcessEnvironment_NoExclusions_ChildAndParentEnvShouldBeEqual() {
        EnvironmentProperties environmentProperties = mock(EnvironmentProperties.class);
        when(environmentProperties.getPrefixesRemovedFromChildProcess())
                .thenReturn(Lists.newArrayList());
        when(properties.getEnvironment()).thenReturn(environmentProperties);

        Map<String, String> result = target.getChildProcessEnvironment();

        assertEquals(result, MOCK_ENVIRONMENT);
    }

    @Test
    public void getChildProcessEnvironment_OnePrefixExcluded_ChildAndParentEnvShouldBeDifferent() {
        EnvironmentProperties environmentProperties = mock(EnvironmentProperties.class);
        when(environmentProperties.getPrefixesRemovedFromChildProcess())
                .thenReturn(Lists.newArrayList("prefix1"));
        when(properties.getEnvironment()).thenReturn(environmentProperties);

        Map<String, String> result = target.getChildProcessEnvironment();

        assertNotEquals(result, MOCK_ENVIRONMENT);
        assertNull(result.get("prefix1.name.here"));
        assertEquals("value2", result.get("prefix2.name.here"));
    }
}
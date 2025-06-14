package org.opencourse.configs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link ApplicationConfig}.
 * 
 * @author !EEExp3rt
 */
@SpringBootTest(classes = ApplicationConfig.class)
class ApplicationConfigTest {

    // Application configuration class to be tested.
    @Autowired
    private ApplicationConfig applicationConfig;

    @Test
    @DisplayName("Test if ApplicationConfig is injected correctly")
    void testConfigurationInjected() {
        assertThat(applicationConfig).isNotNull();
        assertThat(applicationConfig.getActivity()).isNotNull();
        assertThat(applicationConfig.getActivity().getResource()).isNotNull();
        assertThat(applicationConfig.getActivity().getInteraction()).isNotNull();

        assertThat(applicationConfig.getActivity().getResource().getAdd()).isNotZero();
        assertThat(applicationConfig.getActivity().getResource().getDelete()).isNotZero();
        assertThat(applicationConfig.getActivity().getResource().getLike()).isNotZero();
        assertThat(applicationConfig.getActivity().getResource().getUnlike()).isNotZero();
        assertThat(applicationConfig.getActivity().getResource().getView()).isNotZero();
        assertThat(applicationConfig.getActivity().getInteraction().getAdd()).isNotZero();
        assertThat(applicationConfig.getActivity().getInteraction().getDelete()).isNotZero();
        assertThat(applicationConfig.getActivity().getInteraction().getLike()).isNotZero();
        assertThat(applicationConfig.getActivity().getInteraction().getUnlike()).isNotZero();

        System.out.println(applicationConfig);
    }
}

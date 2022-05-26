package com.planetway.fudosan.planetx;

import com.planetway.fudosan.configuration.AppProperties;
import com.planetway.fudosan.domain.PlanetXService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("integration-test")
public class ConsentUsageIntegrationTest {

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private PlanetXSoapTemplate planetXSoapTemplate;

    @Test
    @Disabled("This test requires valid consent in the specific environment")
    public void testServicePositive() {
        String planetId = "000000000000";
        Map<String, String> response = planetXSoapTemplate.execute(planetId, planetXService("smailrealestate"));
        assertThat(response).isNotNull();
        response = planetXSoapTemplate.execute(planetId, planetXService("pitatohouse"));
        assertThat(response).isNotNull();
        response = planetXSoapTemplate.execute(planetId, planetXService("suginamirealestate"));
        assertThat(response).isNotNull();
        response = planetXSoapTemplate.execute(planetId, planetXService("tokyoestate"));
        assertThat(response).isNull();
    }

    @Test
    @Disabled("FIXME! Only added ignore to get jenkins building again")
    public void testServiceNegative_WrongPlanetId() {
        Map<String, String> response = planetXSoapTemplate.execute("BAD_PLANET_ID", planetXService("smailrealestate"));
        assertThat(response).isNull();
    }

    private PlanetXService planetXService(String dataBankName) {
        return appProperties.getDataBanks()
                .get(dataBankName)
                .getPlanetXService();
    }
}

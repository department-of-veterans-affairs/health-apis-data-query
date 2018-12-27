package gov.va.health.api.sentinel;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.health.api.sentinel.IdMeOauthRobot.Configuration.Authorization;
import gov.va.health.api.sentinel.IdMeOauthRobot.Configuration.UserCredentials;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

@RequiredArgsConstructor(staticName = "get")
@Slf4j
public class LabRobots {

  private final Config labConfig;

  @Getter(lazy = true)
  private final IdMeOauthRobot user1 = makeRobot1();

  @Getter(lazy = true)
  private final IdMeOauthRobot user2 = makeRobot2();

  @Getter(lazy = true)
  private final IdMeOauthRobot user3 = makeRobot3();

  @Getter(lazy = true)
  private final IdMeOauthRobot user4 = makeRobot4();

  @Getter(lazy = true)
  private final IdMeOauthRobot user5 = makeRobot5();

  @SneakyThrows
  public static LabRobots fromSystemProperties() {
    Config config = new Config(new File("config/lab.properties"));
    return LabRobots.get(config);
  }

  IdMeOauthRobot makeRobot(UserCredentials user) {
    IdMeOauthRobot.Configuration config =
        IdMeOauthRobot.Configuration.builder()
            .authorization(
                Authorization.builder()
                    .clientId(labConfig.clientId())
                    .clientSecret(labConfig.clientSecret())
                    .authorizeUrl("https://dev-api.va.gov/oauth2/authorization")
                    .redirectUrl("https://app/after-auth")
                    .state("2VV5RqFzBG4GcgS-k6OKL6dMEUyt4FH5E-OcwaYaVzU")
                    .aud("alec")
                    .scope("openid")
                    .scope("profile")
                    .scope("offline_access")
                    .scope("launch/patient")
                    .scope("patient/AllergyIntolerance.read")
                    .scope("patient/Condition.read")
                    .scope("patient/DiagnosticReport.read")
                    .scope("patient/Immunization.read")
                    .scope("patient/Medication.read")
                    .scope("patient/MedicationOrder.read")
                    .scope("patient/MedicationStatement.read")
                    .scope("patient/Observation.read")
                    .scope("patient/Patient.read")
                    .scope("patient/Procedure.read")
                    .build())
            .tokenUrl("https://dev-api.va.gov/oauth2/token")
            .user(user)
            .chromeDriver(labConfig.driver())
            .headless(labConfig.headless())
            .build();

    IdMeOauthRobot robot = IdMeOauthRobot.of(config);
    return robot;
  }

  private IdMeOauthRobot makeRobot1() {
    return makeRobot(userCredentials1());
  }

  private IdMeOauthRobot makeRobot2() {
    return makeRobot(userCredentials2());
  }

  private IdMeOauthRobot makeRobot3() {
    return makeRobot(userCredentials3());
  }

  private IdMeOauthRobot makeRobot4() {
    return makeRobot(userCredentials4());
  }

  private IdMeOauthRobot makeRobot5() {
    return makeRobot(userCredentials5());
  }

  UserCredentials userCredentials1() {
    return UserCredentials.builder()
        .id("vasdvp+IDME_01@gmail.com")
        .icn("1017283132V631076")
        .password(labConfig.userPassword())
        .build();
  }

  UserCredentials userCredentials2() {
    return UserCredentials.builder()
        .id("vasdvp+IDME_02@gmail.com")
        .icn("1017283179V257219")
        .password(labConfig.userPassword())
        .build();
  }

  UserCredentials userCredentials3() {
    return UserCredentials.builder()
        .id("vasdvp+IDME_03@gmail.com")
        .icn("1012704686V159887")
        .password(labConfig.userPassword())
        .build();
  }

  UserCredentials userCredentials4() {
    return UserCredentials.builder()
        .id("vasdvp+IDME_04@gmail.com")
        .icn("1017283180V801730")
        .password(labConfig.userPassword())
        .build();
  }

  UserCredentials userCredentials5() {
    return UserCredentials.builder()
        .id("vasdvp+IDME_05@gmail.com")
        .icn("1017283148V813263")
        .password(labConfig.userPassword())
        .build();
  }

  private static class Config {
    private Properties properties;

    @SneakyThrows
    Config(File file) {
      if (file.exists()) {
        log.info("Loading lab properties from: {}", file);
        properties = new Properties(System.getProperties());
        try (FileInputStream in = new FileInputStream(file)) {
          properties.load(in);
        }
      } else {
        log.info("Lab properties not found: {}, using System properties", file);
        properties = System.getProperties();
      }
    }

    String clientId() {
      return valueOf("lab.client-id");
    }

    String clientSecret() {
      return valueOf("lab.client-secret");
    }

    String driver() {
      return valueOf("webdriver.chrome.driver");
    }

    boolean headless() {
      return BooleanUtils.toBoolean(valueOf("webdriver.chrome.headless"));
    }

    String userPassword() {
      return valueOf("lab.user-password");
    }

    private String valueOf(String name) {
      String value = properties.getProperty(name, "");
      assertThat(value).withFailMessage("System property %s must be specified.", name).isNotBlank();
      return value;
    }
  }
}

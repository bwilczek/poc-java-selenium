import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.get;

public class ApiTests {
  @Test
  @DisplayName("A basic request for a remote resource")
  void testBasicGet() {
    get("https://httpbingo.org/json").then().assertThat().body("slideshow.title", equalTo("Sample Slide Show"));
  }
}

package agent;

import java.net.URL;

import static agent.util.Utils.cp;

/**
 * Created by jason on 7/25/14.
 */
public
class TestApp {

    public static
    void main(String[] args) {
      URL url=TestApp.class.getResource(cp(TestApp.class));
        System.out.println(url);
    }
}

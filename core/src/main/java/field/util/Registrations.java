package field.util;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jason on 7/22/14.
 */
public
class Registrations implements Registration {
    public static
    Registration concat(Registration... regs) {
        return new Registrations(Arrays.asList(regs));
    }

    private final List<Registration> registrations;


    public
    Registrations(List<Registration> registrations) {
        this.registrations = registrations;
    }

    @Override
    public
    void remove() {
        for (Registration r : registrations) {
            r.remove();
        }
    }
}

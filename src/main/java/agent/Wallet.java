package agent;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Wallet {

    private final Map<Account, Key> accounts = Collections.synchronizedMap(new LinkedHashMap<>());

}

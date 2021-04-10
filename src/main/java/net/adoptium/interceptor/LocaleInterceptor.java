package net.adoptium.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

// possible alternative: `@Locale String locale` custom bean
// but this solution is much more transparent
/*@Interceptor
public class LocaleInterceptor {

    @AroundInvoke
    public Object intercept(InvocationContext invocationContext) throws Exception {
        invocationContext.
    }
}*/

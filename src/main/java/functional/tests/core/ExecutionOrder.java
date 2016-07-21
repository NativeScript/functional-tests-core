package functional.tests.core;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import java.util.Arrays;
import java.util.Comparator;

public class ExecutionOrder implements IMethodInterceptor {
    @Override
    public java.util.List<IMethodInstance> intercept(java.util.List<IMethodInstance> list, ITestContext iTestContext) {
        Comparator<IMethodInstance> comparator = new Comparator<IMethodInstance>() {
            private int getPriority(IMethodInstance mi) {
                int result = 0;
                int methodIndex =  Integer.parseInt(mi.getMethod().getMethodName().replaceAll("\\D+",""));
//                if (a1 != null) {
//                    result = a1.value();
                return methodIndex;
            }

            public int compare(IMethodInstance m1, IMethodInstance m2) {
                return getPriority(m1) - getPriority(m2);
            }
        };

        IMethodInstance[] array = list.toArray(new IMethodInstance[list.size()]);
        Arrays.sort(array, comparator);
        return Arrays.asList(array);
    }
}

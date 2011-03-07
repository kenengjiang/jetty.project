package org.eclipse.jetty.policy;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.Before;
import org.junit.Test;

public class PolicyMonitorTest
{    
    
    private HashMap<String, String> evaluator = new HashMap<String, String>();

    @Before
    public void init() throws Exception
    {
        System.setProperty( "basedir", MavenTestingUtils.getBaseURI().toASCIIString() );
    }
    
    @Test
    public void testSimpleLoading() throws Exception
    {
        final AtomicInteger count = new AtomicInteger(0);
        
        PolicyMonitor monitor = new PolicyMonitor( MavenTestingUtils.getTestResourceDir("monitor-test-1").getAbsolutePath())
        {
            
            @Override
            public void onPolicyChange(PolicyBlock grant)
            {
                count.incrementAndGet();
            }
        };
        
        monitor.start();
        
        while (!monitor.isInitialized() )
        {
            Thread.sleep(100);
        }
        
        Assert.assertEquals(1,count.get());
        monitor.stop();
    }
    
    @Test
    public void testSimpleReloading() throws Exception
    {
        final AtomicInteger count = new AtomicInteger(0);
        
        PolicyMonitor monitor = new PolicyMonitor( MavenTestingUtils.getTestResourceDir("monitor-test-2").getAbsolutePath())
        {       
            @Override
            public void onPolicyChange(PolicyBlock grant)
            {
                count.incrementAndGet();
            }
        };

        monitor.setScanInterval(1);
        
        monitor.start();
        
        while (! monitor.isInitialized() )
        {
            Thread.sleep(100);
        }
        
        File permFile = MavenTestingUtils.getTestResourceFile("monitor-test-2/global-all-permission.policy");
        
        permFile.setLastModified(System.currentTimeMillis());
                
        monitor.waitForScan();

        Assert.assertEquals(2,count.get());
        monitor.stop();
    }
    
    @Test
    public void testLoading() throws Exception
    {
        final AtomicInteger count = new AtomicInteger(0);
        
        PolicyMonitor monitor = new PolicyMonitor( MavenTestingUtils.getTestResourceDir("monitor-test-3").getAbsolutePath())
        {       
            @Override
            public void onPolicyChange(PolicyBlock grant)
            {
                count.incrementAndGet();
            }
        };
        
        monitor.setScanInterval(1);
        
        monitor.start();
        
        while (! monitor.isInitialized() )
        {
            Thread.sleep(100);
        }
        
        Assert.assertEquals(16,count.get());
        monitor.stop();
    }
}
package com.enonic.cms.itest.framework.blob;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.enonic.cms.framework.blob.BlobStore;
import com.enonic.cms.framework.blob.memory.MemoryBlobStore;

@Configuration
@Profile("itest")
public class BlobStoreBeans
{
    @Bean
    public BlobStore blobStore()
    {
        return new MemoryBlobStore();
    }
}

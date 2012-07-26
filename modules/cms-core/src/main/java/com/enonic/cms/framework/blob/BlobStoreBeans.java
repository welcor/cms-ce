package com.enonic.cms.framework.blob;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.enonic.cms.framework.blob.file.FileBlobStore;

@Configuration
@Profile("default")
public class BlobStoreBeans
{
    @Bean
    public BlobStore blobStore( @Value("${cms.blobstore.dir}") File directory )
    {
        FileBlobStore fileBlobStore = new FileBlobStore();
        fileBlobStore.setDirectory( directory );
        return fileBlobStore;
    }
}

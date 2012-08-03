package com.enonic.cms.framework.blob;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.enonic.cms.framework.blob.file.FileBlobStore;

@Configuration
public class BlobStoreBeans
{
    @Bean
    public BlobStore blobStore( final @Value("${cms.blobstore.dir}") File directory )
    {
        final FileBlobStore fileBlobStore = new FileBlobStore();
        fileBlobStore.setDirectory( directory );
        return fileBlobStore;
    }
}
